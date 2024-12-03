/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.server.reactive;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HttpHeadResponseDecorator
extends ServerHttpResponseDecorator {
    public HttpHeadResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return this.getDelegate().writeWith((Publisher<? extends DataBuffer>)Flux.from(body).reduce((Object)0, (current, buffer) -> {
            int next = current + buffer.readableByteCount();
            DataBufferUtils.release(buffer);
            return next;
        }).doOnNext(count -> this.getHeaders().setContentLength(count.intValue())).then(Mono.empty()));
    }

    @Override
    public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return this.setComplete();
    }
}

