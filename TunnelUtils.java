package com.vpnmoddervpn.vpn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.ultrasshservice.logger.SkStatus;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import java.io.OutputStream;
import java.io.IOException;
import androidx.collection.ArrayMap;
import java.util.Map;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.Inet4Address;
import java.util.regex.Matcher;
import java.util.Set;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.trilead.ssh2.transport.TransportManager;
import java.io.InputStream;
import android.net.Network;
import android.net.NetworkCapabilities;
import java.util.*;

public class TunnelUtils
{
	public static Map<String, CharSequence> BBCODES_LIST;

	public static String formatCustomPayload(String hostname, int port, String payload) {
		BBCODES_LIST = new ArrayMap<>();

		BBCODES_LIST.put("[method]", "CONNECT");
		BBCODES_LIST.put("[host]", hostname);
		BBCODES_LIST.put("[port]", Integer.toString(port));
		BBCODES_LIST.put("[host_port]", String.format("%s:%d", hostname, port));
		BBCODES_LIST.put("[protocol]", "HTTP/1.0");
		BBCODES_LIST.put("[ssh]", String.format("%s:%d", hostname, port));

		BBCODES_LIST.put("[crlf]", "\r\n");
		BBCODES_LIST.put("[cr]", "\r");
		BBCODES_LIST.put("[lf]", "\n");
		BBCODES_LIST.put("[lfcr]", "\n\r");
		
		// para corrigir bugs
		BBCODES_LIST.put("\\n", "\n");
		BBCODES_LIST.put("\\r", "\r");

		String ua = System.getProperty("http.agent");
		BBCODES_LIST.put("[ua]", ua == null ? "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36" : ua);
		
		if (!payload.isEmpty()) {
			for (String key : BBCODES_LIST.keySet()) {
				key = key.toLowerCase();
				payload = payload.replace(key, BBCODES_LIST.get(key));
			}
			
			// busca pôr palavras chaves inválidas
			/*Matcher matcher = Pattern.compile("\\[(.*?)\\]")
				.matcher(payload);
			
			while (matcher.find()) {
				String group = matcher.group(1).toLowerCase();
				
				Set<String> listKeys = BBCODES_LIST.keySet();
				listKeys.add("[rotate]");
				listKeys.add("[delay_split]");
				listKeys.add("[split]");
				
				if (!listKeys.contains(String.format("[%s]", group)) && !group.startsWith("rotate=")) {
					SkStatus.logWarning(String.format("<strong>Warning:</strong> [%s] não suportado", group));
				}
			}*/
			
			// add rotate e random
			payload = parseRandom(parseRotate(payload));
			
			SkStatus.logDebug("Payload: " + payload.replace("\n", "\\n").replace("\r", "\\r"));
		}
		
		return payload;
	}

	public static boolean injectSplitPayload(String requestPayload, OutputStream out) throws IOException {
		if (requestPayload.contains("[delay_split]")) {
			String[] split = requestPayload.split(Pattern.quote("[delay_split]"));

			for (int n = 0; n < split.length; n++) {
				String str = split[n];

				if (!injectSimpleSplit(str, out)) {
					try {
						out.write(str.getBytes("ISO-8859-1"));
					} catch (UnsupportedEncodingException e2) {
						out.write(str.getBytes());
					}
					out.flush();
				}
				
				// cria delay
				try {
					if (n != (split.length-1))
						Thread.sleep(1000);
				} catch(InterruptedException e) {}
			}

			return true;
		}
		else if (injectSimpleSplit(requestPayload, out)) {
			return true;
		}

		return false;
	}

	private static boolean injectSimpleSplitOriginal(String requestPayload, OutputStream out) throws IOException {
		if (requestPayload.contains("[split]")) {
			String[] split2 = requestPayload.split(Pattern.quote("[split]"));

			for (int i = 0; i < split2.length; i++) {
				try {
					out.write(split2[i].getBytes("ISO-8859-1"));
				} catch (UnsupportedEncodingException e2) {
					out.write(split2[i].getBytes());
				}
				out.flush();
			}

			return true;
		}

		return false;
	}
    
    
    
    private static boolean injectSimpleSplit(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("[split]")) {
            String[] split2 = requestPayload.split(Pattern.quote("[split]"));
            for (int i = 0; i < split2.length; i++) {
                try {
                    out.write(split2[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split2[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split2.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectBaselineControl(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("BASELINE-CONTROL")) {
            String[] split3 = requestPayload.split(Pattern.quote("BASELINE-CONTROL"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectCONNECT(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("CONNECT")) {
            String[] split3 = requestPayload.split(Pattern.quote("CONNECT"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectCOPY(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("COPY")) {
            String[] split3 = requestPayload.split(Pattern.quote("COPY"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectCheckin(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("CHECKIN")) {
            String[] split3 = requestPayload.split(Pattern.quote("CHECKIN"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectDELETE(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("DELETE")) {
            String[] split3 = requestPayload.split(Pattern.quote("DELETE"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectGet(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("GET")) {
            String[] split3 = requestPayload.split(Pattern.quote("GET"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectHead(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("HEAD")) {
            String[] split3 = requestPayload.split(Pattern.quote("HEAD"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectICheckout(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("CHECKOUT")) {
            String[] split3 = requestPayload.split(Pattern.quote("CHECKOUT"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectInstantSplit(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("instant_split")) {
            String[] split3 = requestPayload.split(Pattern.quote("instant_split"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectLabel(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("LABEL")) {
            String[] split3 = requestPayload.split(Pattern.quote("LABEL"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectLock(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("LOCK")) {
            String[] split3 = requestPayload.split(Pattern.quote("LOCK"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectMKCOL(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("MKCOL")) {
            String[] split3 = requestPayload.split(Pattern.quote("MKCOL"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectMKWORKSPACE(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("MKWORKSPACE")) {
            String[] split3 = requestPayload.split(Pattern.quote("MKWORKSPACE"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectMOVE(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("MOVE")) {
            String[] split3 = requestPayload.split(Pattern.quote("MOVE"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectMerge(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("MERGE")) {
            String[] split3 = requestPayload.split(Pattern.quote("MERGE"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectMkActivity(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("MKACTIVITY")) {
            String[] split3 = requestPayload.split(Pattern.quote("MKACTIVITY"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectOptions(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("OPTIONS")) {
            String[] split3 = requestPayload.split(Pattern.quote("OPTIONS"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectOrderPatch(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("ORDERPATCH")) {
            String[] split3 = requestPayload.split(Pattern.quote("ORDERPATCH"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectPATCH(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("PATCH")) {
            String[] split3 = requestPayload.split(Pattern.quote("PATCH"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectPROPFIND(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("PROPFIND")) {
            String[] split3 = requestPayload.split(Pattern.quote("PROPFIND"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectPROPPATCH(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("PROPPATCH")) {
            String[] split3 = requestPayload.split(Pattern.quote("PROPPATCH"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectPost(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("POST")) {
            String[] split3 = requestPayload.split(Pattern.quote("POST"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectPut(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("PUT")) {
            String[] split3 = requestPayload.split(Pattern.quote("PUT"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectREPORT(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("REPORT")) {
            String[] split3 = requestPayload.split(Pattern.quote("REPORT"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectRFC(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("RFC")) {
            String[] split3 = requestPayload.split(Pattern.quote("RFC"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectSearch(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("SEARCH")) {
            String[] split3 = requestPayload.split(Pattern.quote("SEARCH"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectTRACE(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("TRACE")) {
            String[] split3 = requestPayload.split(Pattern.quote("TRACE"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectUNCHECKOUT(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("UNCHECKOUT")) {
            String[] split3 = requestPayload.split(Pattern.quote("UNCHECKOUT"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectUNLOCK(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("UNLOCK")) {
            String[] split3 = requestPayload.split(Pattern.quote("UNLOCK"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectUpdate(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("UPDATE")) {
            String[] split3 = requestPayload.split(Pattern.quote("UPDATE"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }

    private static boolean injectVERSIONCONTROL(String requestPayload, OutputStream out) throws IOException {
        if (requestPayload.contains("VERSION-CONTROL")) {
            String[] split3 = requestPayload.split(Pattern.quote("VERSION-CONTROL"));
            for (int i = 0; i < split3.length; i++) {
                try {
                    out.write(split3[i].getBytes("ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    out.write(split3[i].getBytes());
                }
                out.flush();
                try {
                    if (i != split3.length - 1) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e2) {
                }
            }
            return true;
        }
        return false;
    }
	
	
	/**
	* Rotate
	*/
	
	private static Map<Integer,Integer> lastRotateList = new ArrayMap<>();
	private static String lastPayload = "";
	
	public static String parseRotate(String payload) {
		Matcher match = Pattern.compile("\\[rotate=(.*?)\\]")
			.matcher(payload);
		
		// limpa dados quando a payload fôr alterada
		if (!lastPayload.equals(payload)) {
			restartRotateAndRandom();
			lastPayload = payload;
		}
		
		int i = 0;
		while (match.find()) {
			String group = match.group(1);

			String[] split = group.split(";");
			if (split.length <= 0) continue;
			
			int split_key;
			if (lastRotateList.containsKey(i)) {
				split_key = lastRotateList.get(i)+1;
				if (split_key >= split.length) {
					split_key = 0;
				}
			}
			else  {
				split_key = 0;
			}
			
			String host = split[split_key];
			
			payload = payload.replace(match.group(0), host);
			
			lastRotateList.put(i, split_key);
			
			i++;
		}
		
		return payload;
	}
	
	
	/**
	* Random
	*/
	
	//private static List<Integer> lastRandomHostsList = new ArrayList<>();
	
	// precisa melhorar
	public static String parseRandom(String payload) {
		Matcher match = Pattern.compile("\\[random=(.*?)\\]")
			.matcher(payload);

		int i = 0;
		while (match.find()) {
			String group = match.group(1);

			String[] split = group.split(";");
			if (split.length <= 0) continue;

			Random r = new Random();
			int split_key = r.nextInt(split.length);

			if (split_key >= split.length || split_key < 0) {
				split_key = 0;
			}

			String host = split[split_key];

			payload = payload.replace(match.group(0), host);
			
			i++;
		}

		return payload;
	}
	
	public static void restartRotateAndRandom() {
		lastRotateList.clear();
		//lastRandomHostsList.clear();
	}
	
	
	public static boolean isNetworkOnline(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
			.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		return (networkInfo != null && networkInfo.isConnectedOrConnecting());
	}
	
	public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
					 .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
						 .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						String sAddr = inetAddress.getHostAddress();
						
						return sAddr.toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return "ERROR Obtaining IP";
        }
        return "No IP Available";
    }
	
	/*public static String getLocationIp(String ip) throws IOException {
		//String ipapihost = TransportManager.createInetAddress("")
			//.getHostAddress();
		
		URL ipapi = new URL("https://ipapi.co/"+ ip + "/country/");

		URLConnection conn = ipapi.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36");
		//conn.setRequestProperty("Host", ipapihost);

		InputStream input = conn.getInputStream();

		StringBuilder location = new StringBuilder();
		
		int len;
		while ((len = input.read()) != -1) {
			location.append((char) len);
		}
		
		try {
			input.close();
		} catch(IOException e){}

		return location.toString();
	}*/
	
	public static boolean isActiveVpn(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			Network network = cm.getActiveNetwork();
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            
           return (capabilities!= null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
		}
		else {
			NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_VPN);
			
			return (info != null && info.isConnectedOrConnecting());
		}
	}
}
