/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.PartialRequestBuilder;
import com.sun.jersey.client.impl.ClientRequestImpl;
import java.net.URI;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;

public abstract class ClientRequest {
    private static final RuntimeDelegate rd = RuntimeDelegate.getInstance();

    public abstract Map<String, Object> getProperties();

    public abstract void setProperties(Map<String, Object> var1);

    public boolean getPropertyAsFeature(String name) {
        return this.getPropertyAsFeature(name, false);
    }

    public boolean getPropertyAsFeature(String name, boolean defaultValue) {
        Boolean v = (Boolean)this.getProperties().get(name);
        return v != null ? v : defaultValue;
    }

    public abstract URI getURI();

    public abstract void setURI(URI var1);

    public abstract String getMethod();

    public abstract void setMethod(String var1);

    public abstract Object getEntity();

    public abstract void setEntity(Object var1);

    @Deprecated
    public abstract MultivaluedMap<String, Object> getMetadata();

    public abstract MultivaluedMap<String, Object> getHeaders();

    public abstract ClientRequestAdapter getAdapter();

    public abstract void setAdapter(ClientRequestAdapter var1);

    public abstract ClientRequest clone();

    public static final Builder create() {
        return new Builder();
    }

    public static String getHeaderValue(Object headerValue) {
        RuntimeDelegate.HeaderDelegate<?> hp = rd.createHeaderDelegate(headerValue.getClass());
        return hp != null ? hp.toString(headerValue) : headerValue.toString();
    }

    public static final class Builder
    extends PartialRequestBuilder<Builder> {
        public ClientRequest build(URI uri, String method) {
            ClientRequestImpl ro = new ClientRequestImpl(uri, method, this.entity, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
    }
}

