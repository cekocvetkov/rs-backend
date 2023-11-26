package org.zhvtsv;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.zhvtsv.models.ExtentRequest;
import org.zhvtsv.models.StacItemsResponse;
import org.zhvtsv.stac.STACClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;


@Path("/geotiff")
public class GeoTiffResource {
    private static final Logger LOG = Logger.getLogger(GeoTiffResource.class);

    @Inject
    STACClient stacClient;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGeoTiff(ExtentRequest extentRequest) throws URISyntaxException, IOException {
        String boundingBox = getBoundingBoxString(extentRequest);
        LOG.info("Request for GeoTiffs with extent "+ boundingBox);

        StacItemsResponse stacItemsResponse = stacClient.getItems(boundingBox, "2021-06-01T09:59:31.293Z/2023-06-01T09:59:31.293Z&eo:cloud_cover=90");


//        return Response.seeOther(new URI(features.get(5).getDownloadUrl())).build();

        return Response.ok(stacItemsResponse)
                .build();
    }

    @POST
    @Path("/blob")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGeoTiffRedirect(ExtentRequest extentRequest) throws URISyntaxException, IOException {
        String boundingBox = getBoundingBoxString(extentRequest);
        LOG.info("Request for GeoTiffs with extent "+ boundingBox);

        StacItemsResponse stacItemsResponse = stacClient.getItems(boundingBox, "2021-06-01T09:59:31.293Z/2023-06-01T09:59:31.293Z&eo:cloud_cover=90");

        return Response.seeOther(new URI(stacItemsResponse.getStacItems().get(5).getDownloadUrl())).build();

    }

    @POST
    @Path("/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGeoTiffJson(ExtentRequest extentRequest)  {
        String boundingBox = getBoundingBoxString(extentRequest);
        LOG.info("Request for GeoTiffs with extent "+ boundingBox);

        JSONObject j = stacClient.getFeatureJSON(boundingBox, "2021-06-01T09:59:31.293Z/2023-06-01T09:59:31.293Z&eo:cloud_cover=90");

        System.out.println(j);
        return Response.ok(j.toString()).build();

    }

    private static String getBoundingBoxString(ExtentRequest extentRequest) {
        String array = Arrays.toString(extentRequest.getExtent());
        return array.substring(1, array.length()-2).replaceAll("\\s+", "");
    }
}
