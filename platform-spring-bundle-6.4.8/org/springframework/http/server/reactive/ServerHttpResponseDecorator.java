/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.server.reactive;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

public class ServerHttpResponseDecorator
implements ServerHttpResponse {
    private final ServerHttpResponse delegate;

    public ServerHttpResponseDecorator(ServerHttpResponse delegate) {
        Assert.notNull((Object)delegate, "Delegate is required");
        this.delegate = delegate;
    }

    public ServerHttpResponse getDelegate() {
        return this.delegate;
    }

    @Override
    public boolean setStatusCode(@Nullable HttpStatus status) {
        return this.getDelegate().setStatusCode(status);
    }

    @Override
    public HttpStatus getStatusCode() {
        return this.getDelegate().getStatusCode();
    }

    @Override
    public boolean setRawStatusCode(@Nullable Integer value) {
        return this.getDelegate().setRawStatusCode(value);
    }

    @Override
    public Integer getRawStatusCode() {
        return this.getDelegate().getRawStatusCode();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.getDelegate().getHeaders();
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return this.getDelegate().getCookies();
    }

    @Override
    public void addCookie(ResponseCookie cookie) {
        this.getDelegate().addCookie(cookie);
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return this.getDelegate().bufferFactory();
    }

    @Override
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        this.getDelegate().beforeCommit(action);
    }

    @Override
    public boolean isCommitted() {
        return this.getDelegate().isCommitted();
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body2) {
        return this.getDelegate().writeWith(body2);
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body2) {
        return this.getDelegate().writeAndFlushWith(body2);
    }

    @Override
    public Mono<Void> setComplete() {
        return this.getDelegate().setComplete();
    }

    public static <T> T getNativeResponse(ServerHttpResponse response) {
        if (response instanceof AbstractServerHttpResponse) {
            return ((AbstractServerHttpResponse)response).getNativeResponse();
        }
        if (response instanceof ServerHttpResponseDecorator) {
            return ServerHttpResponseDecorator.getNativeResponse(((ServerHttpResponseDecorator)response).getDelegate());
        }
        throw new IllegalArgumentException("Can't find native response in " + response.getClass().getName());
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [delegate=" + this.getDelegate() + "]";
    }
}

