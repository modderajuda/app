package com.ultrasshservice;

import android.app.Service;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;


import com.ultrasshservice.config.V2Config;
import com.ultrasshservice.tunnel.V2Proxy;
import com.ultrasshservice.tunnel.vpn.V2Listener;

import java.io.File;
import java.io.FileDescriptor;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.ultrasshservice.tunnel.*;
import com.ultrasshservice.*;
import com.ultrasshservice.tunnel.vpn.*;
import android.content.Context;
import com.ultrasshservice.logger.SkStatus;
import android.os.Handler; // Importar Handler
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import com.ultrasshservice.tunnel.vpn.V2Listener;

public class V2Service extends VpnService implements V2Listener {
    private ParcelFileDescriptor mInterface;
    private Process process;
    private V2Config v2Config;
    private boolean isRunning = true;

    @Override
    public void onCreate() {
        super.onCreate();
        V2Core.getInstance().setUpListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        V2Configs.V2RAY_SERVICE_COMMANDS startCommand = (V2Configs.V2RAY_SERVICE_COMMANDS) intent.getSerializableExtra("COMMAND");
        if (startCommand.equals(V2Configs.V2RAY_SERVICE_COMMANDS.START_SERVICE)) {
            v2Config = (V2Config) intent.getSerializableExtra("V2RAY_CONFIG");
            if (v2Config == null) {
                this.onDestroy();
            }
            if (V2Core.getInstance().isXrayCoreRunning()) {
                V2Core.getInstance().stopCore();
            }
            if (V2Core.getInstance().startCore(v2Config)) {
                Log.e(V2Proxy.class.getSimpleName(), "onStartCommand success => v2ray core started.");
            } else {
                this.onDestroy();
            }
        } else if (startCommand.equals(V2Configs.V2RAY_SERVICE_COMMANDS.STOP_SERVICE)) {
            V2Core.getInstance().stopCore();
        } else if (startCommand.equals(V2Configs.V2RAY_SERVICE_COMMANDS.MEASURE_DELAY)) {
            new Thread(() -> {
                Intent sendB = new Intent("CONNECTED_V2RAY_SERVER_DELAY");
                sendB.putExtra("DELAY", String.valueOf(V2Core.getInstance().getConnectedV2rayServerDelay()));
                sendBroadcast(sendB);
            }, "MEASURE_CONNECTED_V2RAY_SERVER_DELAY").start();
        } else {
            this.onDestroy();
        }
        return START_STICKY;
    }

    private void stopAllProcess() {
        SkStatus.logInfo("<font color='red'><strong>v2 service stopAllProcess</strong></font>");
        stopForeground(true);
        isRunning = false;
        if (process != null) {
            process.destroy();
            SkStatus.logInfo("<font color='red'><strong>v2 service stopAllProcess 1</strong></font>");
        }
        V2Core.getInstance().stopCore();
        try {
            stopSelf();
            SkStatus.logInfo("<font color='red'><strong>v2 service stopSelf</strong></font>");
        } catch (Exception e) {
            //ignore
            Log.e("CANT_STOP", "SELF");
        }
        try {
            mInterface.close();
            SkStatus.logInfo("<font color='red'><strong>mInterface</strong></font>");
        } catch (Exception e) {
            // ignored
        }

    }

    private void setup() {
        Intent prepare_intent = prepare(this);
        if (prepare_intent != null) {
            return;
        }
        Builder builder = new Builder();
        builder.setSession(v2Config.REMARK);
        builder.setMtu(1500);
        builder.addAddress("26.26.26.1", 30);
        builder.addRoute("0.0.0.0", 0);
        builder.addDnsServer("1.1.1.1");
        builder.addDnsServer("8.8.4.4");
        if (v2Config.BLOCKED_APPS != null) {
            for (int i = 0; i < v2Config.BLOCKED_APPS.size(); i++) {
                try {
                    builder.addDisallowedApplication(v2Config.BLOCKED_APPS.get(i));
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        try {
            mInterface.close();
        } catch (Exception e) {
            //ignore
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setMetered(false);
        }

        try {
            mInterface = builder.establish();
            isRunning = true;
            runTun2socks();
        } catch (Exception e) {
            stopAllProcess();
        }

    }

    private void runTun2socks() {
        ArrayList<String> cmd = new ArrayList<>(Arrays.asList(new File(getApplicationInfo().nativeLibraryDir, "libv2ray.so").getAbsolutePath(),
                "--netif-ipaddr", "26.26.26.2",
                "--netif-netmask", "255.255.255.252",
                "--socks-server-addr", "127.0.0.1:" + v2Config.LOCAL_SOCKS5_PORT,
                "--tunmtu", "1500",
                "--sock-path", "sock_path",
                "--enable-udprelay",
                "--loglevel", "error"));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.directory(getApplicationContext().getFilesDir()).start();
            new Thread(() -> {
                try {
                    process.waitFor();
                    if (isRunning) {
                        runTun2socks();
                    }
                } catch (InterruptedException e) {
                    //ignore
                }
            }, "Tun2socks_Thread").start();
            sendFileDescriptor();
        } catch (Exception e) {
            Log.e("VPN_SERVICE", "FAILED=>", e);
            this.onDestroy();
        }
    }

    private void sendFileDescriptor() {
        String localSocksFile = new File(getApplicationContext().getFilesDir(), "sock_path").getAbsolutePath();
        FileDescriptor tunFd = mInterface.getFileDescriptor();
        new Thread(() -> {
            int tries = 0;
            while (true) {
                try {
                    Thread.sleep(50L * tries);
                    LocalSocket clientLocalSocket = new LocalSocket();
                    clientLocalSocket.connect(new LocalSocketAddress(localSocksFile, LocalSocketAddress.Namespace.FILESYSTEM));
                    if (!clientLocalSocket.isConnected()) {
                        Log.e("SOCK_FILE", "Unable to connect to localSocksFile [" + localSocksFile + "]");
                    } else {
                        Log.e("SOCK_FILE", "connected to sock file [" + localSocksFile + "]");
                    }
                    OutputStream clientOutStream = clientLocalSocket.getOutputStream();
                    clientLocalSocket.setFileDescriptorsForSend(new FileDescriptor[]{tunFd});
                    clientOutStream.write(32);
                    clientLocalSocket.setFileDescriptorsForSend(null);
                    clientLocalSocket.shutdownOutput();
                    clientLocalSocket.close();
                    break;
                } catch (Exception e) {
                    Log.e(V2Service.class.getSimpleName(), "sendFd failed =>", e);
                    if (tries > 5){
                        return;
                    }
                    tries ++;
                }
            }
        }, "sendFd_Thread").start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SkStatus.logInfo("<font color='red'><strong>v2service ondestroy</strong></font>");
    }

    @Override
    public void onRevoke() {
        stopAllProcess();
        SkStatus.logInfo("<font color='red'><strong>v2service onrevoke</strong></font>");
    }

    @Override
    public boolean onProtect(int socket) {
        return protect(socket);
    }

    @Override
    public Service getService() {
        return this;
    }

    @Override
    public void startService() {
        setup();
    }

    @Override
    public void stopService() {
        stopAllProcess();
        SkStatus.logInfo("<font color='red'><strong>v2service stopservice</strong></font>");
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onError() {
        SkStatus.logInfo("<font color='red'><strong>v2service onerror</strong></font>");

    }
}
