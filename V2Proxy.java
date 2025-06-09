package com.ultrasshservice.tunnel;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ultrasshservice.V2Configs;
import com.ultrasshservice.V2Core;
import com.ultrasshservice.config.V2Config;
import com.ultrasshservice.tunnel.vpn.V2Listener;

import com.ultrasshservice.tunnel.*;
import com.ultrasshservice.tunnel.vpn.*;
import android.content.Context;
import com.ultrasshservice.logger.SkStatus;

public class V2Proxy extends Service implements V2Listener {


    @Override
    public void onCreate() {
        super.onCreate();
        V2Core.getInstance().setUpListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        V2Configs.V2RAY_SERVICE_COMMANDS startCommand = (V2Configs.V2RAY_SERVICE_COMMANDS) intent.getSerializableExtra("COMMAND");
        if (startCommand.equals(V2Configs.V2RAY_SERVICE_COMMANDS.START_SERVICE)) {
            V2Config v2Config = (V2Config) intent.getSerializableExtra("V2RAY_CONFIG");
            if (v2Config == null) {
                this.onDestroy();
            }
            if (V2Core.getInstance().isXrayCoreRunning()) {
                V2Core.getInstance().stopCore();
                SkStatus.logInfo("<font color='red'><strong>v2proxy stopCore 0</strong></font>");
            }
            //assert v2Config != null;
            if (V2Core.getInstance().startCore(v2Config)) {
                Log.e(V2Proxy.class.getSimpleName(), "onStartCommand success => v2ray core started.");
                return START_STICKY;
            }
        } else if (startCommand.equals(V2Configs.V2RAY_SERVICE_COMMANDS.STOP_SERVICE)) {
            V2Core.getInstance().stopCore();
            SkStatus.logInfo("<font color='red'><strong>v2proxy stopCore</strong></font>");
            return START_STICKY;
        } else if (startCommand.equals(V2Configs.V2RAY_SERVICE_COMMANDS.MEASURE_DELAY)) {
            new Thread(() -> {
                Intent sendB = new Intent("CONNECTED_V2RAY_SERVER_DELAY");
                sendB.putExtra("DELAY", String.valueOf(V2Core.getInstance().getConnectedV2rayServerDelay()));
                sendBroadcast(sendB);
            }, "MEASURE_CONNECTED_V2RAY_SERVER_DELAY").start();
            return START_STICKY;
        }
        this.onDestroy();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SkStatus.logInfo("<font color='red'><strong>v2proxy ondestroy</strong></font>");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onProtect(int socket) {
        return true;
    }

    @Override
    public Service getService() {
        return this;
    }

    @Override
    public void startService() {
        //ignore
    }

    @Override
    public void stopService() {
        SkStatus.logInfo("<font color='red'><strong>v2proxy stopservice</strong></font>");
        try {
            stopSelf();
            SkStatus.logInfo("<font color='red'><strong>v2proxy stopservice</strong></font>");
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onError() {
        SkStatus.logInfo("<font color='red'><strong>v2proxy onerror</strong></font>");

    }
}
