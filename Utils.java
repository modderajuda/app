package com.vpnmoddervpn.vpn.util;

import android.content.pm.PackageInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.annotation.SuppressLint;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import android.view.WindowManager;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Build;
import com.ultrasshservice.MainService;
import com.ultrasshservice.V2Service;
import com.ultrasshservice.V2Configs;
import com.vpnmoddervpn.vpn.audiofechado;
import com.ultrasshservice.config.Settings;

public class Utils
{
    private Settings mConfig;
    
    
    public static String CurrentSMSVersion = "00";
	@SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void copyToClipboard(Context context, String text) throws Exception {
		int sdk = android.os.Build.VERSION.SDK_INT;
            
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
		}
		else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
					.newPlainText(
					"Message", text);
                clipboard.setPrimaryClip(clip);
         }
	}
	
	public static PackageInfo getAppInfo(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine + '\n'); // process line
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
	
	public static void hideKeyboard(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
					0);
        }
    }
	
	public static void exitAll(Activity activity) {
		Intent stopTunnel = new Intent(MainService.TUNNEL_SSH_STOP_SERVICE);
		LocalBroadcastManager.getInstance(activity)
			.sendBroadcast(stopTunnel);
        
        Intent stopTunnelv2 = new Intent(activity, V2Service.class); // Substitua `YourV2rayService` pelo nome da classe do serviço V2Ray
stopTunnelv2.putExtra("COMMAND", V2Configs.V2RAY_SERVICE_COMMANDS.STOP_SERVICE);
activity.startService(stopTunnelv2);
        
        
        
        
        

		if (Build.VERSION.SDK_INT >= 16) {
			activity.finishAffinity();
		}

		System.exit(0);
	}
}
