package org.zhvtsv;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.zhvtsv.models.ExtentRequest;
import org.zhvtsv.stac.Feature;
import org.zhvtsv.stac.STACClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


@Path("/geotiff")
public class GeoTiffResource {
    private static final Logger LOG = Logger.getLogger(GeoTiffResource.class);

    @Inject
    STACClient stacClient;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getGeoTiff(ExtentRequest extentRequest) throws URISyntaxException {
        String boundingBox = getBoundingBoxString(extentRequest);
        LOG.info("Request for GeoTiffs with extent "+ boundingBox);

        List<Feature> features = stacClient.getItems(boundingBox, "2021-06-01T09:59:31.293Z/2023-06-01T09:59:31.293Z&eo:cloud_cover=90");
        LOG.info("Features list: ");
        LOG.info(features);

        return Response.seeOther(new URI(features.get(4).getDownloadUrl())).build();

//        return Response.ok(getClass().getClassLoader().getResourceAsStream("responseBG.tif"))
//                .build();
    }

    private static String getBoundingBoxString(ExtentRequest extentRequest) {
        String array = Arrays.toString(extentRequest.getExtent());
        return array.substring(1, array.length()-2).replaceAll("\\s+", "");
    }
}
