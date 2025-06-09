package com.ultrasshservice.util;

import android.content.Context;
import android.util.Log;

import com.ultrasshservice.V2Configs;
import com.ultrasshservice.V2Core;
import com.ultrasshservice.config.V2Config;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import com.ultrasshservice.logger.SkStatus;
import org.json.JSONException;
import android.util.Base64;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URISyntaxException;
import libv2ray.Libv2ray;
import android.provider.Settings;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;


public class V2Utilities {

    public static void CopyFiles(InputStream src, File dst) throws IOException {
        try (OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = src.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    public static String getUserAssetsPath(Context context) {
        File extDir = context.getExternalFilesDir("assets");
        if (extDir == null) {
            return "";
        }
        if (!extDir.exists()) {
            return context.getDir("assets", 0).getAbsolutePath();
        } else {
            return extDir.getAbsolutePath();
        }
    }

    public static void copyAssets(final Context context) {
        String extFolder = getUserAssetsPath(context);
        try {
            String geo = "geosite.dat,geoip.dat";
            for (String assets_obj : context.getAssets().list("")) {
                if (geo.contains(assets_obj)) {
                    CopyFiles(context.getAssets().open(assets_obj), new File(extFolder, assets_obj));
                }
            }
        } catch (Exception e) {
            Log.e("V2Utilities", "copyAssets failed=>", e);
        }
    }


    public static String convertIntToTwoDigit(int value) {
        if (value < 10) return "0" + value;
        else return value + "";
    }

    public static String parseTraffic(final double bytes, final boolean inBits, final boolean isMomentary) {
        double value = inBits ? bytes * 8 : bytes;
        if (value < V2Configs.KILO_BYTE) {
            return String.format(Locale.getDefault(), "%.1f " + (inBits ? "b" : "B") + (isMomentary ? "/s" : ""), value);
        } else if (value < V2Configs.MEGA_BYTE) {
            return String.format(Locale.getDefault(), "%.1f K" + (inBits ? "b" : "B") + (isMomentary ? "/s" : ""), value / V2Configs.KILO_BYTE);
        } else if (value < V2Configs.GIGA_BYTE) {
            return String.format(Locale.getDefault(), "%.1f M" + (inBits ? "b" : "B") + (isMomentary ? "/s" : ""), value / V2Configs.MEGA_BYTE);
        } else {
            return String.format(Locale.getDefault(), "%.2f G" + (inBits ? "b" : "B") + (isMomentary ? "/s" : ""), value / V2Configs.GIGA_BYTE);
        }
    }

    public static V2Config parseV2rayJsonFile(final String remark, String config, final ArrayList<String> blockedApplication) {
    final V2Config v2Config = new V2Config();
    v2Config.REMARK = remark;
    v2Config.BLOCKED_APPS = blockedApplication;
    v2Config.APPLICATION_ICON = V2Configs.APPLICATION_ICON;
    v2Config.APPLICATION_NAME = V2Configs.APPLICATION_NAME;
    Log.i("V2Utilities", "Parsing JSON config: " + config);
    try {
        JSONObject config_json = new JSONObject(config);
        try {
            JSONArray inbounds = config_json.getJSONArray("inbounds");
            for (int i = 0; i < inbounds.length(); i++) {
                JSONObject inbound = inbounds.getJSONObject(i);
                String protocol = inbound.getString("protocol");
                Log.i("V2Utilities", "Found inbound protocol: " + protocol + ", port: " + inbound.getInt("port"));
                if (protocol.equals("socks")) {
                    v2Config.LOCAL_SOCKS5_PORT = inbound.getInt("port");
                } else if (protocol.equals("http")) {
                    v2Config.LOCAL_HTTP_PORT = inbound.getInt("port");
                }
            }
        } catch (Exception e) {
            Log.e("V2Utilities", "Failed to parse inbound ports", e);
            return null;
        }
        try {
            JSONObject outbound = config_json.getJSONArray("outbounds").getJSONObject(0);
            JSONObject settings = outbound.getJSONObject("settings");
            try {
                JSONObject vnext = settings.getJSONArray("vnext").getJSONObject(0);
                v2Config.CONNECTED_V2RAY_SERVER_ADDRESS = vnext.getString("address");
                v2Config.CONNECTED_V2RAY_SERVER_PORT = vnext.getString("port");
                Log.i("V2Utilities", "Parsed vnext address: " + v2Config.CONNECTED_V2RAY_SERVER_ADDRESS + ", port: " + v2Config.CONNECTED_V2RAY_SERVER_PORT);
            } catch (Exception e) {
                JSONObject server = settings.getJSONArray("servers").getJSONObject(0);
                v2Config.CONNECTED_V2RAY_SERVER_ADDRESS = server.getString("address");
                v2Config.CONNECTED_V2RAY_SERVER_PORT = server.getString("port");
                Log.i("V2Utilities", "Parsed server address: " + v2Config.CONNECTED_V2RAY_SERVER_ADDRESS + ", port: " + v2Config.CONNECTED_V2RAY_SERVER_PORT);
            }
        } catch (Exception e) {
            Log.e("V2Utilities", "Failed to parse server address/port", e);
            return null;
        }
        try {
            if (config_json.has("policy")) {
                config_json.remove("policy");
            }
            if (config_json.has("stats")) {
                config_json.remove("stats");
            }
        } catch (Exception ignore_error) {
        }
        if (V2Configs.ENABLE_TRAFFIC_AND_SPEED_STATICS) {
            try {
                JSONObject policy = new JSONObject();
                JSONObject levels = new JSONObject();
                levels.put("8", new JSONObject()
                        .put("connIdle", 300)
                        .put("downlinkOnly", 1)
                        .put("handshake", 4)
                        .put("uplinkOnly", 1));
                JSONObject system = new JSONObject()
                        .put("statsOutboundUplink", true)
                        .put("statsOutboundDownlink", true);
                policy.put("levels", levels);
                policy.put("system", system);
                config_json.put("policy", policy);
                config_json.put("stats", new JSONObject());
                config = config_json.toString();
                v2Config.ENABLE_TRAFFIC_STATICS = true;
                Log.i("V2Utilities", "Added traffic statistics policy");
            } catch (Exception e) {
                Log.e("V2Utilities", "Failed to add traffic statistics policy", e);
            }
        }
        v2Config.V2RAY_FULL_JSON_CONFIG = config;
        Log.i("V2Utilities", "Parsed V2Config successfully: " + v2Config.toString());
        return v2Config;
    } catch (Exception e) {
        Log.e("V2Utilities", "parseV2rayJsonFile failed", e);
        return null;
    }
}


}
