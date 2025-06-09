package com.ultrasshservice.config;

import java.io.Serializable;
import java.util.ArrayList;

public class V2Config implements Serializable {

    public String CONNECTED_V2RAY_SERVER_ADDRESS = "";
    public String CONNECTED_V2RAY_SERVER_PORT = "";
    public int LOCAL_SOCKS5_PORT = 10808;
    public int LOCAL_HTTP_PORT = 10809;
    public ArrayList<String> BLOCKED_APPS = null;
    public String V2RAY_FULL_JSON_CONFIG = null;
    public boolean ENABLE_TRAFFIC_STATICS = false;
    public String REMARK = "";
    public String APPLICATION_NAME;
    public int APPLICATION_ICON;

    // Novos campos adicionados para suportar a nova configuração
    public String SECURITY = ""; // Para armazenar o nível de segurança (ex: "tls")
    public String WS_PATH = ""; // Para armazenar o caminho WebSocket (se aplicável)
    public String WS_HOST = ""; // Para armazenar o Host do WebSocket (se aplicável)
    
    // Novos campos para a configuração VLESS
    public String USER_ID = ""; // Para armazenar o ID do usuário da URL VLESS
    public String ENCRYPTION = ""; // Para armazenar o tipo de criptografia (ex: "none")
}