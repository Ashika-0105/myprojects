package framework;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AuthService {

    public static String getSite24x7AccessToken(String accountsUrl, String clientId, String clientSecret, String refreshToken) throws Exception {
        
        String url = accountsUrl.trim().replaceAll("/$", "") + "/oauth/v2/token";
        
        System.out.println("DEBUG: Sending Auth Request to -> " + url);
        
        
        String formBody = "client_id=" + URLEncoder.encode(clientId.trim(), StandardCharsets.UTF_8)
                         + "&client_secret=" + URLEncoder.encode(clientSecret.trim(), StandardCharsets.UTF_8)
                         + "&refresh_token=" + URLEncoder.encode(refreshToken.trim(), StandardCharsets.UTF_8)
                         + "&grant_type=refresh_token";

        System.out.println("DEBUG: Encoded Payload -> " + formBody);

        
        String response = GenericClient.postForToken(url, formBody);

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject json = (JSONObject) parser.parse(response);

        Object token = json.get("access_token");
        if (token == null) {
            System.err.println("DEBUG: Zoho Rejection Response -> " + response);
            throw new RuntimeException("Auth failed: " + response);
        }

        return "Zoho-oauthtoken " + token.toString();
    }
}