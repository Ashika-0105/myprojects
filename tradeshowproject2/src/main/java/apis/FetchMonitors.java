package apis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import framework.AuthService;
import framework.GenericClient;
import framework.MainEngine;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@WebServlet("/FetchMonitors")
public class FetchMonitors extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JSONObject input = parseRequest(request);
            String vendor = (String) input.get("vendor");
            
            HttpSession session = request.getSession();
            JSONObject s247 = (JSONObject) session.getAttribute("s247_creds");
            
            if (s247 == null) {
                response.setStatus(401);
                writeError(response, "Site24x7 session expired. Please re-authenticate.");
                return;
            }

            String mapXml = getServletContext().getRealPath("/WEB-INF/site24x7-mappings.xml");
            String normXml = getServletContext().getRealPath("/WEB-INF/normalization-rules.xml");

            String targetBaseUrl = (String) session.getAttribute("s247_base_url");
            String accountsUrl = (String) session.getAttribute("s247_accounts_url");


            if (targetBaseUrl == null) {
                targetBaseUrl = framework.XmlLoader.getSetting(mapXml, "base-url");
            }

            String token;
            Long expiry = (Long) session.getAttribute("s247_token_expiry");
            if (expiry != null && System.currentTimeMillis() < expiry) {
                token = (String) session.getAttribute("s247_token");
            } else {
                token = AuthService.getSite24x7AccessToken(
                    accountsUrl, 
                    (String)s247.get("client_id"), 
                    (String)s247.get("client_secret"), 
                    (String)s247.get("refresh_token")
                );
                session.setAttribute("s247_token", token);
                session.setAttribute("s247_token_expiry", System.currentTimeMillis() + (3600 * 1000));
            }

            Map<String, String> targetHeaders = Map.of("Authorization", token);
            org.json.JSONObject finalResponse = new org.json.JSONObject();

            List<String> endpoints = framework.XmlLoader.getLookupEndpoints(mapXml); 
            for (String endpoint : endpoints) {
                try {
                    String url = targetBaseUrl + endpoint;
                    String jsonResponse = GenericClient.fetch(url, targetHeaders);
                    
                    String key = endpoint.replace("/", "").split("_")[0] + "s";
                    if (endpoint.contains("user_groups")) key = "groups";
                    
                    List<Map<String, Object>> data = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.data");
                    finalResponse.put(key, data);
                } catch (Exception e) { }
            }

            Map<String, String> srcHeaders = new HashMap<>();
            input.forEach((k, v) -> { 
                if(!k.equals("vendor")) srcHeaders.put(k, v.toString()); 
            });

            MainEngine engine = new MainEngine();
            List<Map<String, Object>> preparedMonitors = engine.fetchAndPrepare(
                vendor, srcHeaders, normXml, mapXml, targetBaseUrl, targetHeaders
            );

            finalResponse.put("monitors", preparedMonitors);
            response.getWriter().write(finalResponse.toString());

        } catch (Exception e) {
            response.setStatus(500);
            e.printStackTrace(); 
            writeError(response, "Fetch failed: " + e.getMessage());
        }
    }
    
    private JSONObject parseRequest(HttpServletRequest request) throws Exception {
        StringBuilder sb = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(sb.toString());
    }

    private void writeError(HttpServletResponse response, String message) throws IOException {
        JSONObject errorJson = new JSONObject();
        errorJson.put("error", message);
        response.getWriter().write(errorJson.toString());
    }
}
