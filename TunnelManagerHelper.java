package com.ultrasshservice.tunnel;

import android.content.Intent;
import android.os.Build;
import android.content.Context;
import com.ultrasshservice.MainService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.ultrasshservice.config.Settings;

import com.vpnmoddervpn.vpn.*;
import com.ultrasshservice.*;
import com.ultrasshservice.config.*;
import com.ultrasshservice.logger.*;
import com.ultrasshservice.tunnel.*;
import com.ultrasshservice.util.*;

public class TunnelManagerHelper
{
    
    public static void startSocksHttp(Context context) {
        Intent intent = new Intent(context, MainService.class);
        TunnelUtils.restartRotateAndRandom();
        if (Build.VERSION.SDK_INT < 26) {
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
    }

    public static void stopSocksHttp(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainService.TUNNEL_SSH_STOP_SERVICE));
    }
    
    
    public static void restartSocksHttp(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainService.TUNNEL_SSH_RESTART_SERVICE));
    }
    
    
    
    
    
    
}
