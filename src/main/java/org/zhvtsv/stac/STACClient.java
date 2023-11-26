package org.zhvtsv.stac;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zhvtsv.models.StacItem;
import org.zhvtsv.models.StacItemsResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class STACClient {
    private static final Logger LOG = Logger.getLogger(STACClient.class);

    @ConfigProperty(name = "stac-client.url")
    private String url;

    public StacItemsResponse getItems(String boundingBox, String dateTimeRange) {
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

        LOG.info("Features list: ");
        LOG.info(features);

        StacItemsResponse stackItemsResponse = new StacItemsResponse();
        List<StacItem> stacItems = new ArrayList<>();
        for(Feature f : features){
            StacItem stacItem = new StacItem();
            stacItem.setId(f.getId());
            System.out.println(f.getDownloadUrl());

            stacItem.setThumbnail(f.getThumbnail());
            stacItem.setDownloadUrl(f.getDownloadUrl());
            stacItems.add(stacItem);

        }
        stackItemsResponse.setStacItems(stacItems);

        return stackItemsResponse;
    }

    public JSONObject getFeatureJSON(String boundingBox, String dateTimeRange) {
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "bbox=" + boundingBox + "&datetime=" + dateTimeRange))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for GeoTIFF Data failed", Response.Status.BAD_GATEWAY);
        }
        JSONObject json = new JSONObject(response.body());
        JSONArray featuresJsonArray = json.getJSONArray("features");
        JSONObject featureR = featuresJsonArray.getJSONObject(5);
        return featureR;
    }

    public InputStream getDataFromAssetUrl(String url){
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            System.out.println(response.statusCode());
            System.out.println(response.body());

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for Stac Data failed", Response.Status.BAD_GATEWAY);
        }
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
                String thumbnailUrl = featureJSON.getJSONObject("assets").getJSONObject("thumbnail").getString("href");
                f.setDownloadUrl(downloadUrl);
                f.setThumbnail(thumbnailUrl);
                features.add(f);
            } catch (JsonProcessingException e) {
                throw new ServerErrorException("Request for GeoTIFF Data failed", Response.Status.BAD_GATEWAY);
            }
        }
        return features;
    }

}