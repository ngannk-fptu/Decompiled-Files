/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Processor
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.server.reactive;

import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import reactor.core.publisher.Mono;

public abstract class AbstractListenerServerHttpResponse
extends AbstractServerHttpResponse {
    private final AtomicBoolean writeCalled = new AtomicBoolean();

    public AbstractListenerServerHttpResponse(DataBufferFactory bufferFactory) {
        super(bufferFactory);
    }

    public AbstractListenerServerHttpResponse(DataBufferFactory bufferFactory, HttpHeaders headers) {
        super(bufferFactory, headers);
    }

    @Override
    protected final Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body2) {
        return this.writeAndFlushWithInternal((Publisher<? extends Publisher<? extends DataBuffer>>)Mono.just(body2));
    }

    @Override
    protected final Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body2) {
        if (!this.writeCalled.compareAndSet(false, true)) {
            return Mono.error((Throwable)new IllegalStateException("writeWith() or writeAndFlushWith() has already been called"));
        }
        Processor<? super Publisher<? extends DataBuffer>, Void> processor = this.createBodyFlushProcessor();
        return Mono.from(subscriber -> {
            body2.subscribe((Subscriber)processor);
            processor.subscribe(subscriber);
        });
    }

    protected abstract Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor();
}

