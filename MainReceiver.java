package com.ultrasshservice;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.ultrasshservice.MainService;
import com.ultrasshservice.tunnel.TunnelManagerHelper;
import com.vpnmoddervpn.vpn.V2Tunnel;

public class MainReceiver extends BroadcastReceiver
{
	public static final String ACTION_SERVICE_RESTART = "sshTunnelServiceRestart",
		ACTION_SERVICE_STOP = "sshtunnelservicestop";
		
	@Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        action.hashCode();
        if (action.equals(ACTION_SERVICE_STOP)) {
            TunnelManagerHelper.stopSocksHttp(context);
            //V2Tunnel.StopV2ray(context);
            
        } /*
        else if (action.equals(ACTION_SERVICE_RESTART)) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainService.TUNNEL_SSH_RESTART_SERVICE));
        } */ else if (action.equals(ACTION_SERVICE_RESTART)) {
            TunnelManagerHelper.restartSocksHttp(context);
        }
        
        
        
        
        
        
    }
    
    
    
   
    
    
    
}
