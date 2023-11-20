package org.zhvtsv.stac;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class STACClient {
    @ConfigProperty(name = "stac-client.url")
    private String url;

    public List<Feature> getItems(String boundingBox, String dateTimeRange) {
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "bbox=" + boundingBox + "&datetime=" + dateTimeRange))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for GeoTIFF Data failed", Response.Status.BAD_GATEWAY);
        }
        List<Feature> features = getGeoJsonFeatures(response);

        return features;
    }

    private static List<Feature> getGeoJsonFeatures(HttpResponse<String> response) {
        JSONObject json = new JSONObject(response.body());
        JSONArray featuresJsonArray = json.getJSONArray("features");
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i < featuresJsonArray.length(); i++) {
            JSONObject featureJSON = featuresJsonArray.getJSONObject(i);
            try {
                Feature f = StacResponseFeatureMapper.getStacResponse(featureJSON.toString());
                String downloadUrl = featureJSON.getJSONObject("assets").getJSONObject("visual").getString("href");
                f.setDownloadUrl(downloadUrl);
                features.add(f);
            } catch (JsonProcessingException e) {
                throw new ServerErrorException("Request for GeoTIFF Data failed", Response.Status.BAD_GATEWAY);
            }
        }
        return features;
    }

}