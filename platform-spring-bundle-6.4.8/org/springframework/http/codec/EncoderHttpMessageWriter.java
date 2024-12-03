/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpLogging;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EncoderHttpMessageWriter<T>
implements HttpMessageWriter<T> {
    private static final Log logger = HttpLogging.forLogName(EncoderHttpMessageWriter.class);
    private final Encoder<T> encoder;
    private final List<MediaType> mediaTypes;
    @Nullable
    private final MediaType defaultMediaType;

    public EncoderHttpMessageWriter(Encoder<T> encoder) {
        Assert.notNull(encoder, "Encoder is required");
        EncoderHttpMessageWriter.initLogger(encoder);
        this.encoder = encoder;
        this.mediaTypes = MediaType.asMediaTypes(encoder.getEncodableMimeTypes());
        this.defaultMediaType = EncoderHttpMessageWriter.initDefaultMediaType(this.mediaTypes);
    }

    private static void initLogger(Encoder<?> encoder) {
        if (encoder instanceof AbstractEncoder && encoder.getClass().getName().startsWith("org.springframework.core.codec")) {
            Log logger = HttpLogging.forLog(((AbstractEncoder)encoder).getLogger());
            ((AbstractEncoder)encoder).setLogger(logger);
        }
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
    public List<MediaType> getWritableMediaTypes(ResolvableType elementType) {
        return MediaType.asMediaTypes(this.getEncoder().getEncodableMimeTypes(elementType));
    }

    @Override
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return this.encoder.canEncode(elementType, mediaType);
    }

    @Override
    public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        MediaType contentType = this.updateContentType(message, mediaType);
        Flux body2 = this.encoder.encode(inputStream, message.bufferFactory(), elementType, contentType, hints);
        if (inputStream instanceof Mono) {
            return body2.singleOrEmpty().switchIfEmpty(Mono.defer(() -> {
                message.getHeaders().setContentLength(0L);
                return message.setComplete().then(Mono.empty());
            })).flatMap(buffer -> {
                Hints.touchDataBuffer(buffer, hints, logger);
                message.getHeaders().setContentLength(buffer.readableByteCount());
                return message.writeWith((Publisher<? extends DataBuffer>)Mono.just((Object)buffer).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release));
            }).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
        }
        if (this.isStreamingMediaType(contentType)) {
            return message.writeAndFlushWith((Publisher<? extends Publisher<? extends DataBuffer>>)body2.map(buffer -> {
                Hints.touchDataBuffer(buffer, hints, logger);
                return Mono.just((Object)buffer).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
            }));
        }
        if (logger.isDebugEnabled()) {
            body2 = body2.doOnNext(buffer -> Hints.touchDataBuffer(buffer, hints, logger));
        }
        return message.writeWith((Publisher<? extends DataBuffer>)body2);
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

    private boolean isStreamingMediaType(@Nullable MediaType mediaType) {
        if (mediaType == null || !(this.encoder instanceof HttpMessageEncoder)) {
            return false;
        }
        for (MediaType streamingMediaType : ((HttpMessageEncoder)this.encoder).getStreamingMediaTypes()) {
            if (!mediaType.isCompatibleWith(streamingMediaType) || !this.matchParameters(mediaType, streamingMediaType)) continue;
            return true;
        }
        return false;
    }

    private boolean matchParameters(MediaType streamingMediaType, MediaType mediaType) {
        for (String name : streamingMediaType.getParameters().keySet()) {
            String s1 = streamingMediaType.getParameter(name);
            String s2 = mediaType.getParameter(name);
            if (!StringUtils.hasText(s1) || !StringUtils.hasText(s2) || s1.equalsIgnoreCase(s2)) continue;
            return false;
        }
        return true;
    }

    @Override
    public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, this.getWriteHints(actualType, elementType, mediaType, request, response));
        return this.write(inputStream, elementType, mediaType, response, allHints);
    }

    protected Map<String, Object> getWriteHints(ResolvableType streamType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        if (this.encoder instanceof HttpMessageEncoder) {
            HttpMessageEncoder encoder = (HttpMessageEncoder)this.encoder;
            return encoder.getEncodeHints(streamType, elementType, mediaType, request, response);
        }
        return Hints.none();
    }
}

