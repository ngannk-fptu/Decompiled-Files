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
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EncoderHttpMessageWriter<T>
implements HttpMessageWriter<T> {
    private final Encoder<T> encoder;
    private final List<MediaType> mediaTypes;
    @Nullable
    private final MediaType defaultMediaType;

    public EncoderHttpMessageWriter(Encoder<T> encoder) {
        Assert.notNull(encoder, "Encoder is required");
        this.encoder = encoder;
        this.mediaTypes = MediaType.asMediaTypes(encoder.getEncodableMimeTypes());
        this.defaultMediaType = EncoderHttpMessageWriter.initDefaultMediaType(this.mediaTypes);
    }

    @Nullable
    private static MediaType initDefaultMediaType(List<MediaType> mediaTypes) {
        return mediaTypes.stream().filter(MimeType::isConcrete).findFirst().orElse(null);
    }

    public Encoder<T> getEncoder() {
        return this.encoder;
    }

    @Override
    public List<MediaType> getWritableMediaTypes() {
        return this.mediaTypes;
    }

    @Override
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return this.encoder.canEncode(elementType, mediaType);
    }

    @Override
    public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        MediaType contentType = this.updateContentType(message, mediaType);
        Flux<DataBuffer> body = this.encoder.encode(inputStream, message.bufferFactory(), elementType, contentType, hints);
        if (inputStream instanceof Mono) {
            HttpHeaders headers = message.getHeaders();
            return Mono.from(body).defaultIfEmpty((Object)message.bufferFactory().wrap(new byte[0])).flatMap(buffer -> {
                headers.setContentLength(buffer.readableByteCount());
                return message.writeWith((Publisher<? extends DataBuffer>)Mono.just((Object)buffer));
            });
        }
        return this.isStreamingMediaType(contentType) ? message.writeAndFlushWith((Publisher<? extends Publisher<? extends DataBuffer>>)body.map(Flux::just)) : message.writeWith((Publisher<? extends DataBuffer>)body);
    }

    @Nullable
    private MediaType updateContentType(ReactiveHttpOutputMessage message, @Nullable MediaType mediaType) {
        MediaType result = message.getHeaders().getContentType();
        if (result != null) {
            return result;
        }
        MediaType fallback = this.defaultMediaType;
        MediaType mediaType2 = result = EncoderHttpMessageWriter.useFallback(mediaType, fallback) ? fallback : mediaType;
        if (result != null) {
            result = EncoderHttpMessageWriter.addDefaultCharset(result, fallback);
            message.getHeaders().setContentType(result);
        }
        return result;
    }

    private static boolean useFallback(@Nullable MediaType main, @Nullable MediaType fallback) {
        return main == null || !main.isConcrete() || main.equals(MediaType.APPLICATION_OCTET_STREAM) && fallback != null;
    }

    private static MediaType addDefaultCharset(MediaType main, @Nullable MediaType defaultType) {
        if (main.getCharset() == null && defaultType != null && defaultType.getCharset() != null) {
            return new MediaType(main, defaultType.getCharset());
        }
        return main;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean isStreamingMediaType(@Nullable MediaType contentType) {
        if (contentType == null) return false;
        if (!(this.encoder instanceof HttpMessageEncoder)) return false;
        if (!((HttpMessageEncoder)this.encoder).getStreamingMediaTypes().stream().anyMatch(contentType::isCompatibleWith)) return false;
        return true;
    }

    @Override
    public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        HashMap<String, Object> allHints = new HashMap<String, Object>();
        allHints.putAll(this.getWriteHints(actualType, elementType, mediaType, request, response));
        allHints.putAll(hints);
        return this.write(inputStream, elementType, mediaType, response, allHints);
    }

    protected Map<String, Object> getWriteHints(ResolvableType streamType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        if (this.encoder instanceof HttpMessageEncoder) {
            HttpMessageEncoder httpEncoder = (HttpMessageEncoder)this.encoder;
            return httpEncoder.getEncodeHints(streamType, elementType, mediaType, request, response);
        }
        return Collections.emptyMap();
    }
}

