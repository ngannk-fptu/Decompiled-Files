/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

public class CharSequenceEncoder
extends AbstractEncoder<CharSequence> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private CharSequenceEncoder(MimeType ... mimeTypes) {
        super(mimeTypes);
    }

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.resolve(Object.class);
        return super.canEncode(elementType, mimeType) && CharSequence.class.isAssignableFrom(clazz);
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<? extends CharSequence> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Charset charset = this.getCharset(mimeType);
        return Flux.from(inputStream).map(charSequence -> {
            CharBuffer charBuffer = CharBuffer.wrap(charSequence);
            ByteBuffer byteBuffer = charset.encode(charBuffer);
            return bufferFactory.wrap(byteBuffer);
        });
    }

    private Charset getCharset(@Nullable MimeType mimeType) {
        Charset charset = mimeType != null && mimeType.getCharset() != null ? mimeType.getCharset() : DEFAULT_CHARSET;
        return charset;
    }

    public static CharSequenceEncoder textPlainOnly() {
        return new CharSequenceEncoder(new MimeType("text", "plain", DEFAULT_CHARSET));
    }

    public static CharSequenceEncoder allMimeTypes() {
        return new CharSequenceEncoder(new MimeType("text", "plain", DEFAULT_CHARSET), MimeTypeUtils.ALL);
    }
}

