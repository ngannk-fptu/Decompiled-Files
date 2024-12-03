/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageDecoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DecoderHttpMessageReader<T>
implements HttpMessageReader<T> {
    private final Decoder<T> decoder;
    private final List<MediaType> mediaTypes;

    public DecoderHttpMessageReader(Decoder<T> decoder) {
        Assert.notNull(decoder, "Decoder is required");
        this.decoder = decoder;
        this.mediaTypes = MediaType.asMediaTypes(decoder.getDecodableMimeTypes());
    }

    public Decoder<T> getDecoder() {
        return this.decoder;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return this.mediaTypes;
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return this.decoder.canDecode(elementType, mediaType);
    }

    @Override
    public Flux<T> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        MediaType contentType = this.getContentType(message);
        return this.decoder.decode((Publisher<DataBuffer>)message.getBody(), elementType, contentType, hints);
    }

    @Override
    public Mono<T> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        MediaType contentType = this.getContentType(message);
        return this.decoder.decodeToMono((Publisher<DataBuffer>)message.getBody(), elementType, contentType, hints);
    }

    private MediaType getContentType(HttpMessage inputMessage) {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        return contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM;
    }

    @Override
    public Flux<T> read(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        HashMap<String, Object> allHints = new HashMap<String, Object>(4);
        allHints.putAll(this.getReadHints(actualType, elementType, request, response));
        allHints.putAll(hints);
        return this.read(elementType, request, allHints);
    }

    @Override
    public Mono<T> readMono(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        HashMap<String, Object> allHints = new HashMap<String, Object>(4);
        allHints.putAll(this.getReadHints(actualType, elementType, request, response));
        allHints.putAll(hints);
        return this.readMono(elementType, request, allHints);
    }

    protected Map<String, Object> getReadHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
        if (this.decoder instanceof HttpMessageDecoder) {
            HttpMessageDecoder httpDecoder = (HttpMessageDecoder)this.decoder;
            return httpDecoder.getDecodeHints(actualType, elementType, request, response);
        }
        return Collections.emptyMap();
    }
}

