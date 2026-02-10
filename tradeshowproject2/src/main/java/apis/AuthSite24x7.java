package apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import framework.AuthService;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import static net.minidev.json.parser.JSONParser.MODE_JSON_SIMPLE;

@WebServlet("/AuthSite24x7")
public class AuthSite24x7 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String rawBody = sb.toString().trim();

            System.out.println("-------------------------------------------");
            System.out.println("JSON : " + rawBody);
            System.out.println("-------------------------------------------");

            if (rawBody.isEmpty()) {
                throw new Exception("Empty request payload received");
            }

           
            JSONParser parser = new JSONParser(MODE_JSON_SIMPLE);
            JSONObject input = (JSONObject) parser.parse(rawBody);

        
            String dc = (String) input.get("dc_region");
            if (dc == null || dc.isEmpty()) {
                dc = "in"; 
                System.out.println("DEBUG: dc_region missing in JSON, forced to 'in'");
            }
            
            String mapXml = getServletContext().getRealPath("/WEB-INF/site24x7-mappings.xml");

          
            Map<String, String> regionConfig = framework.XmlLoader.getRegionConfig(mapXml, dc);
            String accountsUrl = regionConfig.getOrDefault("accounts-url", "https://accounts.zoho.in");
            String targetBaseUrl = regionConfig.getOrDefault("base-url", "https://www.site24x7.in/api");

            String token = AuthService.getSite24x7AccessToken(
                accountsUrl, 
                (String)input.get("client_id"), 
                (String)input.get("client_secret"), 
                (String)input.get("refresh_token")
            );

           
            HttpSession session = request.getSession();
            session.setAttribute("s247_creds", input); 
            session.setAttribute("s247_token", token);
            session.setAttribute("s247_token_expiry", System.currentTimeMillis() + (3600 * 1000));
            session.setAttribute("s247_base_url", targetBaseUrl); 
            session.setAttribute("s247_accounts_url", accountsUrl);
            
            response.getWriter().write("{\"status\":\"authenticated\"}");
            
        } catch (Exception e) {
            response.setStatus(401); 
            String errMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'").replace("\n", " ") : "Unknown Error";
            response.getWriter().write("{\"error\":\"Auth failed: " + errMsg + "\"}");
            e.printStackTrace();
        }
    }
}