package com.vpnmoddervpn.vpn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.reward.*;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.vpnmoddervpn.vpn.activities.BaseActivity;
import com.vpnmoddervpn.vpn.activities.ConfigGeralActivity;
import com.vpnmoddervpn.vpn.adapter.LogsAdapter;
import com.vpnmoddervpn.vpn.adapter.SpinnerAdapter;
import com.vpnmoddervpn.vpn.fragments.ClearConfigDialogFragment;
import com.vpnmoddervpn.vpn.util.AESCrypt;
import com.vpnmoddervpn.vpn.util.ConfigUpdate;
import com.vpnmoddervpn.vpn.util.ConfigUtil;
import com.vpnmoddervpn.vpn.util.SMSuPdater;
import com.vpnmoddervpn.vpn.util.Utils;
import com.ultrasshservice.LaunchVpn;
import com.ultrasshservice.config.Settings;
import com.ultrasshservice.logger.ConnectionStatus;
import com.ultrasshservice.logger.SkStatus;
import com.ultrasshservice.tunnel.TunnelManagerHelper;
import com.vpnmoddervpn.vpn.TunnelManagerThread;
import com.vpnmoddervpn.vpn.TunnelUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import android.os.AsyncTask;
import com.ultrasshservice.util.securepreferences.SecurePreferences;
import cn.pedant.SweetAlert.widget.SweetAlertDialog;
import com.ultrasshservice.config.SettingsConstants;
import android.util.Base64;
import java.util.TimerTask;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import androidx.appcompat.widget.SwitchCompat;
import android.app.Dialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import java.util.Timer;
import android.graphics.Bitmap;
import android.content.pm.ApplicationInfo;
import android.view.Gravity;
import java.io.FileInputStream;
import android.graphics.BitmapFactory;
import org.json.JSONArray;
import me.dawson.proxyserver.ui.ProxySettings;
import android.graphics.PorterDuff;
import android.os.Looper;
import android.os.Vibrator;
import android.os.VibrationEffect;
import org.json.JSONTokener;
import com.vpnmoddervpn.vpn.audiofechado;
import android.widget.RelativeLayout;

public class SocksHttpMainActivity extends BaseActivity
	implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, SkStatus.StateListener
{


private static final String TAG = SocksHttpMainActivity.class.getSimpleName();
private static final String UPDATE_VIEWS = "MainUpdate";
public static final String OPEN_LOGS = "com.vpnmoddervpn.vpn:openLogs";
private static final String APP_NAME = "app_name";
private static final String NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS";
private static final int PERMISSION_REQUEST_CODE = 1;
private static final String PREFS_NAME = "MyPrefs";
private static final String USERNAME_KEY = "username";
private static final String PASSWORD_KEY = "password";
private static final String[] tabTitle = {"INICIO", "REGISTRO"};
private static int currentPayloadIndex = 1;
private static int currentProxyIndex = 0;
private static int currentPortIndex = 0;
private Settings mConfig;
private ConfigUtil config;
private boolean isRunning;
private boolean spinnerTouched = false;
private SharedPreferences sharedPreferences;
private Handler mHandler, configUpdateHandler;
private boolean typenetwork;
private TextView txtVersion, txtVersion2, operadoraMain, Corbotaostart, Vencimento;
private TextView connectionStatus, status, proxyText, configMsgText;
private Button botaoregistro, starterButton, botaoatualizar, botaoconfigurar, botaoferramentas, botaoimportar;
private Button botaootimizarbateria, botaorotearhome, botaospeedtest, botaotelegram, botaotermos, botaotorre;
private Button botaowhatsapp, botaoyoutube, miPhoneConfig, APNsettings;
private LinearLayout fundoCaixaCentral, fundoCaixaConexao, fundoBotaoIniciar, fundoBotaoAtualizar;
private LinearLayout fundobotaoferramentas, fundobotaoiniciar, fundobotaoregistro, fundoferramentas;
private LinearLayout fundocaixacentral, fundocaixaconexao, fundocxregistro, mainLayout, loginLayout;
private LinearLayout proxyInputLayout, payloadLayout, ssl_layout, configMsgLayout;
private RelativeLayout fundoregistro;
private TextInputEditText username, password, payloadEdit, sslEdit;
private Spinner serverSpinner, payloadSpinner;
private SpinnerAdapter serverAdapter, payloadAdapter;
private RadioGroup metodoConexaoRadio;
private SwitchCompat customPayloadSwitch;
private SweetAlertDialog pDialog, mDialog;
private Dialog dialog2, dialog3, dialogRegistro;
private ImageView reloadIU, backgroundapp, logoapp, fundoonline, logoonline, banneronline;
private Toolbar toolbar_main, toolbarMain;
private RecyclerView logList;
private ViewPager vp;
private TabLayout tabs;
private DrawerPanelMain mDrawerPanel;
private ArrayList<JSONObject> udpList, serverList, payloadList;
private LogsAdapter mLogAdapter;
private TunnelManagerThread tunnelManagerThread;
private Handler handler;
private String nextPayloadKey = "", nextProxyIP, nextProxyIPDirect, nextProxyIPTlsws;
private int currentSniIndex = 0, currentTlsIpIndex = 0;
private String TLS_IP_KEY;
private Context mContext;
private ImageButton inputPwShowPass;
    public static SocksHttpMainActivity instance;

// Outros Controles
private int PICK_FILE, contador = 0;
    
    private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() { // from class: com.vpnmoddervpn.vpn.SocksHttpMainActivity.9
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(SocksHttpMainActivity.UPDATE_VIEWS) && !SocksHttpMainActivity.this.isFinishing()) {
                SocksHttpMainActivity.this.doUpdateLayout();
            } else if (action.equals(SocksHttpMainActivity.OPEN_LOGS)) {
                SocksHttpMainActivity.this.vp.setCurrentItem(1, true);
            }
        }
    };
    
    
    
	
	private String[] torrentList = new String[] {
		"com.tdo.showbox",
		"com.nitroxenon.terrarium",
		"com.pklbox.translatorspro",
		"com.xunlei.downloadprovider",
		"com.epic.app.iTorrent",
		"hu.bute.daai.amorg.drtorrent",
		"com.mobilityflow.torrent.prof",
		"com.brute.torrentolite",
		"com.nebula.swift",
		"tv.bitx.media",
		"com.DroiDownloader",
		"bitking.torrent.downloader",
		"org.transdroid.lite",
		"com.mobilityflow.tvp",
		"com.gabordemko.torrnado",
		"com.frostwire.android",
		"com.vuze.android.remote",
		"com.akingi.torrent",
		"com.utorrent.web",
		"com.paolod.torrentsearch2",
		"com.delphicoder.flud.paid",
		"com.teeonsoft.ztorrent",
		"megabyte.tdm",
		"com.bittorrent.client.pro",
		"com.mobilityflow.torrent",
		"com.utorrent.client",
		"com.utorrent.client.pro",
		"com.bittorrent.client",
		"torrent",
		"com.AndroidA.DroiDownloader",
		"com.indris.yifytorrents",
		"com.delphicoder.flud",
		"com.oidapps.bittorrent",
		"dwleee.torrentsearch",
		"com.vuze.torrent.downloader",
		"megabyte.dm",
		"com.fgrouptech.kickasstorrents",
		"com.jrummyapps.rootbrowser.classic",
		"com.bittorrent.client",
		"hu.tagsoft.ttorrent.lite",
		"co.we.torrent"};

	

	private Handler fHandler = new Handler();
	private TextView vencimento;
	private TextView user_limite;
	private TextView dias_check;
    
    
	public void alertdiasexpirado() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    if (vibrator != null && vibrator.hasVibrator()) {
        vibrator.vibrate(500); // Vibra por 500ms
    }
        
		SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 3);
		this.pDialog = sweetAlertDialog;
		sweetAlertDialog.setTitleText("ATENÇÃO!!");
		this.pDialog.setContentText("SUA INTERNET ESTA VENCIDA!\n\nPOR FAVOR, CONTATE SEU \nVENDEDOR PARA RENOVAR SEU ACESSO.");
		this.pDialog.setConfirmText("Certo");
		this.pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		this.pDialog.show();
	}
    
    
    /*
    public void alertlimiterSemvibrad(int i, int i2) {
		SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 3);
		this.pDialog = sweetAlertDialog;
		sweetAlertDialog.setTitleText("ATENÇÃO!!");
		SweetAlertDialog sweetAlertDialog2 = this.pDialog;
		sweetAlertDialog2.setContentText("ESSE USUARIO JÁ ESTA\n EM USO NO MOMENTO.\n\nNUNCA COMPARTILHE SEUS\nDADOS DE LOGIN.\n\n CASO ESSE PROBLEMA\nPERSISTA ENTRE EM CONTATO\nCOM SEU VENDEDOR.\n\nLIMITE: " + i + " | " + i2);
		this.pDialog.setConfirmText("Certo");
		this.pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		this.pDialog.show();
	}
    */
    
    
    
    public void alertlimiter(int i, int i2) {
    // Configurar vibração
    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    if (vibrator != null && vibrator.hasVibrator()) {
        vibrator.vibrate(500); // Vibra por 500ms
    }

    // Configurar alerta
    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 3);
    this.pDialog = sweetAlertDialog;
    sweetAlertDialog.setTitleText("ATENÇÃO!!");
    SweetAlertDialog sweetAlertDialog2 = this.pDialog;
    sweetAlertDialog2.setContentText("ESSE USUARIO JÁ ESTA\n EM USO NO MOMENTO.\n\nNUNCA COMPARTILHE SEUS\nDADOS DE LOGIN.\n\n CASO ESSE PROBLEMA\nPERSISTA ENTRE EM CONTATO\nCOM SEU VENDEDOR.\n\nLIMITE: " + i + " | " + i2);
    this.pDialog.setConfirmText("Certo");
    this.pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismissWithAnimation();
        }
    });
    this.pDialog.show();
}
    
    
    
    
    public void alertdias(int i, String str) {
        if (getResources().getString(getResources().getIdentifier(APP_NAME, "string", getPackageName())).contains("MODDERC5G")) {
            
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    if (vibrator != null && vibrator.hasVibrator()) {
        vibrator.vibrate(500); // Vibra por 500ms
    }
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 3);
            this.pDialog = sweetAlertDialog;
            sweetAlertDialog.setTitleText("ATENÇÃO!!");
            SweetAlertDialog sweetAlertDialog2 = this.pDialog;
            sweetAlertDialog2.setContentText("RESTAM " + i + str + " PARA O \nTERMINO DA SUA INTERNET\n\nEVITE FICAR SEM INTERNET \nE RENOVE SEU ACESSO");
            this.pDialog.setConfirmText("Certo");
            this.pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() { // from class: com.vpnmoddervpn.vpn.SocksHttpMainActivity.16
                public void onClick(SweetAlertDialog sweetAlertDialog3) {
                    sweetAlertDialog3.dismissWithAnimation();
                }
            });
            this.pDialog.show();
            return;
        }
        finish();
    }
    
    
    
    
    /*
    
    public void CheckUserC5g() {
		SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
		final String string = prefsPrivate.getString(Settings.USUARIO_KEY, "");
		final String string2 = prefsPrivate.getString(Settings.URL, "");
		new Thread(new Runnable() {
			public void run() {
				BufferedReader bufferedReader;
				try {

					HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(string2).openConnection();
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
					httpURLConnection.setRequestProperty("Accept", "application/json");
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setDoInput(true);
					JSONObject jSONObject = new JSONObject();
					jSONObject.put("user", string);
					Log.i("JSON", jSONObject.toString());
					DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
					dataOutputStream.writeBytes(jSONObject.toString());
					dataOutputStream.flush();
					dataOutputStream.close();
					if (httpURLConnection.getResponseCode() == 200) {
						bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
						while (true) {
							String readLine = bufferedReader.readLine();
							if (readLine == null) {
								break;
							}
							JSONObject jSONObject2 = new JSONObject(readLine);
							final String string = jSONObject2.getString("username");
							final int parseInt = Integer.parseInt(jSONObject2.getString("count_connection"));
							final String string2 = jSONObject2.getString("expiration_date");
							final String string3 = jSONObject2.getString("expiration_days");
							final int parseInt2 = Integer.parseInt(jSONObject2.getString("limiter_user"));
							SocksHttpMainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									try {
										if (string.equals("not exist")) {
											SocksHttpMainActivity.this.runOnUiThread(new Runnable() {
												public void run() {
													SocksHttpMainActivity.this.vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Usuário não encontrado no banco de dados!</strong>"));
													SocksHttpMainActivity.this.user_limite.setText("");
													SocksHttpMainActivity.this.dias_check.setText("");
												}
											});
											return;
										}
										if (parseInt > parseInt2) {
											SocksHttpMainActivity.this.alertlimiter(parseInt, parseInt2);
											SocksHttpMainActivity.this.startOrStopTunnel(SocksHttpMainActivity.this);
											TextView access$1900 = SocksHttpMainActivity.this.user_limite;
											access$1900.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"red\">" + parseInt + " | " + parseInt2 + "</strong>"));
										} else {
											TextView access$19002 = SocksHttpMainActivity.this.user_limite;
											access$19002.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"green\">" + parseInt + " | " + parseInt2 + "</strong>"));
										}
										TextView access$1800 = SocksHttpMainActivity.this.vencimento;
										access$1800.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Vencimento: </strong><strong><font color=\"green\">" + string2 + "</strong>"));
										int parseInt = Integer.parseInt(string3);
										if (parseInt > 7) {
											TextView access$2000 = SocksHttpMainActivity.this.dias_check;
											access$2000.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"green\">" + parseInt + "</strong>"));
										} else if (parseInt == 0) {
											TextView access$20002 = SocksHttpMainActivity.this.dias_check;
											access$20002.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + parseInt + "</strong>"));
											SocksHttpMainActivity.this.alertdiasexpirado();
										} else {
											TextView access$20003 = SocksHttpMainActivity.this.dias_check;
											access$20003.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + parseInt + "</strong>"));
											if (parseInt == 1) {
												SocksHttpMainActivity.this.alertdias(parseInt, " DIA");
												return;
											}
											SocksHttpMainActivity.this.alertdias(parseInt, " DIAS");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
						bufferedReader.close();
					} else {
						SocksHttpMainActivity.this.vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Ocorreu um erro ao obter as informações do login!</strong>"));
						SocksHttpMainActivity.this.user_limite.setText("");
						SocksHttpMainActivity.this.dias_check.setText("");
					}
					httpURLConnection.disconnect();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				} catch (Throwable th) {
					th.addSuppressed(th);
				}
				Throwable th;
			}
		}).start();
	}
    
*/
    
    
    
    public void CheckUser() {
		SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
    final String string = prefsPrivate.getString(Settings.USUARIO_KEY, "");
    final String string2 = prefsPrivate.getString(Settings.URL, "");

    // Define um ExecutorService com um único thread
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
        BufferedReader bufferedReader;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(string2).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            JSONObject jSONObject = new JSONObject();
            jSONObject.put("user", string);
            Log.i("JSON", jSONObject.toString());

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(jSONObject.toString());
            dataOutputStream.flush();
            dataOutputStream.close();

            if (httpURLConnection.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    JSONObject jSONObject2 = new JSONObject(readLine);
                    final String username = jSONObject2.getString("username");
                    final int countConnection = Integer.parseInt(jSONObject2.getString("count_connection"));
                    final String expirationDate = jSONObject2.getString("expiration_date");
                    final String expirationDays = jSONObject2.getString("expiration_days");
                    final int userLimit = Integer.parseInt(jSONObject2.getString("limiter_user"));

                    // Atualiza a UI
                    SocksHttpMainActivity.this.runOnUiThread(() -> {
                        try {
                            if (username.equals("not exist")) {
                                SocksHttpMainActivity.this.vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Usuário não encontrado no banco de dados!</strong>"));
                                SocksHttpMainActivity.this.user_limite.setText("");
                                SocksHttpMainActivity.this.dias_check.setText("");
                                return;
                            }
                            if (countConnection > userLimit) {
                                SocksHttpMainActivity.this.alertlimiter(countConnection, userLimit);
                                SocksHttpMainActivity.this.startOrStopTunnel(SocksHttpMainActivity.this);
                                SocksHttpMainActivity.this.user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"red\">" + countConnection + " | " + userLimit + "</strong>"));
                            } else {
                                SocksHttpMainActivity.this.user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"green\">" + countConnection + " | " + userLimit + "</strong>"));
                            }
                            SocksHttpMainActivity.this.vencimento.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Vencimento: </strong><strong><font color=\"green\">" + expirationDate + "</strong>"));

                            int daysRemaining = Integer.parseInt(expirationDays);
                            if (daysRemaining > 7) {
                                SocksHttpMainActivity.this.dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"green\">" + daysRemaining + "</strong>"));
                            } else if (daysRemaining == 0) {
                                SocksHttpMainActivity.this.dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + daysRemaining + "</strong>"));
                                SocksHttpMainActivity.this.alertdiasexpirado();
                            } else {
                                SocksHttpMainActivity.this.dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + daysRemaining + "</strong>"));
                                if (daysRemaining == 1) {
                                    SocksHttpMainActivity.this.alertdias(daysRemaining, " DIA");
                                } else {
                                    SocksHttpMainActivity.this.alertdias(daysRemaining, " DIAS");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                bufferedReader.close();
            } else {
                SocksHttpMainActivity.this.runOnUiThread(() -> {
                    SocksHttpMainActivity.this.vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Ocorreu um erro ao obter as informações do login!</strong>"));
                    SocksHttpMainActivity.this.user_limite.setText("");
                    SocksHttpMainActivity.this.dias_check.setText("");
                });
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    // Encerra o executor após a execução
    executor.shutdown();
}
    
    
    /*
    
    public void CheckUser1() {
    SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
    final String usuarioKey = prefsPrivate.getString(Settings.USUARIO_KEY, "");
    final String urlCheck = prefsPrivate.getString(Settings.URL, "");

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    executor.execute(() -> {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlCheck);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            JSONObject requestJson = new JSONObject();
            requestJson.put("user", usuarioKey);

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                dataOutputStream.writeBytes(requestJson.toString());
                dataOutputStream.flush();
            }

            if (httpURLConnection.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String responseLine;
                while ((responseLine = bufferedReader.readLine()) != null) {
                    JSONObject responseJson = new JSONObject(responseLine);
                    final String username = responseJson.getString("username");
                    final int currentConnections = responseJson.getInt("count_connection");
                    final String expirationDate = responseJson.getString("expiration_date");
                    final int remainingDays = responseJson.getInt("expiration_days");
                    final int maxConnections = responseJson.getInt("limiter_user");

                    handler.post(() -> {
                        try {
                            if ("not exist".equals(username)) {
                                vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Usuário não encontrado no banco de dados!</strong>"));
                                user_limite.setText("");
                                dias_check.setText("");
                                return;
                            }

                            if (currentConnections > maxConnections) {
                                alertlimiter(currentConnections, maxConnections);
                                startOrStopTunnel(this);
                                user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"red\">" + currentConnections + " | " + maxConnections + "</strong>"));
                            } else {
                                user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"green\">" + currentConnections + " | " + maxConnections + "</strong>"));
                            }

                            vencimento.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Vencimento: </strong><strong><font color=\"green\">" + expirationDate + "</strong>"));

                            if (remainingDays > 7) {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"green\">" + remainingDays + "</strong>"));
                            } else if (remainingDays == 0) {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + remainingDays + "</strong>"));
                                alertdiasexpirado();
                            } else {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + remainingDays + "</strong>"));
                                if (remainingDays == 1) {
                                    alertdias(remainingDays, " DIA");
                                } else {
                                    alertdias(remainingDays, " DIAS");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } else {
                handler.post(() -> {
                    vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Ocorreu um erro ao obter as informações do login!</strong>"));
                    user_limite.setText("");
                    dias_check.setText("");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void CheckUserSempopup() {
    SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
    final String username = prefsPrivate.getString(Settings.USUARIO_KEY, "");
    final String urlCheckUser = prefsPrivate.getString(Settings.urlcheckuser, "");

    // ExecutorService para operações em background
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());

    executor.execute(() -> {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(urlCheckUser).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);

            // Criar JSON e enviar os dados
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user", username);
            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                dataOutputStream.writeBytes(jsonObject.toString());
                dataOutputStream.flush();
            }

            // Processar resposta do servidor
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                // Interpretar o JSON da resposta
                JSONObject responseObject = new JSONObject(response.toString());
                String usernameResponse = responseObject.getString("username");
                int countConnection = responseObject.getInt("count_connection");
                String expirationDate = responseObject.getString("expiration_date");
                int expirationDays = responseObject.getInt("expiration_days");
                int userLimit = responseObject.getInt("limiter_user");

                // Atualizar a UI
                mainHandler.post(() -> {
                    try {
                        if (usernameResponse.equals("not exist")) {
                            vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Usuário não encontrado no banco de dados!</strong>"));
                            user_limite.setText("");
                            dias_check.setText("");
                        } else {
                            if (countConnection > userLimit) {
                                alertlimiter(countConnection, userLimit); // Limite excedido
                                startOrStopTunnel(this);
                                user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"red\">" +
                                        countConnection + " | " + userLimit + "</strong>"));
                            } else {
                                user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"green\">" +
                                        countConnection + " | " + userLimit + "</strong>"));
                            }

                            vencimento.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Vencimento: </strong><strong><font color=\"green\">" +
                                    expirationDate + "</strong>"));

                            if (expirationDays > 7) {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"green\">" +
                                        expirationDays + "</strong>"));
                            } else if (expirationDays == 0) {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" +
                                        expirationDays + "</strong>"));
                                alertdiasexpirado(); // Dias expirados
                            } else {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" +
                                        expirationDays + "</strong>"));
                                alertdias(expirationDays, expirationDays == 1 ? " DIA" : " DIAS");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                // Erro na resposta do servidor
                mainHandler.post(() -> {
                    vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Erro ao obter as informações do login!</strong>"));
                    user_limite.setText("");
                    dias_check.setText("");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Exibir erro de conexão
            mainHandler.post(() -> {
                vencimento.setText("");
                user_limite.setText("");
                dias_check.setText("");
            });
        }
    });
}
    */
    
    
    
    
    
    
    
    public void CheckUser2() {
    SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
    final String usuarioKey = prefsPrivate.getString(Settings.USUARIO_KEY, "");
    final String urlCheck = prefsPrivate.getString(Settings.URL, "");

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    executor.execute(() -> {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            // Configuração da conexão HTTP
            URL url = new URL(urlCheck);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            // Criação do JSON e envio
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user", usuarioKey);
            Log.i("JSON", jsonObject.toString());

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                dataOutputStream.writeBytes(jsonObject.toString());
                dataOutputStream.flush();
            }

            // Leitura da resposta
            if (httpURLConnection.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                // Processar a resposta JSON
                JSONObject responseObject = new JSONObject(response.toString());
                final String username = responseObject.getString("username");
                final int countConnection = responseObject.getInt("count_connection");
                final String expirationDate = responseObject.getString("expiration_date");
                final String expirationDays = responseObject.getString("expiration_days");
                final int limiterUser = responseObject.getInt("limiter_user");

                handler.post(() -> {
                    try {
                        if ("not exist".equals(username)) {
                            vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Usuário não encontrado no banco de dados!</strong>"));
                            user_limite.setText("");
                            dias_check.setText("");
                        } else {
                            if (countConnection > limiterUser) {
                                startOrStopTunnel(this);
                                user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"red\">" + countConnection + " | " + limiterUser + "</strong>"));
                            } else {
                                user_limite.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"green\">" + countConnection + " | " + limiterUser + "</strong>"));
                            }

                            vencimento.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Vencimento: </strong><strong><font color=\"green\">" + expirationDate + "</strong>"));

                            int daysRemaining = Integer.parseInt(expirationDays);
                            if (daysRemaining > 7) {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"green\">" + daysRemaining + "</strong>"));
                            } else if (daysRemaining == 0) {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + daysRemaining + "</strong>"));
                            } else {
                                dias_check.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + daysRemaining + "</strong>"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                handler.post(() -> {
                    vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Ocorreu um erro ao obter as informações do login!</strong>"));
                    user_limite.setText("");
                    dias_check.setText("");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(() -> {
                vencimento.setText("");
                user_limite.setText("");
                dias_check.setText("");
            });
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    });
}
    
    
    
    
    
    
    
    /*
    
    public void CheckUserC5G() {
		SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
		final String string = prefsPrivate.getString(Settings.USUARIO_KEY, "");
		final String string2 = prefsPrivate.getString(Settings.URL, "");
		new Thread(new Runnable() {
			public void run() {
				BufferedReader bufferedReader;
				try {

					HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(string2).openConnection();
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
					httpURLConnection.setRequestProperty("Accept", "application/json");
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setDoInput(true);
					JSONObject jSONObject = new JSONObject();
					jSONObject.put("user", string);
					Log.i("JSON", jSONObject.toString());
					DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
					dataOutputStream.writeBytes(jSONObject.toString());
					dataOutputStream.flush();
					dataOutputStream.close();
					if (httpURLConnection.getResponseCode() == 200) {
						bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
						while (true) {
							String readLine = bufferedReader.readLine();
							if (readLine == null) {
								break;
							}
							JSONObject jSONObject2 = new JSONObject(readLine);
							final String string = jSONObject2.getString("username");
							final int parseInt = Integer.parseInt(jSONObject2.getString("count_connection"));
							final String string2 = jSONObject2.getString("expiration_date");
							final String string3 = jSONObject2.getString("expiration_days");
							final int parseInt2 = Integer.parseInt(jSONObject2.getString("limiter_user"));
							SocksHttpMainActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									try {
										if (string.equals("not exist")) {
											SocksHttpMainActivity.this.runOnUiThread(new Runnable() {
												public void run() {
													SocksHttpMainActivity.this.vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Usuário não encontrado no banco de dados!</strong>"));
													SocksHttpMainActivity.this.user_limite.setText("");
													SocksHttpMainActivity.this.dias_check.setText("");
												}
											});
											return;
										}
										if (parseInt > parseInt2) {
											//SocksHttpMainActivity.this.alertlimiter(parseInt, parseInt2);
											SocksHttpMainActivity.this.startOrStopTunnel(SocksHttpMainActivity.this);
											TextView access$1900 = SocksHttpMainActivity.this.user_limite;
											access$1900.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"red\">" + parseInt + " | " + parseInt2 + "</strong>"));
										} else {
											TextView access$19002 = SocksHttpMainActivity.this.user_limite;
											access$19002.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Limite: </strong><strong><font color=\"green\">" + parseInt + " | " + parseInt2 + "</strong>"));
										}
										TextView access$1800 = SocksHttpMainActivity.this.vencimento;
										access$1800.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Vencimento: </strong><strong><font color=\"green\">" + string2 + "</strong>"));
										int parseInt = Integer.parseInt(string3);
										if (parseInt > 7) {
											TextView access$2000 = SocksHttpMainActivity.this.dias_check;
											access$2000.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"green\">" + parseInt + "</strong>"));
										} else if (parseInt == 0) {
											TextView access$20002 = SocksHttpMainActivity.this.dias_check;
											access$20002.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + parseInt + "</strong>"));
											//SocksHttpMainActivity.this.alertdiasexpirado();
										} else {
											TextView access$20003 = SocksHttpMainActivity.this.dias_check;
											access$20003.setText(Html.fromHtml("<strong><font color=\"#ffffff\">Dias restantes: </strong><strong><font color=\"red\">" + parseInt + "</strong>"));
											if (parseInt == 1) {
												//SocksHttpMainActivity.this.alertdias(parseInt, " DIA");
												return;
											}
											//SocksHttpMainActivity.this.alertdias(parseInt, " DIAS");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
						bufferedReader.close();
					} else {
						SocksHttpMainActivity.this.vencimento.setText(Html.fromHtml("<strong><font color=\"red\">Ocorreu um erro ao obter as informações do login!</strong>"));
						SocksHttpMainActivity.this.user_limite.setText("");
						SocksHttpMainActivity.this.dias_check.setText("");
					}
					httpURLConnection.disconnect();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				} catch (Throwable th) {
					th.addSuppressed(th);
				}
				Throwable th;
			}
		}).start();
	}

*/
    
    
    public class DrawerPanelMain implements NavigationView.OnNavigationItemSelectedListener {
        private DrawerLayout drawerLayout;
        private AppCompatActivity mActivity;
        private ActionBarDrawerToggle toggle;

        public DrawerPanelMain(AppCompatActivity appCompatActivity) {
            this.mActivity = appCompatActivity;
        }

        public DrawerLayout getDrawerLayout() {
            return this.drawerLayout;
        }

        public ActionBarDrawerToggle getToogle() {
            return this.toggle;
        }

        
        public void setDrawer(Toolbar toolbar) {
            
        }
        

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			int id = item.getItemId();

			switch(id)
			{

				case R.id.APNsettings:
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction("android.settings.APN_SETTINGS");
					this.mActivity.startActivity(intent);
					break;
				case R.id.BATTERY_OPTIMIZATION:
					try {
						Intent intent2 = new Intent();
						intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent2.setAction("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
						this.mActivity.startActivity(intent2);
						Toast.makeText((Context) this.mActivity,  "Battery optimization > All apps > " + getString(R.string.app_name) + " > Dont't optimize", Toast.LENGTH_LONG).show();
						break;
					} catch (ActivityNotFoundException unused) {
						Toast.makeText((Context) this.mActivity, "Não disponivel para o seu aparelho", Toast.LENGTH_LONG).show();
						break;
					}

				case R.id.miPhoneConfig:
					if (Utils.getAppInfo(this.mActivity) != null) {
						try {
							try {
								Intent intent4 = new Intent("android.intent.action.MAIN");
								intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent4.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
								this.mActivity.startActivity(intent4);
								break;
							} catch (Exception unused2) {
								Intent intent5 = new Intent("android.intent.action.MAIN");
								intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent5.setClassName("com.android.settings", "com.android.settings.RadioInfo");
								this.mActivity.startActivity(intent5);
								break;
							}
						} catch (Exception unused3) {
							
							break;
						}
					}
					break;
				case R.id.miSendFeedback:
					Intent feedbackIntent = new Intent(Intent.ACTION_SEND);
					feedbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
					feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, "Socks Injector - Feedback");
					feedbackIntent.setType("message/rfc822");
					startActivity(Intent.createChooser(feedbackIntent, "Enviar feedback"));
					return true;

				case R.id.miSettings:
					Intent intent7 = new Intent((Context) this.mActivity, (Class<?>) ConfigGeralActivity.class);
					intent7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					this.mActivity.startActivity(intent7);
					break;

				case R.id.termos:
					String url = config.getUrlTermos();
					Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mActivity.startActivity(Intent.createChooser(intent3, "Abrir com"));
					break;
			}

			return true;
		}
	}
    
    
    
    /*
    
    private void doUpdateLayoutC5g() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
		setStarterButton(starterButton, this);
		sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		// Carregar os valores salvos (se existirem)
		String savedUsername = sharedPreferences.getString(USERNAME_KEY, "");
		String savedPassword = sharedPreferences.getString(PASSWORD_KEY, "");
		username.setText(savedUsername);
		password.setText(savedPassword);

		// Adicionar listeners de texto para salvar os valores ao digitar
		username.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// Não é necessário implementar neste exemplo
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Salvar o valor atual
				String username = s.toString();
				saveString(USERNAME_KEY, username);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// Não é necessário implementar neste exemplo
			}
		});

		password.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// Não é necessário implementar neste exemplo
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Salvar o valor atual
				String password = s.toString();
				saveString(PASSWORD_KEY, password);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// Não é necessário implementar neste exemplo
			}
		});

	}
    
    
    
    
    
    
    
    
    private void doUpdateLayoutNovo() {
        CharSequence charSequence;
        SecurePreferences prefsPrivate = this.mConfig.getPrefsPrivate();
        boolean isTunnelActive = SkStatus.isTunnelActive();
        int i = prefsPrivate.getInt("tunnelType", PERMISSION_REQUEST_CODE);
        setStarterButton(this.starterButton, this);
        String charSequence2 = getText(R.string.no_value).toString();
        int i2 = 0;
        if (prefsPrivate.getBoolean("protegerConfig", false)) {
            this.proxyInputLayout.setEnabled(false);
            charSequence = "*******";
        } else {
            String privString = this.mConfig.getPrivString("proxyRemoto");
            if (!(privString == null || privString.isEmpty())) {
                charSequence2 = String.format("%s:%s", new Object[]{privString, this.mConfig.getPrivString("proxyRemotoPorta")});
            }
            this.proxyInputLayout.setEnabled(!isTunnelActive);
            charSequence = charSequence2;
        }
        this.proxyText.setText(charSequence);
        if (i == PERMISSION_REQUEST_CODE) {
            ((AppCompatRadioButton) findViewById(R.id.activity_mainSSHDirectRadioButton)).setChecked(true);
        } else if (i == 2) {
            ((AppCompatRadioButton) findViewById(R.id.activity_mainSSHProxyRadioButton)).setChecked(true);
        } else if (i == 3) {
            ((AppCompatRadioButton) findViewById(R.id.activity_mainSSHSSLRadioButton)).setChecked(true);
        }
        boolean i3 = isTunnelActive;
        this.loginLayout.setVisibility(0);
        this.configMsgText.setText("");
        this.configMsgLayout.setVisibility(8);
        while (i2 < this.metodoConexaoRadio.getChildCount()) {
            this.metodoConexaoRadio.getChildAt(i2).setEnabled(i3);
            i2 += PERMISSION_REQUEST_CODE;
        }
        spinnernme();
    }
    
    */
    
    
    
    
    private void doUpdateLayout() {
        String str;
    SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
    boolean isTunnelActive = SkStatus.isTunnelActive();
        
        
    int i = prefsPrivate.getInt("tunnelType", PERMISSION_REQUEST_CODE);
        
        
        setStarterButton(starterButton, this);
        
        String charSequence = getText(R.string.no_value).toString();
        if (prefsPrivate.getBoolean(SettingsConstants.CONFIG_PROTEGER_KEY, false)) {
            this.proxyInputLayout.setEnabled(false);
            str = "*******";
        } else {
            String privString = this.mConfig.getPrivString(SettingsConstants.PROXY_IP_KEY);
            if (privString != null && !privString.isEmpty()) {
                charSequence = String.format("%s:%s", privString, this.mConfig.getPrivString(SettingsConstants.PROXY_PORTA_KEY));
            }
            this.proxyInputLayout.setEnabled(!isTunnelActive);
            str = charSequence;
        }
        this.proxyText.setText(str);
        if (i == 1) {
            ((AppCompatRadioButton) findViewById(R.id.activity_mainSSHDirectRadioButton)).setChecked(true);
        } else if (i == 2) {
            ((AppCompatRadioButton) findViewById(R.id.activity_mainSSHProxyRadioButton)).setChecked(true);
        } else if (i == 3) {
            ((AppCompatRadioButton) findViewById(R.id.activity_mainSSHSSLRadioButton)).setChecked(true);
        }
        boolean z = !isTunnelActive;
        this.loginLayout.setVisibility(0);
        this.configMsgText.setText("");
        this.configMsgLayout.setVisibility(8);
        for (int i2 = 0; i2 < this.metodoConexaoRadio.getChildCount(); i2++) {
            this.metodoConexaoRadio.getChildAt(i2).setEnabled(z);
        }
        
        spinnernme();
        
        /*
        //ESSO CHAMA UMA CLASSE QUE VERIFICA O NOME DO APP ONLINE E FECHA SE NAO BATER O NOME QUE ESTIVER NO RESOURCES
        new ImageDownload(this).execute();
        */
        
        serverSpinner.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						spinnerTouched = true; // User DID touched the spinner!
					}

					return false;
				}
			});


		final SharedPreferences prefsave = mConfig.getPrefsPrivate();

		serverSpinner.setSelection(prefsave.getInt("LastSelectedServer", 0));
		serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
					SharedPreferences prefs = mConfig.getPrefsPrivate();
					SharedPreferences.Editor edit = prefs.edit();
					edit.putInt("LastSelectedServer", p3).apply();

					if (spinnerTouched) {
						// Do something
						new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {

									loadServerData();
								}
							}, 2000);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1) {

				}
			});
		payloadSpinner.setSelection(prefsave.getInt("LastSelectedPayload", 0));
		payloadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
					SharedPreferences prefs = mConfig.getPrefsPrivate();
					SharedPreferences.Editor edit = prefs.edit();
					edit.putInt("LastSelectedPayload", p3).apply();
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1) {
				}
			});
}

    
    
    
    
    
    
    
    
    
    
   
	private void saveString(String key, String value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}

	private int mainposition() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		return prefs.getInt("LastSelectedServer", 0);//bygatesccn
	}
	private int mainposition1() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		return prefs.getInt("LastSelectedPayload", 0);//bygatesccn
	}
    
    
    
    
    
    
    //METODO COM V2RAY 22 04 25
    
    private void loadServerData() {
		String str;
		String str2;
        //String str3;
		try {
			SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
			SharedPreferences.Editor edit = prefsPrivate.edit();
			this.serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
					SocksHttpMainActivity.this.mConfig.getPrefsPrivate().edit().putInt("LastSelectedServer", i).apply();
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
				}
			});
			this.payloadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
					SocksHttpMainActivity.this.mConfig.getPrefsPrivate().edit().putInt("LastSelectedPayload", i).apply();
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
				}
			});
			int selectedItemPosition = this.serverSpinner.getSelectedItemPosition();
			int selectedItemPosition2 = this.payloadSpinner.getSelectedItemPosition();
			String string = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("ServerIP");
			String string2 = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("ServerPort");
			String string3 = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("SSLPort");
			String string4 = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("CheckUser");

			String string5 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("Payload");
			String string6 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SNI");
			String string7 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("TlsIP");

			String string8 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("ProxyIP");
			String string9 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("ProxyPort");
            String string10 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SlowChave");
            String string11 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SlowNameServer");
            String string12 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SlowDns");
			String string13 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("Info");
            
            
            //String authlatamsrc = config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("apilatamsrcv2ray");
            //boolean v2ray = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getBoolean("isV2ray");
            //String v24 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("v2rayJson");
            
            String string100 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("Name");
            //String stringpx;
            
			if (string7.equals("[app_host]")) {
				string7 = string;
			}
			if (string6.equals("[app_host]")) {
				string6 = string;
			}
			if (string8.equals("[app_host]")) {
				string8 = string;
			}
			if (string5.contains("[app_host]")) {
				string5 = string5.replace("[app_host]", string);
			}
            


			edit.putString(Settings.USUARIO_KEY, username.getEditableText().toString());
			edit.putString(Settings.SENHA_KEY, password.getEditableText().toString());
			SharedPreferences.Editor editor = edit;
            String string72 = string7;
			String str4 = string7;
            String str40 = string6;
            String str50 = string;
            
            
            String currentProxyIP = prefsPrivate.getString(SettingsConstants.PROXY_IP_KEY, "");
            
			if (string13.equals("Ssl")) {
				str = string8;
				prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 3).apply();
                //prefsPrivate.edit().putString("sshServer", str50).apply();
                prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				//prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, (string3)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_SNI, string6).apply();
                prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
			} else {
				str = string8;
			}
			if (string13.equals("Direct")) {
                prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 1).apply();
                str2 = string6;
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, (string2)).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_IP_KEY, string8).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
			} else {
				str2 = string6;
			}
			if (string13.equals("Proxy")) {
                //str3 = string;
				prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 2).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, (string2)).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_IP_KEY, string8).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
			} 
            
            
            if (string13.equals("Tlsws")) {
                prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 4).apply();
                prefsPrivate.edit().putString("sshServer", str4).apply();
                prefsPrivate.edit().putString("sshPort", string3).apply();
                prefsPrivate.edit().putString("proxyTlsRemoto", string72).apply();
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
                prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                prefsPrivate.edit().putString(SettingsConstants.CUSTOM_SNI, str2).apply();
                
			}
            
            if (string13.equals("V2ray")) {
				prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 5).apply();
				prefsPrivate.edit().putString(SettingsConstants.V2RAY_JSON, string5).apply();
                prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                //prefsPrivate.edit().putString(SettingsConstants.APILATAMIP, authlatamsrc).apply();
				//boolean CheckUser = false;
				
			}
            
            if (string13.equals("Slowdns")) {
                prefsPrivate.edit().putString(SettingsConstants.CHAVE_KEY, string10).apply();
                prefsPrivate.edit().putString(SettingsConstants.NAMESERVER_IP_KEY, string11).apply();
                //prefsPrivate.edit().putString("SlowNameServer", string11).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.DNS_IP_KEY, string12).apply();
                //prefsPrivate.edit().putString("proxyDnsRemoto", string12).apply();
                prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, "127.0.0.1").apply();
                prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, "2222").apply();
                prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, true).apply();
                prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 6).apply();
                prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
                //prefsPrivate.edit().remove("proxyDnsRemoto").apply();
            }
            
            if (string13.equals("Auto")) {
    
            prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 7).apply(); // Tipo Auto

}
            
			prefsPrivate.edit().putString(SettingsConstants.APP_HOST, string).apply();
            prefsPrivate.edit().putString(SettingsConstants.URL, string4).apply();
            edit.apply();
            
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    /*
    
    //METODO SEM V2RAY 22 04 25
    
    private void loadServerData() {
		String str;
		String str2;
        //String str3;
		try {
			SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
			SharedPreferences.Editor edit = prefsPrivate.edit();
			this.serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
					SocksHttpMainActivity.this.mConfig.getPrefsPrivate().edit().putInt("LastSelectedServer", i).apply();
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
				}
			});
			this.payloadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
					SocksHttpMainActivity.this.mConfig.getPrefsPrivate().edit().putInt("LastSelectedPayload", i).apply();
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
				}
			});
			int selectedItemPosition = this.serverSpinner.getSelectedItemPosition();
			int selectedItemPosition2 = this.payloadSpinner.getSelectedItemPosition();
			String string = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("ServerIP");
			String string2 = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("ServerPort");
			String string3 = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("SSLPort");
			String string4 = this.config.getServersArray().getJSONObject(selectedItemPosition).getString("CheckUser");

			String string5 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("Payload");
			String string6 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SNI");
			String string7 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("TlsIP");

			String string8 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("ProxyIP");
			String string9 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("ProxyPort");
            String string10 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SlowChave");
            String string11 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SlowNameServer");
            String string12 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("SlowDns");
			String string13 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("Info");
            
            
            //String authlatamsrc = config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("apilatamsrcv2ray");
            //boolean v2ray = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getBoolean("isV2ray");
            //String v24 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("v2rayJson");
            
            String string100 = this.config.getNetworksArray().getJSONObject(selectedItemPosition2).getString("Name");
            //String stringpx;
            
			if (string7.equals("[app_host]")) {
				string7 = string;
			}
			if (string6.equals("[app_host]")) {
				string6 = string;
			}
			if (string8.equals("[app_host]")) {
				string8 = string;
			}
			if (string5.contains("[app_host]")) {
				string5 = string5.replace("[app_host]", string);
			}
            


			edit.putString(Settings.USUARIO_KEY, username.getEditableText().toString());
			edit.putString(Settings.SENHA_KEY, password.getEditableText().toString());
			SharedPreferences.Editor editor = edit;
            String string72 = string7;
			String str4 = string7;
            String str40 = string6;
            String str50 = string;
            
            
            String currentProxyIP = prefsPrivate.getString(SettingsConstants.PROXY_IP_KEY, "");
            
			if (string13.equals("Ssl")) {
				str = string8;
				prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 3).apply();
                //prefsPrivate.edit().putString("sshServer", str50).apply();
                prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				//prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, (string3)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_SNI, string6).apply();
                prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
			} else {
				str = string8;
			}
			if (string13.equals("Direct")) {
                prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 1).apply();
                str2 = string6;
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, (string2)).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_IP_KEY, string8).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
			} else {
				str2 = string6;
			}
			if (string13.equals("Proxy")) {
                //str3 = string;
				prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 2).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, string).apply();
				prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, (string2)).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_IP_KEY, string8).apply();
				prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
			} 
            
            
            if (string13.equals("Tlsws")) {
                prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 4).apply();
                prefsPrivate.edit().putString("sshServer", str4).apply();
                prefsPrivate.edit().putString("sshPort", string3).apply();
                prefsPrivate.edit().putString("proxyTlsRemoto", string72).apply();
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
                prefsPrivate.edit().putString(SettingsConstants.PROXY_PORTA_KEY, (string9)).apply();
				prefsPrivate.edit().putString(SettingsConstants.CUSTOM_PAYLOAD_KEY, string5).apply();
				prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                prefsPrivate.edit().putString(SettingsConstants.CUSTOM_SNI, str2).apply();
                
			}
            
            if (string13.equals("Slowdns")) {
                prefsPrivate.edit().putString(SettingsConstants.CHAVE_KEY, string10).apply();
                prefsPrivate.edit().putString(SettingsConstants.NAMESERVER_IP_KEY, string11).apply();
                //prefsPrivate.edit().putString("SlowNameServer", string11).apply();
                
                prefsPrivate.edit().putString(SettingsConstants.DNS_IP_KEY, string12).apply();
                //prefsPrivate.edit().putString("proxyDnsRemoto", string12).apply();
                prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_KEY, "127.0.0.1").apply();
                prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, "2222").apply();
                prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, true).apply();
                prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 6).apply();
                prefsPrivate.edit().putString(SettingsConstants.urlcheckuser, string4).apply();
                prefsPrivate.edit().putString(SettingsConstants.PAY_NAME, (string100)).apply();
                //prefsPrivate.edit().remove("proxyDnsRemoto").apply();
            }
            
			prefsPrivate.edit().putString(SettingsConstants.APP_HOST, string).apply();
            prefsPrivate.edit().putString(SettingsConstants.URL, string4).apply();
            edit.apply();
            
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    */
   
    
    
    
    
    
    
    private void loadportasudp() {
    try {
        // Reset the contador if it's greater than zero
        if (this.contador > 0) {
            this.contador = 0;
        }

        // Clear the udpList if it's not empty
        if (this.udpList.size() > 0) {
            this.udpList.clear();
        }

        // Get the UDP array from the configuration
        JSONArray udpArray = this.config.getUdpArray();
        
        // Iterate through the UDP array and add each item to the udpList
        for (int i = 0; i < udpArray.length(); i++) {
            JSONObject udpObject = udpArray.getJSONObject(i);
            this.udpList.add(udpObject);
            this.contador++;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    private void loadNetworks() {
		try {
			if (payloadList.size() > 0) {
				payloadList.clear();
				payloadAdapter.notifyDataSetChanged();
			}
			for (int i = 0; i < config.getNetworksArray().length(); i++) {
				JSONObject obj = config.getNetworksArray().getJSONObject(i);
				payloadList.add(obj);
				payloadAdapter.notifyDataSetChanged();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    
    private void loadServer() {
		try {
			if (serverList.size() > 0) {
				serverList.clear();
				serverAdapter.notifyDataSetChanged();
			}
			for (int i = 0; i < config.getServersArray().length(); i++) {
				JSONObject obj = config.getServersArray().getJSONObject(i);
				serverList.add(obj);
				serverAdapter.notifyDataSetChanged();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public void noUpdateDialog() {
        new SweetAlertDialog(this, 2).setTitleText("MODDERC5G").setContentText("Sua configuração está na versão mais recente").show();
        this.pDialog.dismiss();
    }
    
    public void restartApp() {
        
        SocksHttpApp app = SocksHttpApp.getApp();
        app.startActivity(Intent.makeRestartActivityTask(app.getPackageManager().getLaunchIntentForPackage(app.getPackageName()).getComponent()));
        Runtime.getRuntime().exit(0);
        
    }
    
    
    private synchronized void doSaveData() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		SharedPreferences.Editor edit = prefs.edit();
        
        edit.putString(Settings.USUARIO_KEY, username.getEditableText().toString());
			edit.putString(Settings.SENHA_KEY, password.getEditableText().toString());
	
		edit.apply();
	}

	private void setSpinner(){
		SharedPreferences prefs = mConfig.getPrefsPrivate();
        int server = prefs.getInt("LastSelectedServer", 0);
        int payload = prefs.getInt("LastSelectedPayload", 0);
        serverSpinner.setSelection(server);
        payloadSpinner.setSelection(payload);
	}

	private void saveSpinner(){
		SharedPreferences prefs = mConfig.getPrefsPrivate();
        SharedPreferences.Editor edit = prefs.edit();
        int server = serverSpinner.getSelectedItemPosition();
        int payload = payloadSpinner.getSelectedItemPosition();
        edit.putInt("LastSelectedServer", server);
        edit.putInt("LastSelectedPayload", payload);
        edit.apply();
	}
    
    
    private void setupSSH() {
        try {
            SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
            prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 2).apply();
            SharedPreferences.Editor edit = prefsPrivate.edit();
            edit.putString(SettingsConstants.SERVIDOR_PORTA_KEY, this.config.getServersArray().getJSONObject(this.serverSpinner.getSelectedItemPosition()).getString("ServerPort")).apply();
            if (prefsPrivate.getString(SettingsConstants.PROXY_PORTA_KEY, "").isEmpty()) {
                edit.putString(SettingsConstants.PROXY_PORTA_KEY, "8080").apply();
            }
        } catch (Exception unused) {
        }
    }
    
    
    
    private void setupSSL() {
        setupSSH();
        SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
        prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 3).apply();
        prefsPrivate.edit().putString(SettingsConstants.SERVIDOR_PORTA_KEY, "443").apply();
    }
    
    
    public static void updateMainViews(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(UPDATE_VIEWS));
    }
    
    
    public static void updateMainViewsOri(Context context) {
		Intent updateView = new Intent(UPDATE_VIEWS);
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(updateView);
	}
    
    
    public void doTabs() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.mLogAdapter = new LogsAdapter(linearLayoutManager, this);
        RecyclerView findViewById = findViewById(R.id.recyclerLog);
        this.logList = findViewById;
        findViewById.setAdapter(this.mLogAdapter);
        this.logList.setLayoutManager(linearLayoutManager);
        this.mLogAdapter.scrollToLastPosition();
        this.vp = findViewById(R.id.viewpager);
        this.tabs = (TabLayout) findViewById(R.id.tablayout);
        this.vp.setAdapter(new MyAdapter(Arrays.asList(tabTitle)));
        this.vp.setOffscreenPageLimit(2);
        this.tabs.setTabMode(1);
        this.tabs.setTabGravity(0);
        this.tabs.setupWithViewPager(this.vp);
    }
    
    
    
    
    
public void onBackPressed() {
    new SweetAlertDialog(this, 3)
        .setTitleText(getString(R.string.attention))
        .setContentText(getString(R.string.alert_exit))
        .setConfirmText(getString(R.string.exit))
        .setConfirmClickListener(sweetAlertDialog -> {
            tocarAudioFechado();

            // Aguarda 2 segundos e depois executa o exitAll
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Utils.exitAll(SocksHttpMainActivity.this);
            }, 2000);
        })
        .setCancelText(getString(R.string.minimize))
        .showCancelButton(true)
        .setCancelClickListener(sweetAlertDialog -> {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SocksHttpMainActivity.this.startActivity(intent);
        })
        .show();
}
    
    
    
    
    private void tocarAudioFechado() {
    //new audiofechado().executar(this);
        
        this.mConfig = new Settings(this);
        
         if (this.mConfig != null && this.mConfig.audioFechado()) {
        new audiofechado().executar(this);
  }
}
    
    
    /*
    //METODO ORIGINAL 15 04 25
    
public void onBackPressed() {
    new SweetAlertDialog(this, 3)
        .setTitleText(getString(R.string.attention))
        .setContentText(getString(R.string.alert_exit))
        .setConfirmText(getString(R.string.exit))
        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                    
                Utils.exitAll(SocksHttpMainActivity.this);
                    
            }
        })
        .setCancelText(getString(R.string.minimize))
        .showCancelButton(true)
        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SocksHttpMainActivity.this.startActivity(intent);
            }
        })
        .show();
}
    */
    
    
    
    
    /*o onback abaixo nao mostra o popup de minimizar ele simplesmente minimiza ao clicar em voltar no celular
    
    public void onBackPressed2() {
        Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SocksHttpMainActivity.this.startActivity(intent);
    }
    
    */
    
    
    
    
    
    
    
    
    	
    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
    }
    
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        SecurePreferences.Editor edit = this.mConfig.getPrefsPrivate().edit();
        switch (radioGroup.getCheckedRadioButtonId()) {
            
        }
        edit.apply();
        doUpdateLayout();
    }
    
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mDrawerPanel.getToogle() != null) {
            this.mDrawerPanel.getToogle().onConfigurationChanged(configuration);
        }
    }
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    
    protected void onDestroy() {
        super.onDestroy();
        
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mActivityReceiver);
        
    }
    
    
    public void onPostCreate(Bundle bundle, PersistableBundle persistableBundle) {
        super.onPostCreate(bundle, persistableBundle);
        if (this.mDrawerPanel.getToogle() != null) {
            this.mDrawerPanel.getToogle().syncState();
        }
    }
    
    
    
    
    
    
    
    
public void onResume() {
    super.onResume();
    setSpinner();
    setuserpass();
    this.typenetwork = true;

    final Handler handler = new Handler();
    final Timer timer = new Timer();
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!SocksHttpMainActivity.this.typenetwork) {
                        timer.cancel();
                        timer.purge();
                        return;
                    }
                                
                    SocksHttpMainActivity socksHttpMainActivity = SocksHttpMainActivity.this;
                    socksHttpMainActivity.checkNetwork();
                    socksHttpMainActivity.checkNetworkMain();
                    socksHttpMainActivity.checkNetworkMain2();
                                
                }
            });
        }
    }, 0L, 3000L);

    SkStatus.addStateListener(this);
        
}
    
    
    
    /*
    public void onResume2() {
        super.onResume();
        setSpinner();
        setuserpass();
        this.typenetwork = true;
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() { // from class: com.vpnmoddervpn.vpn.SocksHttpMainActivity.12
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                handler.post(new Runnable() { // from class: com.vpnmoddervpn.vpn.SocksHttpMainActivity.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!SocksHttpMainActivity.this.typenetwork) {
                            timer.cancel();
                            timer.purge();
                            return;
                        }
                        SocksHttpMainActivity socksHttpMainActivity = SocksHttpMainActivity.this;
                        socksHttpMainActivity.checkNetwork();
                        socksHttpMainActivity.checkNetworkMain();
                        socksHttpMainActivity.checkNetworkMain2();
                    }
                });
            }
        }, 0L, 3000L);
        SkStatus.addStateListener(this);
    }
    */
    
    public void setuserpass() {
        SharedPreferences prefsPrivate = this.mConfig.getPrefsPrivate();
        this.username.setText(prefsPrivate.getString(SettingsConstants.USUARIO_KEY, ""));
        this.password.setText(prefsPrivate.getString(SettingsConstants.SENHA_KEY, ""));
    }
    
    
    public void showBoasVindas() {
        new SweetAlertDialog(this, 3).setTitleText("Atenção").setContentText("MODDERC5G oferece a segurança online e a confidencialidade que cada um de nós merece. \nA melhor e mais rápida VPN desenvolvida por seus usuários, para seus usuários.\n\nDica: antes de se conectar coloque sua apn em protocolo IPV4!\n\nCom nosso aplicativo nenhum registro é salvo, seus dados estão 100% seguros!").setConfirmText("Certo").show();
    }
    
    
    public void showExitDialog() {
        AlertDialog create = new AlertDialog.Builder(this).create();
        create.setTitle(getString(R.string.attention));
        create.setMessage(getString(R.string.alert_exit));
        create.setButton(-1, getString(R.string.alert_exit), new DialogInterface.OnClickListener() { // from class: com.vpnmoddervpn.vpn.SocksHttpMainActivity.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.exitAll(SocksHttpMainActivity.this);
            }
        });
        create.setButton(-2, getString(R.string.minimize), new DialogInterface.OnClickListener() { // from class: com.vpnmoddervpn.vpn.SocksHttpMainActivity.14
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SocksHttpMainActivity.this.startActivity(intent);
            }
        });
        create.show();
    }
    
    @Override // com.romzkie.ultrasshservice.logger.SkStatus.StateListener
	public void updateState(final String state, String msg, int localizedResId, final ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				doUpdateLayout();
				if (SkStatus.isTunnelActive()){

					if (level.equals(ConnectionStatus.LEVEL_CONNECTED)){
                            
                        
						connectionStatus.setText("CONECTADO");
						connectionStatus.setTextColor(Color.parseColor("#FF5EFF00"));
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								CheckUser2();
                                        
                                        username.setEnabled(false);
                                        password.setEnabled(false);
                                        serverSpinner.setEnabled(false);
                                        payloadSpinner.setEnabled(false);
							}
						}, 1000);

					}
					if (level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)){
						connectionStatus.setText("DESCONECTADO");
                            connectionStatus.setTextColor(Color.parseColor("#FFFFFFFF"));

					}

					if (level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)){
						connectionStatus.setText("AUTENTICANDO");
                            connectionStatus.setTextColor(Color.parseColor("#FFFFFFFF"));
					}

					if (level.equals(ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET)){
						connectionStatus.setText("CONECTANDO");
						connectionStatus.setTextColor(Color.parseColor("#FFFFFFFF"));
					}
					if (level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)){
						connectionStatus.setText("FALHA NA AUTENTICAÇÃO");
					}
					if (level.equals(ConnectionStatus.UNKNOWN_LEVEL)){
						connectionStatus.setText("DESCONECTADO");
						connectionStatus.setTextColor(Color.parseColor("#FFFFFFFF"));
					}
					if (level.equals(ConnectionStatus.LEVEL_NONETWORK)){
						connectionStatus.setText("SEM INTERNET");
                            connectionStatus.setTextColor(Color.parseColor("#FFFFFFFF"));
					}
				}
			}
			private void progressBar() {
			}
		});
	}
    
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(NOTIFICATION_PERMISSION) != 0) {
            requestPermissions(new String[]{NOTIFICATION_PERMISSION}, 1);
        }
        
        //new audioaberto().executar(this);
        
        tunnelManagerThread = new TunnelManagerThread(handler, this);
        
        this.mContext = this;
        instance = this;
        
        this.mHandler = new Handler();
        spinnernme();
        Toast.makeText(this, new String(Base64.decode(" QmVtLXZpbmRvIGFvIE1vZGRlciBUdW5uZWw= ",0)), 1).show();
        
        this.mConfig = new Settings(this);
        this.mDrawerPanel = new DrawerPanelMain(this);
        
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setRequestedOrientation(1);
        startBackgroundTask();
        
        SharedPreferences sharedPreferences = getSharedPreferences(SocksHttpApp.PREFS_GERAL, 0);
        boolean z = sharedPreferences.getBoolean("connect_first_time", true);
        int i = sharedPreferences.getInt("last_version", 0);
        if (z) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("connect_first_time", false);
            edit.apply();
            Settings.setDefaultConfig(this);
            showBoasVindas();
        }
        
        doLayout();
        
        loadServer();
        loadNetworks();
        loadportasudp();
        updateConfig(true);
        
        /*
        //ESSO CHAMA UMA CLASSE QUE VERIFICA O NOME DO APP ONLINE E FECHA SE NAO BATER O NOME QUE ESTIVER NO RESOURCES
        new ImageDownload(this).execute();
        */
        
		
        IntentFilter intentFilter = new IntentFilter();
        if (!getResources().getString(getResources().getIdentifier(APP_NAME, "string", getPackageName())).contains("MODDERC5G")) {
            finish();
        }
        
        intentFilter.addAction(UPDATE_VIEWS);
        intentFilter.addAction(OPEN_LOGS);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mActivityReceiver, intentFilter);
        doUpdateLayout();
        
        
        if (this.mConfig != null && this.mConfig.audioAberto()) {
        new audioaberto().executar(this);
            }
        

	}
    
    
    /*
    protected void onCreateNovo(Bundle bundle) {
        String searchedString;
        String id;
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= 33) {
            searchedString = NOTIFICATION_PERMISSION;
            if (checkSelfPermission(searchedString) != 0) {
                requestPermissions(new String[]{searchedString}, PERMISSION_REQUEST_CODE);
            }
        }
        this.mHandler = new Handler();
        spinnernme();
        Toast.makeText(this, new String(Base64.decode(" QmVtLXZpbmRvIGFvIE1vZGRlciBUdW5uZWw= ",0)), 1).show();
        
        this.mConfig = new Settings(this);
        
        this.mDrawerPanel = new DrawerPanelMain(this);
        //new OneSocks(this, this, this.makefat).init();
        
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setRequestedOrientation(PERMISSION_REQUEST_CODE);
        startBackgroundTask();
        
        SharedPreferences sharedPreferences = getSharedPreferences(SocksHttpApp.PREFS_GERAL, 0);
        //searchedString = getSharedPreferences("SocksHttpGERAL", 0);
        String str = "connect_first_time";
        boolean z = sharedPreferences.getBoolean(str, true);
        String str2 = "last_version";
        int i = sharedPreferences.getInt(str2, 0);
        if (z) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean(str, false);
            edit.apply();
            Settings.setDefaultConfig(this);
            showBoasVindas();
        }
        
        doLayout();
        IntentFilter intentFilter = new IntentFilter();
        
        if (!getResources().getString(getResources().getIdentifier(APP_NAME, "string", getPackageName())).contains("DK CONNECT")) {
            //finish();
        }
        
        
        intentFilter.addAction(UPDATE_VIEWS);
        intentFilter.addAction(OPEN_LOGS);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mActivityReceiver, intentFilter);
        doUpdateLayout();
    }
    
    */
    
    
    
    
    
    
    
    
    
    
    /*
    
    
    private void doLayoutNovo() {
        setContentView(R.layout.activity_main_drawer);
        ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
        this.config = configUtil;
        this.serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
        String ModderCorCaixaServ = configUtil.ModderCorCaixaServ();
        int backgroundColor = getDefaultColor();
        if (ModderCorCaixaServ != null && !ModderCorCaixaServ.isEmpty()) {
            backgroundColor = parseColor(ModderCorCaixaServ, getDefaultColor());
        }
        Drawable buttonBackground = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_show_spec);
        buttonBackground.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
        this.serverSpinner.setBackground(buttonBackground);
        this.payloadSpinner = (Spinner) findViewById(R.id.payloadSpinner);
        String ModderCorCaixaServ2 = configUtil.ModderCorCaixaPay();
        int backgroundColor2 = getDefaultColor();
        if (ModderCorCaixaServ2 != null && !ModderCorCaixaServ2.isEmpty()) {
            backgroundColor2 = parseColor(ModderCorCaixaServ2, getDefaultColor());
        }
        Drawable buttonBackground2 = ContextCompat.getDrawable(this, R.drawable.abc_btn_default_mtrl_trap);
        buttonBackground2.setColorFilter(backgroundColor2, PorterDuff.Mode.MULTIPLY);
        this.payloadSpinner.setBackground(buttonBackground2);
        this.username = findViewById(R.id.username);
        String ModderCorCaixaPay = configUtil.ModderCorCaixaUsuario();
        int backgroundColor3 = getDefaultColor();
        if (ModderCorCaixaPay != null && !ModderCorCaixaPay.isEmpty()) {
            backgroundColor3 = parseColor(ModderCorCaixaPay, getDefaultColor());
        }
        Drawable buttonBackground3 = ContextCompat.getDrawable(this, R.drawable.abc_btn_default_mtrl_trap);
        buttonBackground3.setColorFilter(backgroundColor3, PorterDuff.Mode.MULTIPLY);
        this.username.setBackground(buttonBackground3);
        this.password = findViewById(R.id.password);
        String ModderCorCaixaUsuario = configUtil.ModderCorCaixaSenha();
        int backgroundColor4 = getDefaultColor();
        if (ModderCorCaixaUsuario != null && !ModderCorCaixaUsuario.isEmpty()) {
            backgroundColor4 = parseColor(ModderCorCaixaUsuario, getDefaultColor());
        }
        Drawable buttonBackground4 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_abc_spec);
        buttonBackground4.setColorFilter(backgroundColor4, PorterDuff.Mode.MULTIPLY);
        this.password.setBackground(buttonBackground4);
        this.fundobotaoiniciar = (LinearLayout) findViewById(R.id.fundobotaoiniciar);
        String ModderCorCaixaSenha = configUtil.ModderCorBotaoIniciar();
        int backgroundColor5 = getDefaultColor();
        if (ModderCorCaixaSenha != null && !ModderCorCaixaSenha.isEmpty()) {
            backgroundColor5 = parseColor(ModderCorCaixaSenha, getDefaultColor());
        }
        Drawable buttonBackground5 = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_spec_material);
        buttonBackground5.setColorFilter(backgroundColor5, PorterDuff.Mode.MULTIPLY);
        this.fundobotaoiniciar.setBackground(buttonBackground5);
        this.fundobotaoregistro = (LinearLayout) findViewById(R.id.fundobotaoregistro);
        String ModderCorBotaoIniciar = configUtil.ModderCorBotaoLog();
        int backgroundColor6 = getDefaultColor();
        if (ModderCorBotaoIniciar != null && !ModderCorBotaoIniciar.isEmpty()) {
            backgroundColor6 = parseColor(ModderCorBotaoIniciar, getDefaultColor());
        }
        Drawable buttonBackground6 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_transformation_sheet_expand);
        buttonBackground6.setColorFilter(backgroundColor6, PorterDuff.Mode.MULTIPLY);
        this.fundobotaoregistro.setBackground(buttonBackground6);
        this.fundocaixacentral = (LinearLayout) findViewById(R.id.fundocaixacentral);
        String ModderCorBotaoLog = configUtil.ModderCorCaixaCentral();
        int backgroundColor7 = getDefaultColor();
        if (ModderCorBotaoLog != null && !ModderCorBotaoLog.isEmpty()) {
            backgroundColor7 = parseColor(ModderCorBotaoLog, getDefaultColor());
        }
        Drawable buttonBackground7 = ContextCompat.getDrawable(this, R.drawable.abc_default_mtrl_trap);
        buttonBackground7.setColorFilter(backgroundColor7, PorterDuff.Mode.MULTIPLY);
        this.fundocaixacentral.setBackground(buttonBackground7);
        this.fundocaixaconexao = (LinearLayout) findViewById(R.id.fundocaixaconexao);
        String ModderCorCaixaCentral = configUtil.ModderCorCaixaConexao();
        int backgroundColor8 = getDefaultColor();
        if (ModderCorCaixaCentral != null && !ModderCorCaixaCentral.isEmpty()) {
            backgroundColor8 = parseColor(ModderCorCaixaCentral, getDefaultColor());
        }
        Drawable buttonBackground8 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_abc_spec);
        buttonBackground8.setColorFilter(backgroundColor8, PorterDuff.Mode.MULTIPLY);
        this.fundocaixaconexao.setBackground(buttonBackground8);
        this.fundobotaoferramentas = (LinearLayout) findViewById(R.id.fundobotaoferramentas);
        String ModderCorCaixaConexao = configUtil.ModderCorBotaoConfig();
        int backgroundColor9 = getDefaultColor();
        if (ModderCorCaixaConexao != null && !ModderCorCaixaConexao.isEmpty()) {
            backgroundColor9 = parseColor(ModderCorCaixaConexao, getDefaultColor());
        }
        Drawable buttonBackground9 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_abc_spec);
        buttonBackground9.setColorFilter(backgroundColor9, PorterDuff.Mode.MULTIPLY);
        this.fundobotaoferramentas.setBackground(buttonBackground9);
        this.operadoraMain = (TextView) findViewById(R.id.operadoraMain);
        checkNetworkMain();
        CustomPayloadName();
        updateConfigAndDeleteFileIfNeeded();
        this.txtVersion2 = (TextView) findViewById(R.id.txtVersion2);
        checkNetworkMain2();
        ConfigUtil configUtil2 = new ConfigUtil(getApplicationContext());
        this.config = configUtil2;
        String version = configUtil2.getVersion();
        TextView versionTextView = (TextView) findViewById(R.id.txtVersion);
        versionTextView.setText(version);
        this.logoonline = (ImageView) findViewById(R.id.logoonline);
        exibirImagem1();
        this.fundoonline = (ImageView) findViewById(R.id.fundoonline);
        exibirImagem2();
        //loadServer1();
        //this.textstatus = (TextView) findViewById(2131296664);
        this.toolbar_main = findViewById(R.id.toolbar_main);
        DrawerPanelMain drawerPanelMain = this.mDrawerPanel;
        Toolbar toolbar = this.toolbar_main;
        //this.status = (TextView) findViewById(2131296460);
        this.mainLayout = (LinearLayout) findViewById(R.id.activity_mainLinearLayout);
        this.loginLayout = (LinearLayout) findViewById(R.id.activity_mainInputPasswordLayout);
        this.starterButton = (Button) findViewById(R.id.activity_starterButtonMain);
        this.username = findViewById(R.id.username);
        this.password = findViewById(R.id.password);
        this.inputPwShowPass = (ImageButton) findViewById(R.id.mostrarsenha);
        ((TextView) findViewById(R.id.activity_mainAutorText)).setOnClickListener(this);
        this.proxyInputLayout = (LinearLayout) findViewById(R.id.activity_mainInputProxyLayout);
        this.proxyText = (TextView) findViewById(R.id.activity_mainProxyText);
        SharedPreferences.Editor edit = this.mConfig.getPrefsPrivate().edit();
        SecurePreferences prefsPrivate = this.mConfig.getPrefsPrivate();
        prefsPrivate.edit().putBoolean("usarDefaultPayload", false).apply();
        prefsPrivate.edit().putInt("tunnelType", 2).apply();
        this.config = new ConfigUtil(this);
        this.serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
        this.payloadSpinner = (Spinner) findViewById(R.id.payloadSpinner);
        this.serverList = new ArrayList<>();
        this.payloadList = new ArrayList<>();
        this.udpList = new ArrayList<>();
        this.serverAdapter = new SpinnerAdapter(this, R.id.serverSpinner, this.serverList);
        this.payloadAdapter = new SpinnerAdapter(this, R.id.payloadSpinner, this.payloadList);
        this.reloadIU = (ImageView) findViewById(R.id.reloadIU);
        this.botaoatualizar = (Button) findViewById(R.id.botaoatualizar);
        this.botaoregistro = (Button) findViewById(R.id.botaoregistro);
        this.botaoferramentas = (Button) findViewById(R.id.botaoferramentas);
        this.botaoconfigurar = (Button) findViewById(R.id.botaoconfigurar);
        this.APNsettings = (Button) findViewById(R.id.APNsettings);
        this.botaowhatsapp = (Button) findViewById(R.id.botaowhatsapp);
        this.botaotelegram = (Button) findViewById(R.id.botaotelegram);
        this.botaoyoutube = (Button) findViewById(R.id.botaoyoutube);
        this.botaoimportar = (Button) findViewById(R.id.botaoimportar);
        this.botaotermos = (Button) findViewById(R.id.botaotermos);
        this.botaootimizarbateria = (Button) findViewById(R.id.botaootimizarbateria);
        this.botaospeedtest = (Button) findViewById(R.id.botaospeedtest);
        this.botaorotearhome = (Button) findViewById(R.id.botaorotearhome);
        this.Vencimento = (TextView) findViewById(R.id.venci);
        this.dias_check = (TextView) findViewById(R.id.dias);
        this.user_limite = (TextView) findViewById(R.id.limite);
        this.serverSpinner.setAdapter((android.widget.SpinnerAdapter) this.serverAdapter);
        this.payloadSpinner.setAdapter((android.widget.SpinnerAdapter) this.payloadAdapter);
        //this.status.setBackground(getResources().getDrawable(2131230972));
        loadServer();
        loadNetworks();
        loadportasudp();
        updateConfig(true);
        this.metodoConexaoRadio = (RadioGroup) findViewById(R.id.activity_mainMetodoConexaoRadio);
        this.customPayloadSwitch = findViewById(R.id.activity_mainCustomPayloadSwitch);
        this.starterButton.setOnClickListener(this);
        this.proxyInputLayout.setOnClickListener(this);
        this.payloadLayout = (LinearLayout) findViewById(R.id.activity_mainInputPayloadLinearLayout);
        this.payloadEdit = findViewById(R.id.activity_mainInputPayloadEditText);
        this.ssl_layout = (LinearLayout) findViewById(R.id.activity_ssl_layout);
        this.sslEdit = findViewById(R.id.activity_sni_edit);
        this.configMsgLayout = (LinearLayout) findViewById(R.id.activity_mainMensagemConfigLinearLayout);
        TextView versionTextView2 = (TextView) findViewById(R.id.txtVersion);
        
        this.configMsgText = versionTextView2;
        this.customPayloadSwitch.setChecked(true);
        edit.putBoolean("usarDefaultPayload", false);
        this.metodoConexaoRadio.setOnCheckedChangeListener(this);
        this.inputPwShowPass.setOnClickListener(this);
        this.reloadIU.setOnClickListener(this);
        this.botaorotearhome.setOnClickListener(this);
        this.botaoregistro.setOnClickListener(this);
        this.botaoatualizar.setOnClickListener(this);
        this.botaoferramentas.setOnClickListener(this);
        this.botaoconfigurar.setOnClickListener(this);
        this.APNsettings.setOnClickListener(this);
        this.botaowhatsapp.setOnClickListener(this);
        this.botaotelegram.setOnClickListener(this);
        this.botaoyoutube.setOnClickListener(this);
        this.botaoimportar.setOnClickListener(this);
        this.botaotermos.setOnClickListener(this);
        this.botaootimizarbateria.setOnClickListener(this);
        this.botaospeedtest.setOnClickListener(this);
        doTabs();
        setuserpass();
    }
    */
    
    
    
    
    
    /*
    
    private void doLayoutC5g() {
		setContentView(R.layout.activity_main_drawer);
		final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
		toolbar_main = findViewById(R.id.toolbar_main);
		mDrawerPanel.setDrawer(toolbar_main);
		//toolbar_main.setTitle(R.string.app_name);
		//setSupportActionBar(toolbar_main);
		DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutMain);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawerLayout, toolbar_main, R.string.app_name, R.string.app_name);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
		toolbar_main.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DrawerLayout drawerLayout = findViewById(R.id.drawerLayoutMain);
				if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
					drawerLayout.closeDrawer(GravityCompat.START);
				} else {
					drawerLayout.openDrawer(GravityCompat.START);
				}
			}
		});

		serverSpinner = findViewById(R.id.serverSpinner);
		payloadSpinner = findViewById(R.id.payloadSpinner);

		vencimento = findViewById(R.id.venci);
		dias_check = findViewById(R.id.dias);
		user_limite = findViewById(R.id.limite);

		reloadIU = findViewById(R.id.reloadIU);
		reloadIU.setOnClickListener(this);
        
        logoonline = findViewById(R.id.logoonline);
        fundoonline = findViewById(R.id.fundoonline);

		//backgroundapp = findViewById(R.id.backgroundapp);
		//logoapp = findViewById(R.id.logoapp);

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;

		inputPwShowPass = findViewById(R.id.mostrarsenha);
		inputPwShowPass.setOnClickListener(this);

		connectionStatus = findViewById(R.id.status);

		username = findViewById(R.id.username);
		password = findViewById(R.id.password);

		serverList = new ArrayList<>();
		payloadList = new ArrayList<>();

		serverAdapter = new SpinnerAdapter(this, R.id.serverSpinner, serverList);
		payloadAdapter = new SpinnerAdapter(this, R.id.payloadSpinner, payloadList);

		serverSpinner.setAdapter(serverAdapter);
		payloadSpinner.setAdapter(payloadAdapter);
		loadServer();
		loadNetworks();

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		mLogAdapter = new LogsAdapter(layoutManager,this);
		mLogAdapter.scrollToLastPosition();

		starterButton = findViewById(R.id.activity_starterButtonMain);
		starterButton.setOnClickListener(this);
		serverSpinner.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					spinnerTouched = true; // User DID touched the spinner!
				}

				return false;
			}
		});


		final SharedPreferences prefsave = mConfig.getPrefsPrivate();

		serverSpinner.setSelection(prefsave.getInt("LastSelectedServer", 0));
		serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
				SharedPreferences prefs = mConfig.getPrefsPrivate();
				SharedPreferences.Editor edit = prefs.edit();
				edit.putInt("LastSelectedServer", p3).apply();

				if (spinnerTouched) {
					// Do something
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							//loadServerData();
						}
					}, 20);


				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> p1) {

			}
		});
		payloadSpinner.setSelection(prefsave.getInt("LastSelectedPayload", 0));
		payloadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
				SharedPreferences prefs = mConfig.getPrefsPrivate();
				SharedPreferences.Editor edit = prefs.edit();
				edit.putInt("LastSelectedPayload", p3).apply();
			}

			@Override
			public void onNothingSelected(AdapterView<?> p1) {
			}
		});
	}
    
*/











    
    private void doLayout() {
    setContentView(R.layout.activity_main_drawer);
    ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
    this.config = configUtil;
    
    // Set background for various views using the new helper method
    this.serverSpinner = findViewById(R.id.serverSpinner);
    setBackground(this.serverSpinner, configUtil.ModderCorCaixaServ(), R.drawable.mtrl_fab_show_spec);
    
    this.payloadSpinner = findViewById(R.id.payloadSpinner);
    setBackground(this.payloadSpinner, configUtil.ModderCorCaixaPay(), R.drawable.mtrl_fab_show_spec);
    
    this.username = findViewById(R.id.username);
    setBackground(this.username, configUtil.ModderCorCaixaUsuario(), R.drawable.abc_btn_default_mtrl_trap);
    
    this.password = findViewById(R.id.password);
    setBackground(this.password, configUtil.ModderCorCaixaSenha(), R.drawable.abc_btn_default_mtrl_trap);
    
    this.fundobotaoiniciar = findViewById(R.id.fundobotaoiniciar);
    setBackground(this.fundobotaoiniciar, configUtil.ModderCorBotaoIniciar(), R.drawable.mtrl_fab_abc_spec);
    
    this.fundobotaoregistro = findViewById(R.id.fundobotaoregistro);
    setBackground(this.fundobotaoregistro, configUtil.ModderCorBotaoLog(), R.drawable.abc_edit_text_spec_material);
    
    this.fundocaixacentral = findViewById(R.id.fundocaixacentral);
    setBackground(this.fundocaixacentral, configUtil.ModderCorCaixaCentral(), R.drawable.mtrl_fab_transformation_sheet_expand);
    
    this.fundocaixaconexao = findViewById(R.id.fundocaixaconexao);
    setBackground(this.fundocaixaconexao, configUtil.ModderCorCaixaConexao(), R.drawable.abc_default_mtrl_trap);
    
    this.fundobotaoferramentas = findViewById(R.id.fundobotaoferramentas);
    setBackground(this.fundobotaoferramentas, configUtil.ModderCorBotaoConfig(), R.drawable.mtrl_fab_abc_spec);
        
    if (dialog2 != null && dialog2.isShowing()) {
    View fundoFerramentas = dialog2.findViewById(R.id.fundoferramentas);
    setBackground(fundoFerramentas, configUtil.ModderCorCaixaFerramentas(), R.drawable.tooltip_frame_abcd_dark);
}

if (dialogRegistro != null && dialogRegistro.isShowing()) {
    View fundoRegistro = dialogRegistro.findViewById(R.id.fundoregistro);
    setBackground(fundoRegistro, configUtil.ModderCorCaixaRegistro(), R.drawable.tooltip_frame_abcd_dark);
}    
        
        
        
        /*
    this.fundoferramentas = findViewById(R.id.fundoferramentas);
    setBackground(this.fundoferramentas, configUtil.ModderCorCaixaFerramentas(), R.drawable.tooltip_frame_abcd_dark);
        
    this.fundoregistro = findViewById(R.id.fundoregistro);
    setBackground(this.fundoregistro, configUtil.ModderCorCaixaRegistro(), R.drawable.tooltip_frame_abcd_dark);   
        */
    
    // Additional initializations
    this.operadoraMain = findViewById(R.id.operadoraMain);
    checkNetworkMain();
    CustomPayloadName();
    updateConfigAndDeleteFileIfNeeded();
    
    this.txtVersion2 = findViewById(R.id.txtVersion2);
    checkNetworkMain2();
    
    ((TextView) findViewById(R.id.txtVersion)).setText(configUtil.getVersion());
    
    this.logoonline = findViewById(R.id.logoonline);
    this.fundoonline = findViewById(R.id.fundoonline);
    this.banneronline = findViewById(R.id.banneronline);    
        exibirImagem1();
        exibirImagem2();
        exibirImagem3();
        
        loadServer();
        loadNetworks();
        loadportasudp();
        updateConfig(true);
        
    this.toolbar_main = findViewById(R.id.toolbar_main);
    
    connectionStatus = findViewById(R.id.status);
    
    this.mainLayout = findViewById(R.id.activity_mainLinearLayout);
    this.loginLayout = findViewById(R.id.activity_mainInputPasswordLayout);
    this.starterButton = findViewById(R.id.activity_starterButtonMain);
    this.username = findViewById(R.id.username);
    this.password = findViewById(R.id.password);
    this.inputPwShowPass = findViewById(R.id.mostrarsenha);
    ((TextView) findViewById(R.id.activity_mainAutorText)).setOnClickListener(this);
    this.proxyInputLayout = findViewById(R.id.activity_mainInputProxyLayout);
    this.proxyText = findViewById(R.id.activity_mainProxyText);
    
    SecurePreferences prefsPrivate = this.mConfig.getPrefsPrivate();
    prefsPrivate.edit().putBoolean(SettingsConstants.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
    prefsPrivate.edit().putInt(SettingsConstants.TUNNELTYPE_KEY, 2).apply();
    
    this.config = new ConfigUtil(this);
    this.serverSpinner = findViewById(R.id.serverSpinner);
    this.payloadSpinner = findViewById(R.id.payloadSpinner);
    this.serverList = new ArrayList<>();
    this.payloadList = new ArrayList<>();
    this.udpList = new ArrayList<>();
    this.serverAdapter = new SpinnerAdapter(this, R.id.serverSpinner, this.serverList);
    this.payloadAdapter = new SpinnerAdapter(this, R.id.payloadSpinner, this.payloadList);
    this.reloadIU = findViewById(R.id.reloadIU);
    this.botaoatualizar = findViewById(R.id.botaoatualizar);
    this.botaoregistro = findViewById(R.id.botaoregistro);
    this.botaoferramentas = findViewById(R.id.botaoferramentas);
    this.botaoconfigurar = findViewById(R.id.botaoconfigurar);
    this.miPhoneConfig = findViewById(R.id.miPhoneConfig);
        
    this.APNsettings = findViewById(R.id.APNsettings);
    this.botaowhatsapp = findViewById(R.id.botaowhatsapp);
    this.botaotelegram = findViewById(R.id.botaotelegram);
    this.botaoyoutube = findViewById(R.id.botaoyoutube);
    this.botaoimportar = findViewById(R.id.botaoimportar);
    this.botaotermos = findViewById(R.id.botaotermos);
    this.botaootimizarbateria = findViewById(R.id.botaootimizarbateria);
    this.botaospeedtest = findViewById(R.id.botaospeedtest);
    this.botaorotearhome = findViewById(R.id.botaorotearhome);
    
    vencimento = findViewById(R.id.venci);
    dias_check = findViewById(R.id.dias);
    user_limite = findViewById(R.id.limite);
    
    this.serverSpinner.setAdapter(this.serverAdapter);
    this.payloadSpinner.setAdapter(this.payloadAdapter);
    
    this.metodoConexaoRadio = findViewById(R.id.activity_mainMetodoConexaoRadio);
    this.customPayloadSwitch = findViewById(R.id.activity_mainCustomPayloadSwitch);
    this.starterButton.setOnClickListener(this);
    this.proxyInputLayout.setOnClickListener(this);
    this.payloadLayout = findViewById(R.id.activity_mainInputPayloadLinearLayout);
    this.payloadEdit = findViewById(R.id.activity_mainInputPayloadEditText);
    this.ssl_layout = findViewById(R.id.activity_ssl_layout);
    this.sslEdit = findViewById(R.id.activity_sni_edit);
    this.configMsgLayout = findViewById(R.id.activity_mainMensagemConfigLinearLayout);
    this.configMsgText = findViewById(R.id.activity_mainMensagemConfigTextView);
    this.customPayloadSwitch.setChecked(true);
    
    this.metodoConexaoRadio.setOnCheckedChangeListener(this);
    this.inputPwShowPass.setOnClickListener(this);
    this.reloadIU.setOnClickListener(this);
    this.botaorotearhome.setOnClickListener(this);
    this.botaoregistro.setOnClickListener(this);
    this.botaoatualizar.setOnClickListener(this);
    this.botaoferramentas.setOnClickListener(this);
    this.botaoconfigurar.setOnClickListener(this);
    this.miPhoneConfig.setOnClickListener(this);
    this.APNsettings.setOnClickListener(this);
    this.botaowhatsapp.setOnClickListener(this);
    this.botaotelegram.setOnClickListener(this);
    this.botaoyoutube.setOnClickListener(this);
    this.botaoimportar.setOnClickListener(this);
    this.botaotermos.setOnClickListener(this);
    this.botaootimizarbateria.setOnClickListener(this);
    this.botaospeedtest.setOnClickListener(this);
    
    doTabs();
    setuserpass();
}
    
    
    

private void setBackground(View view, String colorString, int drawableId) {
    int backgroundColor = getDefaultColor();
    if (colorString != null && !colorString.isEmpty()) {
        backgroundColor = parseColor(colorString, getDefaultColor());
    }
    Drawable buttonBackground = ContextCompat.getDrawable(this, drawableId);
    buttonBackground.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
    view.setBackground(buttonBackground);
}
    
    /*
private void setBackgroundRegistro(View view, String colorString, int drawableId) {
    int backgroundColor = getDefaultColorRegistro();
    if (colorString != null && !colorString.isEmpty()) {
        backgroundColor = parseColor(colorString, getDefaultColorRegistro());
    }
    Drawable buttonBackground = ContextCompat.getDrawable(this, drawableId);
    buttonBackground.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
    view.setBackgroundRegistro(buttonBackground);
}    
    
private void setBackgroundFerramentas(View view, String colorString, int drawableId) {
    int backgroundColor = getDefaultColorFerramentas();
    if (colorString != null && !colorString.isEmpty()) {
        backgroundColor = parseColor(colorString, getDefaultColor());
    }
    Drawable buttonBackground = ContextCompat.getDrawable(this, drawableId);
    buttonBackground.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
    view.setBackgroundFerramentas(buttonBackground);
}    
    */
    
    
    
    public class MyAdapter extends PagerAdapter {
        private List<String> titles;

        public int getCount() {
            return 1;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            return SocksHttpMainActivity.this.findViewById(new int[]{R.id.tab1, R.id.tab2}[i]);
        }

        public CharSequence getPageTitle(int i) {
            return this.titles.get(i);
        }

        public MyAdapter(List<String> list) {
            this.titles = list;
        }
    }
    
    
    
    
    
    public void update2() {
        ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
        this.config = configUtil;
        
        String str;
        SecurePreferences prefsPrivate = this.mConfig.getPrefsPrivate();
        boolean isTunnelActive = SkStatus.isTunnelActive();
        int i = prefsPrivate.getInt(SettingsConstants.TUNNELTYPE_KEY, 1);
        setStarterButton(this.starterButton, this);
        String charSequence = getText(R.string.no_value).toString();
        if (prefsPrivate.getBoolean(SettingsConstants.CONFIG_PROTEGER_KEY, false)) {
            this.proxyInputLayout.setEnabled(false);
            str = "*******";
        } else {
            String privString = this.mConfig.getPrivString(SettingsConstants.PROXY_IP_KEY);
            if (privString != null && !privString.isEmpty()) {
                charSequence = String.format("%s:%s", new Object[]{privString, this.mConfig.getPrivString(SettingsConstants.PROXY_PORTA_KEY)});
            }
            this.proxyInputLayout.setEnabled(!isTunnelActive);
            str = charSequence;
        }
        
        
        boolean z = !isTunnelActive;
        this.loginLayout.setVisibility(0);
        this.configMsgText.setText("");
        this.configMsgLayout.setVisibility(8);
        for (int i2 = 0; i2 < this.metodoConexaoRadio.getChildCount(); i2++) {
            this.metodoConexaoRadio.getChildAt(i2).setEnabled(z);
        }
        
        this.serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
        String ModderCorCaixaServ = configUtil.ModderCorCaixaServ();
        int backgroundColor = getDefaultColor();
        if (ModderCorCaixaServ != null && !ModderCorCaixaServ.isEmpty()) {
            backgroundColor = parseColor(ModderCorCaixaServ, getDefaultColor());
        }
        Drawable buttonBackground = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_show_spec);
        buttonBackground.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
        this.serverSpinner.setBackground(buttonBackground);
        this.payloadSpinner = (Spinner) findViewById(R.id.payloadSpinner);
        String ModderCorCaixaPay = configUtil.ModderCorCaixaPay();
        int backgroundColor2 = getDefaultColor();
        if (ModderCorCaixaPay != null && !ModderCorCaixaPay.isEmpty()) {
            backgroundColor2 = parseColor(ModderCorCaixaPay, getDefaultColor());
        }
        Drawable buttonBackground2 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_show_spec);
        buttonBackground2.setColorFilter(backgroundColor2, PorterDuff.Mode.MULTIPLY);
        this.payloadSpinner.setBackground(buttonBackground2);
        this.username = (TextInputEditText) findViewById(R.id.username);
        String ModderCorCaixaUsuario = configUtil.ModderCorCaixaUsuario();
        int backgroundColor3 = getDefaultColor();
        if (ModderCorCaixaUsuario != null && !ModderCorCaixaUsuario.isEmpty()) {
            backgroundColor3 = parseColor(ModderCorCaixaUsuario, getDefaultColor());
        }
        Drawable buttonBackground3 = ContextCompat.getDrawable(this, R.drawable.abc_btn_default_mtrl_trap);
        buttonBackground3.setColorFilter(backgroundColor3, PorterDuff.Mode.MULTIPLY);
        this.username.setBackground(buttonBackground3);
        this.password = (TextInputEditText) findViewById(R.id.password);
        String ModderCorCaixaUsuario2 = configUtil.ModderCorCaixaSenha();
        int backgroundColor4 = getDefaultColor();
        if (ModderCorCaixaUsuario2 != null && !ModderCorCaixaUsuario2.isEmpty()) {
            backgroundColor4 = parseColor(ModderCorCaixaUsuario2, getDefaultColor());
        }
        Drawable buttonBackground4 = ContextCompat.getDrawable(this, R.drawable.abc_btn_default_mtrl_trap);
        buttonBackground4.setColorFilter(backgroundColor4, PorterDuff.Mode.MULTIPLY);
        this.password.setBackground(buttonBackground4);
        this.fundobotaoiniciar = (LinearLayout) findViewById(R.id.fundobotaoiniciar);
        String ModderCorBotaoIniciar = configUtil.ModderCorBotaoIniciar();
        int backgroundColor5 = getDefaultColor();
        if (ModderCorBotaoIniciar != null && !ModderCorBotaoIniciar.isEmpty()) {
            backgroundColor5 = parseColor(ModderCorBotaoIniciar, getDefaultColor());
        }
        Drawable buttonBackground5 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_abc_spec);
        buttonBackground5.setColorFilter(backgroundColor5, PorterDuff.Mode.MULTIPLY);
        this.fundobotaoiniciar.setBackground(buttonBackground5);
        this.fundobotaoregistro = (LinearLayout) findViewById(R.id.fundobotaoregistro);
        String ModderCorBotaoIniciar2 = configUtil.ModderCorBotaoLog();
        int backgroundColor6 = getDefaultColor();
        if (ModderCorBotaoIniciar2 != null && !ModderCorBotaoIniciar2.isEmpty()) {
            backgroundColor6 = parseColor(ModderCorBotaoIniciar2, getDefaultColor());
        }
        Drawable buttonBackground6 = ContextCompat.getDrawable(this, R.drawable.abc_edit_text_spec_material);
        buttonBackground6.setColorFilter(backgroundColor6, PorterDuff.Mode.MULTIPLY);
        this.fundobotaoregistro.setBackground(buttonBackground6);
        this.fundocaixacentral = (LinearLayout) findViewById(R.id.fundocaixacentral);
        String ModderCorCaixaCentral = configUtil.ModderCorCaixaCentral();
        int backgroundColor7 = getDefaultColor();
        if (ModderCorCaixaCentral != null && !ModderCorCaixaCentral.isEmpty()) {
            backgroundColor7 = parseColor(ModderCorCaixaCentral, getDefaultColor());
        }
        Drawable buttonBackground7 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_transformation_sheet_expand);
        buttonBackground7.setColorFilter(backgroundColor7, PorterDuff.Mode.MULTIPLY);
        this.fundocaixacentral.setBackground(buttonBackground7);
        this.fundocaixaconexao = (LinearLayout) findViewById(R.id.fundocaixaconexao);
        String ModderCorCaixaConexao = configUtil.ModderCorCaixaConexao();
        int backgroundColor8 = getDefaultColor();
        if (ModderCorCaixaConexao != null && !ModderCorCaixaConexao.isEmpty()) {
            backgroundColor8 = parseColor(ModderCorCaixaConexao, getDefaultColor());
        }
        Drawable buttonBackground8 = ContextCompat.getDrawable(this, R.drawable.abc_default_mtrl_trap);
        buttonBackground8.setColorFilter(backgroundColor8, PorterDuff.Mode.MULTIPLY);
        this.fundocaixaconexao.setBackground(buttonBackground8);
        
        
        this.fundobotaoferramentas = (LinearLayout) findViewById(R.id.fundobotaoferramentas);
        String ModderCorBotaoConfig = configUtil.ModderCorBotaoConfig();
        int backgroundColor9 = getDefaultColor();
        if (ModderCorBotaoConfig != null && !ModderCorBotaoConfig.isEmpty()) {
            backgroundColor9 = parseColor(ModderCorBotaoConfig, getDefaultColor());
        }
        Drawable buttonBackground9 = ContextCompat.getDrawable(this, R.drawable.mtrl_fab_abc_spec);
        buttonBackground9.setColorFilter(backgroundColor9, PorterDuff.Mode.MULTIPLY);
        this.fundobotaoferramentas.setBackground(buttonBackground9);
        
        
        
        if (dialog2 != null && dialog2.isShowing()) {
    View dialogView = dialog2.findViewById(android.R.id.content);
this.fundoferramentas = dialog2.findViewById(R.id.fundoferramentas);
        String ModderCorCaixaFerramentas = configUtil.ModderCorCaixaFerramentas();
        int backgroundColor10 = getDefaultColorFerramentas();
        if (ModderCorCaixaFerramentas != null && !ModderCorCaixaFerramentas.isEmpty()) {
            backgroundColor10 = parseColor(ModderCorCaixaFerramentas, getDefaultColorFerramentas());
        }
        Drawable buttonBackground10 = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
        buttonBackground10.setColorFilter(backgroundColor10, PorterDuff.Mode.MULTIPLY);
        this.fundoferramentas.setBackground(buttonBackground10);
        }
        
        
        if (dialogRegistro != null && dialogRegistro.isShowing()) {
    View dialogView = dialogRegistro.findViewById(android.R.id.content);
    this.fundoregistro = dialogView.findViewById(R.id.fundoregistro); // <-- usa o dialogView!
    String ModderCorCaixaRegistro = configUtil.ModderCorCaixaRegistro();
    int backgroundColor11 = getDefaultColorRegistro();
    if (ModderCorCaixaRegistro != null && !ModderCorCaixaRegistro.isEmpty()) {
        backgroundColor11 = parseColor(ModderCorCaixaRegistro, getDefaultColorRegistro());
    }
    Drawable buttonBackground11 = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
    buttonBackground11.setColorFilter(backgroundColor11, PorterDuff.Mode.MULTIPLY);
    this.fundoregistro.setBackground(buttonBackground11);
}
        
        /*
        RelativeLayout fundoregistro; // ou View

if (dialogRegistro != null && dialogRegistro.isShowing()) {
    fundoregistro = dialogRegistro.findViewById(R.id.fundoregistro);    
    String ModderCorCaixaRegistro = configUtil.ModderCorCaixaRegistro();
    int backgroundColor11 = getDefaultColorRegistro();
    if (ModderCorCaixaRegistro != null && !ModderCorCaixaRegistro.isEmpty()) {
        backgroundColor11 = parseColor(ModderCorCaixaRegistro, getDefaultColorRegistro());
    }
    Drawable buttonBackground11 = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
    buttonBackground11.setColorFilter(backgroundColor11, PorterDuff.Mode.MULTIPLY);
    fundoregistro.setBackground(buttonBackground11);
}
        
        
        if (dialogRegistro != null && dialogRegistro.isShowing()) {
    View dialogView = dialogRegistro.findViewById(android.R.id.content);
        this.fundoregistro = dialogRegistro.findViewById(R.id.fundoregistro);    
        String ModderCorCaixaRegistro = configUtil.ModderCorCaixaRegistro();
        int backgroundColor11 = getDefaultColorRegistro();
        if (ModderCorCaixaRegistro != null && !ModderCorCaixaRegistro.isEmpty()) {
            backgroundColor11 = parseColor(ModderCorCaixaRegistro, getDefaultColorRegistro());
        }
        Drawable buttonBackground11 = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
        buttonBackground11.setColorFilter(backgroundColor11, PorterDuff.Mode.MULTIPLY);
        this.fundoregistro.setBackground(buttonBackground11);
        }
        
        */
        
        
        /*
        if (dialog2 != null && dialog2.isShowing()) {
    View dialogView = dialog2.findViewById(android.R.id.content);
    LinearLayout fundoFerramentas = dialogView.findViewById(R.id.fundoferramentas);

    String ModderCorCaixaFerramentas = config.ModderCorCaixaFerramentas(); // você já tem o config ali
    int corFinal = getDefaultColorFerramentas();
    if (ModderCorCaixaFerramentas != null && !ModderCorCaixaFerramentas.isEmpty()) {
        corFinal = parseColor(ModderCorCaixaFerramentas, getDefaultColorFerramentas());
    }

    Drawable fundoDrawable = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
    fundoDrawable.setColorFilter(corFinal, PorterDuff.Mode.MULTIPLY);
    fundoFerramentas.setBackgroundFerramentas(fundoDrawable);
}
        
        
        if (dialogRegistro != null && dialogRegistro.isShowing()) {
    View dialogView = dialogRegistro.findViewById(android.R.id.content);
    RelativeLayout fundoRegistro = dialogView.findViewById(R.id.fundoregistro);

    String ModderCorCaixaRegistro = config.ModderCorCaixaRegistro();
    int corFinal = getDefaultColorRegistro();
    if (ModderCorCaixaRegistro != null && !ModderCorCaixaRegistro.isEmpty()) {
        corFinal = parseColor(ModderCorCaixaRegistro, getDefaultColorRegistro());
    }

    Drawable fundoDrawable = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
    fundoDrawable.setColorFilter(corFinal, PorterDuff.Mode.MULTIPLY);
    fundoRegistro.setBackgroundRegistro(fundoDrawable);
}
        */
        
        
        
        updateConfigAndDeleteFileIfNeeded();
        
        this.operadoraMain = findViewById(R.id.operadoraMain);
    checkNetworkMain();
    CustomPayloadName();
    
    this.txtVersion2 = findViewById(R.id.txtVersion2);
    checkNetworkMain2();
    
    ((TextView) findViewById(R.id.txtVersion)).setText(configUtil.getVersion());
        
        
        
        this.logoonline = (ImageView) findViewById(R.id.logoonline);
        exibirImagem1();
        this.fundoonline = (ImageView) findViewById(R.id.fundoonline);
        exibirImagem2();
        this.banneronline = (ImageView) findViewById(R.id.banneronline);
        exibirImagem3();
        
        loadServer();
        loadNetworks();
        loadportasudp();
    }
    
    
    
    
    
    
    
    
    /*
    
   public void update21do11() {
    ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
    this.config = configUtil;
    
    // Inicializa elementos de UI
    SecurePreferences prefsPrivate = this.mConfig.getPrefsPrivate();
    boolean isTunnelActive = SkStatus.isTunnelActive();
    int i = prefsPrivate.getInt(SettingsConstants.TUNNELTYPE_KEY, 1);
    setStarterButton(this.starterButton, this);

    // Configuração de Proxy
    String str = setupProxyConfig(prefsPrivate, isTunnelActive);

    // Configura a visibilidade do layout de login
    setupLoginLayout(isTunnelActive);

    // Configura cores de UI (Separado para evitar repetição)
    setupUIColors(configUtil);

    // Atualiza versão
    updateVersion(configUtil);

    // Carrega imagens e configurações
    loadServer();
    loadNetworks();
    loadportasudp();
        
        exibirImagem1();
        exibirImagem2();

    // Ações adicionais
    updateConfigAndDeleteFileIfNeeded();
    checkNetworkMain();
    CustomPayloadName();
    checkNetworkMain2();
}

private String setupProxyConfig(SecurePreferences prefsPrivate, boolean isTunnelActive) {
    String charSequence = getText(R.string.no_value).toString();
    String str = "*******";
    if (prefsPrivate.getBoolean(SettingsConstants.CONFIG_PROTEGER_KEY, false)) {
        this.proxyInputLayout.setEnabled(false);
    } else {
        String privString = this.mConfig.getPrivString(SettingsConstants.PROXY_IP_KEY);
        if (privString != null && !privString.isEmpty()) {
            charSequence = String.format("%s:%s", privString, this.mConfig.getPrivString(SettingsConstants.PROXY_PORTA_KEY));
        }
        this.proxyInputLayout.setEnabled(!isTunnelActive);
        str = charSequence;
    }
    return str;
}

private void setupLoginLayout(boolean isTunnelActive) {
    boolean z = !isTunnelActive;
    this.loginLayout.setVisibility(View.VISIBLE);
    this.configMsgText.setText("");
    this.configMsgLayout.setVisibility(View.GONE);
    for (int i2 = 0; i2 < this.metodoConexaoRadio.getChildCount(); i2++) {
        this.metodoConexaoRadio.getChildAt(i2).setEnabled(z);
    }
}

private void setupUIColors(ConfigUtil configUtil) {
    // Método reutilizável para configurar o fundo de componentes com base na cor fornecida
    setupColorForComponent(this.serverSpinner, configUtil.ModderCorCaixaServ(), R.drawable.mtrl_fab_show_spec);
    setupColorForComponent(this.payloadSpinner, configUtil.ModderCorCaixaPay(), R.drawable.mtrl_fab_show_spec);
    setupColorForComponent(this.username, configUtil.ModderCorCaixaUsuario(), R.drawable.abc_btn_default_mtrl_trap);
    setupColorForComponent(this.password, configUtil.ModderCorCaixaSenha(), R.drawable.abc_btn_default_mtrl_trap);
    setupColorForComponent(this.fundobotaoiniciar, configUtil.ModderCorBotaoIniciar(), R.drawable.mtrl_fab_abc_spec);
    setupColorForComponent(this.fundobotaoregistro, configUtil.ModderCorBotaoLog(), R.drawable.abc_edit_text_spec_material);
    setupColorForComponent(this.fundocaixacentral, configUtil.ModderCorCaixaCentral(), R.drawable.mtrl_fab_transformation_sheet_expand);
    setupColorForComponent(this.fundocaixaconexao, configUtil.ModderCorCaixaConexao(), R.drawable.abc_default_mtrl_trap);
    setupColorForComponent(this.fundobotaoferramentas, configUtil.ModderCorBotaoConfig(), R.drawable.mtrl_fab_abc_spec);
}

private void setupColorForComponent(View component, String colorString, int defaultBackground) {
    int backgroundColor = getDefaultColor();
    if (colorString != null && !colorString.isEmpty()) {
        backgroundColor = parseColor(colorString, getDefaultColor());
    }
    Drawable buttonBackground = ContextCompat.getDrawable(this, defaultBackground);
    buttonBackground.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
    component.setBackground(buttonBackground);
}

private void updateVersion(ConfigUtil configUtil) {
    this.txtVersion2 = findViewById(R.id.txtVersion2);
    ((TextView) findViewById(R.id.txtVersion)).setText(configUtil.getVersion());
}
    
    
    */
    
    
    
    
    
    
    
    
    
    /*
    
    
    public void setStarterButtonNovo(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;
			
			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();
            if (SkStatus.SSH_INICIANDO.equals(state)) {
                    starterButton.setEnabled(false);
                    this.serverSpinner.setEnabled(false);
                    this.payloadSpinner.setEnabled(false);
                    this.sslEdit.setEnabled(false);
                    this.payloadEdit.setEnabled(false);
                    this.username.setEnabled(false);
                    this.password.setEnabled(false);
                    resId = R.string.stop;
                    exibirJanelaRegistro();
                
                }
                else if (SkStatus.SSH_CONECTADO.equals(state)) {
                    starterButton.setEnabled(true);
                    this.username.setEnabled(false);
                    this.password.setEnabled(false);
                    this.serverSpinner.setEnabled(false);
                    this.payloadSpinner.setEnabled(false);
                    exibirImagem1();
                    updateConfigAndDeleteFileIfNeeded();
                    CustomPayloadName();
                    updateConfig(true);
                    resId = R.string.stop;
                   
                } 
                else if (SkStatus.SSH_PARANDO.equals(state)) {
                    
                    starterButton.setEnabled(false);
                    resId = R.string.state_stopping;
                
                }
                else if (SkStatus.SSH_DESCONECTADO.equals(state)) {
                    starterButton.setEnabled(true);
                    this.serverSpinner.setEnabled(true);
                    this.payloadSpinner.setEnabled(true);
                    this.sslEdit.setEnabled(true);
                    this.payloadEdit.setEnabled(true);
                    this.username.setEnabled(true);
                    this.password.setEnabled(true);
                    resId = R.string.start;
                    
				
				}else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);
            }

			starterButton.setText(resId);
		}
	}
        
        
        */
        
        
        
        
    
    
    
    /*ESSE CODIGO DE BOTAO ABAIXO ERA O ORIGINAL DA DATA 26 03 2025 */
    /*
    public void setStarterButton(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;
			
			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();
            if (SkStatus.SSH_CONECTANDO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(false);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                    if (this.mConfig != null && this.mConfig.abrirRegistro()) {
                    exibirJanelaRegistro();
    }
                
                }
                else if (SkStatus.SSH_CONECTADO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                
                exibirImagem1();
        exibirImagem2();
                updateConfigAndDeleteFileIfNeeded();
                    CustomPayloadName();
                
                    username.setEnabled(false);
                    password.setEnabled(false);
                
                
                updateConfig(true);
                } 
                else if (SkStatus.SSH_PARANDO.equals(state)) {
                    resId = R.string.state_stopping;
                    starterButton.setEnabled(false);
                
                }
                else if (SkStatus.SSH_DESCONECTADO.equals(state)) {
                    resId = R.string.start;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(true);
                    payloadSpinner.setEnabled(true);
                    username.setEnabled(true);
                    password.setEnabled(true);
				
				}else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);
            }

			starterButton.setText(resId);
		}
	}
    
    /*
    
    
    
    
    
    
    
    /*ESSE CODIGO DE BOTAO DE START ABAIXO SETA COMO TRUE O BOTAO DE CONECTANDO FAZENDO 
      COM QUE DEIXE PARAR E DESCONECTAR EM CASO DE TRAVAMENTO POR ERRO DENPROXY
    */

/*
    public void setStarterButton2(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;
			
			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();
            if (SkStatus.SSH_CONECTANDO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                    if (this.mConfig != null && this.mConfig.abrirRegistro()) {
                    exibirJanelaRegistro();
    }
                
                }
                else if (SkStatus.SSH_CONECTADO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                
                exibirImagem1();
        exibirImagem2();
                updateConfigAndDeleteFileIfNeeded();
                    CustomPayloadName();
                
                    username.setEnabled(false);
                    password.setEnabled(false);
                
                
                updateConfig(true);
                } 
                else if (SkStatus.SSH_PARANDO.equals(state)) {
                    resId = R.string.state_stopping;
                    starterButton.setEnabled(false);
                
                }
                else if (SkStatus.SSH_DESCONECTADO.equals(state)) {
                    resId = R.string.start;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(true);
                    payloadSpinner.setEnabled(true);
                    username.setEnabled(true);
                    password.setEnabled(true);
				
				}else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);
            }

			starterButton.setText(resId);
		}
	}
    */
    
    
    public void setStarterButton(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;
			
			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();
            if (SkStatus.SSH_INICIANDO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                    if (this.mConfig != null && this.mConfig.abrirRegistro()) {
                    exibirJanelaRegistro();
    }
                
                }
                else if (SkStatus.SSH_CONECTANDO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                } 
                else if (SkStatus.SSH_AUTENTICANDO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                } 
                else if (SkStatus.SSH_CONECTADO.equals(state)) {
                    resId = R.string.stop;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(false);
                    payloadSpinner.setEnabled(false);
                
                    //exibirImagem1();
                    //exibirImagem2();
                    //exibirImagem3();
                    updateConfigAndDeleteFileIfNeeded();
                    CustomPayloadName();
                
                    username.setEnabled(false);
                    password.setEnabled(false);
                
                    updateConfig(true);
                } 
                else if (SkStatus.SSH_PARANDO.equals(state)) {
                    resId = R.string.state_stopping;
                    starterButton.setEnabled(false);
                
                }
                else if (SkStatus.SSH_DESCONECTADO.equals(state)) {
                    resId = R.string.start;
                    starterButton.setEnabled(true);
                    serverSpinner.setEnabled(true);
                    payloadSpinner.setEnabled(true);
                    username.setEnabled(true);
                    password.setEnabled(true);
				
				}else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);
            }

			starterButton.setText(resId);
		}
	}
    
    
    
    
    
    
    
    public boolean isNewVersion(String str) {
        try {
            String version = this.config.getVersion();
            if (getResources().getString(getResources().getIdentifier(APP_NAME, "string", getPackageName())).contains("MODDERC5G")) {
                return this.config.versionCompare(new JSONObject(str).getString("Version"), version);
            }
            finish();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void updateConfig2(final boolean z) {
        updateConfigAndDeleteFileIfNeeded();
    String appName = getResources().getString(getResources().getIdentifier(APP_NAME, "string", getPackageName()));
    
    ConfigUpdate configUpdate = new ConfigUpdate(SocksHttpMainActivity.this, new ConfigUpdate.OnUpdateListener() {
        @Override
        public void onUpdateListener(String result) {
            try {
                if (!result.contains("Error on getting data")) {
                    if (isNewVersion(result)) {
                        newUpdateDialog(result);
                    } else if (!z) {
                        noUpdateDialog();
                    }
                } else if (result.contains("Error on getting data") && !z) {
                    errorUpdateDialog(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    
    configUpdate.start(z);
}
    
    
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.mDrawerPanel.getToogle() != null && this.mDrawerPanel.getToogle().onOptionsItemSelected(menuItem)) {
            return true;
        }
        switch (menuItem.getItemId()) {
            case R.id.configUpdate:
                updateConfig(false);
                break;
            case R.id.miExit:
                if (Build.VERSION.SDK_INT >= 16) {
                    finishAffinity();
                }
                System.exit(0);
                break;
            case R.id.miLimparConfig:
                if (SkStatus.isTunnelActive()) {
                    Toast.makeText(this, R.string.error_tunnel_service_execution, 0).show();
                    break;
                } else {
                    new ClearConfigDialogFragment().show(getSupportFragmentManager(), "alertClearConf");
                    break;
                }
            case R.id.miSettings:
                startActivity(new Intent(this, ConfigGeralActivity.class));
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
    
    
    public void abrirWebView1() {
        Intent intent = new Intent(this, WebViewActivity1.class);
        intent.putExtra(SettingsConstants.URL, getResources().getString(R.string.linkspeedtest));
        startActivity(intent);
    }

    public void abrirWebView2() {
        Intent intent = new Intent(this, WebViewActivity2.class);
        intent.putExtra(SettingsConstants.URL, getResources().getString(R.string.linkyoutube));
        startActivity(intent);
    }
    
    
    public boolean isInternetAvailable() {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    
    
    public void stopVPNService() {
        stopService(new Intent(this, ExceptionHandler.class));
    }
    
    
    public void updateConfigAndDeleteFileIfNeeded() {
        ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
        this.config = configUtil;
        if (configUtil.getUrlUpdate().startsWith("ht" + "tps" + "://" + "pa" + "inel" + "co" + "nec" + "ta5" + "g.c" + "om/")) {
            ConfigUtil configUtil2 = configUtil;
            return;
        }
        ConfigUtil configUtil3 = configUtil;
        File fileToDelete = new File(getApplicationContext().getFilesDir(), "Config.json");
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }
    }
    
    private int parseColor(String colorString, int defaultColor) {
    try {
        if (colorString.startsWith("rgba(") && colorString.endsWith(")")) {
            String rgbaValues = colorString.substring(5, colorString.length() - 1);
            String[] rgbaArray = rgbaValues.split(",");
            if (rgbaArray.length == 4) {
                int red = Integer.parseInt(rgbaArray[0].trim());
                int green = Integer.parseInt(rgbaArray[1].trim());
                int blue = Integer.parseInt(rgbaArray[2].trim());
                int alpha = (int) (Float.parseFloat(rgbaArray[3].trim()) * 255);
                return Color.argb(alpha, red, green, blue);
            }
        } else {
            return Color.parseColor(colorString);
        }
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
    }
    return defaultColor;
}
    
    private int getDefaultColor() {
    return Color.parseColor("#66000000");
}
    
    
    private int getDefaultColorFerramentas() {
    return Color.parseColor("#ff2a2a2a");
}
    
    
    private int getDefaultColorRegistro() {
    return Color.parseColor("#ff2a2a2a");
}
    
    
    
    
    
    public void offlineUpdate() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        startActivityForResult(intent, this.PICK_FILE);
    }
    
    
    
    



    
    
    
    

    /// CODIGO QUE IMPORTA O JSON VERSAO ORIGINAL 10 04 2025
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.PICK_FILE && resultCode == -1) {
            try {
                String intentData = importer(data.getData());
                OutputStream out = new FileOutputStream(new File(getFilesDir(), "Config.json"));
                out.write(intentData.getBytes());
                out.flush();
                out.close();
                loadServer();
                loadNetworks();
                stopAllProcess();
                restartApp();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    
    
    
    
    /*
    /// CODIGO QUE IMPORTA O JSON VERSAO ORIGINAL SEM V2RAY 10 04 2025
    
    public void onActivityResultSemv2(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.PICK_FILE && resultCode == -1) {
            try {
                String intentData = importer(data.getData());
                OutputStream out = new FileOutputStream(new File(getFilesDir(), "Config.json"));
                out.write(intentData.getBytes());
                out.flush();
                out.close();
                loadServer();
                loadNetworks();
                
                restartApp();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */
    
    
    
    
    
    
    private void iniciarAtualizacaoPeriodica() {
        this.isRunning = true;
        this.configUpdateHandler.postDelayed(new TimerTask() {
            public void run() {
                SocksHttpMainActivity.this.updateConfig2(true);
            }
        }, 0);
    }
    
    
    private void startBackgroundTask() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                SocksHttpMainActivity.this.updateConfig2(true);
                    new SMSuPdater(SocksHttpMainActivity.this);
            }
        }, 0, 10000);
    }
    
    public void startOrStopTunnel(Activity activity) {
		Settings mConfig = new Settings(this);
		if (SkStatus.isTunnelActive()) {
			TunnelManagerHelper.stopSocksHttp(this);
		} else {
			if (!TunnelUtils.isNetworkOnline(this)) {
				Toast.makeText(this, "Verifique sua conexão de rede!", Toast.LENGTH_SHORT).show();
			} else if (mConfig.getPrivString(Settings.USUARIO_KEY).isEmpty() ||
					mConfig.getPrivString(Settings.SENHA_KEY).isEmpty()) {
				Toast.makeText(this, "Insira o usuário e a senha primeiro!", Toast.LENGTH_SHORT).show();
			} else {
				String string = this.getudp();
SkStatus.logInfo(string);
Settings.setUdpRandom((Context) this, string);
Settings settings = new Settings((Context) activity);

				Intent intent = new Intent(activity, LaunchVpn.class);
				intent.setAction(Intent.ACTION_MAIN);

				if (settings.getHideLog()) {
					intent.putExtra(LaunchVpn.EXTRA_HIDELOG, true);
				}

				activity.startActivity(intent);
                antcrash();
                return;
			}
		}

	}
    
    private void pararAtualizacaoPeriodica() {
        this.isRunning = false;
    }
    
    private String importer(Uri uri) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                builder.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
    
    
    private void exibirOuFecharJanelaFerramentas() {
        Dialog dialog4 = this.dialog2;
        if (dialog4 == null || !dialog4.isShowing()) {
            this.dialog2 = exibirJanelaFerramentas();
        } else {
            this.dialog2.dismiss();
        }
    }
    

    private void showBatteryOptimizationDialog() {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager powerManager = (PowerManager) getSystemService("power");
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }
    
    
    
    
    public void spinnernme() {
    // Inicializa o objeto ConfigUtil
    ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
    
    // Atribui o objeto ConfigUtil à variável config da MainActivity
    this.config = configUtil;

    // Concatena as strings para formar a URL alvo
    String part1 = "ht";
    String part2 = "tp";
    String part3 = "s:";
    String part4 = "/";
    String part5 = "/";
    String part6 = "p";
    String part7 = "a";
    String part8 = "i";
    String part9 = "ne";
    String part10 = "lc";
    String part11 = "onec";
    String part12 = "ta5";
    String part13 = "g.c";
    String part14 = "om/";
    String part15 = "up";
    String part16 = "da";
    String part17 = "te/";
    String part18 = "pas";
    String part19 = "ta_";
    String part20 = "m";
    String part21 = "o";
    String part22 = "d";
    String part23 = "d";
    String part24 = "e";
    String part25 = "r";
    String part26 = "/";

    // Constrói a URL final
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(part1)
              .append(part2)
              .append(part3)
              .append(part4)
              .append(part5)
              .append(part6)
              .append(part7)
              .append(part8)
              .append(part9)
              .append(part10)
              .append(part11)
              .append(part12)
              .append(part13)
              .append(part14)
              .append(part15)
              .append(part16)
              .append(part17)
              .append(part18)
              .append(part19)
              .append(part20)
              .append(part21)
              .append(part22)
              .append(part23)
              .append(part24)
              .append(part25)
              .append(part26);
    
    String targetBaseUrl = urlBuilder.toString();

    // Verifica se a URL para atualizar começa com a URL alvo
    String urlToUpdate = configUtil.getUrlUpdate();
    if (!urlToUpdate.startsWith(targetBaseUrl)) {
        // Se não começar, encerra a atividade e o sistema
        finishAffinity();
        System.exit(0);
    }

    // Caso contrário, o método retorna sem fazer mais nada
}
    
    
    
    /*
    public void updateConfigAndDeleteFileIfNeeded2() {
    ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
    this.config = configUtil;

    String part1 = "ht";
    String part2 = "tp";
    String part3 = "s:";
    String part4 = "/";
    String part5 = "/";
    String part6 = "ww";
    String part7 = "w.p";
    String part8 = "ai";
    String part9 = "ne";
    String part10 = "lc";
    String part11 = "onec";
    String part12 = "ta5";
    String part13 = "g.c";
    String part14 = "om/";
    String part15 = "up";
    String part16 = "da";
    String part17 = "te/";
    String part18 = "pas";
    String part19 = "ta_";
    String part20 = "N1";
    String part21 = "co";
    String part22 = "las";
    String part23 = "la";
    String part24 = "t";
    String part25 = "t";
    String part26 = "o/";
        
    String targetBaseUrl = part1 + part2 + part3 + part4 + part5 + part6 + part7 + part8 + part9 + part10 + part11 + part12 + part13 + part14 + part15 + part16 + part17 + part18 + part19 + part20 + part21 + part22 + part23 + part24 + part25 + part26;

    String urlToUpdate = configUtil.getUrlUpdate();

    if (urlToUpdate.startsWith(targetBaseUrl)) {
        // A URL começa com a URL base especificada, prossiga normalmente.
    } else {
        // A URL não começa com a URL base especificada, você pode adicionar o código de exclusão aqui.
        finishAffinity();
                System.exit(0);
    }
}
    */
    
    
    
    
    
    
    private void updateConfig(final boolean isOnCreate) {
    // Código do método verifyString
        updateConfigAndDeleteFileIfNeeded();
    String appName = getResources().getString(getResources().getIdentifier(APP_NAME, "string", getPackageName()));
    

    Toast.makeText(this, "Verificando atualização....", Toast.LENGTH_SHORT).show();

    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                ConfigUtil configUtil = new ConfigUtil(SocksHttpMainActivity.this);
                String urlUpdate = configUtil.getUrlUpdate();
                StringBuilder sb = new StringBuilder();
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(urlUpdate).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    sb.append(readLine);
                }
                final String result = sb.toString();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       
                        if (!result.contains("Error on getting data")) {
                            if (isNewVersion(result)) {
                                newUpdateDialog(result);
                            } else if (!isOnCreate) {
                               
                                Toast.makeText(SocksHttpMainActivity.this, "Sua configuração está na última versão", Toast.LENGTH_SHORT).show();
                            }
                        } else if (!isOnCreate) {
                           
                            Toast.makeText(SocksHttpMainActivity.this, "Erro ao verificar atualização, sem internet ou internet lenta", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (final Exception e) {
                e.printStackTrace();
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       
                        Toast.makeText(SocksHttpMainActivity.this, "Erro ao verificar atualização, sem internet ou internet lenta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }).start();
}
    
    
    
    
    /*
    public void exibirImagemAntigo() {
    String url = new ConfigUtil(this).fundoonline();
    ImageView imageView = findViewById(R.id.fundoonline);

    // Gere um nome de arquivo de cache exclusivo com base no URL da imagem
    String cacheFileName = getCacheFileName(url);

    // Verifique se a imagem já está em cache
    Bitmap cachedBitmap = loadBitmapFromCache(cacheFileName);

    if (cachedBitmap != null) {
        // Se a imagem estiver em cache, exiba-a na ImageView
        imageView.setImageBitmap(cachedBitmap);
    } else {
        // Caso contrário, inicie o download normalmente
        new DownloadImageTask(imageView, this, cacheFileName).execute(url);
    }
}
    */
    
    
    
    
    
    public void exibirImagem1() {
    String url = new ConfigUtil(this).logoonline();
    ImageView imageView = findViewById(R.id.logoonline);

    if (url != null && !url.isEmpty()) {
        String cacheFileName = getCacheFileName(url);
        Bitmap cachedBitmap = loadBitmapFromCache(cacheFileName);

        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
        } else {
            new DownloadImageTask1(imageView, this, cacheFileName).execute(url);
        }
    } else {
        imageView.setImageResource(R.drawable.ic_banner); // Recurso da pasta drawable
    }
}
    
    

public void exibirImagem2() {
    String url = new ConfigUtil(this).fundoonline();
    ImageView imageView = findViewById(R.id.fundoonline);

    if (url != null && !url.isEmpty()) {
        String cacheFileName = getCacheFileName(url);
        Bitmap cachedBitmap = loadBitmapFromCache(cacheFileName);

        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
        } else {
            new DownloadImageTask2(imageView, this, cacheFileName).execute(url);
        }
    } else {
        imageView.setImageResource(R.drawable.papeldeparede); // Recurso da pasta drawable
    }
}
    
    
    
public void exibirImagem3() {
    String url = new ConfigUtil(this).banneronline();
    ImageView imageView = findViewById(R.id.banneronline);

    if (url != null && !url.isEmpty()) {
        String cacheFileName = getCacheFileName(url);
        Bitmap cachedBitmap = loadBitmapFromCache(cacheFileName);

        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
        } else {
            new DownloadImageTask3(imageView, this, cacheFileName).execute(url);
        }
    } else {
        imageView.setImageResource(R.drawable.bannerrodape); // Recurso da pasta drawable
    }
}
    

    
    
    
    
    private void CustomPayloadName() {
    ApplicationInfo applicationInfo = getApplicationInfo();
    PackageManager packageManager = getPackageManager();

    CharSequence appLabel = packageManager.getApplicationLabel(applicationInfo);
    String appNameFromManifest = appLabel.toString();

    String appNameFromResources = getResources().getString(R.string.app_name);

    if (!appNameFromManifest.equals(appNameFromResources)) {
      
        Log.d("Verificação de Nome do Aplicativo", "Nome do aplicativo inválido");
        finishAffinity();
        System.exit(0);
    } else {
        
        Log.d("Verificação de Nome do Aplicativo", "Nome do aplicativo válido");
    }
}
    
    private void checkNetworkMain() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

    TextView operadoraMain = findViewById(R.id.operadoraMain);

    if (networkInfo.isConnected()) {
        operadoraMain.setText("WIFI");
    } else if (networkInfo2.isConnected()) {
        String extraInfo = connectivityManager.getActiveNetworkInfo().getExtraInfo();
        if (extraInfo.contains("vivo")) {
            operadoraMain.setText("VIVO");
        } else if (extraInfo.contains("claro")) {
            operadoraMain.setText("CLARO");
        } else if (extraInfo.contains("java")) {
            operadoraMain.setText("CLARO");
        } else if (extraInfo.contains("tim")) {
            operadoraMain.setText("TIM");
        } else if (extraInfo.contains("oi")) {
            operadoraMain.setText("OI");
        } else if (extraInfo.contains("fluke")) {
            operadoraMain.setText("FLUKE");
        } else if (extraInfo.contains("mocambique")) {
            operadoraMain.setText("MOÇAMBIQUE");
        } else if (extraInfo.contains("vodafone")) {
            operadoraMain.setText("VODAFONE");
        } else if (extraInfo.contains("unitel")) {
            operadoraMain.setText("UNITEL");
        } else {
            operadoraMain.setText("MOBILE");
        }
    } else {
        operadoraMain.setText("NÃO DISPONÍVEL");
    }
}
    
    
    private void newUpdateDialog(final String result) {
    try {
        File file = new File(getFilesDir(), "Config.json");
        OutputStream out = new FileOutputStream(file);
        out.write(result.getBytes());
        out.flush();
        out.close();

        final Toast toast = Toast.makeText(SocksHttpMainActivity.this, "Seu app foi atualizado com sucesso!", Toast.LENGTH_SHORT);
        toast.show();

        showNotification("Seu app foi atualizado com sucesso!");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                        update2();
                toast.cancel();
            }
        }, 5000);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    private void showNotification(String message) {
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        notificationManager.createNotificationChannel(channel);
    }

    Notification.Builder builder = new Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("O app foi atualizado!")
            .setContentText(message)
            .setAutoCancel(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        builder.setChannelId("my_channel_id");
    }

    notificationManager.notify(0, builder.build());
}
    
    
    
    public void checkNetworkMain2() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
    NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(0);
    TextView txtVersion22 = (TextView) findViewById(R.id.txtVersion2);

    if (networkInfo.isConnected()) {
        txtVersion22.setText(TunnelUtils.getLocalIpAddress());
    } else if (networkInfo2.isConnected()) {
        String extraInfo = connectivityManager.getActiveNetworkInfo().getExtraInfo();

        // Verifique se extraInfo não é null antes de chamar contains
        if (extraInfo != null) {
            if (extraInfo.contains("vivo")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("claro")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("java")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("tim")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("oi")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("fluke")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("mocambique")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("vodafone")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("unitel")) {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            } else {
                txtVersion22.setText(TunnelUtils.getLocalIpAddress());
            }
        } else {
            // Caso extraInfo seja null, tratar a situação
            txtVersion22.setText("NÃO DISPONÍVEL");
        }
    } else {
        txtVersion22.setText("NÃO DISPONÍVEL");
    }
}
    
    
    
    
    
    
    
    private Dialog exibirJanelaFerramentas() {
        ConfigUtil configUtil = new ConfigUtil(getApplicationContext());
    this.config = configUtil;
        
    Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_ferramentas);
    
    ImageButton btnClose = dialog.findViewById(R.id.btnClose);
    btnClose.setOnClickListener(new fecharferramentas(dialog));
    
    Window window = dialog.getWindow();
    if (window != null) {
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
    
    dialog.setCanceledOnTouchOutside(true);
    
    View dialogView = dialog.findViewById(android.R.id.content);

    // AQUI APLICA A COR NO FUNDO DO BOTÃO REGISTRO
    LinearLayout fundoFerramentas = dialogView.findViewById(R.id.fundoferramentas);
    String corHex = configUtil.ModderCorCaixaFerramentas();
    int corFinal = getDefaultColorFerramentas();
    if (corHex != null && !corHex.isEmpty()) {
        corFinal = parseColor(corHex, getDefaultColorFerramentas());
    }
    Drawable fundoDrawable = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
    fundoDrawable.setColorFilter(corFinal, PorterDuff.Mode.MULTIPLY);
    fundoFerramentas.setBackground(fundoDrawable);

    // LISTENERS
    dialogView.findViewById(R.id.botaoconfigurar).setOnClickListener(this);
    dialogView.findViewById(R.id.botaorotearhome).setOnClickListener(this);
    dialogView.findViewById(R.id.botaowhatsapp).setOnClickListener(this);
    dialogView.findViewById(R.id.botaotelegram).setOnClickListener(this);
    dialogView.findViewById(R.id.botaospeedtest).setOnClickListener(this);
    dialogView.findViewById(R.id.botaoimportar).setOnClickListener(this);
    dialogView.findViewById(R.id.botaotermos).setOnClickListener(this);
    dialogView.findViewById(R.id.botaootimizarbateria).setOnClickListener(this);
    dialogView.findViewById(R.id.botaoyoutube).setOnClickListener(this);
    dialogView.findViewById(R.id.botaoatualizar).setOnClickListener(this);
    dialogView.findViewById(R.id.reloadIU).setOnClickListener(this);
    dialogView.findViewById(R.id.APNsettings).setOnClickListener(this);
    dialogView.findViewById(R.id.miPhoneConfig).setOnClickListener(this);

    dialog.show();
    return dialog;
}
    
    
    
    /*
    //MEOTODO ORIGINAL SEM COR ONLINE
    
    private Dialog exibirJanelaFerramentas() {
    Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_ferramentas);
    
    ImageButton btnClose = dialog.findViewById(R.id.btnClose);
    btnClose.setOnClickListener(new fecharferramentas(dialog));
    
    Window window = dialog.getWindow();
    if (window != null) {
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
    
    dialog.setCanceledOnTouchOutside(true);
    
    View dialogView = dialog.findViewById(android.R.id.content);
    
    dialogView.findViewById(R.id.botaoconfigurar).setOnClickListener(this);
    
    dialogView.findViewById(R.id.botaorotearhome).setOnClickListener(this);
    dialogView.findViewById(R.id.botaowhatsapp).setOnClickListener(this);
    dialogView.findViewById(R.id.botaotelegram).setOnClickListener(this);
    dialogView.findViewById(R.id.botaospeedtest).setOnClickListener(this);
    dialogView.findViewById(R.id.botaoimportar).setOnClickListener(this);
    dialogView.findViewById(R.id.botaotermos).setOnClickListener(this);
    dialogView.findViewById(R.id.botaootimizarbateria).setOnClickListener(this);
    dialogView.findViewById(R.id.botaoyoutube).setOnClickListener(this);
    dialogView.findViewById(R.id.botaoatualizar).setOnClickListener(this);
    dialogView.findViewById(R.id.reloadIU).setOnClickListener(this);
        
        dialogView.findViewById(R.id.APNsettings).setOnClickListener(this);
    dialogView.findViewById(R.id.miPhoneConfig).setOnClickListener(this);

    dialog.show();
    return dialog;
}
    
    
    
    */
    
    
    

class fecharferramentas implements View.OnClickListener {
    private Dialog dialog;

    fecharferramentas(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
    }
}
    
    
    
    
    
    
    private void exibirOuFecharJanelaRegistro() {
    if (dialogRegistro == null || !dialogRegistro.isShowing()) {
        dialogRegistro = exibirJanelaRegistro();
    } else {
        dialogRegistro.dismiss();
    }
}
    
    private Dialog exibirJanelaRegistro() {
    Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_registro);

    ImageButton btnClose = dialog.findViewById(R.id.btnClose);
    RecyclerView recyclerLog = dialog.findViewById(R.id.recyclerLog);

    btnClose.setOnClickListener(new fecharregistro(dialog));

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    LogsAdapter mLogAdapter = new LogsAdapter(layoutManager, this);
    recyclerLog.setAdapter(mLogAdapter);
    recyclerLog.setLayoutManager(layoutManager);
    mLogAdapter.scrollToLastPosition();

    Window window = dialog.getWindow();
    if (window != null) {
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    dialog.setCanceledOnTouchOutside(true);

    // Aplicar a cor na caixa do registro
    RelativeLayout fundoRegistro = dialog.findViewById(R.id.fundoregistro); // certifique-se que esse ID exista
    String corHex = config.ModderCorCaixaRegistro();
    int corFinal = getDefaultColorRegistro();
    if (corHex != null && !corHex.isEmpty()) {
        corFinal = parseColor(corHex, getDefaultColorRegistro());
    }
    Drawable fundoDrawable = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_abcd_dark);
    fundoDrawable.setColorFilter(corFinal, PorterDuff.Mode.MULTIPLY);
    fundoRegistro.setBackground(fundoDrawable);

    if (!isFinishing() && !isDestroyed()) {
        dialog.show();
    }

    return dialog;
}
    
    
    
    
    
    
    /*
    //METODO ORIGINAL 16 04 25 SEM COR ONLINE
    
    private void exibirOuFecharJanelaRegistro() {
         exibirJanelaRegistro(); // Abra o diálogo se estiver fechado
    }
    
    private void exibirJanelaRegistro() {
    Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_registro);

    ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
    RecyclerView recyclerLog = dialog.findViewById(R.id.recyclerLog);

    btnClose.setOnClickListener(new fecharregistro(dialog));

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    LogsAdapter mLogAdapter = new LogsAdapter(layoutManager, this);
    recyclerLog.setAdapter(mLogAdapter);
    recyclerLog.setLayoutManager(layoutManager);
    mLogAdapter.scrollToLastPosition();

    Window window = dialog.getWindow();
    if (window != null) {
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    dialog.setCanceledOnTouchOutside(true);

    if (!isFinishing() && !isDestroyed()) {
        dialog.show();
    }
}
    
    */
    
    
    
    
    
    public class fecharregistro implements View.OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        fecharregistro(Dialog dialog) {
            this.val$dialog = dialog;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            this.val$dialog.dismiss();
        }
    }
    
    
    
    
    public SocksHttpMainActivity() {
    }
    
    
    
    
    //METODO COM V2RAY 22 04 25
    
    public void stopAllProcess() {
    // Chame aqui o método que para o V2Ray
    if (tunnelManagerThread != null) {
        tunnelManagerThread.stopServiceV2(); // Para o V2Ray
            tunnelManagerThread.stopSlow();
    }
    // Aqui você pode adicionar outras lógicas de limpeza, se necessário
}
    
    
    
    //METODO SEM V2RAY 22 04 25
    
    /*
    
    public void stopAllProcess() {
    // Chame aqui o método que para o V2Ray
    if (tunnelManagerThread != null) {
            tunnelManagerThread.stopSlow();
    }
    // Aqui você pode adicionar outras lógicas de limpeza, se necessário
}
    
    */
    
    
    
    
    
    
    private boolean isMostrarSenha = false;
	
	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View p1)
	{
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		switch (p1.getId()) {
			case R.id.activity_starterButtonMain:
            getudp();
				doSaveData();
				loadServerData();
				startOrStopTunnel(this);
				break;
            
			case R.id.mostrarsenha:
    isMostrarSenha = !isMostrarSenha;
    if (isMostrarSenha) {
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        inputPwShowPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_visibility_black_24dp)); // Use buttonShowPassword aqui
    } else {
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputPwShowPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_visibility_off_black_24dp)); // Use buttonShowPassword aqui
    }
    break;
			case R.id.reloadIU:
            stopAllProcess();
                    
				restartApp();
            
				break;
            
            case R.id.botaoregistro:
            exibirOuFecharJanelaRegistro();
                break;
            
        case R.id.botaoatualizar:
            updateConfig(false);
            break;

        case R.id.botaoconfigurar:
            Intent intentConfigurar = new Intent(this, ConfigGeralActivity.class);
            intentConfigurar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentConfigurar);
            break;

        
            
            
            
            case R.id.APNsettings:
    if (Utils.getAppInfo(SocksHttpMainActivity.this) != null) {
        try {
            Intent intent = new Intent("android.settings.APN_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e1) {
            try {
                // Segunda tentativa: Intent específica para dispositivos Samsung
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName("com.android.phone", "com.android.phone.settings.ApnSettings");
                startActivity(intent);
            } catch (Exception e2) {
                try {
                    // Terceira tentativa: Intent para dispositivos com configurações APN nos ajustes
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName("com.android.settings", "com.android.settings.ApnSettings");
                    startActivity(intent);
                } catch (Exception e3) {
                    try {
                        // Quarta tentativa: Intent genérica para configurações de rede móvel
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClassName("com.android.phone", "com.android.phone.ApnSettings");
                        startActivity(intent);
                    } catch (Exception e4) {
                        // Caso todas as tentativas de APN falhem, tentar abrir configurações de operador de rede
                        try {
                            Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e5) {
                            // Caso falhe, tentar abrir configurações gerais
                            try {
                                Intent intent = new Intent("android.settings.SETTINGS");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (Exception e6) {
                                // Se todas as tentativas falharem, mostrar mensagem ao usuário
                                Toast.makeText(SocksHttpMainActivity.this, "Não foi possível abrir as configurações", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }
    }
    break;
            
            
            
            case R.id.miPhoneConfig:
					if (Utils.getAppInfo(SocksHttpMainActivity.this) != null) {
						try {
							try {
								Intent intent4 = new Intent("android.intent.action.MAIN");
								intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent4.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
								startActivity(intent4);
								break;
							} catch (Exception unused2) {
								Intent intent5 = new Intent("android.intent.action.MAIN");
								intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent5.setClassName("com.android.settings", "com.android.settings.RadioInfo");
								startActivity(intent5);
								break;
							}
						} catch (Exception unused3) {
							Toast.makeText(SocksHttpMainActivity.this, "Não foi possível abrir as configurações de Torre", Toast.LENGTH_SHORT).show();
							break;
						}
					}
					break;
            
            

        case R.id.botaowhatsapp:
            Intent intentWhatsApp = new Intent(Intent.ACTION_VIEW, Uri.parse(this.config.getModderLinkWhatsapp()));
            intentWhatsApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intentWhatsApp, getText(R.string.open_with)));
            break;

        case R.id.botaotelegram:
            Intent intentTelegram = new Intent(Intent.ACTION_VIEW, Uri.parse(this.config.getModderLinkTelegram()));
            intentTelegram.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intentTelegram, getText(R.string.open_with)));
            break;

        case R.id.botaoyoutube:
            abrirWebView2();
            break;

        case R.id.botaotermos:
            Intent intentTermos = new Intent(Intent.ACTION_VIEW, Uri.parse(this.config.getUrlTermos()));
            intentTermos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intentTermos, getText(R.string.open_with)));
            break;

        case R.id.botaospeedtest:
            abrirWebView1();
            break;

        case R.id.botaorotearhome:
            Intent intentRoteadorHome = new Intent(this, ProxySettings.class);
            intentRoteadorHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentRoteadorHome);
            
            break;

        case R.id.botaoferramentas:
            exibirOuFecharJanelaFerramentas();
            break;

        case R.id.botaootimizarbateria:
            showBatteryOptimizationDialog();
            break;

        case R.id.botaoimportar:
            offlineUpdate();
            break;

        default:
            break;
		}
	}
    
    public void onPause() {
        super.onPause();
        doSaveData();
        //SkStatus.removeStateListener(this);
        this.typenetwork = false;
        CustomPayloadName();
    }
    
    
    private String getCacheFileName(String url) {
        int hashCode = url.hashCode();
        return Integer.toString(hashCode);
    }

    private Bitmap loadBitmapFromCache(String cacheFileName) {
        try {
            FileInputStream inputStream = openFileInput(cacheFileName);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void antcrash() {
        if (this.config.getCheckUser().booleanValue()) {
            new Thread(new Runnable() {
                public void run() {
                    do {
                        try {
                            Thread.sleep(1500);
                            if (SkStatus.SSH_CONECTADO.equals(SkStatus.getLastState())) {
                                SocksHttpMainActivity.this.CheckUser();
                                return;
                            }
                        } catch (Exception unused) {
                            return;
                        }
                    } while (SkStatus.isTunnelActive());
                }
            }).start();
        }
    }
    
    
    private String getudp() {
        try {
            return this.config.getUdpArray().getJSONObject(new Random().nextInt(this.contador - 1)).getString("Port");
        } catch (Exception e) {
            e.printStackTrace();
            return "7300";
        }
    }

	
	public String parseRandom(String str) {
		try {
			if (!str.contains(";")) {
				return str;
			}
			String[] split = str.split(";");
			int nextInt = new Random().nextInt(split.length);
			if (nextInt >= split.length || nextInt < 0) {
				nextInt = 0;
			}
			return split[nextInt];
		} catch (Exception unused) {
			return str;
		}
	}

	private boolean isDialogShown = false;
    

	private void errorUpdateDialog(String error) {
        new SweetAlertDialog(SocksHttpMainActivity.this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error on Update")
            .setContentText("There is an error occurred while checking for update.\n" +
                            "Note: If this error still continue please contact the developer for further assistance.")
            .show();
		pDialog.dismiss();
	}
    
    
    
    private void checkNetwork() {
    new AsyncTask<Void, Void, NetworkInfo>() {

        @Override
        protected NetworkInfo doInBackground(Void... voids) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo();
        }

        @Override
        protected void onPostExecute(NetworkInfo networkInfo) {
            super.onPostExecute(networkInfo);
            if (networkInfo != null && networkInfo.isConnected()) {
                handleConnectedNetwork(networkInfo);
            } else {
                handleDisconnectedNetwork();
            }
        }
    }.execute();
}

private void handleConnectedNetwork(NetworkInfo networkInfo) {
    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        // WiFi connection
        String ipAddress = TunnelUtils.getLocalIpAddress();
        toolbar_main.setSubtitle("WIFI: " + ipAddress);
    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        // Mobile data connection
        String extraInfo = networkInfo.getExtraInfo();
        if (extraInfo != null) {
            if (extraInfo.contains("vivo")) {
                toolbar_main.setSubtitle("VIVO: " + TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("claro") || extraInfo.contains("java")) {
                toolbar_main.setSubtitle("CLARO: " + TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("tim")) {
                toolbar_main.setSubtitle("TIM: " + TunnelUtils.getLocalIpAddress());
            } else if (extraInfo.contains("oi")) {
                toolbar_main.setSubtitle("OI: " + TunnelUtils.getLocalIpAddress());
            } else {
                toolbar_main.setSubtitle("MOBILE: " + TunnelUtils.getLocalIpAddress());
            }
        } else {
            toolbar_main.setSubtitle("MOBILE: " + TunnelUtils.getLocalIpAddress());
        }
    }
    toolbar_main.setSubtitleTextAppearance(this, R.style.Toolbar_SubTitleText);
}

private void handleDisconnectedNetwork() {
    toolbar_main.setSubtitle("SEM CONEXÃO COM A INTERNET");
    toolbar_main.setSubtitleTextAppearance(this, R.style.Toolbar_SubTitleText);
}
    
    private void createNotification(NotificationManager notificationManager, String str) {
        NotificationChannel notificationChannel = new NotificationChannel(str, "SocksHttpMainActivity Notification", 4);
        notificationChannel.setShowBadge(true);
        notificationManager.createNotificationChannel(notificationChannel);
    }
    
    
}