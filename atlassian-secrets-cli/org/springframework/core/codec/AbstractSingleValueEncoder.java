/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

public abstract class AbstractSingleValueEncoder<T>
extends AbstractEncoder<T> {
    public AbstractSingleValueEncoder(MimeType ... supportedMimeTypes) {
        super(supportedMimeTypes);
    }

    @Override
    public final Flux<DataBuffer> encode(Publisher<? extends T> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).take(1L).concatMap(t -> this.encode(t, bufferFactory, elementType, mimeType, hints));
    }

    protected abstract Flux<DataBuffer> encode(T var1, DataBufferFactory var2, ResolvableType var3, @Nullable MimeType var4, @Nullable Map<String, Object> var5);
}

