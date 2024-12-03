/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

public class ByteBufferDecoder
extends AbstractDataBufferDecoder<ByteBuffer> {
    public ByteBufferDecoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.getRawClass();
        return super.canDecode(elementType, mimeType) && clazz != null && ByteBuffer.class.isAssignableFrom(clazz);
    }

    @Override
    protected ByteBuffer decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        ByteBuffer copy = ByteBuffer.allocate(dataBuffer.readableByteCount());
        copy.put(dataBuffer.asByteBuffer());
        copy.flip();
        DataBufferUtils.release(dataBuffer);
        return copy;
    }
}

