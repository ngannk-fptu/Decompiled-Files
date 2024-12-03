/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;

public class MappingJackson2HttpMessageConverter
extends AbstractJackson2HttpMessageConverter {
    @Nullable
    private String jsonPrefix;

    public MappingJackson2HttpMessageConverter() {
        this((ObjectMapper)Jackson2ObjectMapperBuilder.json().build());
    }

    public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = prefixJson ? ")]}', " : null;
    }

    @Override
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        String jsonpFunction;
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
        String string = jsonpFunction = object instanceof MappingJacksonValue ? ((MappingJacksonValue)object).getJsonpFunction() : null;
        if (jsonpFunction != null) {
            generator.writeRaw("/**/");
            generator.writeRaw(jsonpFunction + "(");
        }
    }

    @Override
    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
        String jsonpFunction;
        String string = jsonpFunction = object instanceof MappingJacksonValue ? ((MappingJacksonValue)object).getJsonpFunction() : null;
        if (jsonpFunction != null) {
            generator.writeRaw(");");
        }
    }
}

