package org.zhvtsv.sentinel.processapi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

@ApplicationScoped
public class SentinelProcessApiClient {
    @Inject
    SentinelAuth sentinelAuth;
    

    public InputStream getGeoTiff(double[] boundingBox, String dateFrom, String dateTo, int cloudCoveragePercent) {
        HttpClient httpClient = HttpClient.newBuilder().build();

        JSONObject payload = getRequestPayload(boundingBox, dateFrom, dateTo, cloudCoveragePercent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://sh.dataspace.copernicus.eu/api/v1/process"))
                .headers("Authorization", "Bearer " + sentinelAuth.getAccessToken(), "Content-Type", "application/json", "Accept", "image/tiff")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
//                .POST(HttpRequest.BodyPublishers.ofString(JsonRequestPayloadSampleData.JSON_BODY_SENTINEL_HUB))
                .build();

        HttpResponse<InputStream> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for Sentinel Processing API Data failed", Response.Status.BAD_GATEWAY);
        }
    }

    private JSONObject getRequestPayload(double[] boundingBox, String dateFrom, String dateTo, int cloudCoveragePercent) {
        JSONObject root = new JSONObject();
        JSONObject input = new JSONObject();
        JSONObject bounds = new JSONObject();
        JSONArray bbox = new JSONArray();
        Arrays.stream(boundingBox).forEach(bbox::put);
        bounds.put("bbox", bbox);
        input.put("bounds", bounds);

        JSONArray data = new JSONArray();
        JSONObject dataObject = new JSONObject();
        JSONObject dataFilter = new JSONObject();
        JSONObject timeRange = new JSONObject();
        timeRange.put("from", dateFrom);
        timeRange.put("to", dateTo);
        dataFilter.put("timeRange", timeRange);
        dataFilter.put("maxCloudCoverage", cloudCoveragePercent);
        dataObject.put("dataFilter", dataFilter);
        dataObject.put("type", "sentinel-2-l2a");
        data.put(dataObject);
        input.put("data", data);

        JSONObject output = new JSONObject();
        JSONArray responses = new JSONArray();
        JSONObject responseObject = new JSONObject();
        JSONObject format = new JSONObject();
        format.put("type", "image/tiff");
        responseObject.put("format", format);
        responseObject.put("identifier", "default");
        responses.put(responseObject);
        output.put("responses", responses);

        root.put("input", input);
        root.put("output", output);
        root.put("evalscript", JsonRequestPayloadSampleData.JSON_EVALSCRIPT);

        return root;
    }

}
