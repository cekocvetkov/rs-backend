package org.zhvtsv.odata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zhvtsv.odata.QuickLookProduct;

public class ODataResponseMapper
{
    private static final ObjectMapper mapper;
    
    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public static QuickLookProduct getODataQuickLookProduct(String content) throws JsonProcessingException {
        return mapper.readValue(content, QuickLookProduct.class);
    }
}