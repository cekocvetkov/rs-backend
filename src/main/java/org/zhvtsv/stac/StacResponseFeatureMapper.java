package org.zhvtsv.stac;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StacResponseFeatureMapper
{
    private static final ObjectMapper mapper;
    
    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    public static Feature getStacResponse(String content) throws JsonProcessingException {
        return mapper.readValue(content, Feature.class);
    }
}