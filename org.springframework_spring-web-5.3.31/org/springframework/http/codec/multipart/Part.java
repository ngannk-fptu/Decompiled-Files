/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.buffer.DataBuffer
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Part {
    public String name();

    public HttpHeaders headers();

    public Flux<DataBuffer> content();

    default public Mono<Void> delete() {
        return Mono.empty();
    }
}

