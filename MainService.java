package com.ultrasshservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import com.vpnmoddervpn.vpn.SocksHttpMainActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import com.ultrasshservice.logger.SkStatus;
import android.os.Binder;
import android.os.Handler;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.List;
import com.ultrasshservice.aidl.IUltraSSHServiceInternal;
import android.os.RemoteException;
import android.annotation.TargetApi;
import android.os.Build;
import android.app.Notification;
import com.ultrasshservice.logger.ConnectionStatus;
import androidx.annotation.NonNull;
import android.app.NotificationManager;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import android.app.PendingIntent;
import android.content.ComponentName;
import java.io.IOException;
import androidx.annotation.RequiresApi;
import com.vpnmoddervpn.vpn.TunnelUtils;
import com.vpnmoddervpn.vpn.TunnelManagerThread;
import android.content.BroadcastReceiver;
import com.ultrasshservice.config.Settings;
import android.app.NotificationChannel;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import android.app.Activity;
import java.net.InetAddress;
import java.net.UnknownHostException;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.LinkAddress;
import android.net.Network;
import android.net.LinkProperties;
import com.ultrasshservice.util.DummyActivity;
import android.graphics.Color;
import com.vpnmoddervpn.vpn.DNSTunnelThread;
import android.content.SharedPreferences;
import com.vpnmoddervpn.vpn.R;
import java.util.Date;
import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.FLAG_IMMUTABLE;
import android.telephony.TelephonyManager;
import java.util.concurrent.TimeUnit;

public class MainService extends Service
implements SkStatus.StateListener
{
    private static final String TAG = MainService.class.getSimpleName();
    public static final String START_SERVICE = 
"com.vpnmoddervpn.vpn:startTunnel";

    private static final int PRIORITY_MIN = -2;
    private static final int PRIORITY_DEFAULT = 0;
    private static final int PRIORITY_MAX = 2;
    public static boolean isRunning = false;
    private NotificationManager mNotificationManager;
    private Handler mHandler;
    private Settings mPrefs;
    private Thread mTunnelThread;
    private PowerManager.WakeLock wakeLock;
    private TunnelManagerThread mTunnelManager;
    private ConnectivityManager connMgr;
    private DNSTunnelThread mDnsThread;
    private static SharedPreferences sp;
    private MainReceiver mConnectivityReceiver;
    private static final int NOTIFICATION_ID = 1234; // Coloque um valor único
    // Variáveis para contagem de tempo e atualização da notificação
    private long mConnectionStartTime = 0;
private Handler mNotificationHandler = new Handler();
private Runnable mNotificationRunnable = new Runnable() {
    @Override
    public void run() {
        updateNotificationTime();
        mNotificationHandler.postDelayed(this, 1000); // Atualiza a cada 1 segundo
    }
};

    private final IBinder mBinder = new IUltraSSHServiceInternal.Stub() {
        @Override
        public void stopVPN() {
            MainService.this.stopTunnel();
        }
    };

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");
        super.onCreate();
        sp = new Settings(this).getPrefsPrivate();
        mPrefs = new Settings(this);
        mHandler = new Handler();
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand");
        startTunnelBroadcast();
        SkStatus.addStateListener(this);
        if (intent != null && START_SERVICE.equals(intent.getAction()))
            return START_NOT_STICKY;
        String stateMsg = getString(SkStatus.getLocalizedState(SkStatus.getLastState()));
        showNotification(stateMsg, NOTIFICATION_CHANNEL_NEWSTATUS_ID, 0, ConnectionStatus.LEVEL_START, null);
        new Thread(new Runnable() {
                @Override
                public void run() {
                    startTunnel();
                }
            }).start();
        return Service.START_NOT_STICKY;
    }

    private void setWakelock() {
        try {
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "MainService::WakelockService");
            this.wakeLock = newWakeLock;
            newWakeLock.acquire();
            SkStatus.logInfo("CPU Wakelock Ativo");
        } catch (Exception e) {
            Log.d("WAKELOCK: ", e.getMessage());
        }
    }

    private void unsetWakelock() {
        PowerManager.WakeLock wakeLock2 = wakeLock;
        if (wakeLock2 != null && wakeLock2.isHeld()) {
            Log.e("WAKELOCK", "is disabled");
            wakeLock.release();
            SkStatus.logInfo("CPU Wakelock Parado");
        }
    }
     
    public static SharedPreferences getSharedPrefs() {
        return sp;
    }
    
    public synchronized void startTunnel() {
        SkStatus.updateStateString(SkStatus.SSH_INICIANDO, getString(R.string.starting_service_ssh));
        networkStateChange(this, true);
        TelephonyManager telephonyManager =
            ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
        String simOperatorName = telephonyManager.getSimOperatorName();
        String RegionSim = telephonyManager.getSimCountryIso();
        if (mPrefs.getWakelock()) {
            setWakelock();
        }
        SkStatus.logInfo(String.format("Ip Local: %s", getIpPublic()));
        try {
            SharedPreferences prefs = mPrefs.getPrefsPrivate();
            int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
            mTunnelManager = new TunnelManagerThread(mHandler, this);
            mTunnelManager.setOnStopClienteListener(new TunnelManagerThread.OnStopCliente() {
                    @Override
                    public void onStop() {
                        endTunnelService();
                    }
                });

            mTunnelThread = new Thread(mTunnelManager);
            mTunnelThread.start();
        } catch(Exception e) {
            SkStatus.logException(e);
            endTunnelService();
        }
    }
    
    public synchronized void stopTunnel() {
        SharedPreferences prefs = mPrefs.getPrefsPrivate();
        int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
        if (mTunnelManager != null) {
            mTunnelManager.stopAll();
            networkStateChange(this, true);
            if (mTunnelThread != null) {
                mTunnelThread.interrupt();
                SkStatus.logInfo("PARANDO TUNNEL THREAD");
            }
            mTunnelManager = null;
            if (wakeLock != null) {
                unsetWakelock();
            }
            stopConnectionTimer(); // Chama o método para parar a contagem do tempo
        }
    }

    protected String getIpPublic() {
        final android.net.NetworkInfo network = connMgr
            .getActiveNetworkInfo();
        if (network != null && network.isConnectedOrConnecting()) {
            return TunnelUtils.getLocalIpAddress();
        }
        else {
            return "Indisponivel";
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        stopTunnel();
        stopTunnelBroadcast();
        SkStatus.removeStateListener(this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.d(TAG,"tarefa removida");
        Intent intent = new Intent(this, DummyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        SkStatus.logWarning("Aviso: pouca memória ram no dispositivo!");
    }

    public void endTunnelService() {
        mHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    stopSelf();
                    SkStatus.removeStateListener(MainService.this);
                }
            });
    }

    private void register_connectivity_receiver() {
        this.mConnectivityReceiver = new MainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(this.mConnectivityReceiver, filter);
    }

    private void unregister_connectivity_receiver() {
        unregisterReceiver(this.mConnectivityReceiver);
    }

    public static final String NOTIFICATION_CHANNEL_BG_ID = "openvpn_bg";
    public static final String NOTIFICATION_CHANNEL_NEWSTATUS_ID = "openvpn_newstat";
    public static final String NOTIFICATION_CHANNEL_USERREQ_ID = "openvpn_userreq";
    private String type = "Direct Connection";
    private Notification.Builder mNotifyBuilder = null;
    private String lastChannel;
    private void showNotification(final String msg, final String channel, long when, final ConnectionStatus status, Intent intent) {
    SharedPreferences prefs = mPrefs.getPrefsPrivate();
    int icon = getIconByConnectionStatus(status);
    int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
    // Define o tipo de conexão baseado no tunnelType
    String type;
    if (tunnelType == Settings.bTUNNEL_TYPE_SSH_DIRECT) {
        type = getString(R.string.app_name);
    } else if (tunnelType == Settings.bTUNNEL_TYPE_SSH_PROXY) {
        type = getString(R.string.app_name);
    } else if (tunnelType == Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL) {
        type = getString(R.string.app_name);
    } else if (tunnelType == Settings.bTUNNEL_TYPE_PAY_SSL) {
        type = getString(R.string.app_name);
    } else if(tunnelType==Settings.bTUNNEL_TYPE_SLOWDNS){
        type = getString(R.string.app_name);
    } else if(tunnelType==Settings.bTUNNEL_TYPE_V2RAY){
        type = getString(R.string.app_name);
    } else {
        type = "Direct Connection";
    }

    // Inicializa o Notification.Builder se necessário
    if (mNotifyBuilder == null) {
        mNotifyBuilder = new Notification.Builder(this)
            .setContentTitle(type) // Título da notificação
            .setOnlyAlertOnce(true) // Evita alertas repetidos
            .setOngoing(true) // Notificação permanente
            .setWhen(new Date().getTime()); // Define o timestamp
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            addVpnActionsToNotification(mNotifyBuilder);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lpNotificationExtras(mNotifyBuilder, Notification.CATEGORY_SERVICE);
        }
    }

    // Define a prioridade ou importância da notificação
    int priority = PRIORITY_MIN;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channelObj = new NotificationChannel(
            NOTIFICATION_CHANNEL_BG_ID,
            "Background Notifications",
            NotificationManager.IMPORTANCE_MIN // Importância mínima
        );
        mNotificationManager.createNotificationChannel(channelObj);
        mNotifyBuilder.setChannelId(NOTIFICATION_CHANNEL_BG_ID);
    } else {
        jbNotificationExtras(priority, mNotifyBuilder);
    }

    // Configura o ícone e o texto da notificação
    mNotifyBuilder.setSmallIcon(icon);
    // Adiciona o tempo conectado se o status for conectado
    String notificationText = msg;
    if (status == ConnectionStatus.LEVEL_CONNECTED && mConnectionStartTime > 0) {
        long elapsedTime = System.currentTimeMillis() - mConnectionStartTime;
        String timeString = formatElapsedTime(elapsedTime);
        notificationText = msg + " - " + timeString;
        Log.d(TAG, "Updating notification time: " + timeString);
    } else {
         Log.d(TAG, "Not connected, showing base message: " + msg);
    }

    mNotifyBuilder.setContentText(notificationText); // Define o texto principal
    // Define a ação da notificação com base no status
    if (status == ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT) {
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        mNotifyBuilder.setContentIntent(pIntent);
    } else {
        mNotifyBuilder.setContentIntent(getGraphPendingIntent(this));
    }
    // Define o timestamp da notificação, se aplicável
    if (when != 0) {
        mNotifyBuilder.setWhen(when);
    }
    // Constrói a notificação
    Notification notification = mNotifyBuilder.build();
    int notificationId = NOTIFICATION_ID;
    startForeground(NOTIFICATION_ID, notification);
    mNotificationManager.notify(NOTIFICATION_ID, notification);
     lastChannel = channel;
}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void lpNotificationExtras(Notification.Builder nbuilder, String category) {
        nbuilder.setCategory(category);
        nbuilder.setLocalOnly(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void jbNotificationExtras(int priority,
                                      Notification.Builder nbuilder) {
        try {
            if (priority != 0) {
                Method setpriority = nbuilder.getClass().getMethod("setPriority", int.class);
                setpriority.invoke(nbuilder, priority);
                Method setUsesChronometer = nbuilder.getClass().getMethod("setUsesChronometer", boolean.class);
                setUsesChronometer.invoke(nbuilder, true);
            }

        } catch (NoSuchMethodException | IllegalArgumentException |
        InvocationTargetException | IllegalAccessException e) {
            SkStatus.logException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addVpnActionsToNotification(Notification.Builder nbuilder) {
           Intent reconnectVPN = new Intent(this, MainReceiver.class);
        reconnectVPN.setAction(MainReceiver.ACTION_SERVICE_RESTART);
        PendingIntent reconnectPendingIntent = PendingIntent.getBroadcast(this, 0, reconnectVPN, FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE);
        nbuilder.addAction(R.drawable.ic_autorenew_black_24dp,
                           getString(R.string.reconnect), reconnectPendingIntent);
        Intent disconnectVPN = new Intent(this, MainReceiver.class);
        disconnectVPN.setAction(MainReceiver.ACTION_SERVICE_STOP);
          PendingIntent disconnectPendingIntent = PendingIntent.getBroadcast(this, 0, disconnectVPN, FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE);
        nbuilder.addAction(R.drawable.ic_power_settings_new_black_24dp,
                           getString(R.string.stop), disconnectPendingIntent);
    }

    private int getIconByConnectionStatus(ConnectionStatus level) {
        switch (level) {
            case LEVEL_CONNECTED:
                return R.drawable.ic_cloud_black_24dp;
            case LEVEL_AUTH_FAILED:
            case LEVEL_NONETWORK:
            case LEVEL_NOTCONNECTED:
            case LEVEL_CONNECTING_NO_SERVER_REPLY_YET:
            case LEVEL_CONNECTING_SERVER_REPLIED:
            case UNKNOWN_LEVEL:
            default:
                return R.drawable.ic_cloud_off_black_24dp;
        }
    }

    public static PendingIntent getGraphPendingIntent(Context context) {
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE | 0: 0;
        Intent intent = new Intent(context, SocksHttpMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return PendingIntent.getActivity(context, 0, intent, flags);
    }

    @Override
public void updateState(String state, String msg, int resid, ConnectionStatus level, Intent intent) {
    if (mTunnelThread == null)
        return;
    String channel = NOTIFICATION_CHANNEL_BG_ID;
    if (level.equals(ConnectionStatus.LEVEL_CONNECTED)) {
        channel = NOTIFICATION_CHANNEL_USERREQ_ID;
        if (mConnectionStartTime == 0) {
             mConnectionStartTime = System.currentTimeMillis(); // Inicia a contagem de tempo
             mNotificationHandler.post(mNotificationRunnable); // Inicia a atualização da notificação
             Log.d(TAG, "Connection state CONNECTED: Starting time update and setting start time.");
        }
    } else {
        mNotificationHandler.removeCallbacks(mNotificationRunnable); // Para a atualização da notificação
        mConnectionStartTime = 0; // Reseta o tempo
        Log.d(TAG, "Connection state changed/disconnected: Stopping time update and resetting start time.");
    }
    String stateMsg = getString(SkStatus.getLocalizedState(SkStatus.getLastState()));
    showNotification(stateMsg, channel, 0, level, null);
}
    private String formatElapsedTime(long millis) {
    long hours = TimeUnit.MILLISECONDS.toHours(millis);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
}
    // Método para atualizar a notificação com o tempo
    private void updateNotificationTime() {
    // Only update if start time is set
    if (mConnectionStartTime > 0) { // Removed SkStatus.getLastState().equals(ConnectionStatus.LEVEL_CONNECTED)
        long elapsedTime = System.currentTimeMillis() - mConnectionStartTime;
        String timeString = formatElapsedTime(elapsedTime);
        String stateMsg = getString(SkStatus.getLocalizedState(SkStatus.getLastState()));
        String notificationText = stateMsg + " - " + timeString;
            //int notificationId = channel.hashCode();
            int notificationId = NOTIFICATION_ID;
        // Atualiza o texto da notificação existente
        if (mNotifyBuilder != null) {
            mNotifyBuilder.setContentText(notificationText);
            Notification notification = mNotifyBuilder.build();
            // Use the constant NOTIFICATION_ID to update the existing notification
            mNotificationManager.notify(notificationId, notification);
            Log.d(TAG, "Notification updated with time: " + timeString);
        }
    } else {
         // If somehow the runnable is still running but not connected, stop it.
         mNotificationHandler.removeCallbacks(mNotificationRunnable);
         mConnectionStartTime = 0;
         Log.d(TAG, "updateNotificationTime: Not connected, stopping runnable.");
    }
}

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network net) {
            SkStatus.logDebug("Rede disponivel");
        }
        @Override
        public void onLost(Network net) {
            SkStatus.logDebug("Rede perdida");
        }
        @Override
        public void onUnavailable() {
            SkStatus.logDebug("Rede indisponivel");
        }
    };

    public static final String TUNNEL_SSH_RESTART_SERVICE = MainService.class.getName() + "::restartservicebroadcast",
    TUNNEL_SSH_STOP_SERVICE = MainService.class.getName() + "::stopservicebroadcast";
    private void startTunnelBroadcast() {
        if (Build.VERSION.SDK_INT >= 24) {
            connMgr.registerDefaultNetworkCallback(networkCallback);
        }
        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(TUNNEL_SSH_STOP_SERVICE);
        broadcastFilter.addAction(TUNNEL_SSH_RESTART_SERVICE);
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mTunnelSSHBroadcastReceiver, broadcastFilter);
    }

    private void stopTunnelBroadcast() {
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(mTunnelSSHBroadcastReceiver);
        if (Build.VERSION.SDK_INT >= 24)
            connMgr.unregisterNetworkCallback(networkCallback);
    }
    
    private BroadcastReceiver mTunnelSSHBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
                if (action.equals(MainService.TUNNEL_SSH_RESTART_SERVICE)) {
                    new Thread(new Runnable() {
                        public void run() {
                            if (MainService.this.mTunnelManager != null) {
                                MainService.this.mTunnelManager.reconnectSSH();
                            }
                        }
                    }).start();
                } else if (action.equals(MainService.TUNNEL_SSH_STOP_SERVICE)) {
                    MainService.this.endTunnelService();
                }
        }
    };

    private static String lastStateMsg;

    protected void networkStateChange(Context context, boolean showStatusRepetido) {
        String netstatestring;
        try {
            // deprecated in 29
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null) {
                netstatestring = "not connected";
            } else {
                String subtype = networkInfo.getSubtypeName();
                if (subtype == null)
                    subtype = "";
                String extrainfo = networkInfo.getExtraInfo();
                if (extrainfo == null)
                    extrainfo = "";
                netstatestring = String.format("%2$s %4$s to %1$s %3$s", networkInfo.getTypeName(),
                                               networkInfo.getDetailedState(), extrainfo, subtype);
            }

        } catch (Exception e) {
            netstatestring = e.getMessage();
        }

        if (showStatusRepetido || !netstatestring.equals(lastStateMsg))
            SkStatus.logInfo(netstatestring);

        lastStateMsg = netstatestring;
    }


private void stopConnectionTimer() {
    mConnectionStartTime = 0; // Reseta o tempo de conexão
    mNotificationHandler.removeCallbacks(mNotificationRunnable); // Para a atualização da notificação
}


}
