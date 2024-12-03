/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.impl;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.core.header.OutBoundHeaders;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

public final class ClientRequestImpl
extends ClientRequest
implements ClientRequestAdapter {
    private Map<String, Object> properties;
    private URI uri;
    private String method;
    private Object entity;
    private final MultivaluedMap<String, Object> metadata;
    private ClientRequestAdapter adapter;

    public ClientRequestImpl(URI uri, String method) {
        this(uri, method, null, null);
    }

    public ClientRequestImpl(URI uri, String method, Object entity) {
        this(uri, method, entity, null);
    }

    public ClientRequestImpl(URI uri, String method, Object entity, MultivaluedMap<String, Object> metadata) {
        this.uri = uri;
        this.method = method;
        this.entity = entity;
        this.metadata = metadata != null ? metadata : new OutBoundHeaders();
        this.adapter = this;
    }

    @Override
    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        return this.properties;
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    public void setURI(URI uri) {
        this.uri = uri;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public Object getEntity() {
        return this.entity;
    }

    @Override
    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return this.getHeaders();
    }

    @Override
    public MultivaluedMap<String, Object> getHeaders() {
        return this.metadata;
    }

    @Override
    public ClientRequestAdapter getAdapter() {
        return this.adapter;
    }

    @Override
    public void setAdapter(ClientRequestAdapter adapter) {
        this.adapter = adapter != null ? adapter : this;
    }

    @Override
    public ClientRequest clone() {
        return new ClientRequestImpl(this.uri, this.method, this.entity, ClientRequestImpl.clone(this.metadata));
    }

    private static MultivaluedMap<String, Object> clone(MultivaluedMap<String, Object> md) {
        OutBoundHeaders clone = new OutBoundHeaders();
        for (Map.Entry e : md.entrySet()) {
            clone.put(e.getKey(), new ArrayList((Collection)e.getValue()));
        }
        return clone;
    }

    @Override
    public OutputStream adapt(ClientRequest request, OutputStream out) throws IOException {
        return out;
    }
}

