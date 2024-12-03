/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.core.codec;

import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

public interface Encoder<T> {
    public boolean canEncode(ResolvableType var1, @Nullable MimeType var2);

    public Flux<DataBuffer> encode(Publisher<? extends T> var1, DataBufferFactory var2, ResolvableType var3, @Nullable MimeType var4, @Nullable Map<String, Object> var5);

    @Nullable
    @Deprecated
    default public Long getContentLength(T t, @Nullable MimeType mimeType) {
        return null;
    }

    public List<MimeType> getEncodableMimeTypes();
}

