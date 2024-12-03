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

import com.sun.jersey.api.client.AsyncUniformInterface;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.PartialRequestBuilder;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.async.AsyncClientHandler;
import com.sun.jersey.api.client.async.FutureListener;
import com.sun.jersey.api.client.async.ITypeListener;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.impl.ClientRequestImpl;
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import com.sun.jersey.client.impl.async.FutureClientResponseListener;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

public class AsyncWebResource
extends Filterable
implements AsyncClientHandler,
RequestBuilder<Builder>,
AsyncUniformInterface {
    private static final Logger LOGGER = Logger.getLogger(AsyncWebResource.class.getName());
    private final ExecutorService executorService;
    private final URI u;
    private CopyOnWriteHashMap<String, Object> properties;

    protected AsyncWebResource(Client c, CopyOnWriteHashMap<String, Object> properties, URI u) {
        super(c);
        this.executorService = c.getExecutorService();
        this.u = u;
        this.properties = properties.clone();
    }

    protected AsyncWebResource(AsyncWebResource that, UriBuilder ub) {
        super(that);
        this.executorService = that.executorService;
        this.u = ub.build(new Object[0]);
        this.properties = that.properties.clone();
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
        if (obj instanceof AsyncWebResource) {
            AsyncWebResource that = (AsyncWebResource)obj;
            return that.u.equals(this.u);
        }
        return false;
    }

    @Override
    public Future<ClientResponse> head() {
        return this.handle(ClientResponse.class, (ClientRequest)new ClientRequestImpl(this.getURI(), "HEAD"));
    }

    @Override
    public Future<ClientResponse> head(ITypeListener<ClientResponse> l) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "HEAD"));
    }

    @Override
    public <T> Future<T> options(Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> Future<T> options(GenericType<T> gt) {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> Future<T> options(ITypeListener<T> l) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> Future<T> get(Class<T> c) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public <T> Future<T> get(GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public <T> Future<T> get(ITypeListener<T> l) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public Future<?> put() throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", null));
    }

    @Override
    public Future<?> put(Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> Future<T> put(Class<T> c) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> Future<T> put(GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> Future<T> put(ITypeListener<T> l) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> Future<T> put(Class<T> c, Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> Future<T> put(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> Future<T> put(ITypeListener<T> l, Object requestEntity) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public Future<?> post() throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public Future<?> post(Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> Future<T> post(Class<T> c) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> Future<T> post(GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> Future<T> post(ITypeListener<T> l) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> Future<T> post(Class<T> c, Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> Future<T> post(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> Future<T> post(ITypeListener<T> l, Object requestEntity) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public Future<?> delete() throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public Future<?> delete(Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> Future<T> delete(Class<T> c) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> Future<T> delete(GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> Future<T> delete(ITypeListener<T> l) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> Future<T> delete(Class<T> c, Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> Future<T> delete(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> Future<T> delete(ITypeListener<T> l, Object requestEntity) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public Future<?> method(String method) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public Future<?> method(String method, Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public <T> Future<T> method(String method, Class<T> c) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> Future<T> method(String method, GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> Future<T> method(String method, ITypeListener<T> l) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> Future<T> method(String method, Class<T> c, Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public <T> Future<T> method(String method, GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public <T> Future<T> method(String method, ITypeListener<T> l, Object requestEntity) {
        return this.handle(l, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
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

    public AsyncWebResource path(String path) {
        return new AsyncWebResource(this, this.getUriBuilder().path(path));
    }

    public AsyncWebResource uri(URI uri) {
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
        return new AsyncWebResource(this, b);
    }

    public AsyncWebResource queryParam(String key, String value) {
        UriBuilder b = this.getUriBuilder();
        b.queryParam(key, new Object[]{value});
        return new AsyncWebResource(this, b);
    }

    public AsyncWebResource queryParams(MultivaluedMap<String, String> params) {
        UriBuilder b = this.getUriBuilder();
        for (Map.Entry e : params.entrySet()) {
            for (String value : (List)e.getValue()) {
                b.queryParam((String)e.getKey(), new Object[]{value});
            }
        }
        return new AsyncWebResource(this, b);
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

    private <T> Future<T> handle(final Class<T> c, final ClientRequest request) {
        this.setProperties(request);
        FutureClientResponseListener ftw = new FutureClientResponseListener<T>(){

            @Override
            protected T get(ClientResponse response) {
                if (c == ClientResponse.class) {
                    return c.cast(response);
                }
                if (response.getStatus() < 300) {
                    return response.getEntity(c);
                }
                throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }

    private <T> Future<T> handle(final GenericType<T> gt, final ClientRequest request) {
        this.setProperties(request);
        FutureClientResponseListener ftw = new FutureClientResponseListener<T>(){

            @Override
            protected T get(ClientResponse response) {
                if (gt.getRawClass() == ClientResponse.class) {
                    return gt.getRawClass().cast(response);
                }
                if (response.getStatus() < 300) {
                    return response.getEntity(gt);
                }
                throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }

    private <T> Future<T> handle(final ITypeListener<T> l, final ClientRequest request) {
        this.setProperties(request);
        FutureClientResponseListener ftw = new FutureClientResponseListener<T>(){

            @Override
            protected void done() {
                try {
                    l.onComplete(this);
                }
                catch (Throwable t) {
                    LOGGER.log(Level.SEVERE, "Throwable caught on call to ITypeListener.onComplete", t);
                }
            }

            @Override
            protected T get(ClientResponse response) {
                if (l.getType() == ClientResponse.class) {
                    return response;
                }
                if (response.getStatus() < 300) {
                    if (l.getGenericType() == null) {
                        return response.getEntity(l.getType());
                    }
                    return response.getEntity(l.getGenericType());
                }
                throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }

    private Future<?> voidHandle(final ClientRequest request) {
        this.setProperties(request);
        FutureClientResponseListener ftw = new FutureClientResponseListener(){

            protected Object get(ClientResponse response) {
                if (response.getStatus() >= 300) {
                    throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
                }
                response.close();
                return null;
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }

    @Override
    public Future<ClientResponse> handle(final ClientRequest request, final FutureListener<ClientResponse> l) {
        this.setProperties(request);
        Callable<ClientResponse> c = new Callable<ClientResponse>(){

            @Override
            public ClientResponse call() throws Exception {
                return AsyncWebResource.this.getHeadHandler().handle(request);
            }
        };
        FutureTask<ClientResponse> ft = new FutureTask<ClientResponse>((Callable)c){

            @Override
            protected void done() {
                try {
                    l.onComplete(this);
                }
                catch (Throwable t) {
                    LOGGER.log(Level.SEVERE, "Throwable caught on call to ClientResponseListener.onComplete", t);
                }
            }
        };
        this.executorService.submit(ft);
        return ft;
    }

    public class Builder
    extends PartialRequestBuilder<Builder>
    implements AsyncUniformInterface {
        private Builder() {
        }

        private ClientRequest build(String method) {
            ClientRequestImpl ro = new ClientRequestImpl(AsyncWebResource.this.u, method, this.entity, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        private ClientRequest build(String method, Object e) {
            ClientRequestImpl ro = new ClientRequestImpl(AsyncWebResource.this.u, method, e, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        @Override
        public Future<ClientResponse> head() {
            return AsyncWebResource.this.handle(ClientResponse.class, this.build("HEAD"));
        }

        @Override
        public Future<ClientResponse> head(ITypeListener<ClientResponse> l) {
            return AsyncWebResource.this.handle(l, this.build("HEAD"));
        }

        @Override
        public <T> Future<T> options(Class<T> c) {
            return AsyncWebResource.this.handle(c, this.build("OPTIONS"));
        }

        @Override
        public <T> Future<T> options(GenericType<T> gt) {
            return AsyncWebResource.this.handle(gt, this.build("OPTIONS"));
        }

        @Override
        public <T> Future<T> options(ITypeListener<T> l) {
            return AsyncWebResource.this.handle(l, this.build("OPTIONS"));
        }

        @Override
        public <T> Future<T> get(Class<T> c) {
            return AsyncWebResource.this.handle(c, this.build("GET"));
        }

        @Override
        public <T> Future<T> get(GenericType<T> gt) {
            return AsyncWebResource.this.handle(gt, this.build("GET"));
        }

        @Override
        public <T> Future<T> get(ITypeListener<T> l) {
            return AsyncWebResource.this.handle(l, this.build("GET"));
        }

        @Override
        public Future<?> put() throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("PUT"));
        }

        @Override
        public Future<?> put(Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("PUT", requestEntity));
        }

        @Override
        public <T> Future<T> put(Class<T> c) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build("PUT"));
        }

        @Override
        public <T> Future<T> put(GenericType<T> gt) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build("PUT"));
        }

        @Override
        public <T> Future<T> put(ITypeListener<T> l) {
            return AsyncWebResource.this.handle(l, this.build("PUT"));
        }

        @Override
        public <T> Future<T> put(Class<T> c, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build("PUT", requestEntity));
        }

        @Override
        public <T> Future<T> put(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build("PUT", requestEntity));
        }

        @Override
        public <T> Future<T> put(ITypeListener<T> l, Object requestEntity) {
            return AsyncWebResource.this.handle(l, this.build("PUT", requestEntity));
        }

        @Override
        public Future<?> post() throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("POST"));
        }

        @Override
        public Future<?> post(Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("POST", requestEntity));
        }

        @Override
        public <T> Future<T> post(Class<T> c) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build("POST"));
        }

        @Override
        public <T> Future<T> post(GenericType<T> gt) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build("POST"));
        }

        @Override
        public <T> Future<T> post(ITypeListener<T> l) {
            return AsyncWebResource.this.handle(l, this.build("POST"));
        }

        @Override
        public <T> Future<T> post(Class<T> c, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build("POST", requestEntity));
        }

        @Override
        public <T> Future<T> post(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build("POST", requestEntity));
        }

        @Override
        public <T> Future<T> post(ITypeListener<T> l, Object requestEntity) {
            return AsyncWebResource.this.handle(l, this.build("POST", requestEntity));
        }

        @Override
        public Future<?> delete() throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("DELETE"));
        }

        @Override
        public Future<?> delete(Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("DELETE", requestEntity));
        }

        @Override
        public <T> Future<T> delete(Class<T> c) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build("DELETE"));
        }

        @Override
        public <T> Future<T> delete(GenericType<T> gt) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build("DELETE"));
        }

        @Override
        public <T> Future<T> delete(ITypeListener<T> l) {
            return AsyncWebResource.this.handle(l, this.build("DELETE"));
        }

        @Override
        public <T> Future<T> delete(Class<T> c, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build("DELETE", requestEntity));
        }

        @Override
        public <T> Future<T> delete(GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build("DELETE", requestEntity));
        }

        @Override
        public <T> Future<T> delete(ITypeListener<T> l, Object requestEntity) {
            return AsyncWebResource.this.handle(l, this.build("DELETE", requestEntity));
        }

        @Override
        public Future<?> method(String method) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build(method));
        }

        @Override
        public Future<?> method(String method, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build(method, requestEntity));
        }

        @Override
        public <T> Future<T> method(String method, Class<T> c) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build(method));
        }

        @Override
        public <T> Future<T> method(String method, GenericType<T> gt) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build(method));
        }

        @Override
        public <T> Future<T> method(String method, ITypeListener<T> l) {
            return AsyncWebResource.this.handle(l, this.build(method));
        }

        @Override
        public <T> Future<T> method(String method, Class<T> c, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(c, this.build(method, requestEntity));
        }

        @Override
        public <T> Future<T> method(String method, GenericType<T> gt, Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.handle(gt, this.build(method, requestEntity));
        }

        @Override
        public <T> Future<T> method(String method, ITypeListener<T> l, Object requestEntity) {
            return AsyncWebResource.this.handle(l, this.build(method, requestEntity));
        }
    }
}

