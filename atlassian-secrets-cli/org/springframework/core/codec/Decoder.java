/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.core.codec;

import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Decoder<T> {
    public boolean canDecode(ResolvableType var1, @Nullable MimeType var2);

    public Flux<T> decode(Publisher<DataBuffer> var1, ResolvableType var2, @Nullable MimeType var3, @Nullable Map<String, Object> var4);

    public Mono<T> decodeToMono(Publisher<DataBuffer> var1, ResolvableType var2, @Nullable MimeType var3, @Nullable Map<String, Object> var4);

    public List<MimeType> getDecodableMimeTypes();
}

