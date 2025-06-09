package com.ultrasshservice;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.ultrasshservice.logger.SkStatus;
import com.ultrasshservice.logger.ConnectionStatus;
import com.ultrasshservice.tunnel.TunnelManagerHelper;
import android.os.Build;
import com.ultrasshservice.config.Settings;
import android.net.VpnService;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.widget.Toast;
import com.vpnmoddervpn.vpn.TunnelUtils;
import android.widget.EditText;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.annotation.SuppressLint;
import android.widget.CheckBox;
import androidx.core.widget.CompoundButtonCompat;
import android.widget.CompoundButton;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.RemoteException;
import com.ultrasshservice.config.PasswordCache;
import android.content.SharedPreferences;
import android.widget.ImageButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.vpnmoddervpn.vpn.R;
public class LaunchVpn extends AppCompatActivity
	implements DialogInterface.OnCancelListener
{
	public static final String EXTRA_HIDELOG = "com.ace.injector.showNoLogWindow";
	public static final String CLEARLOG = "clearlogconnect";
	
	private static final int START_VPN_PROFILE = 70;
	
	private Settings mConfig;
	private String mTransientAuthPW;
	private boolean mhideLog = false;
	private boolean isMostrarSenha = false;
    private SharedPreferences preferencias;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.launchvpn);
		
		mConfig = new Settings(this);

        startVpnFromIntent();
		//throw new RuntimeException();
    }
	
	protected void startVpnFromIntent() {
        // Resolve the intent
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (Intent.ACTION_MAIN.equals(action)) {
            // Check if we need to clear the log
            if (mConfig.getAutoClearLog())
				SkStatus.clearLog();

            mhideLog = intent.getBooleanExtra(EXTRA_HIDELOG, false);
			
            launchVPN();
        }
    }


	@Override
	public void onCancel(DialogInterface p1)
	{
		SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
			ConnectionStatus.LEVEL_NOTCONNECTED);
		finish();
	}
	
	private void showLogWindow() {
        Intent updateView = new Intent("com.vpnmoddervpn.vpn:openLogs");
		LocalBroadcastManager.getInstance(this)
			.sendBroadcast(updateView);
    }
    
    private boolean isv2raymode() {
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        return preferencias.getInt(Settings.TUNNELTYPE_KEY, 0) == Settings.bTUNNEL_TYPE_V2RAY; }
	
	@Override
    
    
    
    
    
    
    /*
    
    //METODO SEM V2RAY 22 04 25
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == START_VPN_PROFILE) {
        if (resultCode == Activity.RESULT_OK) {
            SharedPreferences prefs = mConfig.getPrefsPrivate();

            // Caso não seja V2Ray, realiza as verificações normais
            if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_PROXY) == Settings.bTUNNEL_TYPE_SSH_PROXY &&
                (mConfig.getPrivString(Settings.PROXY_IP_KEY).isEmpty() || mConfig.getPrivString(Settings.PROXY_PORTA_KEY).isEmpty())) {
                SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                    ConnectionStatus.LEVEL_NOTCONNECTED);
                Toast.makeText(this, R.string.error_proxy_invalid,
                    Toast.LENGTH_SHORT).show();
                finish();
            } else if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true) && mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).isEmpty()) {
                SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                    ConnectionStatus.LEVEL_NOTCONNECTED);
                Toast.makeText(this, R.string.error_empty_payload,
                    Toast.LENGTH_SHORT).show();
                finish();
            } else if (mConfig.getPrivString(Settings.SERVIDOR_KEY).isEmpty() || mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY).isEmpty()) {
                SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                    ConnectionStatus.LEVEL_NOTCONNECTED);
                Toast.makeText(this, R.string.error_empty_settings,
                    Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (!mhideLog) {
                    showLogWindow();
                }

                TunnelManagerHelper.startSocksHttp(this);
                finish();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // User does not want us to start, so we just vanish
            SkStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
                ConnectionStatus.LEVEL_NOTCONNECTED);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                SkStatus.logError(R.string.nought_alwayson_warning);

            finish();
        }
    }
}
    
    */
    
    
    
    
    
    
    
    //METODO COM V2RAY 22 04 25
    
    //@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    SkStatus.logInfo("onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
    if (requestCode == START_VPN_PROFILE) {
        if (resultCode == Activity.RESULT_OK) {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
            int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, 0);
            SkStatus.logInfo("TunnelType: " + tunnelType);
            if (tunnelType == Settings.bTUNNEL_TYPE_V2RAY) {
                SkStatus.logInfo("<strong><font color='#FFD700'>Starting V2Ray tunnel</strong>");
                TunnelManagerHelper.startSocksHttp(this);
                finish();
                return;
            }
            // Lógica para outros modos (SSH_PROXY, etc.)
            if (tunnelType == Settings.bTUNNEL_TYPE_SSH_PROXY &&
                (mConfig.getPrivString(Settings.PROXY_IP_KEY).isEmpty() || mConfig.getPrivString(Settings.PROXY_PORTA_KEY).isEmpty())) {
                SkStatus.logError(R.string.error_proxy_invalid);
                Toast.makeText(this, R.string.error_proxy_invalid, Toast.LENGTH_SHORT).show();
                finish();
            } else if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true) && mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).isEmpty()) {
                SkStatus.logError(R.string.error_empty_payload);
                Toast.makeText(this, R.string.error_empty_payload, Toast.LENGTH_SHORT).show();
                finish();
            } else if (mConfig.getPrivString(Settings.SERVIDOR_KEY).isEmpty() || mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY).isEmpty()) {
                SkStatus.logError(R.string.error_empty_settings);
                Toast.makeText(this, R.string.error_empty_settings, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (!mhideLog) {
                    showLogWindow();
                }
                SkStatus.logInfo("Starting SSH tunnel");
                TunnelManagerHelper.startSocksHttp(this);
                finish();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            SkStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
                ConnectionStatus.LEVEL_NOTCONNECTED);
            SkStatus.logError(R.string.state_user_vpn_permission_cancelled);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SkStatus.logError(R.string.nought_alwayson_warning);
            }
            finish();
        }
    }
}
    
    
    
    
    
    
    
    /*
    protected void onActivityResultSemChecar(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
				

                    TunnelManagerHelper.startSocksHttp(this);
					finish();
				
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User does not want us to start, so we just vanish
                SkStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
					ConnectionStatus.LEVEL_NOTCONNECTED);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    SkStatus.logError(R.string.nought_alwayson_warning);

                finish();
                TunnelManagerHelper.startSocksHttp(this);
            }
        }
    }
    */
    
    
    
    /*
    //METODO SEM V2RAY 11 04 25
    
    protected void onActivityResultSemv2(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
				SharedPreferences prefs = mConfig.getPrefsPrivate();
				if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY &&
						(mConfig.getPrivString(Settings.PROXY_IP_KEY).isEmpty() || mConfig.getPrivString(Settings.PROXY_PORTA_KEY).isEmpty())) {
					SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
						ConnectionStatus.LEVEL_NOTCONNECTED);
					Toast.makeText(this, R.string.error_proxy_invalid,
						Toast.LENGTH_SHORT).show();
					finish();
				}
				else if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true) && mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY).isEmpty()) {
					SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
						ConnectionStatus.LEVEL_NOTCONNECTED);
					Toast.makeText(this, R.string.error_empty_payload,
						Toast.LENGTH_SHORT).show();

					finish();
				}
				else if (mConfig.getPrivString(Settings.SERVIDOR_KEY).isEmpty() || mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY).isEmpty()) {
					SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
						ConnectionStatus.LEVEL_NOTCONNECTED);
					Toast.makeText(this, R.string.error_empty_settings,
						Toast.LENGTH_SHORT).show();
				}
				else {
                    if (!mhideLog) {
						showLogWindow();
					}

                    TunnelManagerHelper.startSocksHttp(this);
					finish();
                }
				
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User does not want us to start, so we just vanish
                SkStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
					ConnectionStatus.LEVEL_NOTCONNECTED);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    SkStatus.logError(R.string.nought_alwayson_warning);

                finish();
            }
        }
    }
    
    */
    
    
    
    
	
	private void launchVPN() {
		Intent intent = VpnService.prepare(this);
        	
        if (intent != null) {
            SkStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
				ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                SkStatus.logError(R.string.no_vpn_support_image);
                showLogWindow();
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }
	
}
