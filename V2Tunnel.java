package com.vpnmoddervpn.vpn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import android.content.SharedPreferences;

import com.vpnmoddervpn.vpn.util.ConfigUtil;
import com.ultrasshservice.config.Settings;
import com.ultrasshservice.config.SettingsConstants;
import com.ultrasshservice.logger.SkStatus;
import com.ultrasshservice.tunnel.TunnelManagerHelper;
import com.vpnmoddervpn.vpn.TunnelManagerThread;
import com.ultrasshservice.tunnel.vpn.V2Listener;
import com.ultrasshservice.V2Configs;
import com.ultrasshservice.util.V2Utilities;
import com.ultrasshservice.V2Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.TextView;

import com.ultrasshservice.V2Core;
import com.ultrasshservice.logger.SkStatus;
import com.ultrasshservice.tunnel.vpn.V2Listener;
import android.net.ConnectivityManager;
import com.vpnmoddervpn.vpn.R;
import com.trilead.ssh2.transport.TransportManager;
import java.util.concurrent.CountDownLatch;
import com.trilead.ssh2.Connection;
import android.widget.Toast;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.File;
import com.trilead.ssh2.KnownHosts;
import com.ultrasshservice.config.SettingsConstants;
import com.trilead.ssh2.ProxyData;
import com.trilead.ssh2.DynamicPortForwarder;
import com.trilead.ssh2.ConnectionMonitor;
import com.trilead.ssh2.DebugLogger;
import com.trilead.ssh2.InteractiveCallback;
import com.trilead.ssh2.ServerHostKeyVerifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.vpnmoddervpn.vpn.TunnelUtils;
import android.net.NetworkCapabilities;
import android.os.Handler; // Importar Handler
import android.os.Looper;
import java.util.concurrent.atomic.AtomicBoolean;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.ConnectivityManager;
import com.trilead.ssh2.ConnectionMonitor;
import com.vpnmoddervpn.vpn.V2Tunnel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import android.app.ActivityManager;
import android.app.Service;
import java.util.Arrays;
import com.ultrasshservice.config.V2Config;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
//import libv2ray.V2RayPoint;
//import libv2ray.V2RayVPNServiceSupportsSet;
import com.ultrasshservice.tunnel.*;
import android.content.Context;
import com.ultrasshservice.logger.SkStatus;
import android.content.Context;
import android.net.ConnectivityManager;
import java.util.concurrent.CountDownLatch;
import android.os.Handler;
import com.ultrasshservice.tunnel.vpn.TunnelState;
import com.trilead.ssh2.Connection;
import java.io.IOException;
import android.content.SharedPreferences;
import java.io.IOException;
import java.io.InterruptedIOException;
import com.ultrasshservice.tunnel.vpn.TunnelVpnManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.trilead.ssh2.DynamicPortForwarder;
import com.ultrasshservice.config.SettingsConstants;
import android.content.BroadcastReceiver;
import com.ultrasshservice.config.Settings;
import com.ultrasshservice.tunnel.vpn.TunnelVpnService;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
//import libv2ray.V2RayPoint;
import android.os.ParcelFileDescriptor;
import com.vpnmoddervpn.vpn.audioconectado;
import com.vpnmoddervpn.vpn.audiodesconectado;
import com.ultrasshservice.V2Configs;
import java.net.InetAddress;
import com.ultrasshservice.logger.ConnectionStatus;

import libv2ray.Libv2ray;

public class V2Tunnel implements ConnectionMonitor {
    private static V2Listener v2Listener;
    private static boolean mRunning = false, mStopping = false, mStarting = false;
    public boolean mReconnecting = false;
    private Context mContext;
    private TunnelManagerThread tunnelManagerThread, mTunnelManager;
    private Thread mTunnelThread;
    private Handler mHandler;
    private static Connection mConnection;
    private CountDownLatch mTunnelThreadStopSignal;
    private boolean v2rayrunning = false;
    public static ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback callback;
    private V2Tunnel v2Tunnel;
    private SharedPreferences preferencias;
    private static SharedPreferences sp = null;
    private V2Config v2Config;
    public V2Configs.V2RAY_STATES V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_DISCONNECTED;
    private Pinger pinger;
    private Thread thPing;
    private long lastPingLatency = -1;
    private DynamicPortForwarder dpf;
    private ParcelFileDescriptor mInterface;
    private static boolean vpnEstablished = false;
    public static boolean isManuallyDisconnected = false;
    private static boolean mConnected = false;
    private volatile boolean isWaitingForNetwork = false;
    private boolean isRunning = true;
    private AtomicBoolean isMonitoring = new AtomicBoolean(false);
    private boolean isReconnecting = false;
    private boolean isConnectionLostHandled = false;
    private static Settings mConfig;
    private TunnelManagerThread tunnelManager;
    private static boolean isSetupCompleted = false;

    public V2Tunnel(Context context) {
        v2Listener = TunnelManagerThread.getV2rayServicesListener();
        mContext = context;
        sp = new Settings(context).getPrefsPrivate();
        mConfig = new Settings(context);
        this.tunnelManager = tunnelManagerThread;
        preferencias = new Settings(context).getPrefsPrivate();
    }

    public void setTunnelManager(TunnelManagerThread tunnelManager) {
        this.tunnelManager = tunnelManager;
    }

    @Override
    public void connectionLost(Throwable reason) {
        if (mStarting || mStopping || mReconnecting) {
            return;
        }
        SkStatus.logError("Connection lost: " + reason.getMessage());
        stopAll();
    }
    
    @Override
	public void onReceiveInfo(int id, String msg) {
		if (id == SERVER_BANNER) {
			SkStatus.logInfo("<strong>" + mContext.getString(R.string.log_server_banner) + "</strong> " + msg);
		}
	}

    public void log(int level, String className, String message) {
        SkStatus.logDebug(String.format("%s: %s", className, message));
    }

    private boolean isv2raymode() {
        return preferencias.getInt(Settings.TUNNELTYPE_KEY, 0) == Settings.bTUNNEL_TYPE_V2RAY;
    }

    public void stopAll() {
        if (mStopping) return;
        SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
        SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");
        mStopping = true;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (mTunnelThreadStopSignal != null) {
                mTunnelThreadStopSignal.countDown();
            } else {
                StopV2ray(mContext.getApplicationContext());
            }
            handler.postDelayed(() -> SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected)), 1000);
        });
    }

    public void stopAll2() {
        if (mStopping) return;
        SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
        SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");
        mStopping = true;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (mTunnelThreadStopSignal != null) {
                mTunnelThreadStopSignal.countDown();
            } else {
                StopV2ray(mContext.getApplicationContext());
                closeSSHConnection();
                stopForwarder();
            }
            handler.postDelayed(() -> SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected)), 1000);
        });
    }

    public static void init(final Context context, final int app_icon, final String app_name) {
        V2Utilities.copyAssets(context);
        V2Configs.APPLICATION_ICON = app_icon;
        V2Configs.APPLICATION_NAME = app_name;
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                V2Configs.V2RAY_STATE = (V2Configs.V2RAY_STATES) arg1.getExtras().getSerializable("object");
            }
        }, new IntentFilter("v2rayconnectionstate"));
    }

    public static void changeConnectionMode(final V2Configs.V2RAY_CONNECTION_MODES connection_mode) {
        if (getConnectionState() == V2Configs.V2RAY_STATES.V2RAY_DISCONNECTED) {
            V2Configs.V2RAY_CONNECTION_MODE = connection_mode;
        }
    }

    public static void StartV2ray(final Context context, final String remark, final String vlessURI, final ArrayList<String> blocked_apps) {
    mConfig = new Settings(context);
    String novoUUID = mConfig.getPrivString(Settings.USUARIO_KEY);
    if (!isIdValid(novoUUID)) {
        SkStatus.logError("UUID inválido. Verifique se o UUID possui 36 caracteres.");
        SkStatus.logError("Insira seu UUID na caixa de usuario");
        v2Listener.onError();
        return;
    }

    // Converter URI VLESS para JSON
    String jsonConfig = convertVlessUriToJson(vlessURI);
    if (jsonConfig == null) {
        SkStatus.logError("Falha ao converter URI VLESS para JSON");
        v2Listener.onError();
        return;
    }

    // Modificar o UUID no JSON
    String modifiedJson = editarUsersId(jsonConfig, novoUUID);
    if (modifiedJson != null) {
        V2Config v2Config = V2Utilities.parseV2rayJsonFile(remark, modifiedJson, blocked_apps);
        if (v2Config == null) {
            v2Listener.onError();
            SkStatus.logInfo("V2Ray Error: Falha ao parsear configuração JSON");
            return;
        }
        V2Configs.V2RAY_CONFIG = v2Config;
        Intent start_intent;
        if (V2Configs.V2RAY_CONNECTION_MODE == V2Configs.V2RAY_CONNECTION_MODES.PROXY_ONLY) {
            start_intent = new Intent(context, V2Proxy.class);
        } else if (V2Configs.V2RAY_CONNECTION_MODE == V2Configs.V2RAY_CONNECTION_MODES.VPN_TUN) {
            start_intent = new Intent(context, V2Service.class);
        } else {
            v2Listener.onError();
            SkStatus.logInfo("V2Ray Error: Modo de conexão inválido");
            return;
        }
        start_intent.putExtra("COMMAND", V2Configs.V2RAY_SERVICE_COMMANDS.START_SERVICE);
        start_intent.putExtra("V2RAY_CONFIG", V2Configs.V2RAY_CONFIG);
        SkStatus.logInfo(V2Tunnel.getCoreVersion());
        v2Listener.startService();
        SkStatus.logInfo("<strong><font color=\"#FFD500\">INICIANDO V2RAY</strong>");
        SkStatus.logInfo("<strong><font color=\"#FFD500\">VERIFICANDO CONEXÃO COM A INTERNET</strong>");
        siNoInternet(context);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            context.startForegroundService(start_intent);
        } else {
            context.startService(start_intent);
        }
    } else {
        SkStatus.logError("Falha ao modificar o ID. Verifique os critérios desejados.");
        v2Listener.onError();
    }
}

    public static void StopV2ray(final Context context) {
        SkStatus.logInfo("<strong><font color='#FFD500'>StopXray</strong>");
        Intent stop_intent = new Intent(context, V2Service.class);
        stop_intent.putExtra("COMMAND", V2Configs.V2RAY_SERVICE_COMMANDS.STOP_SERVICE);
        context.startService(stop_intent);
        //V2Configs.V2RAY_CONFIG(null);
        V2Configs.V2RAY_CONFIG = null;
        if (mConfig != null && mConfig.audioDesconectado()) {
            new audiodesconectado().executar(context);
        }
    }

    public static void getConnectedV2rayServerDelay(final Context context, final TextView tvDelay) {
        SkStatus.logInfo("<strong><font color='#FFD500'>getConnectedV2rayServerDelay</strong>");
        Intent check_delay = new Intent(context, V2Service.class);
        check_delay.putExtra("COMMAND", V2Configs.V2RAY_SERVICE_COMMANDS.MEASURE_DELAY);
        context.startService(check_delay);
        context.registerReceiver(new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String delay = arg1.getExtras().getString("DELAY");
                tvDelay.setText("connected server delay : " + delay);
                context.unregisterReceiver(this);
            }
        }, new IntentFilter("CONNECTED_V2RAY_SERVER_DELAY"));
    }

    public static String getV2rayServerDelay(final String config) {
        final long server_delay = V2Core.getInstance().getV2rayServerDelay(config);
        if (server_delay == -1L) {
            return "Network or Server Error";
        } else {
            return String.valueOf(server_delay);
        }
    }

    private static void vpnService(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            for (Network network : connectivityManager.getAllNetworks()) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.getType() == 17 && networkInfo.isConnected()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        connectivityManager.bindProcessToNetwork(null);
                    }
                    connectivityManager.unregisterNetworkCallback(new ConnectivityManager.NetworkCallback());
                }
            }
        }
    }

    public static V2Configs.V2RAY_CONNECTION_MODES getConnectionMode() {
        return V2Configs.V2RAY_CONNECTION_MODE;
    }

    public static V2Configs.V2RAY_STATES getConnectionState() {
        return V2Configs.V2RAY_STATE;
    }

    public static String getCoreVersion() {
        return Libv2ray.checkVersionX();
    }

    public static void stopAllServices(Activity activity) {
        StopV2ray(activity);
    }

    public static boolean isSetupCompleted() {
        return isSetupCompleted;
    }

    public static void setSetupCompleted(boolean status) {
        isSetupCompleted = status;
    }

    public static boolean isV2RayServiceRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.ultrasshservice.V2Service".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNotificationShown(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 1) {
                return true;
            }
        }
        return false;
    }

    public static void siNoInternet(final Context context) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isV2RayServiceRunning(context)) {
                    Log.d("No Internet", "O Xray não está ativo");
                    SkStatus.logInfo("O serviço Xray não está em execução");
                    V2Tunnel instance2 = new V2Tunnel(context);
                    instance2.stopAll();
                    return;
                }
                if (verificarInternet()) {
                    Log.d("Tienes internet", "Você tem acesso à internet");
                    SkStatus.logInfo("Conexão com a Internet estabelecida.");
                    v2Listener.onConnected();
                    //checkConnectionAfterDelay(context);
                    if (!SkStatus.getLastState().equals(SkStatus.SSH_CONECTADO)) {
                        Log.d("No Connected State", "Estado de conexão não é 'CONECTADO'");
                        SkStatus.logError("<strong><font color=\"red\">O Xray foi desconectado</strong>");
                        SkStatus.logError("<strong><font color=\"red\">MOTIVO:</strong><font color=\"#FFD500\">O serviço de túnel não está conectado</font>");
                        V2Tunnel instance2 = new V2Tunnel(context);
                        instance2.stopAll();
                    }
                } else {
                    Log.d("No internet", "Você não tem acesso à internet");
                    SkStatus.logError("<strong><font color=\"red\">ERRO - SEM ACESSO À INTERNET, VERIFIQUE A CONFIGURAÇÃO OU O UUID</font>");
                    v2Listener.onError();
                    V2Tunnel instance2 = new V2Tunnel(context);
                    instance2.stopAll();
                }
            }
        }, 5000);
    }

    public static boolean verificarInternet() {
        try {
            InetAddress address = InetAddress.getByName("google.com");
            return address.isReachable(3000);
        } catch (IOException e) {
            return false;
        }
    }

    public static String editarUsersId(String json, String newid) {
    try {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray outboundsArray = jsonObject.getJSONArray("outbounds");
        if (outboundsArray != null && outboundsArray.length() > 0) {
            JSONObject outbound = outboundsArray.getJSONObject(0);
            JSONObject settings = outbound.getJSONObject("settings");
            JSONArray vnext = settings.getJSONArray("vnext");
            if (vnext != null && vnext.length() > 0) {
                JSONObject vnextObj = vnext.getJSONObject(0);
                JSONArray users = vnextObj.getJSONArray("users");
                if (users != null && users.length() > 0) {
                    JSONObject user = users.getJSONObject(0);
                    if (isIdValid(newid)) {
                        user.put("id", newid);
                    } else {
                        SkStatus.logError("Novo UUID inválido: " + newid);
                        return null;
                    }
                }
            }
        }
        return jsonObject.toString();
    } catch (JSONException e) {
        SkStatus.logError("Erro ao modificar UUID no JSON: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

    private static boolean isIdValid(String id) {
        return id != null && id.length() == 36;
    }

    public static void checkConnectionAfterDelay(final Context context) {
    new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
            String GATESCCNLATAMSRC1 = "mandarin pro"; // CHANGE
            String GATESCCNLATAMSRC = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            mConfig = new Settings(context);
    String novoUUID = mConfig.getPrivString(Settings.USUARIO_KEY);
                    String string3 = sp.getString(SettingsConstants.APILATAMIP, "");
            String result = makeGetRequest(novoUUID + GATESCCNLATAMSRC, 10000); // 10 segundos de espera
                    
                    
                    
            if (result.equals("Fail")) {
                StopV2ray(context);
                TunnelManagerHelper.stopSocksHttp(context);
                        SkStatus.logInfo("<strong><font color=\"red\">ERRO - UUID INCORRETOS OU EXPIRADO 🔑</strong>");
                        //SkStatus.logInfo(string3);
                SkStatus.logInfo(new Object() {
                    public String toString() {
                        // Definindo um array de bytes com valores representativos
                        byte[] ElMandarinSniff = {
                            'E', 'l', ' ', 'M', 'a', 'n', 'd', 'a', 'r', 'i', 'n', ' ', 'S', 'n', 'i', 'f', 'f', ' ',
                            // Adicionando mais bytes conforme necessário
                            // Os valores que estavam ofuscados podem ser substituídos por letras ou significados
                        };
                        return new String(ElMandarinSniff);
                    }
                }.toString());
            }
                    
                    
                    
                    
        }
    }, 5000); // 5 segundos de atraso
}

    public static String makeGetRequest(String urlString, int timeout) {
        SkStatus.logInfo("<strong><font color=\"#FFD500\">makeGetRequest</strong>");
        try {/*BY LATAMSRC*/
            URL url = new URL(urlString);/*BY LATAMSRC*/
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();/*BY LATAMSRC*/
            urlConnection.setConnectTimeout(timeout); /*BY LATAMSRC*//*BY LATAMSRC*/
            urlConnection.setReadTimeout(timeout); /*BY LATAMSRC*//*BY LATAMSRC*/
            urlConnection.setRequestMethod("GET");/*BY LATAMSRC*//*BY LATAMSRC*/
            int responseCode = urlConnection.getResponseCode();/*BY LATAMSRC*/
            if (responseCode == HttpURLConnection.HTTP_OK) {/*BY LATAMSRC*//*BY LATAMSRC*/
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();/*BY LATAMSRC*//*BY LATAMSRC*/
                String inputLine;/*BY LATAMSRC*//*BY LATAMSRC*/
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }/*BY LATAMSRC*/
                in.close();
                return response.toString();
            } else {
                return "Fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "OK";
        }/*BY LATAMSRC*/
    }

    public static boolean isUUIDValid(String uuid) {
        SkStatus.logInfo("<strong><font color='#FFD700'>isUUIDValid</font>");
        try {
            String configContent = new String(Files.readAllBytes(Paths.get("/etc/v2ray/config.json")), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(configContent);
            JSONArray clients = json.getJSONArray("inbounds");
            JSONObject first = clients.getJSONObject(0);
            JSONObject settings = first.getJSONObject("settings");
            JSONArray clientArray = settings.getJSONArray("clients");
            for (int i = 0; i < clientArray.length(); i++) {
                JSONObject client = clientArray.getJSONObject(i);
                String clientUUID = client.getString("id");
                if (clientUUID.equals(uuid)) {
                    return true;
                }
            }
        } catch (IOException | JSONException e) {
            SkStatus.logError("isUUIDValid error: " + e.getMessage());
            return false;
        }
        return false;
    }

    protected void stopForwarder() {
        stopTunnelVpnService();
        interruptPinger();
        stopForwarderSocks();
        SkStatus.logInfo("<strong><font color='red'>stopForwarder</font></strong>");
    }

    private synchronized void stopForwarderSocks() {
        try {
            if (dpf != null) {
                dpf.close();
            }
        } catch (IOException ignored) {
        }
        dpf = null;
    }

    private synchronized void interruptPinger() {
        if (pinger != null && pinger.isAlive()) {
            pinger.interrupt();
        }
    }

    private synchronized void stopPinger() {
        if (thPing != null && thPing.isAlive()) {
            thPing.interrupt();
            thPing = null;
        }
    }

    
    
    public synchronized void closeSSHConnection() {
		stopForwarder();
		stopPinger();
        
        SkStatus.logInfo("<font color='red'><strong>closeSSH</strong></font>");

		if (mConnection != null) {
			SkStatus.logDebug("Stopping SSH");
			mConnection.close();
            SkStatus.logInfo("<font color='red'><strong>closeSSH 2</strong></font>");
		}
	}

    protected synchronized void stopTunnelVpnService() {
        SkStatus.logInfo("<strong><font color='#FFD700'>stopTunnelVpnService</font>");
        // Implementação específica para parar o serviço VPN
        // (pode ser ajustada conforme necessário, dependendo de TunnelVpnService)
    }

    public static boolean isServiceVpnRunning() {
        SkStatus.logInfo("<strong><font color='#FFD700'>isServiceVpnRunning</font>");
        boolean isRunning = false;
        TunnelState tunnelState = TunnelState.getTunnelState();
        isRunning = tunnelState.getStartingTunnelManager() || tunnelState.getTunnelManager() != null;
        return isRunning;
    }
    
    

    protected synchronized void stopTunnelService() {
        if (!isServiceVpnRunning()) {
            SkStatus.logInfo("<strong><font color='red'>SERVIÇO VPN JÁ PARADO</font></strong>");
            return;
        }
        SkStatus.logInfo("PARANDO SERVIÇO DE TÚNEL");
        TunnelVpnManager currentTunnelManager = TunnelState.getTunnelState().getTunnelManager();
        if (currentTunnelManager != null) {
            currentTunnelManager.signalStopService();
            SkStatus.logInfo("<strong><font color='#FFD700'>signalStopService</font>");
        }
        SkStatus.logInfo("<strong><font color='#FFD700'>unregisterReceiver</font>");
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(m_vpnTunnelBroadcastReceiver);
    }

    private final BroadcastReceiver m_vpnTunnelBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            SkStatus.logInfo("<strong><font color='red'>m_vpnTunnelBroadcastReceiver</font></strong>");
            final String action = intent.getAction();
            if (TunnelVpnService.TUNNEL_VPN_START_BROADCAST.equals(action)) {
                boolean startSuccess = intent.getBooleanExtra(TunnelVpnService.TUNNEL_VPN_START_SUCCESS_EXTRA, true);
                if (!startSuccess) {
                    stopAll2();
                }
            } else if (TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST.equals(action)) {
                stopAll2();
            }
        }
    };

    private static String convertVlessUriToJson(String vlessUri) {
    try {
        // Exemplo de URI: vless://UUID@IP:PORT?params
        if (!vlessUri.startsWith("vless://")) {
            SkStatus.logError("Formato de URI VLESS inválido");
            return null;
        }

        // Remove o prefixo "vless://"
        String uriWithoutScheme = vlessUri.substring(8);
        String[] uriParts = uriWithoutScheme.split("@", 2);
        if (uriParts.length != 2) {
            SkStatus.logError("Formato de URI inválido: UUID ou endereço ausente");
            return null;
        }

        String uuid = uriParts[0];
        String[] addressAndParams = uriParts[1].split("\\?", 2);
        String[] addressPort = addressAndParams[0].split(":");
        if (addressPort.length != 2) {
            SkStatus.logError("Formato de URI inválido: endereço ou porta ausente");
            return null;
        }

        String address = addressPort[0];
        int port = Integer.parseInt(addressPort[1].split("#")[0]); // Remove o fragmento (#TIM IOS8875)
        String remark = vlessUri.contains("#") ? vlessUri.split("#")[1] : "VLESS Connection";

        // Parsear parâmetros da query
        String query = addressAndParams.length > 1 ? addressAndParams[1].split("#")[0] : "";
        String mode = "auto", path = "/", security = "none", encryption = "none", host = "", sni = "", type = "";
        if (!query.isEmpty()) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    switch (keyValue[0]) {
                        case "mode":
                            mode = keyValue[1];
                            break;
                        case "path":
                            path = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                            break;
                        case "security":
                            security = keyValue[1];
                            break;
                        case "encryption":
                            encryption = keyValue[1];
                            break;
                        case "host":
                            host = keyValue[1];
                            break;
                        case "sni":
                            sni = keyValue[1];
                            break;
                        case "type":
                            type = keyValue[1];
                            break;
                    }
                }
            }
        }

        // Construir o JSON
        JSONObject json = new JSONObject();
        JSONArray outbounds = new JSONArray();
        JSONObject outbound = new JSONObject();
        outbound.put("protocol", "vless");
        outbound.put("tag", "proxy");

        JSONObject settings = new JSONObject();
        JSONArray vnext = new JSONArray();
        JSONObject vnextObj = new JSONObject();
        vnextObj.put("address", address);
        vnextObj.put("port", port);
        JSONArray users = new JSONArray();
        JSONObject user = new JSONObject();
        user.put("id", uuid);
        user.put("encryption", encryption);
        user.put("level", 8);
        users.put(user);
        vnextObj.put("users", users);
        vnext.put(vnextObj);
        settings.put("vnext", vnext);
        outbound.put("settings", settings);

        JSONObject streamSettings = new JSONObject();
        streamSettings.put("network", type);
        streamSettings.put("security", security);
        if (security.equals("tls")) {
            JSONObject tlsSettings = new JSONObject();
            tlsSettings.put("allowInsecure", false);
            tlsSettings.put("serverName", sni);
            streamSettings.put("tlsSettings", tlsSettings);
        }
        if (type.equals("xhttp")) {
            JSONObject xhttpSettings = new JSONObject();
            xhttpSettings.put("host", host);
            xhttpSettings.put("mode", mode);
            xhttpSettings.put("path", path);
            streamSettings.put("xhttpSettings", xhttpSettings);
        }
        outbound.put("streamSettings", streamSettings);

        outbounds.put(outbound);
        json.put("outbounds", outbounds);

        // Adicionar configurações adicionais (dns, inbounds, etc.) conforme necessário
        JSONObject dns = new JSONObject();
        JSONArray servers = new JSONArray();
        servers.put("1.1.1.1");
        dns.put("servers", servers);
        json.put("dns", dns);

        JSONObject inbound = new JSONObject();
        inbound.put("listen", "127.0.0.1");
        inbound.put("port", 10808);
        inbound.put("protocol", "socks");
        JSONObject inboundSettings = new JSONObject();
        inboundSettings.put("auth", "noauth");
        inboundSettings.put("udp", true);
        inboundSettings.put("userLevel", 8);
        inbound.put("settings", inboundSettings);
        JSONArray inbounds = new JSONArray();
        inbounds.put(inbound);
        json.put("inbounds", inbounds);

        return json.toString();
    } catch (Exception e) {
        SkStatus.logError("Erro ao converter URI VLESS para JSON: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
}