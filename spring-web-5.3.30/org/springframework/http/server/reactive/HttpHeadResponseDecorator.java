/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferUtils
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
        if (this.shouldSetContentLength() && body instanceof Mono) {
            return ((Mono)body).doOnSuccess(buffer -> {
                if (buffer != null) {
                    this.getHeaders().setContentLength(buffer.readableByteCount());
                    DataBufferUtils.release((DataBuffer)buffer);
                } else {
                    this.getHeaders().setContentLength(0L);
                }
            }).then();
        }
        return Flux.from(body).doOnNext(DataBufferUtils::release).then();
    }

    private boolean shouldSetContentLength() {
        return this.getHeaders().getFirst("Content-Length") == null && this.getHeaders().getFirst("Transfer-Encoding") == null;
    }

    @Override
    public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return this.setComplete();
    }
}

