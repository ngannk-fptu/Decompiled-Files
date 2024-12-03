/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.dataformat.smile.SmileFactory
 *  org.springframework.util.Assert
 */
package org.springframework.http.converter.smile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;

public class MappingJackson2SmileHttpMessageConverter
extends AbstractJackson2HttpMessageConverter {
    public MappingJackson2SmileHttpMessageConverter() {
        this((ObjectMapper)Jackson2ObjectMapperBuilder.smile().build());
    }

    public MappingJackson2SmileHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType("application", "x-jackson-smile"));
        Assert.isInstanceOf(SmileFactory.class, (Object)objectMapper.getFactory(), (String)"SmileFactory required");
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.isInstanceOf(SmileFactory.class, (Object)objectMapper.getFactory(), (String)"SmileFactory required");
        super.setObjectMapper(objectMapper);
    }
}

