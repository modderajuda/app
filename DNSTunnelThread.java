package com.vpnmoddervpn.vpn;

import com.ultrasshservice.logger.SkStatus;
import com.ultrasshservice.tunnel.vpn.VpnUtils;
import com.ultrasshservice.config.Settings;
import java.io.IOException;
import java.io.File;
import com.ultrasshservice.util.CustomNativeLoader;
import com.ultrasshservice.util.StreamGobbler;
import android.content.Context;
import android.content.SharedPreferences;

import com.trilead.ssh2.transport.TransportManager;
import java.util.concurrent.CountDownLatch;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.ConnectionMonitor;
import com.vpnmoddervpn.vpn.TunnelUtils;
import com.vpnmoddervpn.vpn.R;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.atomic.AtomicBoolean;
import com.vpnmoddervpn.vpn.TunnelManagerThread;
import com.vpnmoddervpn.vpn.SocksHttpMainActivity;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DNSTunnelThread extends Thread implements ConnectionMonitor
{

    private boolean mRunning = false, mStopping = false, mStarting = false;
    public boolean mReconnecting = false;
	private Context mContext;
	private static final String DNS_BIN = "libdns";
	private Process dnsProcess;
	private File filedns;
	private Settings mConfig;
    
    private Connection mConnection;
    private CountDownLatch mTunnelThreadStopSignal;
    private boolean mConnected = false;
    private DNSTunnelThread mDnsThread;
    private TunnelManagerThread tunnelManagerThread;
    private SocksHttpMainActivity mainActivity;
    
    public static int dnsIndex = 0;
    public static int chaveIndex = 0;
    public static int nameserverIndex = 0;

	private final String dns;
private final String chave;
private final String nameserver;

public DNSTunnelThread(Context context, String dns, String chave, String nameserver) {
    this.mContext = context;
    this.mConfig = new Settings(context);
    this.dns = dns;
    this.chave = chave;
    this.nameserver = nameserver;
}
    
    @Override
public void connectionLost(Throwable reason) {
    if (mStarting || mStopping || mReconnecting) {
        return;
    }

    //SkStatus.logError("<strong>" + mContext.getString(R.string.log_conection_lost) + "</strong>");
    //SkStatus.updateStateString("DESCONECTADO", "SlowDns desconectado");

    // Criar nova instância para chamar stopAll()
        
    Handler handler = new Handler(Looper.getMainLooper());
        
        /*
    TunnelManagerThread manager = new TunnelManagerThread(handler, mContext);
    manager.stopAll();
        */
        
        
        
        
        interrupt();

			
        
        
        

    //SkStatus.logInfo("<strong>Esperando a rede voltar...</strong>");

        
        /*
    new Thread(() -> {
        while (!TunnelUtils.isNetworkOnline(mContext)) {
            try {
                Thread.sleep(5000);
                Log.i("V2Service", "Aguardando conexão de rede...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        //SkStatus.logInfo("<strong>Rede restaurada. Reiniciando o SlowDns...</strong>");

        // Criar nova instância para chamar reconnectSSH()
        TunnelManagerThread reconnectManager = new TunnelManagerThread(handler, mContext);
        //reconnectManager.reconnectSSH();
    }).start();
        
        */
}
    
    
    
    
    
    
    
    
    
    
    
    @Override
	public void onReceiveInfo(int id, String msg) {
		if (id == SERVER_BANNER) {
			SkStatus.logInfo("<strong>" + mContext.getString(R.string.log_server_banner) + "</strong> " + msg);
		}
	}

	@Override
    
    /*
    public void runRotate(){
    try {
        SharedPreferences slowprefs = mConfig.getPrefsPrivate();

        // Pega os arrays rotativos
        String[] dnsList = mConfig.getPrivString(Settings.DNS_KEY).split("#");
        String[] chaveList = mConfig.getPrivString(Settings.CHAVE_KEY).split("#");
        String[] nameserverList = mConfig.getPrivString(Settings.NAMESERVER_KEY).split("#");

        // Garante que os índices não ultrapassem os limites
        if (dnsIndex >= dnsList.length) dnsIndex = 0;
        if (chaveIndex >= chaveList.length) chaveIndex = 0;
        if (nameserverIndex >= nameserverList.length) nameserverIndex = 0;

        // Seleciona os valores atuais
        String dns = dnsList[dnsIndex];
        String chave = chaveList[chaveIndex];
        String nameserver = nameserverList[nameserverIndex];

        // Atualiza os índices
        dnsIndex++;
        if (dnsIndex == 0) {
            chaveIndex++;
            if (chaveIndex == 0) {
                nameserverIndex++;
                if (nameserverIndex >= nameserverList.length) {
                    nameserverIndex = 0;
                }
            }
            if (chaveIndex >= chaveList.length) {
                chaveIndex = 0;
            }
        }

        StringBuilder cmd1 = new StringBuilder();
        String payloadName = mConfig.getPrivString(Settings.PAY_NAME);

        filedns = CustomNativeLoader.loadNativeBinary(mContext, DNS_BIN, new File(mContext.getFilesDir(), DNS_BIN));
        if (filedns == null) throw new IOException("DNS bin not found");

        cmd1.append(filedns.getCanonicalPath());
        cmd1.append(" -udp " + dns + ":53 -pubkey " + chave + " " + nameserver + " 127.0.0.1:2222");

        dnsProcess = Runtime.getRuntime().exec(cmd1.toString());

        StreamGobbler.OnLineListener onLineListener = new StreamGobbler.OnLineListener(){
            @Override
            public void onLine(String log){
                //SkStatus.logWarning("<b>DNS Client: </b>" + log);
            }
        };
        new StreamGobbler(dnsProcess.getInputStream(), onLineListener).start();
        new StreamGobbler(dnsProcess.getErrorStream(), onLineListener).start();

        monitorNetwork();
            
        SkStatus.logInfo("Conectando no DNS: " + (dnsIndex + 1) + " - " + dns);
        SkStatus.logInfo("Conectando na Chave: " + (chaveIndex + 1) + " - " + chave);
        SkStatus.logInfo("Conectando no Nameserver: " + (nameserverIndex + 1) + " - " + nameserver);
        SkStatus.logInfo("Config Selecionada: " + payloadName);

        mConnected = true;
        dnsProcess.waitFor();
    } catch (IOException e) {
        //SkStatus.logWarning("SlowDNS IO: " + e);
    } catch (InterruptedException e){
        //SkStatus.logWarning("SlowDNS Interupt: " + e);
    }
}
    
    */
    
    
    //METODO 14 04 25 SEM ROTACAO
    
	public void run(){
		try {

			
			StringBuilder cmd1 = new StringBuilder();
            
            String payloadName = mConfig.getPrivString(Settings.PAY_NAME);
            
			filedns = CustomNativeLoader.loadNativeBinary(mContext, DNS_BIN, new File(mContext.getFilesDir(),DNS_BIN));

			if (filedns == null){
				throw new IOException("DNS bin not found");
			}

			cmd1.append(filedns.getCanonicalPath());
			cmd1.append(" -udp "+ dns + ":53   -pubkey "+ chave + " " + nameserver + " 127.0.0.1:2222");
			dnsProcess = Runtime.getRuntime().exec(cmd1.toString());

			StreamGobbler.OnLineListener onLineListener = new StreamGobbler.OnLineListener(){
				@Override
				public void onLine(String log){
					//SkStatus.logWarning("<b>DNS Client: </b>" + log);
				}
			};
			StreamGobbler stdoutGobbler = new StreamGobbler(dnsProcess.getInputStream(), onLineListener);
			StreamGobbler stderrGobbler = new StreamGobbler(dnsProcess.getErrorStream(), onLineListener);

			stdoutGobbler.start();
			stderrGobbler.start();
            
            monitorNetwork();
          
            
            
            //SkStatus.logInfo("Config Selecionada: " + payloadName);
            
            //mConnection.addConnectionMonitor(this);
            
            mConnected = true;

			dnsProcess.waitFor();		
		} catch (IOException e) {
			//SkStatus.logWarning("SlowDNS IO: " + e);
		}catch (InterruptedException e){
			//SkStatus.logWarning("SlowDNS Interupt: " + e);
		}
	}
    
    

	@Override
	public void interrupt(){
		if (dnsProcess != null)
			dnsProcess.destroy();
        mConnected = false;
		try {
			if (filedns != null)
				VpnUtils.killProcess(filedns);
		} catch (Exception e) {}

		dnsProcess = null;
		filedns = null;
		super.interrupt();
	}
    
    
    
    
    /*
    
    public void stopAll() {
       if (mStopping) return;

       SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
       SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");

       mStopping = true; // Mova esta linha para evitar múltiplas chamadas

       // Crie um Handler na thread principal
       Handler handler = new Handler(Looper.getMainLooper());

       handler.post(new Runnable() {
           @Override
           public void run() {
               // Aqui você pode colocar a lógica que precisa ser executada na thread principal
               if (mTunnelThreadStopSignal != null) {
                   mTunnelThreadStopSignal.countDown();
               } else {
                   //mDnsThread.interrupt();
                        //dnsProcess.waitFor();	
               }

               // Atualiza a interface do usuário após 1 segundo
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
                   }
               }, 1000);
           }
       });
   }
    
    */
    
    
    
    /*
    public void reconnectSSH() {
       if (TunnelUtils.isNetworkOnline(mContext)) {
           Log.i("V2Service", "Tentando reconectar SSH...");
          
           mDnsThread.start();
       } else {
           Log.w("V2Service", "Rede não disponível, não é possível reconectar o V2Ray.");
       }
   }
    
    */
    
    
    
    
    
    
    
    private boolean isRunning = true;
    
    private AtomicBoolean isMonitoring = new AtomicBoolean(false);

private void monitorNetwork() {
    if (isMonitoring.compareAndSet(false, true)) {
        new Thread(() -> {
            while (isRunning) {
                boolean isOnline = TunnelUtils.isNetworkOnline(mContext);

                if (!isOnline && !isConnectionLostHandled) {
                    isConnectionLostHandled = true;
                    Log.e("V2Service", "Conexão de rede perdida - chamando connectionLost()");
                    connectionLost(null);
                } else if (isOnline && isConnectionLostHandled) {
                    Log.i("V2Service", "Rede restaurada - resetando isConnectionLostHandled");
                    isConnectionLostHandled = false; // Reset para permitir futuras chamadas
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            isMonitoring.set(false);
        }).start();
    } else {
        Log.d("V2Service", "Monitoramento de rede já está em execução");
    }
}
    
    
	
	private boolean isReconnecting = false; // Variável de controle para reconexão

private boolean isConnectionLostHandled = false;
    
    
    
    
    
    
    
    
}

