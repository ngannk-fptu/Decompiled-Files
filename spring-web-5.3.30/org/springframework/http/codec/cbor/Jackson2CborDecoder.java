/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.dataformat.cbor.CBORFactory
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.util.Assert
 *  org.springframework.util.MimeType
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.codec.cbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

public class Jackson2CborDecoder
extends AbstractJackson2Decoder {
    public Jackson2CborDecoder() {
        this((ObjectMapper)Jackson2ObjectMapperBuilder.cbor().build(), MediaType.APPLICATION_CBOR);
    }

    public Jackson2CborDecoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
        Assert.isAssignable(CBORFactory.class, mapper.getFactory().getClass());
    }

    @Override
    public Flux<Object> decode(Publisher<DataBuffer> input, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        throw new UnsupportedOperationException("Does not support stream decoding yet");
    }
}

