package com.revolut.app.config.servletconfig;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Provider
public class JacksonObjectMapperResolver implements ContextResolver<ObjectMapper> {  
    private final ObjectMapper mapper;

    public JacksonObjectMapperResolver() {
        mapper = new ObjectMapper();
        // Now you should use JavaTimeModule instead
        mapper.registerModule(new JavaTimeModule());
        // Ask Jackson to serialize dates as String (ISO-8601 by default)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }  
}