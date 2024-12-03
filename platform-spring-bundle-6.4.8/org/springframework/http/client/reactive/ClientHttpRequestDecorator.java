/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

public class ClientHttpRequestDecorator
implements ClientHttpRequest {
    private final ClientHttpRequest delegate;

    public ClientHttpRequestDecorator(ClientHttpRequest delegate) {
        Assert.notNull((Object)delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ClientHttpRequest getDelegate() {
        return this.delegate;
    }

    @Override
    public HttpMethod getMethod() {
        return this.delegate.getMethod();
    }

    @Override
    public URI getURI() {
        return this.delegate.getURI();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }

    @Override
    public MultiValueMap<String, HttpCookie> getCookies() {
        return this.delegate.getCookies();
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return this.delegate.bufferFactory();
    }

    @Override
    public <T> T getNativeRequest() {
        return this.delegate.getNativeRequest();
    }

    @Override
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        this.delegate.beforeCommit(action);
    }

    @Override
    public boolean isCommitted() {
        return this.delegate.isCommitted();
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body2) {
        return this.delegate.writeWith(body2);
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body2) {
        return this.delegate.writeAndFlushWith(body2);
    }

    @Override
    public Mono<Void> setComplete() {
        return this.delegate.setComplete();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [delegate=" + this.getDelegate() + "]";
    }
}

