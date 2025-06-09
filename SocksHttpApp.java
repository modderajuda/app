package com.vpnmoddervpn.vpn;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import com.ultrasshservice.MainCore;
import com.ultrasshservice.config.Settings;
import com.vpnmoddervpn.vpn.audioaberto;
/**
* App
*/
public class SocksHttpApp extends Application
{
	private static final String TAG = SocksHttpApp.class.getSimpleName();
	public static final String PREFS_GERAL = "GERAL";
    
    private Settings mConfig;
	
	
	
	private static SocksHttpApp mApp;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		mApp = this;
		

			
		// inicia
		MainCore.init(this);
		
		
		
		// Initialize the Mobile Ads SDK.
        
		
		// modo noturno
		setModoNoturno(this);
        
        
        /*
        this.mConfig = new Settings(this);
        
         if (this.mConfig != null && this.mConfig.audioAberto()) {
        new audioaberto().executar(this);
  }
        */
        
        
        
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		//LocaleHelper.setLocale(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//LocaleHelper.setLocale(this);
	}
	
	private void setModoNoturno(Context context) {
		boolean isOff = new Settings(context).getModoNoturno().equals("off");

int night_mode = isOff ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;
AppCompatDelegate.setDefaultNightMode(night_mode);
	}
	
	public static SocksHttpApp getApp() {
		return mApp;
	}
}
