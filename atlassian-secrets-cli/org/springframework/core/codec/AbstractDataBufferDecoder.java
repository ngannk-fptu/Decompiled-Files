/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.core.codec;

import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractDataBufferDecoder<T>
extends AbstractDecoder<T> {
    protected AbstractDataBufferDecoder(MimeType ... supportedMimeTypes) {
        super(supportedMimeTypes);
    }

    @Override
    public Flux<T> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(buffer -> this.decodeDataBuffer((DataBuffer)buffer, elementType, mimeType, hints));
    }

    @Override
    public Mono<T> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(inputStream).map(buffer -> this.decodeDataBuffer((DataBuffer)buffer, elementType, mimeType, hints));
    }

    protected abstract T decodeDataBuffer(DataBuffer var1, ResolvableType var2, @Nullable MimeType var3, @Nullable Map<String, Object> var4);
}

