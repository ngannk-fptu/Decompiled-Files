/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.core.codec;

import java.io.ByteArrayInputStream;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

public class ResourceDecoder
extends AbstractDataBufferDecoder<Resource> {
    public ResourceDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.getRawClass();
        return clazz != null && Resource.class.isAssignableFrom(clazz) && super.canDecode(elementType, mimeType);
    }

    @Override
    public Flux<Resource> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(this.decodeToMono(inputStream, elementType, mimeType, hints));
    }

    @Override
    protected Resource decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        Class<?> clazz = elementType.getRawClass();
        Assert.state(clazz != null, "No resource class");
        if (InputStreamResource.class == clazz) {
            return new InputStreamResource(new ByteArrayInputStream(bytes));
        }
        if (Resource.class.isAssignableFrom(clazz)) {
            return new ByteArrayResource(bytes);
        }
        throw new IllegalStateException("Unsupported resource class: " + clazz);
    }
}

