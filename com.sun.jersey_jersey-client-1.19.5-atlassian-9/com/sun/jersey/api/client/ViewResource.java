/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Cookie
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.core.UriBuilder
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.PartialRequestBuilder;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.ViewUniformInterface;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.impl.ClientRequestImpl;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

public class ViewResource
extends Filterable
implements RequestBuilder<Builder>,
ViewUniformInterface {
    private final Client client;
    private final URI u;

    ViewResource(Client c, URI u) {
        super(c);
        this.client = c;
        this.u = u;
    }

    private ViewResource(ViewResource that, UriBuilder ub) {
        super(that);
        this.client = that.client;
        this.u = ub.build(new Object[0]);
    }

    public URI getURI() {
        return this.u;
    }

    public UriBuilder getUriBuilder() {
        return UriBuilder.fromUri((URI)this.u);
    }

    public Builder getRequestBuilder() {
        return new Builder();
    }

    public String toString() {
        return this.u.toString();
    }

    public int hashCode() {
        return this.u.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ViewResource) {
            ViewResource that = (ViewResource)obj;
            return that.u.equals(this.u);
        }
        return false;
    }

    @Override
    public <T> T head(Class<T> c) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "HEAD"));
    }

    @Override
    public <T> T head(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "HEAD"));
    }

    @Override
    public <T> T options(Class<T> c) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> T options(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> T get(Class<T> c) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public <T> T get(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public <T> T put(Class<T> c) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> T put(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> T put(Class<T> c, Object requestEntity) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> T put(T t, Object requestEntity) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> T post(Class<T> c) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> T post(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> T post(Class<T> c, Object requestEntity) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> T post(T t, Object requestEntity) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> T delete(Class<T> c) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> T delete(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> T delete(Class<T> c, Object requestEntity) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> T delete(T t, Object requestEntity) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> T method(String method, Class<T> c) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> T method(String method, T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> T method(String method, Class<T> c, Object requestEntity) {
        return (T)this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public <T> T method(String method, T t, Object requestEntity) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public Builder entity(Object entity) {
        return (Builder)this.getRequestBuilder().entity(entity);
    }

    @Override
    public Builder entity(Object entity, MediaType type) {
        return (Builder)this.getRequestBuilder().entity(entity, type);
    }

    @Override
    public Builder entity(Object entity, String type) {
        return (Builder)this.getRequestBuilder().entity(entity, type);
    }

    @Override
    public Builder type(MediaType type) {
        return (Builder)this.getRequestBuilder().type(type);
    }

    @Override
    public Builder type(String type) {
        return (Builder)this.getRequestBuilder().type(type);
    }

    @Override
    public Builder accept(MediaType ... types) {
        return (Builder)this.getRequestBuilder().accept(types);
    }

    @Override
    public Builder accept(String ... types) {
        return (Builder)this.getRequestBuilder().accept(types);
    }

    @Override
    public Builder acceptLanguage(Locale ... locales) {
        return (Builder)this.getRequestBuilder().acceptLanguage(locales);
    }

    @Override
    public Builder acceptLanguage(String ... locales) {
        return (Builder)this.getRequestBuilder().acceptLanguage(locales);
    }

    @Override
    public Builder cookie(Cookie cookie) {
        return (Builder)this.getRequestBuilder().cookie(cookie);
    }

    @Override
    public Builder header(String name, Object value) {
        return (Builder)this.getRequestBuilder().header(name, value);
    }

    public ViewResource path(String path) {
        return new ViewResource(this, this.getUriBuilder().path(path));
    }

    public ViewResource uri(URI uri) {
        String query;
        UriBuilder b = this.getUriBuilder();
        String path = uri.getRawPath();
        if (path != null && path.length() > 0) {
            if (path.startsWith("/")) {
                b.replacePath(path);
            } else {
                b.path(path);
            }
        }
        if ((query = uri.getRawQuery()) != null && query.length() > 0) {
            b.replaceQuery(query);
        }
        return new ViewResource(this, b);
    }

    public ViewResource queryParam(String key, String value) {
        UriBuilder b = this.getUriBuilder();
        b.queryParam(key, new Object[]{value});
        return new ViewResource(this, b);
    }

    public ViewResource queryParams(MultivaluedMap<String, String> params) {
        UriBuilder b = this.getUriBuilder();
        for (Map.Entry e : params.entrySet()) {
            for (String value : (List)e.getValue()) {
                b.queryParam((String)e.getKey(), new Object[]{value});
            }
        }
        return new ViewResource(this, b);
    }

    private <T> T handle(Class<T> c, ClientRequest ro) {
        return (T)this.client.getViewProxy(c).view(c, ro, this.getHeadHandler());
    }

    private <T> T handle(T t, ClientRequest ro) {
        return (T)this.client.getViewProxy(t.getClass()).view(t, ro, this.getHeadHandler());
    }

    public final class Builder
    extends PartialRequestBuilder<Builder>
    implements ViewUniformInterface {
        private Builder() {
        }

        private ClientRequest build(String method) {
            ClientRequestImpl ro = new ClientRequestImpl(ViewResource.this.u, method, this.entity, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        private ClientRequest build(String method, Object e) {
            ClientRequestImpl ro = new ClientRequestImpl(ViewResource.this.u, method, e, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        @Override
        public <T> T head(Class<T> c) {
            return (T)ViewResource.this.handle((Object)c, this.build("HEAD"));
        }

        @Override
        public <T> T head(T t) {
            return (T)ViewResource.this.handle(t, this.build("HEAD"));
        }

        @Override
        public <T> T options(Class<T> c) {
            return (T)ViewResource.this.handle((Object)c, this.build("OPTIONS"));
        }

        @Override
        public <T> T options(T t) {
            return (T)ViewResource.this.handle(t, this.build("OPTIONS"));
        }

        @Override
        public <T> T get(Class<T> c) {
            return (T)ViewResource.this.handle((Object)c, this.build("GET"));
        }

        @Override
        public <T> T get(T t) {
            return (T)ViewResource.this.handle(t, this.build("GET"));
        }

        @Override
        public <T> T put(Class<T> c) {
            return (T)ViewResource.this.handle((Object)c, this.build("PUT"));
        }

        @Override
        public <T> T put(T t) {
            return (T)ViewResource.this.handle(t, this.build("PUT"));
        }

        @Override
        public <T> T put(Class<T> c, Object requestEntity) {
            return (T)ViewResource.this.handle((Object)c, this.build("PUT", requestEntity));
        }

        @Override
        public <T> T put(T t, Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build("PUT", requestEntity));
        }

        @Override
        public <T> T post(Class<T> c) {
            return (T)ViewResource.this.handle((Object)c, this.build("POST"));
        }

        @Override
        public <T> T post(T t) {
            return (T)ViewResource.this.handle(t, this.build("POST"));
        }

        @Override
        public <T> T post(Class<T> c, Object requestEntity) {
            return (T)ViewResource.this.handle((Object)c, this.build("POST", requestEntity));
        }

        @Override
        public <T> T post(T t, Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build("POST", requestEntity));
        }

        @Override
        public <T> T delete(Class<T> c) {
            return (T)ViewResource.this.handle((Object)c, this.build("DELETE"));
        }

        @Override
        public <T> T delete(T t) {
            return (T)ViewResource.this.handle(t, this.build("DELETE"));
        }

        @Override
        public <T> T delete(Class<T> c, Object requestEntity) {
            return (T)ViewResource.this.handle((Object)c, this.build("DELETE", requestEntity));
        }

        @Override
        public <T> T delete(T t, Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build("DELETE", requestEntity));
        }

        @Override
        public <T> T method(String method, Class<T> c) {
            return (T)ViewResource.this.handle((Object)c, this.build(method));
        }

        @Override
        public <T> T method(String method, T t) {
            return (T)ViewResource.this.handle(t, this.build(method));
        }

        @Override
        public <T> T method(String method, Class<T> c, Object requestEntity) {
            return (T)ViewResource.this.handle((Object)c, this.build(method, requestEntity));
        }

        @Override
        public <T> T method(String method, T t, Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build(method, requestEntity));
        }
    }
}

