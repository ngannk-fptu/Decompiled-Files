/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.transport.http.client;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.xml.ws.transport.http.client.HttpClientTransport;
import java.util.List;
import java.util.Map;

final class HttpResponseProperties
extends BasePropertySet {
    private final HttpClientTransport deferedCon;
    private static final BasePropertySet.PropertyMap model = HttpResponseProperties.parse(HttpResponseProperties.class);

    public HttpResponseProperties(@NotNull HttpClientTransport con) {
        this.deferedCon = con;
    }

    @PropertySet.Property(value={"javax.xml.ws.http.response.headers"})
    public Map<String, List<String>> getResponseHeaders() {
        return this.deferedCon.getHeaders();
    }

    @PropertySet.Property(value={"javax.xml.ws.http.response.code"})
    public int getResponseCode() {
        return this.deferedCon.statusCode;
    }

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return model;
    }
}

