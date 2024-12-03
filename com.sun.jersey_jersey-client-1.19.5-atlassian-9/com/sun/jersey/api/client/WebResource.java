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

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.PartialRequestBuilder;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.impl.ClientRequestImpl;
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

public class WebResource
extends Filterable
implements RequestBuilder<Builder>,
UniformInterface {
    private final URI u;
    private CopyOnWriteHashMap<String, Object> properties;

    WebResource(ClientHandler c, CopyOnWriteHashMap<String, Object> properties, URI u) {
        super(c);
        this.u = u;
        this.properties = properties.clone();
    }

    private WebResource(WebResource that, UriBuilder ub) {
        super(that);
        this.u = ub.build(new Object[0]);
        this.properties = that.properties == null ? null : that.properties.clone();
    }

    public URI getURI() {
        return this.u;
    }

    @Deprecated
    public UriBuilder getBuilder() {
        return UriBuilder.fromUri((URI)this.u);
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
        if (obj instanceof WebResource) {
            WebResource that = (WebResource)obj;
            return that.u.equals(this.u);
        }
        return false;
    }

    @Override
    public ClientResponse head() throws ClientHandlerException {
        ClientRequestImpl ro = new ClientRequestImpl(this.getURI(), "HEAD");
        this.setProperties(ro);
        return this.getHeadHandler().handle(ro);
    }

    @Override
    public <T> T options(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> T options(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> T get(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public <T> T get(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public void put() throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", null));
    }

    @Override
    public void put(Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> T put(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> T put(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> T put(Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> T put(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public void post() throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public void post(Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> T post(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> T post(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> T post(Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> T post(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public void delete() throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public void delete(Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> T delete(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> T delete(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> T delete(Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> T delete(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public void method(String method) throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public void method(String method, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public <T> T method(String method, Class<T> c) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> T method(String method, GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> T method(String method, Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public <T> T method(String method, GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
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

    public WebResource path(String path) {
        return new WebResource(this, this.getUriBuilder().path(path));
    }

    public WebResource uri(URI uri) {
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
        return new WebResource(this, b);
    }

    public WebResource queryParam(String key, String value) {
        UriBuilder b = this.getUriBuilder();
        b.queryParam(key, new Object[]{value});
        return new WebResource(this, b);
    }

    public WebResource queryParams(MultivaluedMap<String, String> params) {
        UriBuilder b = this.getUriBuilder();
        for (Map.Entry e : params.entrySet()) {
            for (String value : (List)e.getValue()) {
                b.queryParam((String)e.getKey(), new Object[]{value});
            }
        }
        return new WebResource(this, b);
    }

    public void setProperty(String property, Object value) {
        this.getProperties().put(property, value);
    }

    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new CopyOnWriteHashMap();
        }
        return this.properties;
    }

    private void setProperties(ClientRequest ro) {
        if (this.properties != null) {
            ro.setProperties(this.properties);
        }
    }

    private <T> T handle(Class<T> c, ClientRequest ro) throws UniformInterfaceException, ClientHandlerException {
        this.setProperties(ro);
        ClientResponse r = this.getHeadHandler().handle(ro);
        if (c == ClientResponse.class) {
            return c.cast(r);
        }
        if (r.getStatus() < 300) {
            return r.getEntity(c);
        }
        throw new UniformInterfaceException(r, ro.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
    }

    private <T> T handle(GenericType<T> gt, ClientRequest ro) throws UniformInterfaceException, ClientHandlerException {
        this.setProperties(ro);
        ClientResponse r = this.getHeadHandler().handle(ro);
        if (gt.getRawClass() == ClientResponse.class) {
            return gt.getRawClass().cast(r);
        }
        if (r.getStatus() < 300) {
            return r.getEntity(gt);
        }
        throw new UniformInterfaceException(r, ro.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
    }

    private void voidHandle(ClientRequest ro) throws UniformInterfaceException, ClientHandlerException {
        this.setProperties(ro);
        ClientResponse r = this.getHeadHandler().handle(ro);
        if (r.getStatus() >= 300) {
            throw new UniformInterfaceException(r, ro.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
        }
        r.close();
    }

    public class Builder
    extends PartialRequestBuilder<Builder>
    implements UniformInterface {
        private Builder() {
        }

        private ClientRequest build(String method) {
            ClientRequestImpl ro = new ClientRequestImpl(WebResource.this.u, method, this.entity, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        private ClientRequest build(String method, Object e) {
            ClientRequestImpl ro = new ClientRequestImpl(WebResource.this.u, method, e, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        @Override
        public ClientResponse head() throws ClientHandlerException {
            return WebResource.this.getHeadHandler().handle(this.build("HEAD"));
        }

        @Override
        public <T> T options(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("OPTIONS"));
        }

        @Override
        public <T> T options(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("OPTIONS"));
        }

        @Override
        public <T> T get(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("GET"));
        }

        @Override
        public <T> T get(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("GET"));
        }

        @Override
        public void put() throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build("PUT"));
        }

        @Override
        public void put(Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build("PUT", requestEntity));
        }

        @Override
        public <T> T put(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("PUT"));
        }

        @Override
        public <T> T put(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("PUT"));
        }

        @Override
        public <T> T put(Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("PUT", requestEntity));
        }

        @Override
        public <T> T put(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("PUT", requestEntity));
        }

        @Override
        public void post() throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build("POST"));
        }

        @Override
        public void post(Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build("POST", requestEntity));
        }

        @Override
        public <T> T post(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("POST"));
        }

        @Override
        public <T> T post(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("POST"));
        }

        @Override
        public <T> T post(Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("POST", requestEntity));
        }

        @Override
        public <T> T post(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("POST", requestEntity));
        }

        @Override
        public void delete() throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build("DELETE"));
        }

        @Override
        public void delete(Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build("DELETE", requestEntity));
        }

        @Override
        public <T> T delete(Class<T> c) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("DELETE"));
        }

        @Override
        public <T> T delete(GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("DELETE"));
        }

        @Override
        public <T> T delete(Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build("DELETE", requestEntity));
        }

        @Override
        public <T> T delete(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build("DELETE", requestEntity));
        }

        @Override
        public void method(String method) throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build(method));
        }

        @Override
        public void method(String method, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            WebResource.this.voidHandle(this.build(method, requestEntity));
        }

        @Override
        public <T> T method(String method, Class<T> c) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build(method));
        }

        @Override
        public <T> T method(String method, GenericType<T> gt) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build(method));
        }

        @Override
        public <T> T method(String method, Class<T> c, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(c, this.build(method, requestEntity));
        }

        @Override
        public <T> T method(String method, GenericType<T> gt, Object requestEntity) throws UniformInterfaceException, ClientHandlerException {
            return (T)WebResource.this.handle(gt, this.build(method, requestEntity));
        }
    }
}

