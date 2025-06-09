package com.ultrasshservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.vpnmoddervpn.vpn.R;
import com.vpnmoddervpn.vpn.SocksHttpMainActivity;
import com.ultrasshservice.config.V2Config;
import com.ultrasshservice.tunnel.vpn.V2Listener;
import com.ultrasshservice.util.V2Utilities;
import org.json.JSONObject;
import java.util.Objects;

import libv2ray.Libv2ray;
import libv2ray.CoreController;
import libv2ray.CoreCallbackHandler;

public final class V2Core {
    private volatile static V2Core INSTANCE;
    public V2Listener v2Listener = null;
    private boolean isXrayCoreInitialized = false;
    public V2Configs.V2RAY_STATES V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_DISCONNECTED;
    private CountDownTimer countDownTimer;
    private int seconds, minutes, hours;
    private long totalDownload, totalUpload, uploadSpeed, downloadSpeed;
    private String SERVICE_DURATION = "00:00:00";
    private NotificationManager mNotificationManager = null;
    private static final String CHANNEL_ID = "WakkoServiceChannel";
    private CoreController xrayController;

    private V2Core() {
        CoreCallbackHandler callbackHandler = new CoreCallbackHandler() {
            @Override
            public long onEmitStatus(long status, String message) {
                Log.i(V2Core.class.getSimpleName(), "Core status: " + status + ", message: " + message);
                if (message != null && !message.isEmpty()) {
                    Log.e(V2Core.class.getSimpleName(), "Core error: " + message);
                    sendDisconnectedBroadCast();
                    return -1;
                }
                return 0;
            }

            @Override
            public long startup() {
                Log.i(V2Core.class.getSimpleName(), "Xray core started");
                V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_CONNECTED;
                if (v2Listener != null) {
                    v2Listener.onConnected();
                }
                return 0;
            }

            @Override
            public long shutdown() {
                Log.i(V2Core.class.getSimpleName(), "Xray core shutdown");
                V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_DISCONNECTED;
                if (v2Listener != null) {
                    v2Listener.onError();
                }
                return 0;
            }
        };
        xrayController = Libv2ray.newCoreController(callbackHandler);
    }

    public static V2Core getInstance() {
        if (INSTANCE == null) {
            synchronized (V2Core.class) {
                if (INSTANCE == null) {
                    INSTANCE = new V2Core();
                }
            }
        }
        return INSTANCE;
    }

    private void makeDurationTimer(final Context context, final boolean enable_traffic_statics) {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onTick(long millisUntilFinished) {
                seconds++;
                if (seconds == 59) {
                    minutes++;
                    seconds = 0;
                }
                if (minutes == 59) {
                    minutes = 0;
                    hours++;
                }
                if (hours == 23) {
                    hours = 0;
                }
                if (enable_traffic_statics && isXrayCoreRunning()) {
                    try {
                        uploadSpeed = xrayController.queryStats("proxy", "uplink");
                        downloadSpeed = xrayController.queryStats("proxy", "downlink");
                        totalUpload += uploadSpeed;
                        totalDownload += downloadSpeed;
                    } catch (Exception e) {
                        Log.e(V2Core.class.getSimpleName(), "Failed to query stats", e);
                    }
                }
                SERVICE_DURATION = V2Utilities.convertIntToTwoDigit(hours) + ":" + V2Utilities.convertIntToTwoDigit(minutes) + ":" + V2Utilities.convertIntToTwoDigit(seconds);
                Intent connection_info_intent = new Intent("V2RAY_CONNECTION_INFO");
                connection_info_intent.putExtra("STATE", V2Core.getInstance().V2RAY_STATE);
                connection_info_intent.putExtra("DURATION", SERVICE_DURATION);
                connection_info_intent.putExtra("UPLOAD_SPEED", V2Utilities.parseTraffic(uploadSpeed, false, true));
                connection_info_intent.putExtra("DOWNLOAD_SPEED", V2Utilities.parseTraffic(downloadSpeed, false, true));
                connection_info_intent.putExtra("UPLOAD_TRAFFIC", V2Utilities.parseTraffic(totalUpload, false, false));
                connection_info_intent.putExtra("DOWNLOAD_TRAFFIC", V2Utilities.parseTraffic(totalDownload, false, false));
                context.sendBroadcast(connection_info_intent);
            }

            public void onFinish() {
                countDownTimer.cancel();
                if (isXrayCoreRunning()) {
                    makeDurationTimer(context, enable_traffic_statics);
                }
            }
        }.start();
    }

    public void setUpListener(Service targetService) {
        try {
            v2Listener = (V2Listener) targetService;
            Libv2ray.initCoreEnv(V2Utilities.getUserAssetsPath(targetService.getApplicationContext()), "");
            isXrayCoreInitialized = true;
            SERVICE_DURATION = "00:00:00";
            seconds = 0;
            minutes = 0;
            hours = 0;
            uploadSpeed = 0;
            downloadSpeed = 0;
            Log.i(V2Core.class.getSimpleName(), "setUpListener => Initialized Xray core from " + v2Listener.getService().getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(V2Core.class.getSimpleName(), "setUpListener failed => ", e);
            isXrayCoreInitialized = false;
        }
    }

    public boolean startCore(final V2Config v2Config) {
        makeDurationTimer(v2Listener.getService().getApplicationContext(), v2Config.ENABLE_TRAFFIC_STATICS);
        V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_CONNECTING;
        if (!isXrayCoreInitialized) {
            Log.e(V2Core.class.getSimpleName(), "startCore failed => Xray core should be initialized before start.");
            return false;
        }
        if (isXrayCoreRunning()) {
            stopCore();
        }
        try {
            // Simular protect para sockets, se necessário
            if (v2Listener != null) {
                v2Listener.startService();
            }
            xrayController.startLoop(v2Config.V2RAY_FULL_JSON_CONFIG);
            showNotification(v2Config);
            Log.i(V2Core.class.getSimpleName(), "startCore success => Xray core started with config: " + v2Config.V2RAY_FULL_JSON_CONFIG);
            return true;
        } catch (Exception e) {
            Log.e(V2Core.class.getSimpleName(), "startCore failed => ", e);
            sendDisconnectedBroadCast();
            return false;
        }
    }

    public void stopCore() {
        try {
            if (isXrayCoreRunning()) {
                xrayController.stopLoop();
                if (v2Listener != null) {
                    v2Listener.stopService();
                }
                Log.i(V2Core.class.getSimpleName(), "stopCore success => Xray core stopped.");
            } else {
                Log.i(V2Core.class.getSimpleName(), "stopCore => Xray core not running.");
            }
            sendDisconnectedBroadCast();
        } catch (Exception e) {
            Log.e(V2Core.class.getSimpleName(), "stopCore failed => ", e);
        }
    }

    private void sendDisconnectedBroadCast() {
        V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_DISCONNECTED;
        SERVICE_DURATION = "00:00:00";
        seconds = 0;
        minutes = 0;
        hours = 0;
        uploadSpeed = 0;
        downloadSpeed = 0;
        if (v2Listener != null) {
            Intent connection_info_intent = new Intent("V2RAY_CONNECTION_INFO");
            connection_info_intent.putExtra("STATE", V2Core.getInstance().V2RAY_STATE);
            connection_info_intent.putExtra("DURATION", SERVICE_DURATION);
            connection_info_intent.putExtra("UPLOAD_SPEED", V2Utilities.parseTraffic(0, false, true));
            connection_info_intent.putExtra("DOWNLOAD_SPEED", V2Utilities.parseTraffic(0, false, true));
            connection_info_intent.putExtra("UPLOAD_TRAFFIC", V2Utilities.parseTraffic(0, false, false));
            connection_info_intent.putExtra("DOWNLOAD_TRAFFIC", V2Utilities.parseTraffic(0, false, false));
            try {
                v2Listener.getService().getApplicationContext().sendBroadcast(connection_info_intent);
            } catch (Exception e) {
                // Ignorar
            }
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            try {
                mNotificationManager = (NotificationManager) v2Listener.getService().getSystemService(Context.NOTIFICATION_SERVICE);
            } catch (Exception e) {
                return null;
            }
        }
        return mNotificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannelID(final String application_name) {
        String notification_channel_id = CHANNEL_ID;
        NotificationChannel notificationChannel = new NotificationChannel(notification_channel_id, "Xray Service", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setLightColor(R.color.blue);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationChannel.setImportance(NotificationManager.IMPORTANCE_NONE);
        Objects.requireNonNull(getNotificationManager()).createNotificationChannel(notificationChannel);
        return notification_channel_id;
    }

    private int judgeForNotificationFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            return PendingIntent.FLAG_UPDATE_CURRENT;
        }
    }

    public void showNotification(final V2Config v2Config) {
        if (v2Listener == null) {
            return;
        }
        Intent launchIntent = new Intent(v2Listener.getService().getApplicationContext(), SocksHttpMainActivity.class);
        launchIntent.setAction("FROM_DISCONNECT_BTN");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent notificationContentPendingIntent = PendingIntent.getActivity(v2Listener.getService(), 0, launchIntent, judgeForNotificationFlag());
        String notificationChannelID = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannelID = createNotificationChannelID(v2Config.APPLICATION_NAME);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(v2Listener.getService(), notificationChannelID);
        mBuilder.setSmallIcon(R.drawable.v2icon)
                .setContentTitle("Conectado a Xray")
                .setOngoing(true)
                .setShowWhen(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(notificationContentPendingIntent)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        v2Listener.getService().startForeground(1, mBuilder.build());
    }

    public boolean isXrayCoreRunning() {
        return xrayController != null && xrayController.getIsRunning();
    }

    public Long getConnectedV2rayServerDelay() {
        try {
            return xrayController.measureDelay("");
        } catch (Exception e) {
            Log.e(V2Core.class.getSimpleName(), "getConnectedV2rayServerDelay failed => ", e);
            return -1L;
        }
    }

    public Long getV2rayServerDelay(final String config) {
        try {
            JSONObject config_json = new JSONObject(config);
            JSONObject new_routing_json = config_json.getJSONObject("routing");
            new_routing_json.remove("rules");
            config_json.remove("routing");
            config_json.put("routing", new_routing_json);
            return Libv2ray.measureOutboundDelay(config_json.toString(), "proxy");
        } catch (Exception e) {
            Log.e(V2Core.class.getSimpleName(), "getV2rayServerDelay failed => ", e);
            return -1L;
        }
    }

    // Método para proteger sockets, simulando comportamento da biblioteca antiga
    public boolean protectSocket(int socket) {
        if (v2Listener != null) {
            return v2Listener.onProtect(socket);
        }
        return true;
    }
}