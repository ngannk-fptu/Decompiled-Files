/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.StringDecoder
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DefaultDataBufferFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.MimeType
 *  org.springframework.util.MimeTypeUtils
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

public class Jackson2JsonDecoder
extends AbstractJackson2Decoder {
    private static final StringDecoder STRING_DECODER = StringDecoder.textPlainOnly(Arrays.asList(",", "\n"), (boolean)false);
    private static final ResolvableType STRING_TYPE = ResolvableType.forClass(String.class);

    public Jackson2JsonDecoder() {
        super((ObjectMapper)Jackson2ObjectMapperBuilder.json().build(), new MimeType[0]);
    }

    public Jackson2JsonDecoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
    }

    @Override
    protected Flux<DataBuffer> processInput(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux flux = Flux.from(input);
        if (mimeType == null) {
            return flux;
        }
        Charset charset = mimeType.getCharset();
        if (charset == null || StandardCharsets.UTF_8.equals(charset) || StandardCharsets.US_ASCII.equals(charset)) {
            return flux;
        }
        MimeType textMimeType = new MimeType(MimeTypeUtils.TEXT_PLAIN, charset);
        Flux decoded = STRING_DECODER.decode(input, STRING_TYPE, textMimeType, null);
        return decoded.map(s -> DefaultDataBufferFactory.sharedInstance.wrap(s.getBytes(StandardCharsets.UTF_8)));
    }
}

