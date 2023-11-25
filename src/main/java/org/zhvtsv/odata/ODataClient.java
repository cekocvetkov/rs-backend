package org.zhvtsv.odata;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zhvtsv.models.QuickLookProductResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ODataClient {
    private static final Logger LOG = Logger.getLogger(ODataClient.class);

    @ConfigProperty(name = "odata-client.url")
    private String url;
    @ConfigProperty(name = "odata-download.url")
    private String downloadUrl;

    @Inject
    ODataAuth oDataAuth;

    public QuickLookProductResponse getProducts(String boundingBox) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        String params = "?$filter=OData.CSC.Intersects(area=geography%27SRID=4326;POLYGON(("+boundingBox+"))%27)%20" +
        "and%20Collection/Name%20eq%20%27SENTINEL-2%27%20and%20ContentDate/Start%20gt%202020-05-03T00:00:00.000Z%20and%20ContentDate/Start%20lt%202022-05-03T00:11:00.000Z%20" +
        "and%20Attributes/OData.CSC.DoubleAttribute/any(att:att/Name%20eq%20'cloudCover'%20and%20att/OData.CSC.DoubleAttribute/Value%20le%2010.00)&$expand=Assets";

        System.out.println(params);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + params))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("!!!");
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for OData Data failed", Response.Status.BAD_GATEWAY);
        }
        return getQuickLookProductResponse(response);
    }

    public InputStream getProductRgb(String productId, String productName) {
        HttpClient httpClient = HttpClient.newBuilder().build();
        String params = "("+productId+")/Nodes("+productName+")/Nodes(GRANULE)/Nodes";

        System.out.println(params);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl + params))
                .GET()
                .build();

        HttpResponse<String> response = null;
        System.out.println(request.uri());
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for OData Data failed", Response.Status.BAD_GATEWAY);
        }

        JSONObject json = new JSONObject(response.body());
        JSONArray result = json.getJSONArray("result");
        String granuleId = result.getJSONObject(0).getString("Id");

        String downloadUrl = getRGBImageDownloadUrlFromGranule(productId, productName, granuleId);
        System.out.println(downloadUrl);
        return downloadRgb(downloadUrl);
    }


    private String getRGBImageDownloadUrlFromGranule(String productId, String productName, String granuleId){
        LOG.info("Getting download url for granule "+granuleId+"...");
        HttpClient httpClient = HttpClient.newBuilder().build();
        String params = "("+productId+")/Nodes("+productName+")/Nodes(GRANULE)/Nodes("+granuleId+")/Nodes(IMG_DATA)/Nodes(R10m)/Nodes";

        System.out.println(params);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl + params))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for OData Data failed", Response.Status.BAD_GATEWAY);
        }

        JSONObject json = new JSONObject(response.body());
        JSONArray results = json.getJSONArray("result");

        String rgbImageName = null;
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String imageName = result.getString("Id");
            if(imageName.endsWith("TCI_10m.jp2")){
                rgbImageName = imageName;
                break;
            }
        }
        if(rgbImageName == null) {
            throw new ServerErrorException("Request for OData Data failed. No RGB Image.", Response.Status.BAD_GATEWAY);
        }

        return downloadUrl + params + "("+rgbImageName+")/$value";
    }

    public InputStream downloadRgb(String url) {
        System.out.println(url);
        HttpClient httpClient = HttpClient.newBuilder().build();
        String accessToken = oDataAuth.getAccessToken();
        System.out.println(accessToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<InputStream> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            System.out.println(response.statusCode());
            System.out.println(response.body());
//            try (InputStream inputStream = response.body(); FileOutputStream outputStream = new FileOutputStream("file.jp2")) {
//                IOUtils.copy(inputStream, outputStream);
//                System.out.println("File saved to " + "file.jp2");
//            }
//            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "gdal_translate file.jp2 OUT.tif");
//            builder.redirectErrorStream(true);
//            Process p = builder.start();
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("Request for OData Data failed", Response.Status.BAD_GATEWAY);
        }
    }

    private QuickLookProductResponse getQuickLookProductResponse(HttpResponse<String> response) {
        JSONObject json = new JSONObject(response.body());
        JSONArray valuesArray = json.getJSONArray("value");
        List<QuickLookProduct> quickLookProducts = new ArrayList<>();
        System.out.println(valuesArray.getJSONObject(0));
        for (int i = 0; i < valuesArray.length(); i++) {
            JSONObject valueObject = valuesArray.getJSONObject(i);

            try {
                QuickLookProduct quickLookProduct = ODataResponseMapper.getODataQuickLookProduct(valueObject.toString());
                quickLookProducts.add(quickLookProduct);
//                System.out.println(quickLookProduct);
            } catch (JsonProcessingException e) {
                throw new ServerErrorException("Request for OData failed", Response.Status.BAD_GATEWAY);
            }
        }
        return new QuickLookProductResponse(quickLookProducts);
    }
}
