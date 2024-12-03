/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HttpMessageReader<T> {
    public List<MediaType> getReadableMediaTypes();

    default public List<MediaType> getReadableMediaTypes(ResolvableType elementType) {
        return this.canRead(elementType, null) ? this.getReadableMediaTypes() : Collections.emptyList();
    }

    public boolean canRead(ResolvableType var1, @Nullable MediaType var2);

    public Flux<T> read(ResolvableType var1, ReactiveHttpInputMessage var2, Map<String, Object> var3);

    public Mono<T> readMono(ResolvableType var1, ReactiveHttpInputMessage var2, Map<String, Object> var3);

    default public Flux<T> read(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return this.read(elementType, request, hints);
    }

    default public Mono<T> readMono(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return this.readMono(elementType, request, hints);
    }
}

