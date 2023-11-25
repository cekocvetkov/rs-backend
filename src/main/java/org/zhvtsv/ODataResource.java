package org.zhvtsv;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.zhvtsv.models.PolygonCoordsRequest;
import org.zhvtsv.models.ProductRequest;
import org.zhvtsv.models.QuickLookProductResponse;
import org.zhvtsv.odata.ODataClient;

import java.io.InputStream;
import java.net.URISyntaxException;


@Path("/odata")
public class ODataResource {
    private static final Logger LOG = Logger.getLogger(GeoTiffResource.class);

    @Inject
    ODataClient oDataClient;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuickLook(PolygonCoordsRequest polygonCoordsRequest) throws URISyntaxException {
        String coordsString = getCoordsString(polygonCoordsRequest.getCoords());
        LOG.info("Request for OData with coords "+ coordsString);

        QuickLookProductResponse quickLookProductResponse = oDataClient.getProducts(coordsString);

        return Response.ok(quickLookProductResponse).build();

//        return Response.ok(getClass().getClassLoader().getResourceAsStream("responseBG.tif"))
//                .build();
    }

    @POST
    @Path("/product")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProductData(ProductRequest product) throws URISyntaxException {
        LOG.info("Request for OData product with id "+ product + " and name "+product.getProductName());

        InputStream inputStream = oDataClient.getProductRgb(product.getProductId(), product.getProductName());

        return Response.ok(inputStream).build();
//                return Response.ok(getClass().getClassLoader().getResourceAsStream("OUT.tif"))
//                .build();
    }

    private String getCoordsString(double [][] coords){
        StringBuilder sb = new StringBuilder();
        for(double[] c : coords){
            sb.append(c[0]+"%20"+c[1]+",");
        }
        System.out.println(coords[0][0] + " " + coords[0][1]);
        sb.append(coords[0][0]+"%20"+coords[0][1]);
        return sb.toString();
    }
}
