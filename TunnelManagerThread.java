package com.vpnmoddervpn.vpn;

import android.content.Context;
import java.io.IOException;
import com.ultrasshservice.logger.SkStatus;
import android.content.IntentFilter;
import com.ultrasshservice.tunnel.vpn.TunnelVpnService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.List;
import com.ultrasshservice.tunnel.vpn.VpnUtils;
import android.util.Log;
import com.ultrasshservice.tunnel.vpn.TunnelState;
import android.content.Intent;
import com.ultrasshservice.tunnel.vpn.TunnelVpnSettings;
import android.content.BroadcastReceiver;
import com.ultrasshservice.MainService;
import com.ultrasshservice.tunnel.vpn.TunnelVpnManager;
import com.ultrasshservice.config.Settings;
import android.os.Handler;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import com.ultrasshservice.config.PasswordCache;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.ProxyInfo;
import android.os.Build;
import android.net.NetworkRequest;
import android.net.NetworkCapabilities;
import android.net.Network;
import com.ultrasshservice.util.CustomNativeLoader;
import com.ultrasshservice.util.StreamGobbler;
import android.preference.PreferenceManager;
import com.vpnmoddervpn.vpn.R;
import com.ultrasshservice.*;
import com.ultrasshservice.config.*;
import com.ultrasshservice.logger.*;
import com.ultrasshservice.tunnel.*;
import com.ultrasshservice.util.*;
import com.vpnmoddervpn.vpn.util.ConfigUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import android.widget.Spinner;
import com.ultrasshservice.tunnel.vpn.V2Listener;
import com.vpnmoddervpn.vpn.V2Tunnel;
import android.app.Service;
import android.widget.TextView;
import java.util.Vector;
import com.vpnmoddervpn.vpn.audioconectado;
import com.vpnmoddervpn.vpn.audiodesconectado;
import android.os.Looper;
import java.net.NetworkInterface;
import android.net.NetworkInfo;
import android.net.LinkProperties;
import org.json.JSONArray;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TunnelManagerThread
	implements Runnable, ConnectionMonitor, InteractiveCallback,
		ServerHostKeyVerifier, DebugLogger
{
	private static final String TAG = TunnelManagerThread.class.getSimpleName();
	
	private OnStopCliente mListener;
	private Context mContext;
	private Handler mHandler;
	private Settings mConfig, mPrefs;
	private boolean mRunning = false, mStopping = false, mStarting = false;
	public static String PUT_CUSTOM_KEY2 = "";
    public static String PUT_CUSTOM_KEY1 = "";
	private CountDownLatch mTunnelThreadStopSignal;
	//private ConnectivityManager mCmgr;
	private Pinger pinger;
    private boolean lost;
    private boolean mcheck;
    
    private String nextPayloadKey = "";
private static int currentPayloadIndex = 1;
    private String nextProxyIP;
    private int nextPorta;
    private String nextProxyIPDirect;
    private String nextProxyIPTlsws;
    private static int currentProxyIndex = 0;
    private static int currentPortIndex = 0;
    private static int currentServerIndex = 0;
    private String nextSni;
    
    private static int DirectPortIndex = 0;
    private static int DirectProxyIndex = 0;
    private static int DirectPayloadIndex = 0;
    
    private static int PxPortIndex = 0;
    private static int PxProxyIndex = 0;
    private static int PxPayloadIndex = 0;
    
    private static int TlwsPortIndex = 0;
    private static int TlwsProxyIndex = 0;
    private static int TlwsPayloadIndex = 0;
    private static int TlwsSniIndex = 0;
    
    private static int SSLPortIndex = 0;
    private static int SSLProxyIndex = 0;
    private static int SSLPayloadIndex = 0;
    private static int SSLSniIndex = 0;
    
    public static int dnsIndex = 0;
    public static int chaveIndex = 0;
    public static int nameserverIndex = 0;
    
    private static int currentChaveIndex = 0;
    private static int currentDnsIndex = 0;
    private static int currentNameServerIndex = 0;
   
    private String servidor3;
    private ConfigUtil config, ConfigUtil;
    private Spinner payloadSpinner;
    
    private ConnectivityManager connectivityManager;
	private ConnectivityManager.NetworkCallback callback;
	private boolean runningatesccn = false;
    private boolean v2rayrunning = false;
    private V2Tunnel v2Tunnel;
    private V2Core v2Core;
    
    private static V2Listener v2Listener;
    private SharedPreferences preferencias;
    
    private TunnelVpnService m_parentService = null;
    private boolean proxyError = false;  // Variável de controle
    
    private static final String PREFS_NAME = "MyPrefs";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private SocksHttpMainActivity activity;
    private TextView vencimento;
	private TextView user_limite;
	private TextView dias_check;
    private DNSTunnelThread mDnsThread;
    
    private String dns;
    private String chave;
    private String nameserver;
    
    private boolean jaTemProxyFuncional = false;
    
	public String getServidor3() {
        return servidor3;
    }
	
	public interface OnStopCliente {
		void onStop();
	}
    
   
	public TunnelManagerThread(Handler handler, Context context) {
    this.activity = activity; // Verificar se a inicialização correta está feita aqui
    mContext = context;
    mHandler = handler;
        
        

    // Configurações
    mConfig = new Settings(context);
    preferencias = mConfig.getPrefsPrivate();
        
        
        //ADICIONAR ISSO SE FOR BASE COM V2RAY
    //putoelqueloeagatesccn();
        

    // Estruturas de dados
    Vector packets = new Vector();
    Vector<ConnectionMonitor> connMonitors = new Vector<>();

        
        
        
    // Listener para V2Ray
    v2Listener = new V2Listener() {
        @Override
        public boolean onProtect(int socket) {
            return false;
        }

        @Override
        public Service getService() {
            return null;
        }

        @Override
        public void startService() {
            try {
                // Atualiza o estado e inicia o serviço
                SkStatus.updateStateString(SkStatus.SSH_CONECTANDO, "Conectando");
                //mConnected = false;

                //V2Tunnel.checkConnectionAfterDelay(context);
                startTunnelVpnService();
                    
                    
                    //v2Tunnel = new V2Tunnel(mContext.getApplicationContext());
    //v2Tunnel.startMonitoringNetwork();
                    SkStatus.logInfo("<font color='red'><strong>startService DESCONECTADO 15</strong></font>");
                    

            } catch (Exception ignored) {
                // Erro silencioso
                    //SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO 15</strong></font>");
            }
        }

        @Override
        public void stopService() {
            try {
                // Atualiza o estado e para o serviço
                SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, "Conexão ssh parado");
                SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO</strong></font>");
                //mConnected = false;

                V2Tunnel.StopV2ray(mContext);
                V2Core v2Core = V2Core.getInstance();
                // v2Core.stopAll(); // Descomentar se necessário
                    
            } catch (Exception ignored) {
                // Erro silencioso
                    //SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO 16</strong></font>");
            }
        }

        @Override
public void onConnected() {
    try {
        SkStatus.updateStateString(SkStatus.SSH_CONECTADO, "Conexão ssh estabelecida");
        SkStatus.logInfo("<font color='#37CC00'><strong>V2RAY CONECTADO</strong></font>");

        // Verifica se o monitor já não está ativo
                    
                    //mConnection.addConnectionMonitor(TunnelManagerThread.this);
            //mConnected = true;
            //SkStatus.logInfo("<strong>Monitor de rede ativado no V2Ray</strong>");
                    
                    
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
    startNetworkMonitor();
}, 1000); // ou 5000ms
                    
                    
                   
                    
                    
                    
       if (!mConnected && mConnection != null) {
    mConnection.addConnectionMonitor(TunnelManagerThread.this);
    mConnected = true;
    //SkStatus.logInfo("<strong>Monitor de rede ativado no V2Ray</strong>");
}
            
            

        if (TunnelManagerThread.this.mConfig != null && TunnelManagerThread.this.mConfig.audioConectado()) {
            new audioconectado().executar(TunnelManagerThread.this.mContext);
        }

        //V2Tunnel.checkConnectionAfterDelay(mContext);
                    //SkStatus.addStateListener(TunnelManagerThread.this);

    } catch (Exception e) {
        //SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO 14</strong></font>");
    }
}

        @Override
        public void onError() {
            try {
                // Para o serviço em caso de erro
                Intent stopTunnel = new Intent(MainService.TUNNEL_SSH_STOP_SERVICE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(stopTunnel);
                stopAllProcess();
                    SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO ONERROR</strong></font>");
                    //updateConnectionState(false);
                    
            } catch (Exception ignored) {
                // Erro silencioso
                    //SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO 17</strong></font>");
            }
        }
    };
        
        
        
}
        
        
        
        
        
        
    
    
    
    public static V2Listener getV2rayServicesListener() {
        return v2Listener;
    }
    
	
	public void setOnStopClienteListener(OnStopCliente listener) {
		mListener = listener;
	}
    
    private void stopAllProcess() {
        SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO STOPALLPROCESS</strong></font>");
        V2Core.getInstance().stopCore();
        V2Tunnel.StopV2ray(mContext.getApplicationContext());
        closeSSH();

    }
    
    
    public void stopServiceV2() {
    try {
        SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, "Conexão ssh parado");
        SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO</strong></font>");
        mConnected = false;  // Corrigir para mConnected = false
        V2Tunnel.StopV2ray(mContext);
                    V2Core v2Core = V2Core.getInstance();
    } catch (Exception ignored) {

    }
}
    
    
    
    public void stopSlow() {
    try {
        SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, "Conexão ssh parado");
        SkStatus.logInfo("<font color='red'><strong>SLOWDNS DESCONECTADO</strong></font>");
        mConnected = false;  // Corrigir para mConnected = false
        mPrefs.setBypass(false);
            if (mDnsThread != null) {
                mDnsThread.interrupt();
            }
            mDnsThread = null;
    } catch (Exception ignored) {

    }
}
    
    
    
    
    
    
    
	@Override
    
    
    
    
    
    /* O run abaixo era o original sem reconexao em caso de travamento por erro de proxy /*
    
    /*
    public void run26032025() {
    mStarting = true;
    mTunnelThreadStopSignal = new CountDownLatch(1);
    
    SkStatus.logInfo("<strong>" + mContext.getString(R.string.starting_service_ssh) + "</strong>");
    
    int tries = 0;
    while (!mStopping) {
        try {
            if (!TunnelUtils.isNetworkOnline(mContext)) {
                SkStatus.updateStateString(SkStatus.SSH_AGUARDANDO_REDE, mContext.getString(R.string.state_nonetwork));
                SkStatus.logInfo(R.string.state_nonetwork);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e2) {
                    stopAll();
                    break;
                }
            } else {
                if (tries > 0)
                    SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_reconnecting) + "</strong>");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                    stopAll();
                    break;
                }

                // Verifica se está no modo V2Ray antes de verificar o erro de proxy
                if (isv2raymode()) {
                    if (v2Tunnel == null) {
                        v2Tunnel = new V2Tunnel(mContext);
                        V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                        v2rayrunning = true;
                    }
                } else {
                    // Verifica se houve um erro de proxy antes de tentar reconectar
                    if (!proxyError) {
                        startClienteSSH();
                    } else {
                        SkStatus.logError("Conexão interrompida devido a erro de proxy.");
                        stopAll2();  // Para o processo se houver erro de proxy
                        break;
                    }
                }

                break;
            }
        } catch (Exception e) {
            SkStatus.logError("<strong><font color='red'>" + mContext.getString(R.string.state_disconnected) + "</font></strong>");
            closeSSH();
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e2) {
                stopAll();
                break;
            }
        }
        
        tries++;
    }
    
    mStarting = false;
    
    if (!mStopping) {
        try {
            mTunnelThreadStopSignal.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    if (mListener != null) {
        mListener.onStop();
    }
}
    */
    
    
    
    
    
    
    
    
    
    
    //METODO SEM V2RAY 22 04 25
    /*
    
    public void run() {
    this.mStarting = true;
    this.mTunnelThreadStopSignal = new CountDownLatch(1);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<strong>");
    stringBuilder.append(this.mContext.getString(R.string.starting_service_ssh));
    stringBuilder.append("</strong>");
    SkStatus.logInfo((String)stringBuilder.toString());
    int n = 0;
    while (!this.mStopping) {
        block15: {
            try {
                if (!TunnelUtils.isNetworkOnline((Context)this.mContext)) {
                    SkStatus.updateStateString((String)"AGUARDANDO", (String)this.mContext.getString(R.string.state_nonetwork));
                    SkStatus.logInfo((int)R.string.state_nonetwork, (Object[])new Object[0]);
                    try {
                        Thread.sleep((long)5000L);
                        break block15;
                    } catch (InterruptedException interruptedException) {
                        this.stopAll();
                        break;
                    }
                }
                if (n > 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("<strong>");
                    stringBuilder.append(this.mContext.getString(R.string.state_reconnecting));
                    stringBuilder.append("</strong>");
                    SkStatus.logInfo((String)stringBuilder.toString());
                }
                try {
                    Thread.sleep((long)500L);
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                    break;
                }

                // Inicia diretamente o cliente SSH (sem V2Ray)
                startClienteSSH();

            } catch (Exception exception) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("<strong><font color='red'>");
                stringBuilder.append(this.mContext.getString(R.string.state_disconnected));
                stringBuilder.append("</font></strong>");
                SkStatus.logError((String)stringBuilder.toString());
                this.closeSSH();
                try {
                    Thread.sleep((long)500L);
                    break block15;
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                }
            }
            break;
        }
        ++n;
    }
    this.mStarting = false;
    if (!this.mStopping) {
        try {
            this.mTunnelThreadStopSignal.await();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
    if (mListener != null) {
        mListener.onStop();
    }
}
    */
    
    
    
    
    
    
   
    
    //METODO COM V2RAY 22 04 25
    
    public void run() {
    this.mStarting = true;
    this.mTunnelThreadStopSignal = new CountDownLatch(1);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<strong>");
    stringBuilder.append(this.mContext.getString(R.string.starting_service_ssh));
    stringBuilder.append("</strong>");
    SkStatus.logInfo((String)stringBuilder.toString());
    int n = 0;
    while (!this.mStopping) {
        block15: {
            try {
                if (!TunnelUtils.isNetworkOnline((Context)this.mContext)) {
                    SkStatus.updateStateString((String)"AGUARDANDO", (String)this.mContext.getString(R.string.state_nonetwork));
                    SkStatus.logInfo((int)R.string.state_nonetwork, (Object[])new Object[0]);
                    try {
                        Thread.sleep((long)5000L);
                        break block15;
                    } catch (InterruptedException interruptedException) {
                        this.stopAll();
                        break;
                    }
                }
                if (n > 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("<strong>");
                    stringBuilder.append(this.mContext.getString(R.string.state_reconnecting));
                    stringBuilder.append("</strong>");
                    SkStatus.logInfo((String)stringBuilder.toString());
                }
                try {
                    Thread.sleep((long)500L);
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                    break;
                }

                // Verifica se está no modo V2Ray antes de iniciar o túnel
                if (isv2raymode()) {
    if (v2Tunnel == null) {
        v2Tunnel = new V2Tunnel(mContext);
        String v2rayJson = mConfig.getPrivString(Settings.V2RAY_JSON);
        SkStatus.logInfo("V2Ray JSON: " + v2rayJson);
        V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", v2rayJson, null);
        v2rayrunning = true;
        mConnection = new Connection("127.0.0.1", 0);
        SkStatus.logInfo("V2Ray tunnel started");
    }
} else {
                    // Se não estiver em modo V2Ray, inicie o cliente SSH
                    startClienteSSH();
                }
            } catch (Exception exception) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("<strong><font color='red'>");
                stringBuilder.append(this.mContext.getString(R.string.state_disconnected));
                stringBuilder.append("</font></strong>");
                SkStatus.logError((String)stringBuilder.toString());
                this.closeSSH();
                try {
                    Thread.sleep((long)500L);
                    break block15;
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                }
            }
            break;
        }
        ++n;
    }
    this.mStarting = false;
    if (!this.mStopping) {
        try {
            this.mTunnelThreadStopSignal.await();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
    if (mListener != null) {
        mListener.onStop();
    }
}
    
    
    
    
    
    
    
    
    
    /*
    
    public void run3() {
    this.mStarting = true;
    this.mTunnelThreadStopSignal = new CountDownLatch(1);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<strong>");
    stringBuilder.append(this.mContext.getString(R.string.starting_service_ssh));
    stringBuilder.append("</strong>");
    SkStatus.logInfo((String)stringBuilder.toString());
    int n = 0;
    while (!this.mStopping) {
        block15: {
            try {
                if (!TunnelUtils.isNetworkOnline((Context)this.mContext)) {
                    SkStatus.updateStateString((String)"AGUARDANDO", (String)this.mContext.getString(R.string.state_nonetwork));
                    SkStatus.logInfo((int)R.string.state_nonetwork, (Object[])new Object[0]);
                    try {
                        Thread.sleep((long)5000L);
                        break block15;
                    } catch (InterruptedException interruptedException) {
                        this.stopAll();
                        break;
                    }
                }
                if (n > 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("<strong>");
                    stringBuilder.append(this.mContext.getString(R.string.state_reconnecting));
                    stringBuilder.append("</strong>");
                    SkStatus.logInfo((String)stringBuilder.toString());
                }
                try {
                    Thread.sleep((long)500L);
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                    break;
                }

                // Verifica se está no modo V2Ray antes de iniciar o túnel
                if (v2Tunnel == null) {
    v2Tunnel = new V2Tunnel(mContext);
    V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default",
            mConfig.getPrivString(Settings.V2RAY_JSON), null);
    v2rayrunning = true;

    // Instancia conexão fake para permitir monitoramento
    mConnection = new Connection("127.0.0.1", 0); 
    SkStatus.logInfo("<font color='red'><strong>V2RAY INICIADO</strong></font>");

                        //TunnelManagerThread.this.onConnected(); // Garante monitor
                    
} else {
    startClienteSSH();
}
            } catch (Exception exception) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("<strong><font color='red'>");
                stringBuilder.append(this.mContext.getString(R.string.state_disconnected));
                stringBuilder.append("</font></strong>");
                SkStatus.logError((String)stringBuilder.toString());
                this.closeSSH();
                try {
                    Thread.sleep((long)500L);
                    break block15;
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                }
            }
            break;
        }
        ++n;
    }
    this.mStarting = false;
    if (!this.mStopping) {
        try {
            this.mTunnelThreadStopSignal.await();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
    if (mListener != null) {
        mListener.onStop();
    }
}
    
    */
    
    
    
    
    private void startNetworkMonitor2() {
    new Thread(() -> {
        while (!mStopping) {
            if (!TunnelUtils.isNetworkOnline(mContext)) {
                SkStatus.logInfo("<font color='red'><strong>Monitor manual: Sem rede</strong></font>");
                        //stopAllProcess();
                //reconnectSSH();
                        connectionLost(null);
                break;
            }
                    
                    if (isOurVpnActive()) {
                // Another VPN is detected
                SkStatus.logInfo("<font color='red'><strong>Outro VPN detectado</strong></font>");
                stopAll();
                break;
            }
                    
                    
                    /*
                    
                    if (isTunInterfaceDown()) {
                SkStatus.logInfo("<font color='red'><strong>Interface TUN sumiu (possível outro app)</strong></font>");
                //connectionLost(null);
                        stopAll();
                break;
            }
                    */
                    
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }).start();
}
    
    private boolean isVpnActive() {
    ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_VPN;
}
    
    
    private void startNetworkMonitor() {
    new Thread(() -> {
        int fails = 0;

        while (!mStopping) {
            if (!TunnelUtils.isNetworkOnline(mContext)) {
                SkStatus.logInfo("<font color='red'><strong>Monitor manual: Sem rede</strong></font>");
                connectionLost(null);
                break;
            }

            if (!isOurVpnActive()) {
                fails++;
                if (fails >= 3) {
                    SkStatus.logInfo("<font color='red'><strong>Outro app VPN detectado</strong></font>");
                    stopAll();
                    break;
                }
            } else {
                fails = 0; // reset se voltou ao normal
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }).start();
}
    
    
    private boolean isOurVpnActive() {
    try {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        Network[] networks = cm.getAllNetworks();
        for (Network network : networks) {
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    int vpnUid = caps.getOwnerUid();
                    return vpnUid == android.os.Process.myUid();
                } else {
                    // Em versões abaixo do Android 10, assume que a VPN ainda é sua
                    return true;
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
    
    /*
    
    private void startNetworkMonitor0() {
    new Thread(() -> {
        while (!mStopping) {

            if (!TunnelUtils.isNetworkOnline(mContext)) {
                //SkStatus.logInfo("<font color='red'><strong>Sem rede detectada</strong></font>");
                connectionLost(null);
                break;
            }

            if (!SkStatus.isTunnelActive()) {
                //SkStatus.logInfo("<font color='red'><strong>Túnel não está mais ativo</strong></font>");
                //connectionLost(null);
                        stopAll();
                break;
            }

            if (isTunInterfaceDown()) {
                //SkStatus.logInfo("<font color='red'><strong>Interface TUN sumiu (possível outro app)</strong></font>");
                //connectionLost(null);
                        stopAll();
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }).start();
}
    
    */
    
    
    
    private boolean isTunInterfaceDown() {
    try {
        NetworkInterface nif = NetworkInterface.getByName("tun0");
        return nif == null || !nif.isUp();
    } catch (Exception e) {
        return true;
    }
}
    
    
    
    
    /*
    
    private void startNetworkMonitoring2() {
    ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkRequest request = new NetworkRequest.Builder().build();
    connectivityManager.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.d("V2Tunnel", "Rede disponível");
            updateConnectionState(true);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.d("V2Tunnel", "Rede perdida");
            updateConnectionState(false);
        }
    });
}
    */
    
    
    
    
    /*
    public void updateConnectionState(boolean isConnected) {
    if (isConnected) {
        SkStatus.updateStateString(SkStatus.SSH_CONECTADO, "Conexão ssh estabelecida");
        Log.d("V2Tunnel", "Conectado ao V2Ray");
    } else {
        SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, "Conexão ssh desconectada");
        Log.d("V2Tunnel", "Desconectado do V2Ray");
    }
}
    */
    
    
    
    
    /*
    
    //METODO COM V2RAY 17 04 2025
    
    public void run() {
    this.mStarting = true;
    this.mTunnelThreadStopSignal = new CountDownLatch(1);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<strong>");
    stringBuilder.append(this.mContext.getString(R.string.starting_service_ssh));
    stringBuilder.append("</strong>");
    SkStatus.logInfo((String)stringBuilder.toString());
    int n = 0;
    while (!this.mStopping) {
        block15: {
            try {
                if (!TunnelUtils.isNetworkOnline((Context)this.mContext)) {
                    SkStatus.updateStateString((String)"AGUARDANDO", (String)this.mContext.getString(R.string.state_nonetwork));
                    SkStatus.logInfo((int)R.string.state_nonetwork, (Object[])new Object[0]);
                    try {
                        Thread.sleep((long)5000L);
                        break block15;
                    } catch (InterruptedException interruptedException) {
                        this.stopAll();
                        break;
                    }
                }
                if (n > 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("<strong>");
                    stringBuilder.append(this.mContext.getString(R.string.state_reconnecting));
                    stringBuilder.append("</strong>");
                    SkStatus.logInfo((String)stringBuilder.toString());
                }
                try {
                    Thread.sleep((long)500L);
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                    break;
                }

                // Verifica se está no modo V2Ray antes de iniciar o túnel
                if (isv2raymode()) {
                    if (v2Tunnel == null) {
                        v2Tunnel = new V2Tunnel(mContext);
                        V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                        v2rayrunning = true;
                            SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO 18</strong></font>");
                            
                            //mConnection.addConnectionMonitor(this);
        
        
                          
                            
                            
                    }
                } else {
                    // Se não estiver em modo V2Ray, inicie o cliente SSH
                    startClienteSSH();
                }
            } catch (Exception exception) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("<strong><font color='red'>");
                stringBuilder.append(this.mContext.getString(R.string.state_disconnected));
                stringBuilder.append("</font></strong>");
                SkStatus.logError((String)stringBuilder.toString());
                this.closeSSH();
                try {
                    Thread.sleep((long)500L);
                    break block15;
                } catch (InterruptedException interruptedException) {
                    this.stopAll();
                }
            }
            break;
        }
        ++n;
    }
    this.mStarting = false;
    if (!this.mStopping) {
        try {
            this.mTunnelThreadStopSignal.await();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
    if (mListener != null) {
        mListener.onStop();
    }
}
    
    */
    
    
    
    /*
    //METODO SEM V2RAY 11 04 25
    
    
    public void runSemv2() {
        this.mStarting = true;
        this.mTunnelThreadStopSignal = new CountDownLatch(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<strong>");
        stringBuilder.append(this.mContext.getString(R.string.starting_service_ssh));
        stringBuilder.append("</strong>");
        SkStatus.logInfo((String)stringBuilder.toString());
        int n = 0;
        while (!this.mStopping) {
            block15: {
                try {
                    if (!TunnelUtils.isNetworkOnline((Context)this.mContext)) {
                        SkStatus.updateStateString((String)"AGUARDANDO", (String)this.mContext.getString(R.string.state_nonetwork));
                        SkStatus.logInfo((int)R.string.state_nonetwork, (Object[])new Object[0]);
                        try {
                            Thread.sleep((long)5000L);
                            break block15;
                        }
                        catch (InterruptedException interruptedException) {
                            this.stopAll();
                            break;
                        }
                    }
                    if (n > 0) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("<strong>");
                        stringBuilder.append(this.mContext.getString(R.string.state_reconnecting));
                        stringBuilder.append("</strong>");
                        SkStatus.logInfo((String)stringBuilder.toString());
                    }
                    try {
                        Thread.sleep((long)500L);
                    }
                    catch (InterruptedException interruptedException) {
                        this.stopAll();
                        break;
                    }
                    this.startClienteSSH();
                }
                catch (Exception exception) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("<strong><font color='red'>");
                    stringBuilder.append(this.mContext.getString(R.string.state_disconnected));
                    stringBuilder.append("</font></strong>");
                    SkStatus.logError((String)stringBuilder.toString());
                    this.closeSSH();
                    try {
                        Thread.sleep((long)500L);
                        break block15;
                    }
                    catch (InterruptedException interruptedException) {
                        this.stopAll();
                    }
                }
                break;
            }
            ++n;
        }
        this.mStarting = false;
        if (!this.mStopping) {
            try {
                this.mTunnelThreadStopSignal.await();
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        if (mListener != null) {
        mListener.onStop();
    }
    }
    
    
    */
    
    
    
    
    
    
    
    /*
    
    public void runSemv2echegaproxyerror() {
    mStarting = true;
    mTunnelThreadStopSignal = new CountDownLatch(1);
    
    SkStatus.logInfo("<strong>" + mContext.getString(R.string.starting_service_ssh) + "</strong>");
    
    int tries = 0;
    while (!mStopping) {
        try {
            if (!TunnelUtils.isNetworkOnline(mContext)) {
                SkStatus.updateStateString(SkStatus.SSH_AGUARDANDO_REDE, mContext.getString(R.string.state_nonetwork));
                SkStatus.logInfo(R.string.state_nonetwork);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e2) {
                    stopAll();
                    break;
                }
            } else {
                if (tries > 0)
                    SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_reconnecting) + "</strong>");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                    stopAll();
                    break;
                }

                // Verifica se houve um erro de proxy antes de tentar reconectar
                if (!proxyError) {
                    startClienteSSH();
                } else {
                    SkStatus.logError("Conexão interrompida devido a erro de proxy.");
                    stopAll2();  // Para o processo se houver erro de proxy
                    break;
                }

                break;
            }
        } catch (Exception e) {
            SkStatus.logError("<strong><font color='red'>" + mContext.getString(R.string.state_disconnected) + "</font></strong>");
            closeSSH();
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e2) {
                stopAll();
                break;
            }
        }
        
        tries++;
    }
    
    mStarting = false;
    
    if (!mStopping) {
        try {
            mTunnelThreadStopSignal.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    if (mListener != null) {
        mListener.onStop();
    }
}
    */
    
    
    
    
    
    
    
    
    /*
    public void runOri() {
    mStarting = true;
    mTunnelThreadStopSignal = new CountDownLatch(1);

    synchronized (this) {
        if (mStopping) {
            mStarting = false;
            if (mListener != null) {
                mListener.onStop();
            }
            return; // Se já está parando, não inicie novamente
        }
    }

    SkStatus.logInfo("<strong>" + mContext.getString(R.string.starting_service_ssh) + "</strong>");

    int tries = 0;

    while (true) {
        try {
            // Verifica se a rede está online
            if (!TunnelUtils.isNetworkOnline(mContext)) {
                SkStatus.updateStateString(SkStatus.SSH_AGUARDANDO_REDE, mContext.getString(R.string.state_nonetwork));
                SkStatus.logInfo(R.string.state_nonetwork);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e2) {
                    stopAllProcess();
                    stopAll();
                    break;
                }
                continue;
            }

            // Log de reconexão, se aplicável
            if (tries > 0) {
                SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_reconnecting) + "</strong>");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e2) {
                stopAll();
                stopAllProcess();
                break;
            }

            synchronized (this) {
                // Verifica se está parando
                if (mStopping) {
                    mStarting = false;
                    if (mListener != null) {
                        mListener.onStop();
                    }
                    break;
                }

                // Modo V2Ray
                if (isv2raymode()) {
                    if (v2Tunnel == null) {
                        v2Tunnel = new V2Tunnel(mContext);
                        v2rayrunning = true;
                        v2Tunnel.StartV2ray(
                            mContext.getApplicationContext(),
                            "Default",
                            mConfig.getPrivString(Settings.V2RAY_JSON),
                            null
                        );
                    }
                } else {
                    // Conexão SSH
                    if (!proxyError) {
                        startClienteSSH();
                    } else {
                        SkStatus.logError("Conexão interrompida devido a erro de proxy.");
                        stopAll();
                        stopAllProcess();
                        break;
                    }
                }
            }
            break;

        } catch (Exception e) {
            SkStatus.logError("<strong><font color='red'>" + mContext.getString(R.string.state_disconnected) + "</font></strong>");
            closeSSH();
            stopAllProcess();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e2) {
                stopAll();
                stopAllProcess();
                break;
            }

            synchronized (this) {
                if (mStopping) {
                    mStarting = false;
                    if (mListener != null) {
                        mListener.onStop();
                    }
                    break;
                }

                if (isv2raymode()) {
                    V2Tunnel.StopV2ray(mContext);
                    stopAllProcess();
                }
            }
        }

        tries++;
    }

    // Finalização do método
    mStarting = false;

    if (!mStopping) {
        try {
            mTunnelThreadStopSignal.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    if (mListener != null) {
        mListener.onStop();
    }
}
    */
    
    
    
    
    
    
    
    //METODO COM V2RAY 22 04 25
    
    public void stopAll() {
    if (mStopping) return;

    SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
    SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");

    SharedPreferences prefs = mConfig.getPrefsPrivate();
    int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
        //TunnelState.getTunnelState().setTunnelManager(null);
        //SharedPreferences prefs = mConfig.getPrefsPrivate();
                int tunnelType2 = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
                    
                if (tunnelType2 == Settings.bTUNNEL_TYPE_V2RAY) {
                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                                    v2Tunnel = null;
                                    v2rayrunning = false;
            closeSSH();
            SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO TH STOPALL FORA</strong></font>");
                        }

    new Thread(new Runnable() {
        @Override
        public void run() {
            mStopping = true;
            if (mTunnelThreadStopSignal != null)
                mTunnelThreadStopSignal.countDown();

            if (isv2raymode()) {
                if (v2rayrunning) {
                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                    v2Tunnel = null;
                    v2rayrunning = false;
                            
                            mStopping = true;
                            closeSSH();
                            
                            //v2Tunnel = new V2Tunnel(mContext.getApplicationContext());
                            //v2Tunnel.startMonitoringNetwork();
                    //connectivityManager.unregisterNetworkCallback(callback);
                            //TunnelState.getTunnelState().setTunnelManager(null);
                            SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO TH DENTRO</strong></font>");
                }
                if (mConnected) {
                    stopForwarder();
                    stopAllProcess();
                            SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO TH mConnected</strong></font>");
                }

                        //SkStatus.logInfo("<font color='red'><strong>V2RAY DESCONECTADO 21</strong></font>");
                mRunning = false;
                mStarting = false;
                mReconnecting = false;
            } else if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS && mDnsThread != null) {
                    //SkStatus.logInfo("Tipo de túnel: SlowDNS. Interrompendo SlowDNS...");
                    mConfig.setBypass(false);
                    mDnsThread.interrupt();
                    mDnsThread = null;
                        closeSSH();
                }
                    else {
                closeSSH();
                stopAllProcess();

                mRunning = false;
                mStarting = false;
                mReconnecting = false;
            }
        }
    }).start();

    // Handler no thread principal
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
        }
    }, 1000);
}
    
    
    
    
    
    /*
    
    //METODO SEM V2RAY 22 04 25
    
    public void stopAll() {
    if (mStopping) return;

    SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
    SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");

    SharedPreferences prefs = mConfig.getPrefsPrivate();
    int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);

    new Thread(new Runnable() {
        @Override
        public void run() {
            mStopping = true;
            if (mTunnelThreadStopSignal != null)
                mTunnelThreadStopSignal.countDown();

            if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS && mDnsThread != null) {
                mConfig.setBypass(false);
                mDnsThread.interrupt();
                mDnsThread = null;
                closeSSH();
            } else {
                closeSSH();
                //stopAllProcess();

                mRunning = false;
                mStarting = false;
                mReconnecting = false;
            }
        }
    }).start();

    // Handler no thread principal
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
        }
    }, 1000);
}
    */
    
    
    
    
    
    
    
    //METODO SEM V2RAY 11 04 25
    
    /*
    public void stopAll3() {
    if (mStopping) return;

    SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
    SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");
        
        SharedPreferences prefs = mConfig.getPrefsPrivate();
                int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);


    new Thread(new Runnable() {
        @Override
        public void run() {
            mStopping = true;
            if (mTunnelThreadStopSignal != null)
                mTunnelThreadStopSignal.countDown();

            if (isv2raymode()) {
                if (v2rayrunning) {
                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                    v2Tunnel = null;
                    v2rayrunning = false;
                    connectivityManager.unregisterNetworkCallback(callback);
                }
                if (mConnected) {
                    stopForwarder();
                            stopAllProcess();
                            //stopSlow();
                }
                mRunning = false;
                mStarting = false;
                mReconnecting = false;
            } else {
                closeSSH();
                        stopAllProcess();
                        //stopSlow();

                mRunning = false;
                mStarting = false;
                mReconnecting = false;
            }
        }
    }).start();

    // Handler no thread principal
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
        }
    }, 1000);
}
    
    */
    
    
    
    
    
    /*
    //METODO SEM V2RAY 11 04 25
    
    public void stopAllSemv2() {
    if (mStopping) return;

    SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
    SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");

    new Thread(new Runnable() {
        @Override
        public void run() {
            mStopping = true;
            if (mTunnelThreadStopSignal != null)
                mTunnelThreadStopSignal.countDown();

            // Fecha o SSH e redefine os estados
            closeSSH();
            //stopSlow();

            mRunning = false;
            mStarting = false;
            mReconnecting = false;
        }
    }).start();

    // Handler no thread principal
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
        }
    }, 1000);
}
    
    
    
    */
    
    
    
    
    
    
    
    
    
    
    /*
    
    public void stopAllErroThread30do11() {
		if (mStopping) return;

		SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
		SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");

		new Thread(new Runnable() {
			@Override
			public void run() {
				mStopping = true;
				if (mTunnelThreadStopSignal != null)
					mTunnelThreadStopSignal.countDown();

				if (isv2raymode()) {
                    if (v2rayrunning) {
						V2Tunnel.StopV2ray(mContext.getApplicationContext());
						v2Tunnel = null;
						v2rayrunning= false;
						connectivityManager.unregisterNetworkCallback(callback);
					}
					if (mConnected) {
						stopForwarder();
					}
					mRunning = false;
					mStarting = false;
					mReconnecting = false;
                } else {
					closeSSH();

				mRunning = false;
				mStarting = false;
				mReconnecting = false;
			}
                    }
		}).start();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));

				

			}
                
		}, 1000);
	}
    
    */
    
    
    
    
    
    
    public void stopAll2() {
        if (this.mStopping) {
            return;
        }
        SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
        SkStatus.logInfo("<strong>" + this.mContext.getString(R.string.stopping_service_ssh) + "</strong>");
        new Thread(new Runnable() { // from class: com.vpnmoddervpn.vpn.TunnelManagerThread.100
            @Override // java.lang.Runnable
            public void run() {
                mStopping = true;
                if (mTunnelThreadStopSignal != null)
					mTunnelThreadStopSignal.countDown();
            }
        }).start();
        this.mHandler.post(new Runnable() { // from class: com.vpnmoddervpn.vpn.TunnelManagerThread.300
            @Override // java.lang.Runnable
            public void run() {
                SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
                    
                    if (mConfig.network_meter()){
					mContext.stopService(new Intent(mContext, TunnelManagerThread.class));

				}
                    
                
            }
        });
    }
    
    
    
    
    /*
    
    public void stopAll3() {
		if (mStopping) return;
		
		SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
		SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				mStopping = true;

				if (mTunnelThreadStopSignal != null)
					mTunnelThreadStopSignal.countDown();

				closeSSH();
				
				
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e){}

				SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));

				mRunning = false;
				mStarting = false;
				mReconnecting = false;
			}
		}).start();
	}
    
    */
    
    
    
    
    /*
	
    public void stopAllOri() {
    if (mStopping) return;

    SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
    SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");

    new Thread(new Runnable() {
        @Override
        public void run() {
            mStopping = true;

            if (mTunnelThreadStopSignal != null) {
                mTunnelThreadStopSignal.countDown();
            }

            if (isv2raymode()) {
                if (v2rayrunning) {
                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                    v2Tunnel = null;
                    v2rayrunning = false;
                    connectivityManager.unregisterNetworkCallback(callback);
                }

                if (mConnected) {
                    stopForwarder();
                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                }

            } else {
                closeSSH();
                V2Tunnel.StopV2ray(mContext.getApplicationContext());
            }

            // Atualiza status do túnel
            mRunning = false;
            mStarting = false;
            mReconnecting = false;
        }
    }).start();

    // Atualiza o estado após atraso
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
        }
    }, 1000);
}
    */
    
    
    
    
    
    
   

	protected void startForwarder(int portaLocal) throws Exception {
		if (!mConnected) {
			throw new Exception();
		}
		
		startForwarderSocks(portaLocal);
		
		startTunnelVpnService();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!mConnected) break;
					
					try {
						Thread.sleep(2000);
					} catch(InterruptedException e) {
						break;
					}
					
					if (lastPingLatency > 0) {
					//	SkStatus.logInfo(String.format("Ping Latency: %d ms", lastPingLatency));
						break;
					}
				}
			}
		}).start();
		
		String PING = mConfig.setPinger();	

		if (mConfig.setAutoPing()){

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}

			if (!PING.equals(""))
			{
				pinger = new Pinger(mConnection, PING);
				pinger.start();
			}

		}
	}
	
	private synchronized void interruptPinger() {
		if (pinger != null && pinger.isAlive()) {
			SkStatus.logInfo("Parando Pinger");

		    pinger.interrupt();
		}
	}

	protected void stopForwarder() {
		stopTunnelVpnService();
		interruptPinger();
		stopForwarderSocks();
	}
	
	
	/**
	* Cliente SSH
	*/
	
	private final static int AUTH_TRIES = 1;
	private final static int RECONNECT_TRIES = 5;
	
	private Connection mConnection;
	
	private boolean mConnected = false;
	
	public void startClienteSSH() throws Exception {
		mStopping = false;
		mRunning = true;
		
		String servidor = mConfig.getPrivString(Settings.SERVIDOR_KEY);
		int porta = Integer.parseInt(mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY));
		String usuario = mConfig.getPrivString(Settings.USUARIO_KEY);
		SkStatus.logInfo("Usuário : <strong>" + mConfig.getPrivString(Settings.USUARIO_KEY) + "</strong>");
		
		String _senha = mConfig.getPrivString(Settings.SENHA_KEY);
		String senha = _senha.isEmpty() ? PasswordCache.getAuthPassword(null, false) : _senha;
		
		String keyPath = mConfig.getSSHKeypath();
		int portaLocal = Integer.parseInt(mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));

		try {
			
			conectar(servidor, porta);

			for (int i = 0; i < AUTH_TRIES; i++) {
				if (mStopping) {
					return;
				}

				try {
					autenticar(usuario, senha, keyPath);

					break;
				} catch(IOException e) {
					if (i+1 >= AUTH_TRIES) {
						throw new IOException("Failed to Authenticate");
					}
					else {
						try {
							Thread.sleep(3000);
						} catch(InterruptedException e2) {
							return;
						}
					}
				}
			}

			SkStatus.updateStateString(SkStatus.SSH_CONECTADO, "CONEXÃO SSH ESTABELECIDA");
            SkStatus.logInfo("<strong><html><font color='#ffffff'>" + mContext.getString(R.string.state_connected) + "</font></html></strong>");
			//SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_connected) + "</strong>");
			
			if (mConfig.getSSHPinger() > 0) {
				startPinger(mConfig.getSSHPinger());
			}
			
			startForwarder(portaLocal);

		} catch(Exception e) {
			mConnected = false;

			throw e;
		}
	}
	
	public synchronized void closeSSH() {
		stopForwarder();
		stopPinger();

		if (mConnection != null) {
		//	SkStatus.logDebug("Stopping SSH");
			mConnection.close();
		}
	}
    
    
    
    
	
	protected void conectar(String servidor, int porta) throws Exception {
		if (!mStarting) {
			throw new Exception();
		}
		
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		// aqui deve conectar
		try {

			mConnection = new Connection(servidor, porta);
			if (mConfig.getModoDebug() && !prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
				// Desativado, pois estava enchendo o Logger
				//mConnection.enableDebugging(true, this);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mContext, "Debug mode enabled",
							Toast.LENGTH_SHORT).show();
					}
				});
			}
            
            if (this.mConfig.getIsDisabledDelaySSH()) {
                this.mConnection.setTCPNoDelay(true);
            }
            if (this.mConfig.sshCompression()) {
                this.mConnection.setCompression(true);
                SkStatus.logInfo("<strong>COMPRESSÃO SSH HABILITADA</strong>");
            }

			// proxy
			addProxy(prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false), prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT),
                     (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true) ? mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY) : null),  mConfig.getPrivString(Settings.CUSTOM_SNI),
                     mConnection);


			// monitora a conexão
			mConnection.addConnectionMonitor(this);
			
			if (Build.VERSION.SDK_INT >= 23) {
				ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				ProxyInfo proxy = cm.getDefaultProxy();
				if (proxy != null) {
					SkStatus.logInfo("<strong>Network Proxy:</strong> " + String.format("%s:%d", proxy.getHost(), proxy.getPort()));
				}
			}
			
			SkStatus.updateStateString(SkStatus.SSH_CONECTANDO, mContext.getString(R.string.state_connecting));
			SkStatus.logInfo(R.string.ssh_connecting);
			mConnection.connect(this, 10*1000, 20*1000);

			mConnected = true;

		} catch(Exception e) {

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			String cause = e.getCause().toString();
			if (useProxy && cause.contains("Key exchange was not finished")) {
				SkStatus.logError("Perca de conexão com o proxy");
			}
			else {
				SkStatus.logError("SSH: " + cause);
			}
			
			throw new Exception(e);
		}
	}

    
    
    
    
    
   
    
    


	/**
	 * Autenticação
	 */

	private static final String AUTH_PUBLICKEY = "publickey",
			AUTH_PASSWORD = "password", AUTH_KEYBOARDINTERACTIVE = "keyboard-interactive";

    
    
    
    
    
	protected void autenticar(String usuario, String senha, String keyPath) throws IOException {
		if (!mConnected) {
			throw new IOException();
		}
        
        
		
		SkStatus.updateStateString(SkStatus.SSH_AUTENTICANDO, mContext.getString(R.string.state_auth));

		try {
			if (mConnection.isAuthMethodAvailable(usuario,
				AUTH_PASSWORD)) {
                
                
					
				if (mConnection.authenticateWithPassword(usuario,
						senha)) {
					//SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_auth_success) + "</strong>");
                    
                    SkStatus.logInfo("PROCURANDO SEU CADASTRO...");
                    
				}
			}
		} catch (IllegalStateException e) {
			Log.e(TAG,
				  "A CONEXÃO FOI INTERROMPIDA ENQUANTO TENTÁVAMOS AUTENTICAR",
				  e);
		} catch (Exception e) {
			Log.e(TAG, "Problem during handleAuthentication()", e);
		}

		try {
			if (mConnection.isAuthMethodAvailable(usuario,
					AUTH_PUBLICKEY) && keyPath != null && !keyPath.isEmpty()) {
				File f = new File(keyPath);
				if (f.exists()) {
					if (senha.equals("")) senha = null;

					SkStatus.logInfo("AUTENTICANDO COM CHAVE PÚBLICA");
					
					if (mConnection.authenticateWithPublicKey(usuario, f,
							senha)) {
						SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_auth_success) + "</strong>");
					}
				}
			}
		} catch (Exception e) {
			Log.d(TAG, "O host não oferece suporte à autenticação de 'chave pública'.");
		}


		if (!mConnection.isAuthenticationComplete()) {
			SkStatus.logInfo("<strong></font><font color=\"red\">ERRO - USUÁRIO/SENHA INCORRETOS OU LOGIN EXPIRADO 🔑</strong>");
        SkStatus.logInfo("<strong></font><font color=\"red\">VERIFIQUE A DATA DE VENCIMENTO DA SUA INTERNET </strong>");
        SkStatus.logInfo("<strong></font><font color=\"red\">ENTRE EM CONTATO COM O DONO DO SERVIDOR P/ VERIFICAR</strong>");

			throw new IOException("NÃO FOI POSSÍVEL AUTENTICAR COM OS DADOS FORNECIDOS");
            
		}
        
	}
    
    
    

	// XXX: Is it right?
	@Override
	public String[] replyToChallenge(String name, String instruction,
			int numPrompts, String[] prompt, boolean[] echo) throws Exception {
		String[] responses = new String[numPrompts];
		for (int i = 0; i < numPrompts; i++) {
			// request response from user for each prompt
			if (prompt[i].toLowerCase().contains("password"))
				responses[i] = mConfig.getPrivString(Settings.SENHA_KEY);
		}
		return responses;
	}


	/**
	 * ServerHostKeyVerifier
	 * Fingerprint
	 */

	@Override
	public boolean verifyServerHostKey(String hostname, int port,
		String serverHostKeyAlgorithm, byte[] serverHostKey)
	throws Exception {

		String fingerPrint = KnownHosts.createHexFingerprint(
			serverHostKeyAlgorithm, serverHostKey);
		//int fingerPrintStatus = SSHConstants.FINGER_PRINT_CHANGED;

		SkStatus.logInfo("Finger Print :  " + fingerPrint);

		//Log.d(TAG, "Finger Print Type: " + "");

		return true;
	}


	/**
	 * Proxy
	 */

	private boolean useProxy = false;
    

	protected void addProxy(boolean isProteger, int mTunnelType, String mCustomPayload, String mCustomSNI, Connection conn) throws Exception {

		if (mTunnelType != 0) {
			useProxy = false;

			switch (mTunnelType) {
            
                case Settings.bTUNNEL_TYPE_SSH_DIRECT:
    if (mCustomPayload != null && mCustomPayload.isEmpty()) {
        mCustomPayload = null;
    }
    try {
        String[] payloadKeys2 = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] servidoresDirect = mConfig.getPrivString(Settings.SERVIDOR_KEY).split("#");
        String[] portasDirect = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String payloadName4 = mConfig.getPrivString(Settings.PAY_NAME);

        // Resolve domínios para IPs
        String[] resolvedServidoresDirect = new String[servidoresDirect.length];
        for (int i = 0; i < servidoresDirect.length; i++) {
            try {
                resolvedServidoresDirect[i] = VpnUtils.resolveDomainToIp(servidoresDirect[i]);
            } catch (UnknownHostException e) {
                resolvedServidoresDirect[i] = null;
            }
        }

        int maxThreads = 20; // ajuste se necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicReference<String> selectedServidorDirect = new AtomicReference<>(); // Para armazenar o IP selecionado
        AtomicReference<String> selectedPayloadKey = new AtomicReference<>(); // Para armazenar a payload key selecionada

        // Logs iniciais
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName4 + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys2.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Servidores: " + servidoresDirect.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + portasDirect.length + "</strong>");
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo(""); // linha em branco

        for (int DirectPayloadIndex = 0; DirectPayloadIndex < payloadKeys2.length; DirectPayloadIndex++) {
            String payloadKey = payloadKeys2[DirectPayloadIndex];
            for (int DirectServerIndex = 0; DirectServerIndex < servidoresDirect.length; DirectServerIndex++) {
                final String servidorDirect = (resolvedServidoresDirect[DirectServerIndex] != null && !resolvedServidoresDirect[DirectServerIndex].isEmpty())
                        ? resolvedServidoresDirect[DirectServerIndex]
                        : servidoresDirect[DirectServerIndex];
                final int DirectPortIndex = DirectServerIndex % portasDirect.length;
                final int porta = Integer.parseInt(portasDirect[DirectPortIndex]);
                final String finalPayload = payloadKey;

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        HttpProxyCustom httpProxyCustom = new HttpProxyCustom(
                                servidorDirect, porta, null, null, finalPayload, false, this.mContext);

                        // Atualiza o IP e a payload key selecionados
                        selectedServidorDirect.set(servidorDirect);
                        selectedPayloadKey.set(finalPayload);

                        // ✅ Aqui é o ponto onde injeta e mostra os logs "INJETANDO" e HTTP
                        if (!isProteger && conn != null) { // garante que o conn existe
                            conn.setProxyData(httpProxyCustom);
                        }

                    } catch (Exception e) {
                        SkStatus.logError("Erro ao injetar direct: " + e.getMessage());
                    }
                }, executor);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // Após todas as tentativas, você pode definir nextProxyIPDirect e nextPayloadKey
        this.nextProxyIPDirect = selectedServidorDirect.get(); // Atualiza o nextProxyIPDirect com o último IP selecionado
        this.nextPayloadKey = selectedPayloadKey.get(); // Atualiza o nextPayloadKey com a última payload key selecionada

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
                break;
                
                
                
                
                
                
                
                
                /*
                
                case Settings.bTUNNEL_TYPE_SSH_DIRECT:
    if (mCustomPayload != null && mCustomPayload.isEmpty()) {
        mCustomPayload = null;
    }
    try {
        String[] payloadKeys2 = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs2 = mConfig.getPrivString(Settings.SERVIDOR_KEY).split("#");
        String[] proxyPortas2 = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String payloadName4 = mConfig.getPrivString(Settings.PAY_NAME); 

        // Resolve domínios para IPs
        String[] ProxyresolvedProxyIP = new String[proxyIPs2.length];
        for (int i = 0; i < proxyIPs2.length; i++) {
            try {
                ProxyresolvedProxyIP[i] = VpnUtils.resolveDomainToIp(proxyIPs2[i]);
            } catch (UnknownHostException e) {
                ProxyresolvedProxyIP[i] = null;
            }
        }

        int maxThreads = 20; // ajuste se necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean connected = new AtomicBoolean(false);

        // Logs iniciais
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName4 + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys2.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Servidores: " + proxyIPs2.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + proxyPortas2.length + "</strong>");
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo(""); // linha em branco

        for (int DirectPayloadIndex = 0; DirectPayloadIndex < payloadKeys2.length; DirectPayloadIndex++) {
            String payloadKey = payloadKeys2[DirectPayloadIndex];
            for (int DirectProxyIndex = 0; DirectProxyIndex < proxyIPs2.length; DirectProxyIndex++) {
                final String servidorDirect = (ProxyresolvedProxyIP[DirectProxyIndex] != null && !ProxyresolvedProxyIP[DirectProxyIndex].isEmpty())
                        ? ProxyresolvedProxyIP[DirectProxyIndex]
                        : proxyIPs2[DirectProxyIndex];
                final int DirectPortIndex = DirectProxyIndex % proxyPortas2.length;
                final int porta = Integer.parseInt(proxyPortas2[DirectPortIndex]);
                final int finalPayloadIndex = DirectPayloadIndex;
                final int finalProxyIndex = DirectProxyIndex;
                final int finalPortIndex = DirectPortIndex;
                final String finalPayload = payloadKey;

                this.nextProxyIPDirect = servidorDirect;
                this.nextPayloadKey = finalPayload;

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        // Logs iniciais

                        HttpProxyCustom httpProxyCustom = new HttpProxyCustom(
                                servidorDirect, porta, null, null, finalPayload, false, this.mContext);

                        if (!isProteger && conn != null) { // garante que o conn existe
                            conn.setProxyData(httpProxyCustom);
                        }
                    } catch (Exception e) {
                        SkStatus.logError("Erro ao injetar direct: " + e.getMessage());
                    }
                }, executor);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
    
                */
    
    
    
    
    
                /*
                //metodo atual 06 06 25
                case Settings.bTUNNEL_TYPE_SSH_DIRECT:
                if (mCustomPayload != null && mCustomPayload.isEmpty()) {
        mCustomPayload = null;
    }
    try {
        String[] payloadKeys2 = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs2 = mConfig.getPrivString(Settings.SERVIDOR_KEY).split("#");
        String[] proxyPortas2 = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String payloadName4 = mConfig.getPrivString(Settings.PAY_NAME);            
        String[] ProxyresolvedProxyIP = new String[proxyIPs2.length];
        for (int i = 0; i < proxyIPs2.length; i++) {
            try {
                ProxyresolvedProxyIP[i] = VpnUtils.resolveDomainToIp(proxyIPs2[i]);
            } catch (UnknownHostException e) {
                ProxyresolvedProxyIP[i] = null;
            }
        }
        // Verificações de índice
        if (DirectProxyIndex >= proxyIPs2.length) DirectProxyIndex = 0;
        if (DirectPortIndex >= proxyPortas2.length) DirectPortIndex = 0;
        if (DirectPayloadIndex >= payloadKeys2.length) DirectPayloadIndex = 0;
        // Tenta usar o IP resolvido ou o IP original
        String servidorDirect = (ProxyresolvedProxyIP[DirectProxyIndex] != null && !ProxyresolvedProxyIP[DirectProxyIndex].isEmpty())
            ? ProxyresolvedProxyIP[DirectProxyIndex] 
            : proxyIPs2[DirectProxyIndex];
        int porta = Integer.parseInt(proxyPortas2[DirectPortIndex]);
        String nextPayloadKey = payloadKeys2[DirectPayloadIndex];
        this.nextProxyIPDirect = servidorDirect;
        this.nextPayloadKey = nextPayloadKey;
        HttpProxyCustom httpProxyCustom = new HttpProxyCustom(
            servidorDirect, porta, null, null, this.nextPayloadKey, false, this.mContext);
        if (this.nextPayloadKey != null) {
              SkStatus.logInfo("Conectando na Payload: " + (DirectPayloadIndex + 1));          
        } else {
        }            
        SkStatus.logInfo("Conectando no Servidor: " + (DirectProxyIndex + 1));
        SkStatus.logInfo("Conectando na Porta: " + (DirectPortIndex + 1));
        SkStatus.logInfo("Config Selecionada: " + payloadName4);            
        // Rotação
        DirectPortIndex = (DirectPortIndex + 1) % proxyPortas2.length;
        if (DirectPortIndex == 0) {
            DirectProxyIndex = (DirectProxyIndex + 1) % proxyIPs2.length;
        }
        if (DirectProxyIndex == 0) {
            DirectPayloadIndex = (DirectPayloadIndex + 1) % payloadKeys2.length;
        }
        if (!isProteger) {
            conn.setProxyData(httpProxyCustom);
        }
    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
    
    */
                /*
                
                //metodo atual 06 06 25
                case Settings.bTUNNEL_TYPE_SSH_PROXY:
    try {
        // Leia as configurações atualizadas dos SharedPreferences
        String[] payloadKeys = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs = mConfig.getPrivString(Settings.PROXY_IP_KEY).split("#");
        String[] proxyPortas = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String payloadName3 = mConfig.getPrivString(Settings.PAY_NAME);
        // Logando os dados para verificação
        SkStatus.logInfo("Payloads: " + Arrays.toString(payloadKeys));
        SkStatus.logInfo("Proxy IPs: " + Arrays.toString(proxyIPs));
        SkStatus.logInfo("Proxy Ports: " + Arrays.toString(proxyPortas));
        String[] ProxyresolvedProxyIP = new String[proxyIPs.length];
        for (int i = 0; i < proxyIPs.length; i++) {
            try {
                ProxyresolvedProxyIP[i] = VpnUtils.resolveDomainToIp(proxyIPs[i]);
            } catch (UnknownHostException e) {
                ProxyresolvedProxyIP[i] = null;
            }
        }
        // Verificações de índice
        if (PxProxyIndex >= proxyIPs.length) PxProxyIndex = 0;
        if (PxPortIndex >= proxyPortas.length) PxPortIndex = 0;
        if (PxPayloadIndex >= payloadKeys.length) PxPayloadIndex = 0;
        // Tenta usar o IP resolvido ou o IP original
        String servidor = (ProxyresolvedProxyIP[PxProxyIndex] != null && !ProxyresolvedProxyIP[PxProxyIndex].isEmpty())
            ? ProxyresolvedProxyIP[PxProxyIndex] 
            : proxyIPs[PxProxyIndex];
        int porta = Integer.parseInt(proxyPortas[PxPortIndex]);
        String nextPayloadKey = payloadKeys[PxPayloadIndex];
        this.nextProxyIP = servidor;
        this.nextPayloadKey = nextPayloadKey;
        HttpProxyCustom httpProxyCustom = new HttpProxyCustom(
            servidor, porta, null, null, this.nextPayloadKey, false, this.mContext);
        if (this.nextPayloadKey != null) {
            SkStatus.logInfo("Conectando na Payload: " + (PxPayloadIndex + 1));
        } else {
            SkStatus.logInfo("nextPayloadKey é nulo");
        }
        SkStatus.logInfo("Conectando no ProxyIP: " + (PxProxyIndex + 1));
        SkStatus.logInfo("Conectando na Porta: " + (PxPortIndex + 1));
        SkStatus.logInfo("Config Selecionada: " + payloadName3);
        // Rotação
        PxPortIndex = (PxPortIndex + 1) % proxyPortas.length;
        if (PxPortIndex == 0) {
            PxProxyIndex = (PxProxyIndex + 1) % proxyIPs.length;
        }
        if (PxProxyIndex == 0) {
            PxPayloadIndex = (PxPayloadIndex + 1) % payloadKeys.length;
        }
        if (!isProteger) {
            conn.setProxyData(httpProxyCustom);
        }
    } catch (Exception e) {
        SkStatus.logError("Erro ao conectar usando proxy.");
        proxyError = true;
        throw new Exception("Erro ao conectar usando proxy.");
    }
    break;
                
                */


                
                
                
                // Variável de controle (pode ser global, ou salva em outro lugar)


case Settings.bTUNNEL_TYPE_SSH_PROXY:
    try {
        // Leia as configurações
        String[] payloadKeys = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs = mConfig.getPrivString(Settings.PROXY_IP_KEY).split("#");
        String[] proxyPortas = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String payloadName3 = mConfig.getPrivString(Settings.PAY_NAME); // Exemplo: Nome do config

        // Resolve domínios para IPs
        String[] ProxyresolvedProxyIP = new String[proxyIPs.length];
        for (int i = 0; i < proxyIPs.length; i++) {
            try {
                ProxyresolvedProxyIP[i] = VpnUtils.resolveDomainToIp(proxyIPs[i]);
            } catch (UnknownHostException e) {
                ProxyresolvedProxyIP[i] = null;
            }
        }

        int maxThreads = 20; // ajuste se necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicReference<String> selectedProxyIP = new AtomicReference<>(); // Para armazenar o IP selecionado

        // Logs iniciais
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName3 + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Proxys: " + proxyIPs.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + proxyPortas.length + "</strong>");
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo(""); // linha em branco
        
        for (int PxPayloadIndex = 0; PxPayloadIndex < payloadKeys.length; PxPayloadIndex++) {
            String payloadKey = payloadKeys[PxPayloadIndex];
            for (int PxProxyIndex = 0; PxProxyIndex < proxyIPs.length; PxProxyIndex++) {
                final String servidor = (ProxyresolvedProxyIP[PxProxyIndex] != null && !ProxyresolvedProxyIP[PxProxyIndex].isEmpty())
                        ? ProxyresolvedProxyIP[PxProxyIndex]
                        : proxyIPs[PxProxyIndex];
                final int PxPortIndex = PxProxyIndex % proxyPortas.length;
                final int porta = Integer.parseInt(proxyPortas[PxPortIndex]);
                final String finalPayload = payloadKey;

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        HttpProxyCustom httpProxyCustom = new HttpProxyCustom(
                                servidor, porta, null, null, finalPayload, false, this.mContext);

                        // Atualiza o IP selecionado
                        selectedProxyIP.set(servidor);

                        // ✅ Aqui é o ponto onde injeta e mostra os logs "INJETANDO" e HTTP
                        if (!isProteger && conn != null) { // garante que o conn existe
                            conn.setProxyData(httpProxyCustom);
                        }

                    } catch (Exception e) {
                        SkStatus.logError("Erro ao injetar proxy: " + e.getMessage());
                    }
                }, executor);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // Após todas as tentativas, você pode definir nextProxyIP
        this.nextProxyIP = selectedProxyIP.get(); // Atualiza o nextProxyIP com o último IP selecionado

    } catch (Exception e) {
        SkStatus.logError("Erro ao conectar usando proxy.");
        proxyError = true;
        throw new Exception("Erro ao conectar usando proxy.");
    }
    break;
                
                
                
                
                
                
                
                
                
                


/*
case Settings.bTUNNEL_TYPE_SSH_PROXY:
    try {
        // Leia as configurações
        String[] payloadKeys = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs = mConfig.getPrivString(Settings.PROXY_IP_KEY).split("#");
        String[] proxyPortas = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String payloadName3 = mConfig.getPrivString(Settings.PAY_NAME); // Exemplo: Nome do config

        // Resolve domínios para IPs
        String[] ProxyresolvedProxyIP = new String[proxyIPs.length];
        for (int i = 0; i < proxyIPs.length; i++) {
            try {
                ProxyresolvedProxyIP[i] = VpnUtils.resolveDomainToIp(proxyIPs[i]);
            } catch (UnknownHostException e) {
                ProxyresolvedProxyIP[i] = null;
            }
        }

        int maxThreads = 20; // ajuste se necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean connected = new AtomicBoolean(false);
        
        SkStatus.logInfo("Config Selecionada: " + payloadName3);

        for (int PxPayloadIndex = 0; PxPayloadIndex < payloadKeys.length; PxPayloadIndex++) {
            String payloadKey = payloadKeys[PxPayloadIndex];
            for (int PxProxyIndex = 0; PxProxyIndex < proxyIPs.length; PxProxyIndex++) {
                final String servidor = (ProxyresolvedProxyIP[PxProxyIndex] != null && !ProxyresolvedProxyIP[PxProxyIndex].isEmpty())
                        ? ProxyresolvedProxyIP[PxProxyIndex]
                        : proxyIPs[PxProxyIndex];
                final int PxPortIndex = PxProxyIndex % proxyPortas.length;
                final int porta = Integer.parseInt(proxyPortas[PxPortIndex]);
                final int finalPayloadIndex = PxPayloadIndex;
                final int finalProxyIndex = PxProxyIndex;
                final int finalPortIndex = PxPortIndex;
                final String finalPayload = payloadKey;
                
                this.nextProxyIP = servidor;

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
    try {
        // Logs iniciais
        SkStatus.logInfo("Conectando na Payload: " + (finalPayloadIndex + 1));
        SkStatus.logInfo("Conectando no ProxyIP: " + (finalProxyIndex + 1));
        SkStatus.logInfo("Conectando na Porta: " + (finalPortIndex + 1));
        SkStatus.logInfo("Config Selecionada: " + payloadName3);

        HttpProxyCustom httpProxyCustom = new HttpProxyCustom(
                servidor, porta, null, null, finalPayload, false, this.mContext);

        // ✅ Aqui é o ponto onde injeta e mostra os logs "INJETANDO" e HTTP
        if (!isProteger && conn != null) { // garante que o conn existe
            conn.setProxyData(httpProxyCustom);
            SkStatus.logInfo("Conectado na Payload: " + (finalPayloadIndex + 1));
            SkStatus.logInfo("Conectado no ProxyIP: " + (finalProxyIndex + 1));
            SkStatus.logInfo("Conectado na Porta: " + (finalPortIndex + 1));
            SkStatus.logInfo("Config Selecionada: " + payloadName3);
        }

    } catch (Exception e) {
        SkStatus.logError("Erro ao injetar proxy: " + e.getMessage());
    }
}, executor);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();


    } catch (Exception e) {
        SkStatus.logError("Erro ao conectar usando proxy.");
        proxyError = true;
        throw new Exception("Erro ao conectar usando proxy.");
    }
    break;

*/
                
                
                
                
                /*

//metodo novo

case Settings.bTUNNEL_TYPE_SSH_PROXY:
    try {
        // Leia as configurações
        String[] payloadKeys = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs = mConfig.getPrivString(Settings.PROXY_IP_KEY).split("#");
        String[] proxyPortas = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String payloadName3 = mConfig.getPrivString(Settings.PAY_NAME); // Exemplo: Nome do config

        // Resolve domínios para IPs
        String[] ProxyresolvedProxyIP = new String[proxyIPs.length];
        for (int i = 0; i < proxyIPs.length; i++) {
            try {
                ProxyresolvedProxyIP[i] = VpnUtils.resolveDomainToIp(proxyIPs[i]);
            } catch (UnknownHostException e) {
                ProxyresolvedProxyIP[i] = null;
            }
        }

        int maxThreads = 20; // ajuste se necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean connected = new AtomicBoolean(false);
        
        // Logs iniciais
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName3 + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Proxys: " + proxyIPs.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + proxyPortas.length + "</strong>");
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo(""); // linha em branco
        

        for (int PxPayloadIndex = 0; PxPayloadIndex < payloadKeys.length; PxPayloadIndex++) {
            String payloadKey = payloadKeys[PxPayloadIndex];
            for (int PxProxyIndex = 0; PxProxyIndex < proxyIPs.length; PxProxyIndex++) {
                final String servidor = (ProxyresolvedProxyIP[PxProxyIndex] != null && !ProxyresolvedProxyIP[PxProxyIndex].isEmpty())
                        ? ProxyresolvedProxyIP[PxProxyIndex]
                        : proxyIPs[PxProxyIndex];
                final int PxPortIndex = PxProxyIndex % proxyPortas.length;
                final int porta = Integer.parseInt(proxyPortas[PxPortIndex]);
                final int finalPayloadIndex = PxPayloadIndex;
                final int finalProxyIndex = PxProxyIndex;
                final int finalPortIndex = PxPortIndex;
                final String finalPayload = payloadKey;
                
                this.nextProxyIP = servidor;
                this.nextPayloadKey = finalPayload;

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
    try {
        // Logs iniciais
        

        HttpProxyCustom httpProxyCustom = new HttpProxyCustom(
                servidor, porta, null, null, finalPayload, false, this.mContext);


        // ✅ Aqui é o ponto onde injeta e mostra os logs "INJETANDO" e HTTP
        if (!isProteger && conn != null) { // garante que o conn existe
            conn.setProxyData(httpProxyCustom);
            
        }
                                    
                                    

    } catch (Exception e) {
        SkStatus.logError("Erro ao injetar proxy: " + e.getMessage());
    }
}, executor);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;

*/

/*
                //metodo atual 06 06 25
                
                case Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL:
    TLSSocketFactory.TLSv3 = mConfig.tls_version();
                String customSNI = mCustomSNI;
    if (customSNI != null && customSNI.isEmpty()) {
        customSNI = null; // Corrige o valor se for vazio
    }
    try {
        // Obter listas de payloads, proxies e portas
        String[] payloadKeys = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs = mConfig.getPrivString(Settings.SERVIDOR_KEY).split("#");
        String[] proxyPortas = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String[] sniList = mConfig.getPrivString(SettingsConstants.CUSTOM_SNI).split("#");
        String payloadName = mConfig.getPrivString(Settings.PAY_NAME);
        String[] resolvedSshServer = new String[proxyIPs.length];
        for (int i = 0; i < proxyIPs.length; i++) {
            try {
                resolvedSshServer[i] = VpnUtils.resolveDomainToIp(proxyIPs[i]);
            } catch (UnknownHostException e) {
                resolvedSshServer[i] = null;
            }
        }
        String[] resolvedSshni = new String[sniList.length];
        for (int i = 0; i < sniList.length; i++) {
            try {
                resolvedSshni[i] = VpnUtils.resolveDomainToIp(sniList[i]);
            } catch (UnknownHostException e) {
                resolvedSshni[i] = null;
            }
        }            
        // Verificações de índice
        if (SSLProxyIndex >= proxyIPs.length) SSLProxyIndex = 0;
        if (SSLPortIndex >= proxyPortas.length) SSLPortIndex = 0;
        if (SSLPayloadIndex >= payloadKeys.length) SSLPayloadIndex = 0;
        if (SSLSniIndex >= sniList.length) SSLSniIndex = 0;
        // Tenta usar o IP resolvido ou o IP original
        String servidor = (resolvedSshServer[SSLProxyIndex] != null && !resolvedSshServer[SSLProxyIndex].isEmpty())
            ? resolvedSshServer[SSLProxyIndex] 
            : proxyIPs[SSLProxyIndex];
        String selectedSni = (resolvedSshni[SSLSniIndex] != null && !resolvedSshni[SSLSniIndex].isEmpty())
            ? resolvedSshni[SSLSniIndex] 
            : sniList[SSLSniIndex];            
        int porta = Integer.parseInt(proxyPortas[SSLPortIndex]);
        String nextPayloadKey = payloadKeys[SSLPayloadIndex];
        //String selectedSni = sniList[SSLSniIndex];
        this.nextProxyIPDirect = servidor;
        this.nextPayloadKey = nextPayloadKey;
                    SSLTunnelProxy sslTypeData = new SSLTunnelProxy(servidor, porta, selectedSni);
        if (this.nextPayloadKey != null) {
              SkStatus.logInfo("Conectando na Payload: " + (SSLPayloadIndex + 1));          
        } else {
        }
        SkStatus.logInfo("Conectando no Servidor: " + (SSLProxyIndex + 1));                    
        SkStatus.logInfo("Conectando na SNI: " + (SSLSniIndex + 1));
        SkStatus.logInfo("Conectando na Porta: " + (SSLPortIndex + 1));
        SkStatus.logInfo("Config Selecionada: " + payloadName);            
        // Rotação
        SSLPortIndex = (SSLPortIndex + 1) % proxyPortas.length;
        if (SSLPortIndex == 0) {
            SSLSniIndex = (SSLSniIndex + 1) % proxyIPs.length;
            if (SSLSniIndex == 0) {
                SSLProxyIndex = (SSLProxyIndex + 1) % sniList.length;
                if (SSLProxyIndex == 0) {
                    SSLPayloadIndex = (SSLPayloadIndex + 1) % payloadKeys.length;
                }
            }
        }
        if (!isProteger) {
            conn.setProxyData(sslTypeData);
        }
    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                */
                
                
                
                case Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL:
    TLSSocketFactory.TLSv3 = mConfig.tls_version();
    String customSNI = mCustomSNI;
    if (customSNI != null && customSNI.isEmpty()) {
        customSNI = null;
    }
    try {
        // Obter listas
        String[] payloadKeys = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs = mConfig.getPrivString(Settings.SERVIDOR_KEY).split("#");
        String[] proxyPortas = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String[] sniList = mConfig.getPrivString(SettingsConstants.CUSTOM_SNI).split("#");
        String sshServer = mConfig.getPrivString(Settings.SERVIDOR_KEY);
        String payloadName = mConfig.getPrivString(Settings.PAY_NAME);

        // Resolve domínios para IPs
        String[] resolvedProxyIPs = new String[proxyIPs.length];
        for (int i = 0; i < proxyIPs.length; i++) {
            try {
                resolvedProxyIPs[i] = VpnUtils.resolveDomainToIp(proxyIPs[i]);
            } catch (UnknownHostException e) {
                resolvedProxyIPs[i] = null;
            }
        }
        String[] resolvedSshni = new String[sniList.length];
        for (int i = 0; i < sniList.length; i++) {
            try {
                resolvedSshni[i] = VpnUtils.resolveDomainToIp(sniList[i]);
            } catch (UnknownHostException e) {
                resolvedSshni[i] = null;
            }
        }

        // Cria pool de threads
        int maxThreads = 20; // ajuste se necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicReference<String> selectedServidor = new AtomicReference<>(); // Para armazenar o IP selecionado
        AtomicReference<String> selectedPayloadKey = new AtomicReference<>(); // Para armazenar a payload key selecionada
        AtomicReference<String> selectedSni = new AtomicReference<>(); // Para armazenar o SNI selecionado

        // Logs iniciais
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Servidores: " + proxyIPs.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de SNIs: " + sniList.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + proxyPortas.length + "</strong>");
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo(""); // linha em branco

        for (int SSLPayloadIndex = 0; SSLPayloadIndex < payloadKeys.length; SSLPayloadIndex++) {
            String payloadKey = payloadKeys[SSLPayloadIndex];
            for (int SSLProxyIndex = 0; SSLProxyIndex < proxyIPs.length; SSLProxyIndex++) {
                for (int SSLPortIndex = 0; SSLPortIndex < proxyPortas.length; SSLPortIndex++) {
                    for (int SSLSniIndex = 0; SSLSniIndex < sniList.length; SSLSniIndex++) {
                        final String servidor = (resolvedProxyIPs[SSLProxyIndex] != null && !resolvedProxyIPs[SSLProxyIndex].isEmpty())
                                ? resolvedProxyIPs[SSLProxyIndex]
                                : proxyIPs[SSLProxyIndex];
                        final String porta = proxyPortas[SSLPortIndex];
                        final String currentSni = (resolvedSshni[SSLSniIndex] != null && !resolvedSshni[SSLSniIndex].isEmpty())
                                ? resolvedSshni[SSLSniIndex]
                                : sniList[SSLSniIndex];
                        final String finalPayload = payloadKey;

                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            try {
                                SSLTunnelProxy sslTypeData = new SSLTunnelProxy(servidor, Integer.parseInt(porta), currentSni);

                                // Atualiza o IP, a payload key e o SNI selecionados
                                selectedServidor.set(servidor);
                                selectedPayloadKey.set(finalPayload);
                                selectedSni.set(currentSni);

                                if (!isProteger && conn != null) {
                                    conn.setProxyData(sslTypeData);
                                }
                            } catch (Exception e) {
                                SkStatus.logError("Erro ao injetar SSL: " + e.getMessage());
                            }
                        }, executor);

                        futures.add(future);
                    }
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // Após todas as tentativas, você pode definir nextProxyIPDirect, nextPayloadKey e nextSniSSL
        this.nextProxyIPDirect = selectedServidor.get(); // Atualiza o nextProxyIPDirect com o último IP selecionado
        this.nextPayloadKey = selectedPayloadKey.get(); // Atualiza o nextPayloadKey com a última payload key selecionada
        // Se você tiver uma variável para o SNI selecionado, atualize-a aqui também
        // this.nextSniSSL = selectedSni.get(); // Exemplo, se houver uma variável global para o SNI

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                
                
                
                /*
                
                
                
                case Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL:
    TLSSocketFactory.TLSv3 = mConfig.tls_version();
    String customSNI = mCustomSNI;
    if (customSNI != null && customSNI.isEmpty()) {
        customSNI = null;
    }
    try {
        // Obter listas
        String[] payloadKeys = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs = mConfig.getPrivString(Settings.SERVIDOR_KEY).split("#");
        String[] proxyPortas = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String[] sniList = mConfig.getPrivString(SettingsConstants.CUSTOM_SNI).split("#");
        String payloadName = mConfig.getPrivString(Settings.PAY_NAME);

        // Resolve domínios para IPs
        String[] resolvedSshServer = new String[proxyIPs.length];
        for (int i = 0; i < proxyIPs.length; i++) {
            try {
                resolvedSshServer[i] = VpnUtils.resolveDomainToIp(proxyIPs[i]);
            } catch (UnknownHostException e) {
                resolvedSshServer[i] = null;
            }
        }
        String[] resolvedSshni = new String[sniList.length];
        for (int i = 0; i < sniList.length; i++) {
            try {
                resolvedSshni[i] = VpnUtils.resolveDomainToIp(sniList[i]);
            } catch (UnknownHostException e) {
                resolvedSshni[i] = null;
            }
        }

        // Cria pool de threads
        int maxThreads = 20; // ajuste se necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean connected = new AtomicBoolean(false);

        // Logs iniciais
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Servidores: " + proxyIPs.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de SNIs: " + sniList.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + proxyPortas.length + "</strong>");
        SkStatus.logInfo(""); // linha em branco
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo(""); // linha em branco

        for (int SSLPayloadIndex = 0; SSLPayloadIndex < payloadKeys.length; SSLPayloadIndex++) {
            String payloadKey = payloadKeys[SSLPayloadIndex];
            for (int SSLProxyIndex = 0; SSLProxyIndex < proxyIPs.length; SSLProxyIndex++) {
                for (int SSLPortIndex = 0; SSLPortIndex < proxyPortas.length; SSLPortIndex++) {
                    for (int SSLSniIndex = 0; SSLSniIndex < sniList.length; SSLSniIndex++) {
                        final int finalPayloadIndex = SSLPayloadIndex;
                        final int finalProxyIndex = SSLProxyIndex;
                        final int finalPortIndex = SSLPortIndex;
                        final int finalSniIndex = SSLSniIndex;

                        final String servidor = (resolvedSshServer[SSLProxyIndex] != null && !resolvedSshServer[SSLProxyIndex].isEmpty())
                                ? resolvedSshServer[SSLProxyIndex]
                                : proxyIPs[SSLProxyIndex];
                        final String porta = proxyPortas[SSLPortIndex];
                        final String selectedSni = (resolvedSshni[SSLSniIndex] != null && !resolvedSshni[SSLSniIndex].isEmpty())
                                ? resolvedSshni[SSLSniIndex]
                                : sniList[SSLSniIndex];
                        final String finalPayload = payloadKey;

                        this.nextProxyIPDirect = servidor;
                        this.nextPayloadKey = finalPayload;

                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            try {

                                SSLTunnelProxy sslTypeData = new SSLTunnelProxy(servidor, Integer.parseInt(porta), selectedSni);

                                if (!isProteger && conn != null) {
                                    conn.setProxyData(sslTypeData);
                                }
                            } catch (Exception e) {
                                SkStatus.logError("Erro ao injetar SSL: " + e.getMessage());
                            }
                        }, executor);

                        futures.add(future);
                    }
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                */
                
                
                
                
                
                
                
                
                
                /*
                //metodo atual 06 06 25
                
                case Settings.bTUNNEL_TYPE_PAY_SSL:
    if (mConfig.tls_version()) {
						TLSSocketFactory.TLSv3 = true;
					} else {
						TLSSocketFactory.TLSv3 = false;
					}
					String customSNI3 = mCustomSNI;
					if (customSNI3 != null && customSNI3.isEmpty()) {
						customSNI3 = null;
					}
					String customPayload3 = mCustomPayload;
					if (customPayload3 != null && customPayload3.isEmpty()) {
						customPayload3= null;
					}
    try {
        // Obter listas de payloads, proxies e portas
        String[] payloadKeys3 = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs3 = mConfig.getPrivString("proxyTlsRemoto").split("#");
        String[] proxyPortas3 = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String[] sniList3 = mConfig.getPrivString(SettingsConstants.CUSTOM_SNI).split("#");
        String sshServer = mConfig.getPrivString(Settings.SERVIDOR_KEY);            
        String payloadName = mConfig.getPrivString(Settings.PAY_NAME);
        String[] resolvedSshServer = new String[proxyIPs3.length];
        for (int i = 0; i < proxyIPs3.length; i++) {
            try {
                resolvedSshServer[i] = VpnUtils.resolveDomainToIp(proxyIPs3[i]);
            } catch (UnknownHostException e) {
                resolvedSshServer[i] = null;
            }
        }
        // Verificações de índice
        if (TlwsProxyIndex >= proxyIPs3.length) TlwsProxyIndex = 0;
        if (TlwsPortIndex >= proxyPortas3.length) TlwsPortIndex = 0;
        if (TlwsPayloadIndex >= payloadKeys3.length) TlwsPayloadIndex = 0;
        if (TlwsSniIndex >= sniList3.length) TlwsSniIndex = 0;
        // Tenta usar o IP resolvido ou o IP original
        String servidor3 = (resolvedSshServer[TlwsProxyIndex] != null && !resolvedSshServer[TlwsProxyIndex].isEmpty())
            ? resolvedSshServer[TlwsProxyIndex] 
            : proxyIPs3[TlwsProxyIndex];
        int porta3 = Integer.parseInt(proxyPortas3[TlwsPortIndex]);
        String nextPayloadKey3 = payloadKeys3[TlwsPayloadIndex];
        String selectedSni = sniList3[TlwsSniIndex];            
        this.nextProxyIPDirect = servidor3;
        this.nextPayloadKey = nextPayloadKey3;
        SSLProxy sslTun = new SSLProxy(servidor3, porta3, selectedSni, sshServer, nextPayloadKey, false, this.mContext);
    conn.setProxyData(sslTun);            
        if (this.nextPayloadKey != null) {
            //SkStatus.logInfo("Conectando na Payload: " + (TlwsPayloadIndex + 1) + nextPayloadKey);        
            SkStatus.logInfo("Conectando na Payload: " + (TlwsPayloadIndex + 1));              
        } else {
            //SkStatus.logInfo("nextPayloadKey é nulo");
        }
        SkStatus.logInfo("Conectando na SNI: " + (TlwsSniIndex + 1));
        SkStatus.logInfo("Conectando no TlsIP: " + (TlwsProxyIndex + 1));             
        SkStatus.logInfo("Conectando na Porta: " + (TlwsPortIndex + 1));
        SkStatus.logInfo("Config Selecionada: " + payloadName);
        // Rotação
        TlwsPortIndex = (TlwsPortIndex + 1) % proxyPortas3.length;
        if (TlwsPortIndex == 0) {
            TlwsProxyIndex = (TlwsProxyIndex + 1) % proxyIPs3.length;
            if (TlwsProxyIndex == 0) {
                TlwsSniIndex = (TlwsSniIndex + 1) % sniList3.length;
                if (TlwsSniIndex == 0) {
                    TlwsPayloadIndex = (TlwsPayloadIndex + 1) % payloadKeys3.length;
                }
            }
        }

        if (!isProteger) {
            conn.setProxyData(sslTun);
        }

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                
                */
                
                
                
                
                
                
                case Settings.bTUNNEL_TYPE_PAY_SSL:
    if (mConfig.tls_version()) {
                        TLSSocketFactory.TLSv3 = true;
                    } else {
                        TLSSocketFactory.TLSv3 = false;
                    }
                    String customSNI3 = mCustomSNI;
                    if (customSNI3 != null && customSNI3.isEmpty()) {
                        customSNI3 = null;
                    }
                    String customPayload3 = mCustomPayload;
                    if (customPayload3 != null && customPayload3.isEmpty()) {
                        customPayload3= null;
                    }

    try {
        // Obter listas de payloads, proxies, portas e SNIs
        String[] payloadKeys3 = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs3 = mConfig.getPrivString("proxyTlsRemoto").split("#");
        String[] proxyPortas3 = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String[] sniList3 = mConfig.getPrivString(SettingsConstants.CUSTOM_SNI).split("#");
        String sshServer = mConfig.getPrivString(Settings.SERVIDOR_KEY);
        String payloadName = mConfig.getPrivString(Settings.PAY_NAME);

        // Resolver domínios para IPs
        String[] resolvedProxyIPs3 = new String[proxyIPs3.length];
        for (int i = 0; i < proxyIPs3.length; i++) {
            try {
                resolvedProxyIPs3[i] = VpnUtils.resolveDomainToIp(proxyIPs3[i]);
            } catch (UnknownHostException e) {
                resolvedProxyIPs3[i] = null;
            }
        }

        // Logs iniciais
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys3.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de TlsIP: " + proxyIPs3.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de SNIs: " + sniList3.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + proxyPortas3.length + "</strong>");
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo("");

        // Multi-thread: executor
        int maxThreads = 20; // ajuste conforme necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicReference<String> selectedServidorSSL = new AtomicReference<>(); // Para armazenar o IP selecionado
        AtomicReference<String> selectedPayloadKeySSL = new AtomicReference<>(); // Para armazenar a payload key selecionada
        AtomicReference<String> selectedSniSSL = new AtomicReference<>(); // Para armazenar o SNI selecionado

        // Para cada combinação
        for (int TlwsPayloadIndex = 0; TlwsPayloadIndex < payloadKeys3.length; TlwsPayloadIndex++) {
            String payloadKey = payloadKeys3[TlwsPayloadIndex];
            for (int TlwsProxyIndex = 0; TlwsProxyIndex < proxyIPs3.length; TlwsProxyIndex++) {
                for (int TlwsPortIndex = 0; TlwsPortIndex < proxyPortas3.length; TlwsPortIndex++) {
                    for (int TlwsSniIndex = 0; TlwsSniIndex < sniList3.length; TlwsSniIndex++) {
                        final String servidor3 = (resolvedProxyIPs3[TlwsProxyIndex] != null && !resolvedProxyIPs3[TlwsProxyIndex].isEmpty())
                                ? resolvedProxyIPs3[TlwsProxyIndex]
                                : proxyIPs3[TlwsProxyIndex];

                        final String porta = proxyPortas3[TlwsPortIndex];

                        String selectedSni = sniList3[TlwsSniIndex];

                        final String finalPayload = payloadKey;

                        // Multi-thread task
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            try {
                                SSLProxy sslTun = new SSLProxy(servidor3, Integer.parseInt(porta), selectedSni, sshServer, finalPayload, false, this.mContext);

                                // Atualiza o IP, a payload key e o SNI selecionados
                                selectedServidorSSL.set(servidor3);
                                selectedPayloadKeySSL.set(finalPayload);
                                selectedSniSSL.set(selectedSni);

                                if (!isProteger && conn != null) {
                                    conn.setProxyData(sslTun);
                                }

                            } catch (Exception e) {
                                SkStatus.logError("Erro ao injetar SSL: " + e.getMessage());
                            }
                        }, executor);

                        futures.add(future);
                    }
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // Após todas as tentativas, você pode definir nextProxyIPDirect, nextPayloadKey e nextSniSSL
        this.nextProxyIPDirect = selectedServidorSSL.get(); // Atualiza o nextProxyIPDirect com o último IP selecionado
        this.nextPayloadKey = selectedPayloadKeySSL.get(); // Atualiza o nextPayloadKey com a última payload key selecionada
        // Se você tiver uma variável para o SNI selecionado, atualize-a aqui também
        // this.nextSniSSL = selectedSniSSL.get();

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
                break;
                
                
                
                /*
                
                case Settings.bTUNNEL_TYPE_PAY_SSL:
    if (mConfig.tls_version()) {
						TLSSocketFactory.TLSv3 = true;
					} else {
						TLSSocketFactory.TLSv3 = false;
					}
					String customSNI3 = mCustomSNI;
					if (customSNI3 != null && customSNI3.isEmpty()) {
						customSNI3 = null;
					}
					String customPayload3 = mCustomPayload;
					if (customPayload3 != null && customPayload3.isEmpty()) {
						customPayload3= null;
					}

    try {
        // Obter listas de payloads, proxies, portas e SNIs
        String[] payloadKeys3 = mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).split("@@@");
        String[] proxyIPs3 = mConfig.getPrivString("proxyTlsRemoto").split("#");
        String[] proxyPortas3 = mConfig.getPrivString(Settings.PROXY_PORTA_KEY).split("#");
        String[] sniList3 = mConfig.getPrivString(SettingsConstants.CUSTOM_SNI).split("#");
        String sshServer = mConfig.getPrivString(Settings.SERVIDOR_KEY);
        String payloadName = mConfig.getPrivString(Settings.PAY_NAME);

        // Resolver domínios para IPs
        String[] resolvedSshServer = new String[proxyIPs3.length];
        for (int i = 0; i < proxyIPs3.length; i++) {
            try {
                resolvedSshServer[i] = VpnUtils.resolveDomainToIp(proxyIPs3[i]);
            } catch (UnknownHostException e) {
                resolvedSshServer[i] = null;
            }
        }
        

        // Logs iniciais
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Payloads: " + payloadKeys3.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de TlsIP: " + proxyIPs3.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de SNIs: " + sniList3.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Portas: " + proxyPortas3.length + "</strong>");
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo("");

        // Multi-thread: executor
        int maxThreads = 20; // ajuste conforme necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Para cada combinação
        for (int TlwsPayloadIndex = 0; TlwsPayloadIndex < payloadKeys3.length; TlwsPayloadIndex++) {
            String payloadKey = payloadKeys3[TlwsPayloadIndex];
            for (int TlwsProxyIndex = 0; TlwsProxyIndex < proxyIPs3.length; TlwsProxyIndex++) {
                for (int TlwsPortIndex = 0; TlwsPortIndex < proxyPortas3.length; TlwsPortIndex++) {
                    for (int TlwsSniIndex = 0; TlwsSniIndex < sniList3.length; TlwsSniIndex++) {
                        final int finalPayloadIndex = TlwsPayloadIndex;
                        final int finalProxyIndex = TlwsProxyIndex;
                        final int finalPortIndex = TlwsPortIndex;
                        final int finalSniIndex = TlwsSniIndex;

                        final String servidor3 = (resolvedSshServer[TlwsProxyIndex] != null && !resolvedSshServer[TlwsProxyIndex].isEmpty())
                                ? resolvedSshServer[TlwsProxyIndex]
                                : proxyIPs3[TlwsProxyIndex];
                                
                        final String porta = proxyPortas3[TlwsPortIndex];
                        
                        String selectedSni = sniList3[TlwsSniIndex];
                                
                        final String finalPayload = payloadKey;
                                    
                                    this.nextProxyIPDirect = servidor3;
                                this.nextPayloadKey = finalPayload;

                        // Multi-thread task
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            try {
                                // Ajusta variáveis globais para o log
                                

                                

                                

                                SSLProxy sslTun = new SSLProxy(servidor3, Integer.parseInt(porta), selectedSni, sshServer, finalPayload, false, this.mContext);
                                if (!isProteger && conn != null) {
                                    conn.setProxyData(sslTun);
                                }

                            } catch (Exception e) {
                                SkStatus.logError("Erro ao injetar SSL: " + e.getMessage());
                            }
                        }, executor);

                        futures.add(future);
                    }
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                
                */
                
                
                /*
                //metodo atual 06 06 25

                case Settings.bTUNNEL_TYPE_SLOWDNS:
    String customPayload6 = mCustomPayload;
    if (customPayload6 != null && customPayload6.isEmpty()) {
        customPayload6 = null;
    }
    try {
        // Obter listas de configuração
        String[] nameserverList = mConfig.getPrivString(SettingsConstants.NAMESERVER_IP_KEY).split("#");
        String[] chaveList = mConfig.getPrivString(Settings.CHAVE_KEY).split("#");
        String[] dnsList = mConfig.getPrivString(SettingsConstants.DNS_IP_KEY).split("#");
        String payloadName3 = mConfig.getPrivString(Settings.PAY_NAME);
        // Verificações de segurança dos índices
        if (currentChaveIndex >= chaveList.length) currentChaveIndex = 0;
        if (currentDnsIndex >= dnsList.length) currentDnsIndex = 0;
        if (currentNameServerIndex >= nameserverList.length) currentNameServerIndex = 0;
        // Usa os valores atuais ANTES de rotacionar
        String chave = chaveList[currentChaveIndex];
        String dns = dnsList[currentDnsIndex];
        String nameserver = nameserverList[currentNameServerIndex];
        // Define os valores
        this.nextProxyIP = chave;
        this.nextPayloadKey = nameserver;
        this.nextProxyIPDirect = dns;
        // Finaliza thread anterior, se houver
        if (mDnsThread != null && mDnsThread.isAlive()) {
            mDnsThread.interrupt();
            mDnsThread = null;
        }
        // Inicia nova thread DNS
        mDnsThread = new DNSTunnelThread(mContext, dns, chave, nameserver);
        mDnsThread.start();
        SkStatus.logInfo("Conectando no NameServer: " + (currentNameServerIndex + 1));
        SkStatus.logInfo("Conectando na Chave: " + (currentChaveIndex + 1));
        SkStatus.logInfo("Conectando no DNS: " + (currentDnsIndex + 1));
        SkStatus.logInfo("Config Selecionada: " + payloadName3);            
        // Agora rotaciona para a próxima tentativa
        currentDnsIndex = (currentDnsIndex + 1) % dnsList.length;
        if (currentDnsIndex == 0) {
            currentChaveIndex = (currentChaveIndex + 1) % chaveList.length;
        }
        if (currentChaveIndex == 0) {
            currentNameServerIndex = (currentNameServerIndex + 1) % nameserverList.length;
        }
    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                */
                
                
                
                
                case Settings.bTUNNEL_TYPE_SLOWDNS:
    String customPayload6 = mCustomPayload;
    if (customPayload6 != null && customPayload6.isEmpty()) {
        customPayload6 = null;
    }
    try {
        // Obter listas
        String[] nameserverList = mConfig.getPrivString(SettingsConstants.NAMESERVER_IP_KEY).split("#");
        String[] chaveList = mConfig.getPrivString(Settings.CHAVE_KEY).split("#");
        String[] dnsList = mConfig.getPrivString(SettingsConstants.DNS_IP_KEY).split("#");
        String payloadName3 = mConfig.getPrivString(Settings.PAY_NAME);

        // Log inicial
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName3 + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Nameservers: " + nameserverList.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Chaves: " + chaveList.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de DNS: " + dnsList.length + "</strong>");
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo("");

        // Multi-thread executor
        int maxThreads = 20; // ajuste conforme necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicReference<String> selectedNameserver = new AtomicReference<>(); // Para armazenar o nameserver selecionado
        AtomicReference<String> selectedChave = new AtomicReference<>(); // Para armazenar a chave selecionada
        AtomicReference<String> selectedDns = new AtomicReference<>(); // Para armazenar o DNS selecionado

        for (int nameServerIndex = 0; nameServerIndex < nameserverList.length; nameServerIndex++) {
            String nameserver = nameserverList[nameServerIndex];
            for (int chaveIndex = 0; chaveIndex < chaveList.length; chaveIndex++) {
                String chave = chaveList[chaveIndex];
                for (int dnsIndex = 0; dnsIndex < dnsList.length; dnsIndex++) {
                    String dns = dnsList[dnsIndex];

                    final String finalNameserver = nameserver;
                    final String finalChave = chave;
                    final String finalDns = dns;

                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            // Finaliza thread DNS antiga, se houver
                            if (mDnsThread != null && mDnsThread.isAlive()) {
                                mDnsThread.interrupt();
                                mDnsThread = null;
                            }

                            // Inicia nova thread DNS
                            mDnsThread = new DNSTunnelThread(mContext, finalDns, finalChave, finalNameserver);
                            mDnsThread.start();

                            // Atualiza os valores selecionados (exemplo: se a thread iniciar com sucesso)
                            selectedNameserver.set(finalNameserver);
                            selectedChave.set(finalChave);
                            selectedDns.set(finalDns);

                        } catch (Exception e) {
                            SkStatus.logError("Erro ao injetar DNS: " + e.getMessage());
                        }
                    }, executor);

                    futures.add(future);
                }
            }
        }

        // Espera todas as threads finalizarem
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // Após todas as tentativas, você pode definir as variáveis globais
        this.nextPayloadKey = selectedNameserver.get(); // nameserver
        this.nextProxyIP = selectedChave.get(); // chave
        this.nextProxyIPDirect = selectedDns.get(); // dns

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                
                
                
                
                /*
                
                case Settings.bTUNNEL_TYPE_SLOWDNS:
    String customPayload6 = mCustomPayload;
    if (customPayload6 != null && customPayload6.isEmpty()) {
        customPayload6 = null;
    }
    try {
        // Obter listas
        String[] nameserverList = mConfig.getPrivString(SettingsConstants.NAMESERVER_IP_KEY).split("#");
        String[] chaveList = mConfig.getPrivString(Settings.CHAVE_KEY).split("#");
        String[] dnsList = mConfig.getPrivString(SettingsConstants.DNS_IP_KEY).split("#");
        String payloadName3 = mConfig.getPrivString(Settings.PAY_NAME);

        // Log inicial
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Config Selecionada: " + payloadName3 + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Nameservers: " + nameserverList.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de Chaves: " + chaveList.length + "</strong>");
        SkStatus.logInfo("<strong>Quantidade de DNS: " + dnsList.length + "</strong>");
        SkStatus.logInfo("");
        SkStatus.logInfo("<strong>Iniciando rotação: aguarde...</strong>");
        SkStatus.logInfo("");

        // Multi-thread executor
        int maxThreads = 20; // ajuste conforme necessário
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int nameServerIndex = 0; nameServerIndex < nameserverList.length; nameServerIndex++) {
            String nameserver = nameserverList[nameServerIndex];
            for (int chaveIndex = 0; chaveIndex < chaveList.length; chaveIndex++) {
                String chave = chaveList[chaveIndex];
                for (int dnsIndex = 0; dnsIndex < dnsList.length; dnsIndex++) {
                    String dns = dnsList[dnsIndex];

                    final int finalNameServerIndex = nameServerIndex;
                    final int finalChaveIndex = chaveIndex;
                    final int finalDnsIndex = dnsIndex;
                                
                                this.nextProxyIP = chave;
                            this.nextPayloadKey = nameserver;
                            this.nextProxyIPDirect = dns;

                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            // Ajusta variáveis globais para o log
                           

                            // Usa os valores atuais
                            

                            // Finaliza thread DNS antiga, se houver
                            if (mDnsThread != null && mDnsThread.isAlive()) {
                                mDnsThread.interrupt();
                                mDnsThread = null;
                            }

                            // Inicia nova thread DNS
                            mDnsThread = new DNSTunnelThread(mContext, dns, chave, nameserver);
                            mDnsThread.start();

                            

                        } catch (Exception e) {
                            SkStatus.logError("Erro ao injetar DNS: " + e.getMessage());
                        }
                    }, executor);

                    futures.add(future);
                }
            }
        }

        // Espera todas as threads finalizarem
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
                
                */






                case Settings.bTUNNEL_TYPE_V2RAY:
    String customPayload7 = mCustomPayload;
    if (customPayload7 != null && customPayload7.isEmpty()) {
        customPayload7 = null;
    }
    try {
        // Inicia nova thread DNS
        V2Tunnel v2Tunnel = new V2Tunnel(mContext.getApplicationContext());
                v2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
    } catch (Exception e) {
        SkStatus.logError(R.string.error_proxy_invalid);
        proxyError = true;
        throw new Exception(mContext.getString(R.string.error_proxy_invalid));
    }
    break;
			}
		}
	}
    
    
    
				
	/**
	 * Socks5 Forwarder
	 */

	private DynamicPortForwarder dpf;

	private synchronized void startForwarderSocks(int portaLocal) throws Exception {
		if (!mConnected) {
			throw new Exception();
		}

	//	SkStatus.logInfo("starting socks local");
		SkStatus.logInfo(String.format("Socks Local : %d", portaLocal));
		
		try {

			int nThreads = mConfig.getMaximoThreadsSocks();

			if (nThreads > 0) {
				dpf = mConnection.createDynamicPortForwarder(portaLocal, nThreads);

				SkStatus.logDebug("socks local number threads: " + Integer.toString(nThreads));
			}
			else {
				dpf = mConnection.createDynamicPortForwarder(portaLocal);
			}

		} catch (Exception e) {
			SkStatus.logError("Socks Local: " + e.getCause().toString());

			throw new Exception();
		}
	}

	private synchronized void stopForwarderSocks() {
		if (dpf != null) {
			try {
				dpf.close(); 
			} catch(IOException e){}
			dpf = null;
		}
	}
    
    
    
    
    
    


	/**
	 * Pinger
	 */

	private Thread thPing;
	private long lastPingLatency = -1;
	
	private void startPinger(final int timePing) throws Exception {
		if (!mConnected) {
			throw new Exception();
		}

		SkStatus.logInfo("INICIANDO REDE PRIVADA...");

		thPing = new Thread() {
			@Override
			public void run() {
				while (mConnected) {
					try {
						makePinger();
					} catch(InterruptedException e) {
						break;
					}
				}
				//SkStatus.logDebug("pinger stopped");
			}
			
			private synchronized void makePinger() throws InterruptedException {
				try {
					if (mConnection != null) {
						long ping = mConnection.ping();
						if (lastPingLatency < 0) {
							lastPingLatency = ping;
						}
					}
					else throw new InterruptedException();
				} catch(Exception e) {
					Log.e(TAG, "ping error", e);
				}
				
				if (timePing == 0)
					return;

				if (timePing > 0)
					sleep(timePing*1000);
				else {
					//SkStatus.logError("ping invalid");
					throw new InterruptedException();
				}
			}
		};

		// inicia
		thPing.start();
	}
    
    
    
    

	private synchronized void stopPinger() {
		if (thPing != null && thPing.isAlive()) {
			//SkStatus.logInfo("stopping pinger");
			
			thPing.interrupt();
			thPing = null;
		}
	}
	
    
    
    
    
    
	/**
	 * Connection Monitor
	 */

	@Override
    public void connectionLost(Throwable reason) {
        if (mStarting || mStopping || mReconnecting) {
            return;
            
        }
        SkStatus.logError("<strong>" + mContext.getString(R.string.log_conection_lost) + "</strong>");
        //SkStatus.logError("SSH: " + (reason.getMessage().toString()));
        reconnectSSH();
    }
    
    
    /*
    public void connectionLost2(Throwable reason)
	{
		if (mStarting || mStopping || mReconnecting) {
			return;
		}
		
		SkStatus.logError("<strong>" + mContext.getString(R.string.log_conection_lost) + "</strong>");

		if (reason != null) {
			if (reason.getMessage().contains(
					"There was a problem during connect")) {
				return;
			} else if (reason.getMessage().contains(
						   "Closed due to user request")) {
				return;
			} else if (reason.getMessage().contains(
						   "The connect timeout expired")) {
				stopAll();
				return;
			}
		} else {
			stopAll();
			return;
		}
		
		reconnectSSH();
	}
    */
    
    
    
    
    /*
    
    // METODO ORIGINAL 15 04 25
    
	public void connectionLost(Throwable reason)
	{
		if (mStarting || mStopping || mReconnecting) {
			return;
		}
		
		SkStatus.logError("<strong>" + mContext.getString(R.string.log_conection_lost) + "</strong>");

		if (reason != null) {
			if (reason.getMessage().contains(
					"There was a problem during connect")) {
				return;
			} else if (reason.getMessage().contains(
						   "Closed due to user request")) {
				return;
			} else if (reason.getMessage().contains(
						   "The connect timeout expired")) {
				stopAll();
				return;
			}
		} else {
			stopAll();
			return;
		}
		
		reconnectSSH();
	}
    
    */
    
	
	public boolean mReconnecting = false;
	
    
    
    

private void attemptReconnect() {
    for (int i = 0; i < RECONNECT_TRIES; i++) {
        if (mStopping) {
            mReconnecting = false;
            return;
        }

        int sleepTime = 5;
        if (!TunnelUtils.isNetworkOnline(mContext)) {
            SkStatus.updateStateString(SkStatus.SSH_AGUARDANDO_REDE, "AGUARDANDO REDE..");
            SkStatus.logInfo(R.string.state_nonetwork);
        } else {
            sleepTime = 3;
            mStarting = true;
            SkStatus.updateStateString(SkStatus.SSH_RECONECTANDO, "RECONECTANDO..");

            SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_reconnecting) + "</strong>");

            try {
                startClienteSSH();

                mStarting = false;
                mReconnecting = false;
                return;
            } catch (Exception e) {
                SkStatus.logInfo("<strong><font color='red'>" + mContext.getString(R.string.state_disconnected) + "</font></strong>");
            }

            mStarting = false;
        }

        try {
            Thread.sleep(sleepTime * 1000);
            i--;
        } catch (InterruptedException e2) {
            mReconnecting = false;
            return;
        }
    }

    mReconnecting = false;
    stopAll();
}
    
    
    
    
    
    /*
    
    private void startNetworkMonitoring() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest networkRequest = new NetworkRequest.Builder().build();
            connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(Network network) {
                    connectionLost(null); // Chama o método de conexão perdida
                }

                @Override
                public void onAvailable(Network network) {
                    SkStatus.logInfo("<strong>Rede restaurada. Tentando reconectar...</strong>");
                    reconnectSSH(); // Tenta reconectar o V2Ray
                }
            });
        }
    }

    public void stopNetworkMonitoring() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(new ConnectivityManager.NetworkCallback());
            SkStatus.logInfo("Monitoramento de rede parado.");
        }
    }
    
    */
    
    
    
    
    
    
    /*
    
    //METODO SEM V2RAY 22 04 25
    
    public void reconnectSSH() {
    SkStatus.logInfo("");
    SkStatus.logInfo("RECONECTANDO EM 3 SEGUNDOS");
    SkStatus.logInfo("");
    
    if (!(this.mStarting || this.mStopping)) {
        if (!this.mReconnecting) {
            this.mReconnecting = true;
            this.lost = false;
            this.mcheck = false;
            closeSSH();
            String str = "RECONECTANDO";
            String str2 = "Reconnecting..";
            SkStatus.updateStateString(str, str2);
            this.mReconnecting = false;
            try {
                Thread.sleep(3000);

                while (!this.mStopping) {
                    int sleepTime = 5;
                    if (TunnelUtils.isNetworkOnline(this.mContext)) {
                        sleepTime = 3;
                        this.mStarting = true;
                        SkStatus.updateStateString(str, str2);
                        
                        SkStatus.logInfo("<strong><font color=\"#ff8c00\">" + this.mContext.getString(R.string.state_reconnecting) + "</font></strong>");
                        
                        try {
                            startClienteSSH();
                            this.mStarting = false;
                            this.mReconnecting = false;
                            return;
                        } catch (Exception e) {
                            SkStatus.logInfo("<strong><font color=\"red\">" + this.mContext.getString(R.string.state_disconnected) + "</font></strong>");
                            this.mStarting = false;
                        }
                    } else {
                        SkStatus.updateStateString("AGUARDANDO", "ESPERANDO POR REDES MOVÉIS ..");
                        SkStatus.logInfo(R.string.state_nonetwork, new Object[0]);
                    }

                    try {
                        Thread.sleep(sleepTime * 3000);
                    } catch (InterruptedException e2) {
                        this.mReconnecting = false;
                        return;
                    }
                }

                this.mReconnecting = false;
            } catch (InterruptedException e3) {
                this.mReconnecting = false;
            }
        }
    }
}
    
    */
    
    
    
    
    
    
    
    
    
    
    
    
    //METODO COM V2RAY 22 04 25
    
    public void reconnectSSH() {
    SkStatus.logInfo("");
    SkStatus.logInfo("RECONECTANDO EM 3 SEGUNDOS");
    SkStatus.logInfo("");
    
    if (!(this.mStarting || this.mStopping)) {
        if (!this.mReconnecting) {
            this.mReconnecting = true;
            this.lost = false;
            this.mcheck = false;
            closeSSH();
            String str = "RECONECTANDO";
            String str2 = "Reconnecting..";
            SkStatus.updateStateString(str, str2);
            this.mReconnecting = false;
            try {
                Thread.sleep(3000);
                    
                    SharedPreferences prefs = mConfig.getPrefsPrivate();
                int tunnelType2 = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
                    
                if (tunnelType2 == Settings.bTUNNEL_TYPE_V2RAY) {
                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                                    v2Tunnel = null;
                                    v2rayrunning = false;
                        }
                        
                while (!this.mStopping) {
                    int sleepTime = 5;
                    if (TunnelUtils.isNetworkOnline(this.mContext)) {
                        sleepTime = 3;
                        this.mStarting = true;
                        SkStatus.updateStateString(str, str2);
                        
                        SkStatus.logInfo("<strong><font color=\"#ff8c00\">" + this.mContext.getString(R.string.state_reconnecting) + "</font></strong>");
                        
                        try {
                                
                            if (isv2raymode()) {
                                if (v2rayrunning) {
                                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                                    v2Tunnel = null;
                                    v2rayrunning = false;
                                    connectivityManager.unregisterNetworkCallback(callback);
                                }
                                if (mConnected) {
                                    closeSSH();
                                }
                                mRunning = false;
                                mStarting = false;
                                mReconnecting = false;
                                
                                // Inicia o V2Ray novamente
                                    V2Tunnel v2Tunnel = new V2Tunnel(mContext.getApplicationContext());

        // Agora chame o método StartV2ray através da instância
        v2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                                    
                                v2Tunnel = new V2Tunnel(mContext);
                                //V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                                v2rayrunning = true;
                            }
                                
                                else {
                                // Caso contrário, inicie o cliente SSH normalmente
                                startClienteSSH();
                            }
                            
                            this.mStarting = false;
                            this.mReconnecting = false;
                            return;
                            
                        } catch (Exception e) {
                            SkStatus.logInfo("<strong><font color=\"red\">" + this.mContext.getString(R.string.state_disconnected) + "</font></strong>");
                            this.mStarting = false;
                        }
                    } else {
                        SkStatus.updateStateString("AGUARDANDO", "ESPERANDO POR REDES MOVÉIS ..");
                        SkStatus.logInfo(R.string.state_nonetwork, new Object[0]);
                    }
                    
                    try {
                        Thread.sleep(sleepTime * 3000);
                    } catch (InterruptedException e2) {
                        this.mReconnecting = false;
                        return;
                    }
                }
                
                this.mReconnecting = false;
            } catch (InterruptedException e3) {
                this.mReconnecting = false;
            }
        }
    }
}
    
    
    
    
    
    
    
    
    
    /*

    //METODO COM V2RAY 11 04 25
    
    public void reconnectSSH2() {
    SkStatus.logInfo("");
    SkStatus.logInfo("RECONECTANDO EM 3 SEGUNDOS");
    SkStatus.logInfo("");

    if (!(this.mStarting || this.mStopping)) {
        if (!this.mReconnecting) {
            this.mReconnecting = true;
            this.lost = false;
            this.mcheck = false;

            try {
                SharedPreferences prefs = mConfig.getPrefsPrivate();
                int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);

                // Se for SlowDNS, interrompe a thread
                    /*
                if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS && mDnsThread != null) {
                    SkStatus.logInfo("Tipo de túnel: SlowDNS. Interrompendo SlowDNS...");
                    mConfig.setBypass(false);
                    mDnsThread.interrupt();
                    mDnsThread = null;
                }
    

                closeSSH();
                SkStatus.updateStateString("RECONECTANDO", "Reconnecting..");

                Thread.sleep(3000);

                while (!this.mStopping) {
                    int sleepTime = 5;

                    if (TunnelUtils.isNetworkOnline(this.mContext)) {
                        sleepTime = 3;
                        this.mStarting = true;
                        SkStatus.updateStateString("RECONECTANDO", "Reconnecting..");

                        SkStatus.logInfo("<strong><font color=\"#ff8c00\">" +
                                this.mContext.getString(R.string.state_reconnecting) + "</font></strong>");

                        try {
                            if (isv2raymode()) {
                                if (v2rayrunning) {
                                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                                    v2Tunnel = null;
                                    v2rayrunning = false;
                                    connectivityManager.unregisterNetworkCallback(callback);
                                }

                                if (mConnected) {
                                    closeSSH();
                                }

                                mRunning = false;
                                mStarting = false;
                                mReconnecting = false;

                                //SkStatus.logInfo("Iniciando V2Ray novamente...");
                                v2Tunnel = new V2Tunnel(mContext);
                                v2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default",
                                        mConfig.getPrivString(Settings.V2RAY_JSON), null);
                                v2rayrunning = true;

                            } else {
                                // SSH padrão
                                //SkStatus.logInfo("Chamando startClienteSSH()");
                                startClienteSSH();
                            }

                            this.mStarting = false;
                            this.mReconnecting = false;
                            return;

                        } catch (Exception e) {
                            SkStatus.logInfo("<strong><font color=\"red\">" +
                                    this.mContext.getString(R.string.state_disconnected) + "</font></strong>");
                            //SkStatus.logInfo("Erro ao tentar reconectar: " + e.getMessage());
                            e.printStackTrace();
                            this.mStarting = false;
                        }

                    } else {
                        SkStatus.updateStateString("AGUARDANDO", "ESPERANDO POR REDES MÓVEIS ..");
                        SkStatus.logInfo(R.string.state_nonetwork, new Object[0]);
                    }

                    try {
                        Thread.sleep(sleepTime * 3000);
                    } catch (InterruptedException e2) {
                        this.mReconnecting = false;
                        return;
                    }
                }

                this.mReconnecting = false;

            } catch (InterruptedException e3) {
                this.mReconnecting = false;
            }
        }
    }
}
    */
    
    
    
    
    
    
    /*
    //METODO SEM V2RAY 11 04 25
    
    public void reconnectSSHSemv2() {
    SkStatus.logInfo("");
    SkStatus.logInfo("RECONECTANDO EM 3 SEGUNDOS");
    SkStatus.logInfo("");

    if (!(this.mStarting || this.mStopping)) {
        if (!this.mReconnecting) {
            this.mReconnecting = true;
            this.lost = false;
            this.mcheck = false;

            try {
                SharedPreferences prefs = mConfig.getPrefsPrivate();
                int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);

                // Se for SlowDNS, interrompe a thread
                if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS && mDnsThread != null) {
                    SkStatus.logInfo("Tipo de túnel: SlowDNS. Interrompendo SlowDNS...");
                    mConfig.setBypass(false);
                    mDnsThread.interrupt();
                    mDnsThread = null;
                }

                closeSSH();
                SkStatus.updateStateString("RECONECTANDO", "Reconnecting..");

                Thread.sleep(3000);

                while (!this.mStopping) {
                    int sleepTime = 5;

                    if (TunnelUtils.isNetworkOnline(this.mContext)) {
                        sleepTime = 3;
                        this.mStarting = true;
                        SkStatus.updateStateString("RECONECTANDO", "Reconnecting..");

                        SkStatus.logInfo("<strong><font color=\"#ff8c00\">" +
                                this.mContext.getString(R.string.state_reconnecting) + "</font></strong>");

                        try {
                            if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS) {
                                // Se for SlowDNS, inicia nova thread
                                mConfig.setBypass(true);
                                mDnsThread = new DNSTunnelThread(mContext);
                                mDnsThread.start();
                                startClienteSSH();

                            } else {
                                // SSH padrão
                                startClienteSSH();
                            }

                            this.mStarting = false;
                            this.mReconnecting = false;
                            return;

                        } catch (Exception e) {
                            SkStatus.logInfo("<strong><font color=\"red\">" +
                                    this.mContext.getString(R.string.state_disconnected) + "</font></strong>");
                            e.printStackTrace();
                            this.mStarting = false;
                        }

                    } else {
                        SkStatus.updateStateString("AGUARDANDO", "ESPERANDO POR REDES MÓVEIS ..");
                        SkStatus.logInfo(R.string.state_nonetwork, new Object[0]);
                    }

                    try {
                        Thread.sleep(sleepTime * 3000);
                    } catch (InterruptedException e2) {
                        this.mReconnecting = false;
                        return;
                    }
                }

                this.mReconnecting = false;

            } catch (InterruptedException e3) {
                this.mReconnecting = false;
            }
        }
    }
}
    
    */
    
    
    
    
    
    
    /* 
    //RECONNECT ORIGINAL ANTES DO COLOCAR O SLOWDNS
    
    public void reconnectSSHOriginal() {
    SkStatus.logInfo("");
    SkStatus.logInfo("RECONECTANDO EM 3 SEGUNDOS");
    SkStatus.logInfo("");
    
    if (!(this.mStarting || this.mStopping)) {
        if (!this.mReconnecting) {
            this.mReconnecting = true;
            this.lost = false;
            this.mcheck = false;
            closeSSH();
            String str = "RECONECTANDO";
            String str2 = "Reconnecting..";
            SkStatus.updateStateString(str, str2);
            this.mReconnecting = false;
            try {
                Thread.sleep(3000);
                    
                    /*
                if (this.mConfig.getAutoClearLog()) {
                    SkStatus.clearLog();
                } 
                
                while (!this.mStopping) {
                    int sleepTime = 5;
                    if (TunnelUtils.isNetworkOnline(this.mContext)) {
                        sleepTime = 3;
                        this.mStarting = true;
                        SkStatus.updateStateString(str, str2);
                        
                        SkStatus.logInfo("<strong><font color=\"#ff8c00\">" + this.mContext.getString(R.string.state_reconnecting) + "</font></strong>");
                        
                        try {
                            // Verifica se está no modo V2Ray e gerencia a reconexão V2Ray
                            if (isv2raymode()) {
                                if (v2rayrunning) {
                                    V2Tunnel.StopV2ray(mContext.getApplicationContext());
                                    v2Tunnel = null;
                                    v2rayrunning = false;
                                    connectivityManager.unregisterNetworkCallback(callback);
                                }
                                if (mConnected) {
                                    closeSSH();
                                }
                                mRunning = false;
                                mStarting = false;
                                mReconnecting = false;
                                
                                // Inicia o V2Ray novamente
                                    V2Tunnel v2Tunnel = new V2Tunnel(mContext.getApplicationContext());

        // Agora chame o método StartV2ray através da instância
        v2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                                    
                                v2Tunnel = new V2Tunnel(mContext);
                                //V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                                v2rayrunning = true;
                            } else {
                                // Caso contrário, inicie o cliente SSH normalmente
                                startClienteSSH();
                            }
                            
                            this.mStarting = false;
                            this.mReconnecting = false;
                            return;
                            
                        } catch (Exception e) {
                            SkStatus.logInfo("<strong><font color=\"red\">" + this.mContext.getString(R.string.state_disconnected) + "</font></strong>");
                            this.mStarting = false;
                        }
                    } else {
                        SkStatus.updateStateString("AGUARDANDO", "ESPERANDO POR REDES MOVÉIS ..");
                        SkStatus.logInfo(R.string.state_nonetwork, new Object[0]);
                    }
                    
                    try {
                        Thread.sleep(sleepTime * 3000);
                    } catch (InterruptedException e2) {
                        this.mReconnecting = false;
                        return;
                    }
                }
                
                this.mReconnecting = false;
            } catch (InterruptedException e3) {
                this.mReconnecting = false;
            }
        }
    }
}
    
*/
    
    
    
    
   
    /*
    //SEM V2RAY E TBM SEM SLOWDNS
    
    public void reconnectSSHBaseSemV2ray() {
    SkStatus.logInfo("");
    SkStatus.logInfo("RECONECTANDO EM 3 SEGUNDOS");
    SkStatus.logInfo("");
    
    if (!(this.mStarting || this.mStopping)) {
        if (!this.mReconnecting) {
            this.mReconnecting = true;
            this.lost = false;
            this.mcheck = false;
            closeSSH();
            String str = "RECONECTANDO";
            String str2 = "Reconnecting..";
            SkStatus.updateStateString(str, str2);
            this.mReconnecting = false;
            try {
                Thread.sleep(3000);
                if (this.mConfig.getAutoClearLog()) {
                    SkStatus.clearLog();
                }
                
                while (!this.mStopping) {
                    int sleepTime = 5;
                    if (TunnelUtils.isNetworkOnline(this.mContext)) {
                        sleepTime = 3;
                        this.mStarting = true;
                        SkStatus.updateStateString(str, str2);
                        
                        SkStatus.logInfo("<strong><font color=\"#ff8c00\">" + this.mContext.getString(R.string.state_reconnecting) + "</font></strong>");
                        
                        try {
                            // Inicia o cliente SSH normalmente
                            startClienteSSH();
                            
                            this.mStarting = false;
                            this.mReconnecting = false;
                            return;
                            
                        } catch (Exception e) {
                            SkStatus.logInfo("<strong><font color=\"red\">" + this.mContext.getString(R.string.state_disconnected) + "</font></strong>");
                            this.mStarting = false;
                        }
                    } else {
                        SkStatus.updateStateString("AGUARDANDO", "ESPERANDO POR REDES MOVÉIS ..");
                        SkStatus.logInfo(R.string.state_nonetwork, new Object[0]);
                    }
                    
                    try {
                        Thread.sleep(sleepTime * 3000);
                    } catch (InterruptedException e2) {
                        this.mReconnecting = false;
                        return;
                    }
                }
                
                this.mReconnecting = false;
            } catch (InterruptedException e3) {
                this.mReconnecting = false;
            }
        }
    }
}
    
    */
    
    
    
    
    
    
    
   

	@Override
	public void onReceiveInfo(int id, String msg) {
		if (id == SERVER_BANNER) {
			SkStatus.logInfo("<strong>" + mContext.getString(R.string.log_server_banner) + "</strong> " + msg);
		}
	}


	/**
	 * Debug Logger
	 */

	@Override
	public void log(int level, String className, String message)
	{
		SkStatus.logDebug(String.format("%s: %s", className, message));
	}
	

	/**
	 * Vpn Tunnel
	 */
	 
    
    
    
    public String resolveDomainToIp(String domain) {
        try {
            InetAddress inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (Exception e) {
            System.err.println("Erro ao resolver domínio para IP: " + e.getMessage());
            return null;
        }
    }
    
    
    
    
	String serverAddr;
    
    
    
    
    /*
    
    protected void startTunnelVpnServiceIPV6() throws IOException {
    String servidorIP;
    if (!mConnected) {
        throw new IOException();
    }

    SkStatus.logInfo("INICIANDO SERVIÇO DE TUNNEL");
    SharedPreferences prefs = mConfig.getPrefsPrivate();

    // Broadcast
    IntentFilter broadcastFilter =
            new IntentFilter(TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST);
    broadcastFilter.addAction(TunnelVpnService.TUNNEL_VPN_START_BROADCAST);
    // Inicia Broadcast
    LocalBroadcastManager.getInstance(mContext)
            .registerReceiver(m_vpnTunnelBroadcastReceiver, broadcastFilter);

    String m_socksServerAddress = String.format("127.0.0.1:%s", mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
    boolean m_dnsForward = mConfig.getVpnDnsForward();
    String m_udpResolver = mConfig.getVpnUdpForward() ? mConfig.getVpnUdpResolver() : null;

    // Verifica se o tipo de túnel é SSH_SSLTUNNEL
    if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL) {
        servidorIP = nextProxyIPDirect;  // Usa o servidor já rotacionado para SSH_SSLTUNNEL
    } else if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY) {
        try {
            servidorIP = nextProxyIP;  // Se for SSH_PROXY, usa a variável de proxy
        } catch (Exception e) {
            SkStatus.logError(R.string.error_proxy_invalid);
            throw new IOException(mContext.getString(R.string.error_proxy_invalid));
        }
    } else {
        // Caso padrão, usa a configuração original do servidor
        servidorIP = mConfig.getPrivString(Settings.SERVIDOR_KEY);
    }

    try {
        InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP);
        servidorIP = servidorAddr.getHostAddress();  // Obtém o endereço IP do servidor
    } catch (UnknownHostException e) {
        throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
    }

    String[] m_excludeIps = {servidorIP};

    String[] m_dnsResolvers = null;
    if (m_dnsForward) {
        if (mConfig.getVpnDnsResolver2().isEmpty()) {
            m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1()};
        } else {
            m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1(), mConfig.getVpnDnsResolver2()};
        }
    } else {
        List<String> lista = VpnUtils.getNetworkDnsServer(mContext);
        m_dnsResolvers = new String[]{lista.get(0)};
    }

    if (isServiceVpnRunning()) {
        Log.d(TAG, "already running service");

        TunnelVpnManager tunnelManager = TunnelState.getTunnelState()
                .getTunnelManager();

        if (tunnelManager != null) {
            tunnelManager.restartTunnel(m_socksServerAddress);
        }

        return;
    }

    Intent startTunnelVpn = new Intent(mContext, TunnelVpnService.class);
    startTunnelVpn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    TunnelVpnSettings settings = new TunnelVpnSettings(m_socksServerAddress, m_dnsForward, m_dnsResolvers,
                                                       (m_dnsForward && m_udpResolver == null || !m_dnsForward && m_udpResolver != null), m_udpResolver, m_excludeIps,
                                                       mConfig.getIsFilterApps(), mConfig.getIsFilterBypassMode(), mConfig.getFilterApps(), mConfig.getIsTetheringSubnet(), mConfig.getBypass());
    startTunnelVpn.putExtra(TunnelVpnManager.VPN_SETTINGS, settings);

    if (mContext.startService(startTunnelVpn) == null) {
        SkStatus.logInfo("<strong><font color=\"red\">FALHA EM INICIAR O SERVIÇO DE TUNNEL VPN</strong>");
        throw new IOException("Vpn Service failed to start");
    }

    TunnelState.getTunnelState().setStartingTunnelManager();
}
    
    */
    
    
    
    
    
    
    
    
    
    
    
    
    /*
    
    protected void startTunnelVpnServiceQuse() throws IOException {
        String servidorIP;
		if (!mConnected) {
			throw new IOException();
		}

		SkStatus.logInfo("INICIANDO SERVIÇO DE TUNNEL");
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		// Broadcast
		IntentFilter broadcastFilter =
				new IntentFilter(TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST);
		broadcastFilter.addAction(TunnelVpnService.TUNNEL_VPN_START_BROADCAST);
		// Inicia Broadcast
		LocalBroadcastManager.getInstance(mContext)
				.registerReceiver(m_vpnTunnelBroadcastReceiver, broadcastFilter);

		String m_socksServerAddress = String.format("127.0.0.1:%s", mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
		boolean m_dnsForward = mConfig.getVpnDnsForward();
		String m_udpResolver = mConfig.getVpnUdpForward() ? mConfig.getVpnUdpResolver() : null;

		String servidorIP2 = nextProxyIPDirect;
        //String servidorIP = mConfig.getPrivString(Settings.SERVIDOR_KEY);

		if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY) {
			try {
				servidorIP2 = nextProxyIP;
			} catch (Exception e) {
				SkStatus.logError(R.string.error_proxy_invalid);

				throw new IOException(mContext.getString(R.string.error_proxy_invalid));
			}
		}
        
        

        
        
        
        try {
    String servidorIP3 = mConfig.getPrivString(Settings.SERVIDOR_KEY); // pega o IP ou domínio da config
    InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP3);
    serverAddr = servidorIP = servidorAddr.getHostAddress();

} catch (UnknownHostException e) {
    throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
}
        
        
       

		String[] m_excludeIps = {servidorIP};

		String[] m_dnsResolvers = null;
		if (m_dnsForward) {
            if(mConfig.getVpnDnsResolver2().isEmpty()){
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1()};
            }else{
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1(),mConfig.getVpnDnsResolver2()};
            }
        }
        else {
            List<String> lista = VpnUtils.getNetworkDnsServer(mContext);
            m_dnsResolvers = new String[]{lista.get(0)};
        }

		if (isServiceVpnRunning()) {
			Log.d(TAG, "already running service");

			TunnelVpnManager tunnelManager = TunnelState.getTunnelState()
					.getTunnelManager();

			if (tunnelManager != null) {
				tunnelManager.restartTunnel(m_socksServerAddress);
			}

			return;
		}

		Intent startTunnelVpn = new Intent(mContext, TunnelVpnService.class);
		startTunnelVpn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TunnelVpnSettings settings = new TunnelVpnSettings(m_socksServerAddress, m_dnsForward, m_dnsResolvers,
														   (m_dnsForward && m_udpResolver == null || !m_dnsForward && m_udpResolver != null), m_udpResolver, m_excludeIps,
														   mConfig.getIsFilterApps(), mConfig.getIsFilterBypassMode(), mConfig.getFilterApps(), mConfig.getIsTetheringSubnet(), mConfig.getBypass());
		startTunnelVpn.putExtra(TunnelVpnManager.VPN_SETTINGS, settings);

		if (mContext.startService(startTunnelVpn) == null) {
			//SkStatus.logInfo("failed to start tunnel vpn service");
            SkStatus.logInfo("<strong><font color=\"red\">FALHA EM INICIAR O SERVIÇO DE TUNNEL VPN</strong>");

			throw new IOException("Vpn Service failed to start");
		}
        
        

		TunnelState.getTunnelState().setStartingTunnelManager();
	}
    
    
    
    */
    
    
    
    
    
    
    
    
    /*
    
    //METODO SEM V2RAY 22 04 25
    
    protected void startTunnelVpnService() throws IOException {
        String servidorIP;
		if (!mConnected) {
			throw new IOException();
		}

		SkStatus.logInfo("INICIANDO SERVIÇO DE TUNNEL");
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		// Broadcast
		IntentFilter broadcastFilter =
				new IntentFilter(TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST);
		broadcastFilter.addAction(TunnelVpnService.TUNNEL_VPN_START_BROADCAST);
		// Inicia Broadcast
		LocalBroadcastManager.getInstance(mContext)
				.registerReceiver(m_vpnTunnelBroadcastReceiver, broadcastFilter);

		String m_socksServerAddress = String.format("127.0.0.1:%s", mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
		boolean m_dnsForward = mConfig.getVpnDnsForward();
		String m_udpResolver = mConfig.getVpnUdpForward() ? mConfig.getVpnUdpResolver() : null;

		String servidorIP2 = nextProxyIPDirect;
        //String servidorIP = mConfig.getPrivString(Settings.SERVIDOR_KEY);

		if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY) {
			try {
				servidorIP2 = nextProxyIP;
			} catch (Exception e) {
				SkStatus.logError(R.string.error_proxy_invalid);

				throw new IOException(mContext.getString(R.string.error_proxy_invalid));
			}
		}
        
        

		try {
			InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP2);
			serverAddr = servidorIP = servidorAddr.getHostAddress();
		} catch (UnknownHostException e) {
			throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
		}

		String[] m_excludeIps = {servidorIP};

		String[] m_dnsResolvers = null;
		if (m_dnsForward) {
            if(mConfig.getVpnDnsResolver2().isEmpty()){
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1()};
            }else{
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1(),mConfig.getVpnDnsResolver2()};
            }
        }
        else {
            List<String> lista = VpnUtils.getNetworkDnsServer(mContext);
            m_dnsResolvers = new String[]{lista.get(0)};
        }

		if (isServiceVpnRunning()) {
			Log.d(TAG, "already running service");

			TunnelVpnManager tunnelManager = TunnelState.getTunnelState()
					.getTunnelManager();

			if (tunnelManager != null) {
				tunnelManager.restartTunnel(m_socksServerAddress);
			}

			return;
		}

		Intent startTunnelVpn = new Intent(mContext, TunnelVpnService.class);
		startTunnelVpn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TunnelVpnSettings settings = new TunnelVpnSettings(m_socksServerAddress, m_dnsForward, m_dnsResolvers,
														   (m_dnsForward && m_udpResolver == null || !m_dnsForward && m_udpResolver != null), m_udpResolver, m_excludeIps,
														   mConfig.getIsFilterApps(), mConfig.getIsFilterBypassMode(), mConfig.getFilterApps(), mConfig.getIsTetheringSubnet(), mConfig.getBypass());
		startTunnelVpn.putExtra(TunnelVpnManager.VPN_SETTINGS, settings);

		if (mContext.startService(startTunnelVpn) == null) {
			//SkStatus.logInfo("failed to start tunnel vpn service");
            SkStatus.logInfo("<strong><font color=\"red\">FALHA EM INICIAR O SERVIÇO DE TUNNEL VPN</strong>");

			throw new IOException("Vpn Service failed to start");
		}
        
        

		TunnelState.getTunnelState().setStartingTunnelManager();
	}
    
    */
    
    
    
    
    
    
    
    
    
    //METODO COM V2RAY 22 04 25
    
    protected void startTunnelVpnService() throws IOException {
        String servidorIP;
		if (!mConnected) {
			throw new IOException();
		}

		SkStatus.logInfo("INICIANDO SERVIÇO DE TUNNEL");
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		// Broadcast
		IntentFilter broadcastFilter =
				new IntentFilter(TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST);
		broadcastFilter.addAction(TunnelVpnService.TUNNEL_VPN_START_BROADCAST);
		// Inicia Broadcast
		LocalBroadcastManager.getInstance(mContext)
				.registerReceiver(m_vpnTunnelBroadcastReceiver, broadcastFilter);

		String m_socksServerAddress = String.format("127.0.0.1:%s", mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
		boolean m_dnsForward = mConfig.getVpnDnsForward();
		String m_udpResolver = mConfig.getVpnUdpForward() ? mConfig.getVpnUdpResolver() : null;

		String servidorIP2 = nextProxyIPDirect;
        //String servidorIP = mConfig.getPrivString(Settings.SERVIDOR_KEY);

		if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY) {
			try {
				servidorIP2 = nextProxyIP;
			} catch (Exception e) {
				SkStatus.logError(R.string.error_proxy_invalid);

				throw new IOException(mContext.getString(R.string.error_proxy_invalid));
			}
		}
        
        
        
        
        
        if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_V2RAY) {
            return;
            
            /*
    V2Tunnel v2Tunnel = new V2Tunnel(mContext.getApplicationContext());
        // Agora chame o método StartV2ray através da instância
        v2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                                v2Tunnel = new V2Tunnel(mContext);
                                //V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                                v2rayrunning = true;
            */
}
        
    
    
    
    
        
        
        /*
        if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SLOWDNS) {
    String servidorIP3 = mConfig.getPrivString(Settings.SERVIDOR_KEY);

    if (servidorIP3 == null || servidorIP3.isEmpty()) {
        SkStatus.logError(mContext.getString(R.string.error_proxy_invalid));
        throw new IOException(mContext.getString(R.string.error_proxy_invalid));
    }

    try {
        InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP3);
        servidorIP2 = servidorAddr.getHostAddress(); // aqui sim usamos como valor final
    } catch (UnknownHostException e) {
        throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
    }
}
        
    */
    
    
        
        

		try {
			InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP2);
			serverAddr = servidorIP = servidorAddr.getHostAddress();
		} catch (UnknownHostException e) {
			throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
		}

		String[] m_excludeIps = {servidorIP};

		String[] m_dnsResolvers = null;
		if (m_dnsForward) {
            if(mConfig.getVpnDnsResolver2().isEmpty()){
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1()};
            }else{
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1(),mConfig.getVpnDnsResolver2()};
            }
        }
        else {
            List<String> lista = VpnUtils.getNetworkDnsServer(mContext);
            m_dnsResolvers = new String[]{lista.get(0)};
        }

		if (isServiceVpnRunning()) {
			Log.d(TAG, "already running service");

			TunnelVpnManager tunnelManager = TunnelState.getTunnelState()
					.getTunnelManager();

			if (tunnelManager != null) {
				tunnelManager.restartTunnel(m_socksServerAddress);
			}

			return;
		}

		Intent startTunnelVpn = new Intent(mContext, TunnelVpnService.class);
		startTunnelVpn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TunnelVpnSettings settings = new TunnelVpnSettings(m_socksServerAddress, m_dnsForward, m_dnsResolvers,
														   (m_dnsForward && m_udpResolver == null || !m_dnsForward && m_udpResolver != null), m_udpResolver, m_excludeIps,
														   mConfig.getIsFilterApps(), mConfig.getIsFilterBypassMode(), mConfig.getFilterApps(), mConfig.getIsTetheringSubnet(), mConfig.getBypass());
		startTunnelVpn.putExtra(TunnelVpnManager.VPN_SETTINGS, settings);

		if (mContext.startService(startTunnelVpn) == null) {
			//SkStatus.logInfo("failed to start tunnel vpn service");
            SkStatus.logInfo("<strong><font color=\"red\">FALHA EM INICIAR O SERVIÇO DE TUNNEL VPN</strong>");

			throw new IOException("Vpn Service failed to start");
		}
        
        

		TunnelState.getTunnelState().setStartingTunnelManager();
	}
    
    
    
    
    
    
    /*
    protected void startTunnelVpnService() throws IOException {
        String servidorIP;
		if (!mConnected) {
			throw new IOException();
		}

		SkStatus.logInfo("INICIANDO SERVIÇO DE TUNNEL");
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		// Broadcast
		IntentFilter broadcastFilter =
				new IntentFilter(TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST);
		broadcastFilter.addAction(TunnelVpnService.TUNNEL_VPN_START_BROADCAST);
		// Inicia Broadcast
		LocalBroadcastManager.getInstance(mContext)
				.registerReceiver(m_vpnTunnelBroadcastReceiver, broadcastFilter);

		String m_socksServerAddress = String.format("127.0.0.1:%s", mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
		boolean m_dnsForward = mConfig.getVpnDnsForward();
		String m_udpResolver = mConfig.getVpnUdpForward() ? mConfig.getVpnUdpResolver() : null;

		String servidorIP2 = nextProxyIPDirect;
        //String servidorIP = mConfig.getPrivString(Settings.SERVIDOR_KEY);
        
      
        

		if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY) {
			try {
				servidorIP2 = nextProxyIP;
			} catch (Exception e) {
				SkStatus.logError(R.string.error_proxy_invalid);

				throw new IOException(mContext.getString(R.string.error_proxy_invalid));
			}
		}
        
        

		try {
			InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP2);
			serverAddr = servidorIP = servidorAddr.getHostAddress();
		} catch (UnknownHostException e) {
			throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
		}

		String[] m_excludeIps = {servidorIP};

		String[] m_dnsResolvers = null;
		if (m_dnsForward) {
            if(mConfig.getVpnDnsResolver2().isEmpty()){
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1()};
            }else{
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1(),mConfig.getVpnDnsResolver2()};
            }
        }
        else {
            List<String> lista = VpnUtils.getNetworkDnsServer(mContext);
            m_dnsResolvers = new String[]{lista.get(0)};
        }

		if (isServiceVpnRunning()) {
			Log.d(TAG, "already running service");

			TunnelVpnManager tunnelManager = TunnelState.getTunnelState()
					.getTunnelManager();

			if (tunnelManager != null) {
				tunnelManager.restartTunnel(m_socksServerAddress);
			}

			return;
		}

		Intent startTunnelVpn = new Intent(mContext, TunnelVpnService.class);
		startTunnelVpn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TunnelVpnSettings settings = new TunnelVpnSettings(m_socksServerAddress, m_dnsForward, m_dnsResolvers,
														   (m_dnsForward && m_udpResolver == null || !m_dnsForward && m_udpResolver != null), m_udpResolver, m_excludeIps,
														   mConfig.getIsFilterApps(), mConfig.getIsFilterBypassMode(), mConfig.getFilterApps(), mConfig.getIsTetheringSubnet(), mConfig.getBypass());
		startTunnelVpn.putExtra(TunnelVpnManager.VPN_SETTINGS, settings);

		if (mContext.startService(startTunnelVpn) == null) {
			//SkStatus.logInfo("failed to start tunnel vpn service");
            SkStatus.logInfo("<strong><font color=\"red\">FALHA EM INICIAR O SERVIÇO DE TUNNEL VPN</strong>");

			throw new IOException("Vpn Service failed to start");
		}
        
        

		TunnelState.getTunnelState().setStartingTunnelManager();
	}
    */
    
    
    
    
    //MERODO ORIGINAL ANTES DE COLOCAR O SLOWDNS
    
    /*
    
    protected void startTunnelVpnService() throws IOException {
        String servidorIP;
		if (!mConnected) {
			throw new IOException();
		}

		SkStatus.logInfo("INICIANDO SERVIÇO DE TUNNEL");
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		// Broadcast
		IntentFilter broadcastFilter =
				new IntentFilter(TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST);
		broadcastFilter.addAction(TunnelVpnService.TUNNEL_VPN_START_BROADCAST);
		// Inicia Broadcast
		LocalBroadcastManager.getInstance(mContext)
				.registerReceiver(m_vpnTunnelBroadcastReceiver, broadcastFilter);

		String m_socksServerAddress = String.format("127.0.0.1:%s", mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
		boolean m_dnsForward = mConfig.getVpnDnsForward();
		String m_udpResolver = mConfig.getVpnUdpForward() ? mConfig.getVpnUdpResolver() : null;

		String servidorIP2 = nextProxyIPDirect;
        //String servidorIP = mConfig.getPrivString(Settings.SERVIDOR_KEY);

		if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY) {
			try {
				servidorIP2 = nextProxyIP;
			} catch (Exception e) {
				SkStatus.logError(R.string.error_proxy_invalid);

				throw new IOException(mContext.getString(R.string.error_proxy_invalid));
			}
		}
        
        

		try {
			InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP2);
			serverAddr = servidorIP = servidorAddr.getHostAddress();
		} catch (UnknownHostException e) {
			throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
		}

		String[] m_excludeIps = {servidorIP};

		String[] m_dnsResolvers = null;
		if (m_dnsForward) {
            if(mConfig.getVpnDnsResolver2().isEmpty()){
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1()};
            }else{
                m_dnsResolvers = new String[]{mConfig.getVpnDnsResolver1(),mConfig.getVpnDnsResolver2()};
            }
        }
        else {
            List<String> lista = VpnUtils.getNetworkDnsServer(mContext);
            m_dnsResolvers = new String[]{lista.get(0)};
        }

		if (isServiceVpnRunning()) {
			Log.d(TAG, "already running service");

			TunnelVpnManager tunnelManager = TunnelState.getTunnelState()
					.getTunnelManager();

			if (tunnelManager != null) {
				tunnelManager.restartTunnel(m_socksServerAddress);
			}

			return;
		}

		Intent startTunnelVpn = new Intent(mContext, TunnelVpnService.class);
		startTunnelVpn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TunnelVpnSettings settings = new TunnelVpnSettings(m_socksServerAddress, m_dnsForward, m_dnsResolvers,
														   (m_dnsForward && m_udpResolver == null || !m_dnsForward && m_udpResolver != null), m_udpResolver, m_excludeIps,
														   mConfig.getIsFilterApps(), mConfig.getIsFilterBypassMode(), mConfig.getFilterApps(), mConfig.getIsTetheringSubnet(), mConfig.getBypass());
		startTunnelVpn.putExtra(TunnelVpnManager.VPN_SETTINGS, settings);

		if (mContext.startService(startTunnelVpn) == null) {
			//SkStatus.logInfo("failed to start tunnel vpn service");
            SkStatus.logInfo("<strong><font color=\"red\">FALHA EM INICIAR O SERVIÇO DE TUNNEL VPN</strong>");

			throw new IOException("Vpn Service failed to start");
		}
        
        

		TunnelState.getTunnelState().setStartingTunnelManager();
	}
    
    */
    
    
    
    
   
    

	public static boolean isServiceVpnRunning() {
		TunnelState tunnelState = TunnelState.getTunnelState();
		return tunnelState.getStartingTunnelManager() || tunnelState.getTunnelManager() != null;
	}
    
    

	protected synchronized void stopTunnelVpnService() {
		if (!isServiceVpnRunning()) {
			return;
		}
        
		SkStatus.logInfo("PARANDO SERVIÇO DE TÚNNEL");
		
		TunnelVpnManager currentTunnelManager = TunnelState.getTunnelState()
			.getTunnelManager();
		
		if (currentTunnelManager != null) {
			currentTunnelManager.signalStopService();
		}
		
		/*if (mThreadLocation != null && mThreadLocation.isAlive()) {
			mThreadLocation.interrupt();
		}
		mThreadLocation = null;*/

		// Parando Broadcast
		LocalBroadcastManager.getInstance(mContext)
			.unregisterReceiver(m_vpnTunnelBroadcastReceiver);
	}
    
    
    
    
    
    
    private boolean isv2raymode() {
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        return preferencias.getInt(Settings.TUNNELTYPE_KEY, 0) == Settings.bTUNNEL_TYPE_V2RAY; }
    
    
    
    
    
    private void putoelqueloeagatesccn() {
		
		connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkRequest request = new NetworkRequest.Builder().build();
		callback = new ConnectivityManager.NetworkCallback() {
			@Override
			public void onAvailable(Network network) {

			}
			@Override
			public void onLost(Network network) {
				if (runningatesccn) {
					if(isv2raymode()){
						
					} } } };
		connectivityManager.registerNetworkCallback(request, callback);
        }
    
    
    
	
	//private Thread mThreadLocation;

	// Local BroadcastReceiver
	private BroadcastReceiver m_vpnTunnelBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (TunnelVpnService.TUNNEL_VPN_START_BROADCAST.equals(action)) {
				boolean startSuccess = intent.getBooleanExtra(TunnelVpnService.TUNNEL_VPN_START_SUCCESS_EXTRA, true);

				if (!startSuccess) {
					stopAll();
				}
				
			} else if (TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST.equals(action)) {
				stopAll();
			}
		}
	};
    
    
	
}
