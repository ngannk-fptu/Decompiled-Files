/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

public interface HttpMessageWriter<T> {
    public List<MediaType> getWritableMediaTypes();

    default public List<MediaType> getWritableMediaTypes(ResolvableType elementType) {
        return this.canWrite(elementType, null) ? this.getWritableMediaTypes() : Collections.emptyList();
    }

    public boolean canWrite(ResolvableType var1, @Nullable MediaType var2);

    public Mono<Void> write(Publisher<? extends T> var1, ResolvableType var2, @Nullable MediaType var3, ReactiveHttpOutputMessage var4, Map<String, Object> var5);

    default public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return this.write(inputStream, elementType, mediaType, response, hints);
    }
}

