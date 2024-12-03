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

import com.sun.jersey.api.client.AsyncViewUniformInterface;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.PartialRequestBuilder;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.async.AsyncClientHandler;
import com.sun.jersey.api.client.async.FutureListener;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.impl.ClientRequestImpl;
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

public class AsyncViewResource
extends Filterable
implements RequestBuilder<Builder>,
AsyncViewUniformInterface,
AsyncClientHandler {
    private static final Logger LOGGER = Logger.getLogger(AsyncWebResource.class.getName());
    private final Client client;
    private final ExecutorService executorService;
    private final URI u;

    protected AsyncViewResource(Client c, URI u) {
        super(c);
        this.client = c;
        this.executorService = c.getExecutorService();
        this.u = u;
    }

    protected AsyncViewResource(AsyncViewResource that, UriBuilder ub) {
        super(that);
        this.client = that.client;
        this.executorService = that.executorService;
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
        if (obj instanceof AsyncViewResource) {
            AsyncViewResource that = (AsyncViewResource)obj;
            return that.u.equals(this.u);
        }
        return false;
    }

    @Override
    public <T> Future<T> head(Class<T> c) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "HEAD"));
    }

    @Override
    public <T> Future<T> head(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "HEAD"));
    }

    @Override
    public <T> Future<T> options(Class<T> c) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> Future<T> options(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }

    @Override
    public <T> Future<T> get(Class<T> c) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public <T> Future<T> get(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }

    @Override
    public <T> Future<T> put(Class<T> c) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> Future<T> put(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }

    @Override
    public <T> Future<T> put(Class<T> c, Object requestEntity) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> Future<T> put(T t, Object requestEntity) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }

    @Override
    public <T> Future<T> post(Class<T> c) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> Future<T> post(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }

    @Override
    public <T> Future<T> post(Class<T> c, Object requestEntity) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> Future<T> post(T t, Object requestEntity) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }

    @Override
    public <T> Future<T> delete(Class<T> c) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> Future<T> delete(T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }

    @Override
    public <T> Future<T> delete(Class<T> c, Object requestEntity) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> Future<T> delete(T t, Object requestEntity) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }

    @Override
    public <T> Future<T> method(String method, Class<T> c) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> Future<T> method(String method, T t) {
        return this.handle(t, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }

    @Override
    public <T> Future<T> method(String method, Class<T> c, Object requestEntity) {
        return this.handle((T)c, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
    }

    @Override
    public <T> Future<T> method(String method, T t, Object requestEntity) {
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

    public AsyncViewResource path(String path) {
        return new AsyncViewResource(this, this.getUriBuilder().path(path));
    }

    public AsyncViewResource uri(URI uri) {
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
        return new AsyncViewResource(this, b);
    }

    public AsyncViewResource queryParam(String key, String value) {
        UriBuilder b = this.getUriBuilder();
        b.queryParam(key, new Object[]{value});
        return new AsyncViewResource(this, b);
    }

    public AsyncViewResource queryParams(MultivaluedMap<String, String> params) {
        UriBuilder b = this.getUriBuilder();
        for (Map.Entry e : params.entrySet()) {
            for (String value : (List)e.getValue()) {
                b.queryParam((String)e.getKey(), new Object[]{value});
            }
        }
        return new AsyncViewResource(this, b);
    }

    private <T> Future<T> handle(Class<T> c, ClientRequest ro) {
        return this.client.getViewProxy(c).asyncView(c, ro, (AsyncClientHandler)this);
    }

    private <T> Future<T> handle(T t, ClientRequest ro) {
        return this.client.getViewProxy(t.getClass()).asyncView(t, ro, (AsyncClientHandler)this);
    }

    @Override
    public Future<ClientResponse> handle(final ClientRequest request, final FutureListener<ClientResponse> l) {
        Callable c = new Callable(){

            public Object call() throws Exception {
                return AsyncViewResource.this.getHeadHandler().handle(request);
            }
        };
        FutureTask<ClientResponse> ft = new FutureTask<ClientResponse>(c){

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
    implements AsyncViewUniformInterface {
        private Builder() {
        }

        private ClientRequest build(String method) {
            ClientRequestImpl ro = new ClientRequestImpl(AsyncViewResource.this.u, method, this.entity, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        private ClientRequest build(String method, Object e) {
            ClientRequestImpl ro = new ClientRequestImpl(AsyncViewResource.this.u, method, e, (MultivaluedMap<String, Object>)this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }

        @Override
        public <T> Future<T> head(Class<T> c) {
            return AsyncViewResource.this.handle((Object)c, this.build("HEAD"));
        }

        @Override
        public <T> Future<T> head(T t) {
            return AsyncViewResource.this.handle(t, this.build("HEAD"));
        }

        @Override
        public <T> Future<T> options(Class<T> c) {
            return AsyncViewResource.this.handle((Object)c, this.build("OPTIONS"));
        }

        @Override
        public <T> Future<T> options(T t) {
            return AsyncViewResource.this.handle(t, this.build("OPTIONS"));
        }

        @Override
        public <T> Future<T> get(Class<T> c) {
            return AsyncViewResource.this.handle((Object)c, this.build("GET"));
        }

        @Override
        public <T> Future<T> get(T t) {
            return AsyncViewResource.this.handle(t, this.build("GET"));
        }

        @Override
        public <T> Future<T> put(Class<T> c) {
            return AsyncViewResource.this.handle((Object)c, this.build("PUT"));
        }

        @Override
        public <T> Future<T> put(T t) {
            return AsyncViewResource.this.handle(t, this.build("PUT"));
        }

        @Override
        public <T> Future<T> put(Class<T> c, Object requestEntity) {
            return AsyncViewResource.this.handle((Object)c, this.build("PUT", requestEntity));
        }

        @Override
        public <T> Future<T> put(T t, Object requestEntity) {
            return AsyncViewResource.this.handle(t, this.build("PUT", requestEntity));
        }

        @Override
        public <T> Future<T> post(Class<T> c) {
            return AsyncViewResource.this.handle((Object)c, this.build("POST"));
        }

        @Override
        public <T> Future<T> post(T t) {
            return AsyncViewResource.this.handle(t, this.build("POST"));
        }

        @Override
        public <T> Future<T> post(Class<T> c, Object requestEntity) {
            return AsyncViewResource.this.handle((Object)c, this.build("POST", requestEntity));
        }

        @Override
        public <T> Future<T> post(T t, Object requestEntity) {
            return AsyncViewResource.this.handle(t, this.build("POST", requestEntity));
        }

        @Override
        public <T> Future<T> delete(Class<T> c) {
            return AsyncViewResource.this.handle((Object)c, this.build("DELETE"));
        }

        @Override
        public <T> Future<T> delete(T t) {
            return AsyncViewResource.this.handle(t, this.build("DELETE"));
        }

        @Override
        public <T> Future<T> delete(Class<T> c, Object requestEntity) {
            return AsyncViewResource.this.handle((Object)c, this.build("DELETE", requestEntity));
        }

        @Override
        public <T> Future<T> delete(T t, Object requestEntity) {
            return AsyncViewResource.this.handle(t, this.build("DELETE", requestEntity));
        }

        @Override
        public <T> Future<T> method(String method, Class<T> c) {
            return AsyncViewResource.this.handle((Object)c, this.build(method));
        }

        @Override
        public <T> Future<T> method(String method, T t) {
            return AsyncViewResource.this.handle(t, this.build(method));
        }

        @Override
        public <T> Future<T> method(String method, Class<T> c, Object requestEntity) {
            return AsyncViewResource.this.handle((Object)c, this.build(method, requestEntity));
        }

        @Override
        public <T> Future<T> method(String method, T t, Object requestEntity) {
            return AsyncViewResource.this.handle(t, this.build(method, requestEntity));
        }
    }
}

