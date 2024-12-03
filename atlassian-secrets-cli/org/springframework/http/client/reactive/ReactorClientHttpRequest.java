/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.ipc.netty.http.client.HttpClientRequest
 */
package org.springframework.http.client.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.client.reactive.AbstractClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

class ReactorClientHttpRequest
extends AbstractClientHttpRequest
implements ZeroCopyHttpOutputMessage {
    private final HttpMethod httpMethod;
    private final URI uri;
    private final HttpClientRequest httpRequest;

    public ReactorClientHttpRequest(HttpMethod httpMethod, URI uri, HttpClientRequest httpRequest) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpRequest = httpRequest.failOnClientError(false).failOnServerError(false);
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return ReactorClientHttpConnector.BUFFER_FACTORY;
    }

    @Override
    public HttpMethod getMethod() {
        return this.httpMethod;
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return this.doCommit(() -> this.httpRequest.send((Publisher)Flux.from((Publisher)body).map(NettyDataBufferFactory::toByteBuf)).then());
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        Flux byteBufs = Flux.from(body).map(ReactorClientHttpRequest::toByteBufs);
        return this.doCommit(() -> this.lambda$writeAndFlushWith$1((Publisher)byteBufs));
    }

    private static Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }

    @Override
    public Mono<Void> writeWith(File file, long position, long count) {
        return this.doCommit(() -> this.httpRequest.sendFile(file.toPath(), position, count).then());
    }

    @Override
    public Mono<Void> setComplete() {
        return this.doCommit(() -> this.httpRequest.sendHeaders().then());
    }

    @Override
    protected void applyHeaders() {
        this.getHeaders().entrySet().forEach(e -> this.httpRequest.requestHeaders().set((String)e.getKey(), (Iterable)e.getValue()));
    }

    @Override
    protected void applyCookies() {
        this.getCookies().values().stream().flatMap(Collection::stream).map(cookie -> new DefaultCookie(cookie.getName(), cookie.getValue())).forEach(arg_0 -> ((HttpClientRequest)this.httpRequest).addCookie(arg_0));
    }

    private /* synthetic */ Publisher lambda$writeAndFlushWith$1(Publisher byteBufs) {
        return this.httpRequest.sendGroups(byteBufs).then();
    }
}

