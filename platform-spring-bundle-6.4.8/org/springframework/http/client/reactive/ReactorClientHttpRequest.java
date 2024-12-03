/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.handler.codec.http.cookie.DefaultCookie
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.netty.NettyOutbound
 *  reactor.netty.http.client.HttpClientRequest
 */
package org.springframework.http.client.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.client.reactive.AbstractClientHttpRequest;
import org.springframework.http.client.reactive.NettyHeadersAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClientRequest;

class ReactorClientHttpRequest
extends AbstractClientHttpRequest
implements ZeroCopyHttpOutputMessage {
    private final HttpMethod httpMethod;
    private final URI uri;
    private final HttpClientRequest request;
    private final NettyOutbound outbound;
    private final NettyDataBufferFactory bufferFactory;

    public ReactorClientHttpRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound outbound) {
        this.httpMethod = method;
        this.uri = uri;
        this.request = request;
        this.outbound = outbound;
        this.bufferFactory = new NettyDataBufferFactory(outbound.alloc());
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
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    @Override
    public <T> T getNativeRequest() {
        return (T)this.request;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body2) {
        return this.doCommit(() -> {
            if (body2 instanceof Mono) {
                Mono byteBufMono = Mono.from((Publisher)body2).map(NettyDataBufferFactory::toByteBuf);
                return this.outbound.send((Publisher)byteBufMono).then();
            }
            Flux byteBufFlux = Flux.from((Publisher)body2).map(NettyDataBufferFactory::toByteBuf);
            return this.outbound.send((Publisher)byteBufFlux).then();
        });
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body2) {
        Flux byteBufs = Flux.from(body2).map(ReactorClientHttpRequest::toByteBufs);
        return this.doCommit(() -> this.lambda$writeAndFlushWith$1((Publisher)byteBufs));
    }

    private static Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }

    @Override
    public Mono<Void> writeWith(Path file, long position, long count) {
        return this.doCommit(() -> this.outbound.sendFile(file, position, count).then());
    }

    @Override
    public Mono<Void> setComplete() {
        return this.doCommit(() -> ((NettyOutbound)this.outbound).then());
    }

    @Override
    protected void applyHeaders() {
        this.getHeaders().forEach((key, value) -> this.request.requestHeaders().set(key, (Iterable)value));
    }

    @Override
    protected void applyCookies() {
        this.getCookies().values().stream().flatMap(Collection::stream).map(cookie -> new DefaultCookie(cookie.getName(), cookie.getValue())).forEach(arg_0 -> ((HttpClientRequest)this.request).addCookie(arg_0));
    }

    @Override
    protected HttpHeaders initReadOnlyHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(new NettyHeadersAdapter(this.request.requestHeaders()));
    }

    private /* synthetic */ Publisher lambda$writeAndFlushWith$1(Publisher byteBufs) {
        return this.outbound.sendGroups(byteBufs).then();
    }
}

