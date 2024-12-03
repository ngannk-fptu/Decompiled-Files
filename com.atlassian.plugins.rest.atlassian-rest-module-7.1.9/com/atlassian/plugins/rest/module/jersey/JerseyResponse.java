/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.module.jersey;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugins.rest.module.ChainingClassLoader;
import com.atlassian.plugins.rest.module.jersey.EntityConversionException;
import com.atlassian.plugins.rest.module.jersey.JerseyEntityHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;

public class JerseyResponse
implements Response {
    private final Response delegateResponse;
    private final JerseyEntityHandler jerseyEntityHandler;
    private final Plugin plugin;

    public JerseyResponse(Response delegateResponse, JerseyEntityHandler jerseyEntityHandler, Plugin plugin) {
        this.delegateResponse = delegateResponse;
        this.jerseyEntityHandler = jerseyEntityHandler;
        this.plugin = plugin;
    }

    public <T> T getEntity(Class<T> entityClass) throws ResponseException {
        InputStream entityStream = this.getResponseBodyAsStream();
        MediaType contentType = this.getContentType(MediaType.APPLICATION_XML_TYPE);
        Map<String, List<String>> unmarshallingHeaders = this.getUnmarshallingHeaders();
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        ChainingClassLoader chainingClassLoader = this.getChainingClassLoader(this.plugin);
        try {
            Thread.currentThread().setContextClassLoader(chainingClassLoader);
            T t = this.jerseyEntityHandler.unmarshall(entityClass, contentType, entityStream, unmarshallingHeaders);
            return t;
        }
        catch (IOException e) {
            throw new EntityConversionException(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    private Map<String, List<String>> getUnmarshallingHeaders() {
        return Maps.transformValues(this.getHeaders(), Collections::singletonList);
    }

    private MediaType getContentType(MediaType defaultType) {
        String headerValue = this.getHeader("Content-Type");
        if (StringUtils.isNotBlank((CharSequence)headerValue)) {
            return MediaType.valueOf(headerValue);
        }
        return defaultType;
    }

    public int getStatusCode() {
        return this.delegateResponse.getStatusCode();
    }

    public String getResponseBodyAsString() throws ResponseException {
        return this.delegateResponse.getResponseBodyAsString();
    }

    public InputStream getResponseBodyAsStream() throws ResponseException {
        return this.delegateResponse.getResponseBodyAsStream();
    }

    public String getStatusText() {
        return this.delegateResponse.getStatusText();
    }

    public boolean isSuccessful() {
        return this.delegateResponse.isSuccessful();
    }

    public String getHeader(String name) {
        return this.delegateResponse.getHeader(name);
    }

    public Map<String, String> getHeaders() {
        return this.delegateResponse.getHeaders();
    }

    private ChainingClassLoader getChainingClassLoader(Plugin plugin) {
        return new ChainingClassLoader(this.getClass().getClassLoader(), plugin.getClassLoader());
    }
}

