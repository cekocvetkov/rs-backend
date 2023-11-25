package org.zhvtsv.odata;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.net.http.HttpRequest.BodyPublishers.ofString;

@ApplicationScoped
public class ODataAuth {
    @ConfigProperty(name = "odata-auth.url")
    private String url;
    @ConfigProperty(name = "odata-auth.username")
    private String username;
    @ConfigProperty(name = "odata-auth.password")
    private String password;

    //TODO: Add refresh token functionality
    public String getAccessToken() {
        HttpClient httpClient = HttpClient.newBuilder().build();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("grant_type", "password");
        parameters.put("client_id", "cdse-public");

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(ofString(form))
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        JSONObject json = new JSONObject(response.body());
        String accessToken = json.getString("access_token");

        return accessToken;
    }
}
