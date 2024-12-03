/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class ServerHttpRequestDecorator
implements ServerHttpRequest {
    private final ServerHttpRequest delegate;

    public ServerHttpRequestDecorator(ServerHttpRequest delegate) {
        Assert.notNull((Object)delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ServerHttpRequest getDelegate() {
        return this.delegate;
    }

    @Override
    @Nullable
    public HttpMethod getMethod() {
        return this.getDelegate().getMethod();
    }

    @Override
    public String getMethodValue() {
        return this.getDelegate().getMethodValue();
    }

    @Override
    public URI getURI() {
        return this.getDelegate().getURI();
    }

    @Override
    public RequestPath getPath() {
        return this.getDelegate().getPath();
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return this.getDelegate().getQueryParams();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.getDelegate().getHeaders();
    }

    @Override
    public MultiValueMap<String, HttpCookie> getCookies() {
        return this.getDelegate().getCookies();
    }

    @Override
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.getDelegate().getRemoteAddress();
    }

    @Override
    @Nullable
    public SslInfo getSslInfo() {
        return this.getDelegate().getSslInfo();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.getDelegate().getBody();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [delegate=" + this.getDelegate() + "]";
    }
}

