/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.util.UnmodifiableMultivaluedMap
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.core.util.UnmodifiableMultivaluedMap;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

class ClientRequestContainer
extends ClientRequest {
    private ClientRequest request;

    ClientRequestContainer(ClientRequest request) {
        this.request = request;
    }

    @Override
    public Map<String, Object> getProperties() {
        if (this.request.getProperties() != null) {
            return Collections.unmodifiableMap(this.request.getProperties());
        }
        return null;
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        throw new UnsupportedOperationException("Read only instance.");
    }

    @Override
    public URI getURI() {
        return this.request.getURI();
    }

    @Override
    public void setURI(URI uri) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getMethod() {
        return this.request.getMethod();
    }

    @Override
    public void setMethod(String method) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Object getEntity() {
        return this.request.getEntity();
    }

    @Override
    public void setEntity(Object entity) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return this.getHeaders();
    }

    @Override
    public MultivaluedMap<String, Object> getHeaders() {
        if (this.request.getHeaders() != null) {
            return new UnmodifiableMultivaluedMap(this.request.getHeaders());
        }
        return null;
    }

    @Override
    public ClientRequestAdapter getAdapter() {
        return this.request.getAdapter();
    }

    @Override
    public void setAdapter(ClientRequestAdapter adapter) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public ClientRequest clone() {
        throw new UnsupportedOperationException("Not supported.");
    }
}

