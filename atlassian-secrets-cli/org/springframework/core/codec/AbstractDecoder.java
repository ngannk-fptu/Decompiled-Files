/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.core.codec;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;

public abstract class AbstractDecoder<T>
implements Decoder<T> {
    private final List<MimeType> decodableMimeTypes;

    protected AbstractDecoder(MimeType ... supportedMimeTypes) {
        this.decodableMimeTypes = Arrays.asList(supportedMimeTypes);
    }

    @Override
    public List<MimeType> getDecodableMimeTypes() {
        return this.decodableMimeTypes;
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        return this.decodableMimeTypes.stream().anyMatch(candidate -> candidate.isCompatibleWith(mimeType));
    }

    @Override
    public Mono<T> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        throw new UnsupportedOperationException();
    }
}

