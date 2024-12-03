/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.dataformat.xml.XmlMapper
 */
package org.springframework.http.converter.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;

public class MappingJackson2XmlHttpMessageConverter
extends AbstractJackson2HttpMessageConverter {
    public MappingJackson2XmlHttpMessageConverter() {
        this((ObjectMapper)Jackson2ObjectMapperBuilder.xml().build());
    }

    public MappingJackson2XmlHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType("application", "xml"), new MediaType("text", "xml"), new MediaType("application", "*+xml"));
        Assert.isInstanceOf(XmlMapper.class, (Object)objectMapper, "XmlMapper required");
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.isInstanceOf(XmlMapper.class, (Object)objectMapper, "XmlMapper required");
        super.setObjectMapper(objectMapper);
    }
}

