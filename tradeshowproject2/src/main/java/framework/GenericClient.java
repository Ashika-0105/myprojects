package framework;

import java.net.URI;
import java.net.http.*;
import java.util.*;

public class GenericClient {
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

  
    public static String fetch(String url, Map<String, String> headers) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).GET();

        if (headers != null) {
            headers.forEach((k, v) -> {
                System.out.println("DEBUG Header: " + k + " = " + v);
                builder.header(k, v);
            });
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode == 401) {
            throw new RuntimeException("UNAUTHORIZED");
        }

        if (statusCode < 200 || statusCode >= 300) {
            throw new RuntimeException("API_ERROR_" + statusCode + ": " + response.body());
        }

        return response.body();
    }


    
    public static void post(String url, Map<String, String> headers, String json) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (headers != null) {
            headers.forEach(builder::header);
        }
        
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        
        int status = response.statusCode();
        System.out.println("[TARGET-POST] Status: " + status);
        
        if (status < 200 || status >= 300) {
             System.err.println("Migration Error Body: " + response.body());
             throw new RuntimeException("POST_FAILED_" + status);
        }
    }

    
    public static String postForToken(String url, String body) throws Exception {

    	HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)); 

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        System.out.println("DEBUG: HTTP Status from Zoho -> " + response.statusCode());

        if (response.statusCode() != 200) {
            System.err.println("DEBUG: Error Body -> " + response.body());
            throw new RuntimeException("TOKEN_EXCHANGE_FAILED: " + response.body());
        }

        return response.body();
    }
}