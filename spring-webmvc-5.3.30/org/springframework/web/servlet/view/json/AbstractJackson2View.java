/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonView
 *  com.fasterxml.jackson.core.JsonEncoding
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectWriter
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.ser.FilterProvider
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.http.converter.json.MappingJacksonValue
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.view.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractJackson2View
extends AbstractView {
    private ObjectMapper objectMapper;
    private JsonEncoding encoding = JsonEncoding.UTF8;
    @Nullable
    private Boolean prettyPrint;
    private boolean disableCaching = true;
    protected boolean updateContentLength = false;

    protected AbstractJackson2View(ObjectMapper objectMapper, String contentType) {
        this.objectMapper = objectMapper;
        this.configurePrettyPrint();
        this.setContentType(contentType);
        this.setExposePathVariables(false);
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.configurePrettyPrint();
    }

    public final ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setEncoding(JsonEncoding encoding) {
        Assert.notNull((Object)encoding, (String)"'encoding' must not be null");
        this.encoding = encoding;
    }

    public final JsonEncoding getEncoding() {
        return this.encoding;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint.booleanValue());
        }
    }

    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    public void setUpdateContentLength(boolean updateContentLength) {
        this.updateContentLength = updateContentLength;
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        this.setResponseContentType(request, response);
        response.setCharacterEncoding(this.encoding.getJavaName());
        if (this.disableCaching) {
            response.addHeader("Cache-Control", "no-store");
        }
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object stream;
        ByteArrayOutputStream temporaryStream = null;
        if (this.updateContentLength) {
            temporaryStream = this.createTemporaryOutputStream();
            stream = temporaryStream;
        } else {
            stream = response.getOutputStream();
        }
        Object value = this.filterAndWrapModel(model, request);
        this.writeContent((OutputStream)stream, value);
        if (temporaryStream != null) {
            this.writeToResponse(response, temporaryStream);
        }
    }

    protected Object filterAndWrapModel(Map<String, Object> model, HttpServletRequest request) {
        Object value = this.filterModel(model);
        Class serializationView = (Class)model.get(JsonView.class.getName());
        FilterProvider filters = (FilterProvider)model.get(FilterProvider.class.getName());
        if (serializationView != null || filters != null) {
            MappingJacksonValue container = new MappingJacksonValue(value);
            if (serializationView != null) {
                container.setSerializationView(serializationView);
            }
            if (filters != null) {
                container.setFilters(filters);
            }
            value = container;
        }
        return value;
    }

    protected void writeContent(OutputStream stream, Object object) throws IOException {
        try (JsonGenerator generator = this.objectMapper.getFactory().createGenerator(stream, this.encoding);){
            ObjectWriter objectWriter;
            this.writePrefix(generator, object);
            Object value = object;
            Class serializationView = null;
            FilterProvider filters = null;
            if (value instanceof MappingJacksonValue) {
                MappingJacksonValue container = (MappingJacksonValue)value;
                value = container.getValue();
                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }
            ObjectWriter objectWriter2 = objectWriter = serializationView != null ? this.objectMapper.writerWithView(serializationView) : this.objectMapper.writer();
            if (filters != null) {
                objectWriter = objectWriter.with(filters);
            }
            objectWriter.writeValue(generator, value);
            this.writeSuffix(generator, object);
            generator.flush();
        }
    }

    public abstract void setModelKey(String var1);

    protected abstract Object filterModel(Map<String, Object> var1);

    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
    }

    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
    }
}

