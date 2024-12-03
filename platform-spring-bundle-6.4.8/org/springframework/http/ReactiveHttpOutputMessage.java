/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Mono
 */
package org.springframework.http;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpMessage;
import reactor.core.publisher.Mono;

public interface ReactiveHttpOutputMessage
extends HttpMessage {
    public DataBufferFactory bufferFactory();

    public void beforeCommit(Supplier<? extends Mono<Void>> var1);

    public boolean isCommitted();

    public Mono<Void> writeWith(Publisher<? extends DataBuffer> var1);

    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> var1);

    public Mono<Void> setComplete();
}

