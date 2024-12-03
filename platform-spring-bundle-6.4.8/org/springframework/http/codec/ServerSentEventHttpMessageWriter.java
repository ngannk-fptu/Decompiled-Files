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

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpLogging;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ServerSentEventHttpMessageWriter
implements HttpMessageWriter<Object> {
    private static final MediaType DEFAULT_MEDIA_TYPE = new MediaType("text", "event-stream", StandardCharsets.UTF_8);
    private static final List<MediaType> WRITABLE_MEDIA_TYPES = Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
    private static final Log logger = HttpLogging.forLogName(ServerSentEventHttpMessageWriter.class);
    @Nullable
    private final Encoder<?> encoder;

    public ServerSentEventHttpMessageWriter() {
        this(null);
    }

    public ServerSentEventHttpMessageWriter(@Nullable Encoder<?> encoder) {
        this.encoder = encoder;
    }

    @Nullable
    public Encoder<?> getEncoder() {
        return this.encoder;
    }

    @Override
    public List<MediaType> getWritableMediaTypes() {
        return WRITABLE_MEDIA_TYPES;
    }

    @Override
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return mediaType == null || MediaType.TEXT_EVENT_STREAM.includes(mediaType) || ServerSentEvent.class.isAssignableFrom(elementType.toClass());
    }

    @Override
    public Mono<Void> write(Publisher<?> input, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        mediaType = mediaType != null && mediaType.getCharset() != null ? mediaType : DEFAULT_MEDIA_TYPE;
        DataBufferFactory bufferFactory = message.bufferFactory();
        message.getHeaders().setContentType(mediaType);
        return message.writeAndFlushWith((Publisher<? extends Publisher<? extends DataBuffer>>)this.encode(input, elementType, mediaType, bufferFactory, hints));
    }

    private Flux<Publisher<DataBuffer>> encode(Publisher<?> input, ResolvableType elementType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
        ResolvableType dataType = ServerSentEvent.class.isAssignableFrom(elementType.toClass()) ? elementType.getGeneric(new int[0]) : elementType;
        return Flux.from(input).map(element -> {
            Flux result;
            ServerSentEvent<Object> sse = element instanceof ServerSentEvent ? (ServerSentEvent<Object>)element : ServerSentEvent.builder().data(element).build();
            StringBuilder sb = new StringBuilder();
            String id = sse.id();
            String event = sse.event();
            Duration retry = sse.retry();
            String comment = sse.comment();
            Object data = sse.data();
            if (id != null) {
                this.writeField("id", id, sb);
            }
            if (event != null) {
                this.writeField("event", event, sb);
            }
            if (retry != null) {
                this.writeField("retry", retry.toMillis(), sb);
            }
            if (comment != null) {
                sb.append(':').append(StringUtils.replace(comment, "\n", "\n:")).append('\n');
            }
            if (data != null) {
                sb.append("data:");
            }
            if (data == null) {
                result = Flux.just((Object)this.encodeText(sb + "\n", mediaType, factory));
            } else if (data instanceof String) {
                data = StringUtils.replace((String)data, "\n", "\ndata:");
                result = Flux.just((Object)this.encodeText(sb + (String)data + "\n\n", mediaType, factory));
            } else {
                result = this.encodeEvent(sb, data, dataType, mediaType, factory, hints);
            }
            return result.doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
        });
    }

    private <T> Flux<DataBuffer> encodeEvent(StringBuilder eventContent, T data, ResolvableType dataType, MediaType mediaType, DataBufferFactory factory, Map<String, Object> hints) {
        if (this.encoder == null) {
            throw new CodecException("No SSE encoder configured and the data is not String.");
        }
        return Flux.defer(() -> {
            DataBuffer startBuffer = this.encodeText(eventContent, mediaType, factory);
            DataBuffer endBuffer = this.encodeText("\n\n", mediaType, factory);
            DataBuffer dataBuffer = this.encoder.encodeValue(data, factory, dataType, mediaType, hints);
            Hints.touchDataBuffer(dataBuffer, hints, logger);
            return Flux.just((Object[])new DataBuffer[]{startBuffer, dataBuffer, endBuffer});
        });
    }

    private void writeField(String fieldName, Object fieldValue, StringBuilder sb) {
        sb.append(fieldName).append(':').append(fieldValue).append('\n');
    }

    private DataBuffer encodeText(CharSequence text, MediaType mediaType, DataBufferFactory bufferFactory) {
        Assert.notNull((Object)mediaType.getCharset(), "Expected MediaType with charset");
        byte[] bytes = text.toString().getBytes(mediaType.getCharset());
        return bufferFactory.wrap(bytes);
    }

    @Override
    public Mono<Void> write(Publisher<?> input, ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, this.getEncodeHints(actualType, elementType, mediaType, request, response));
        return this.write(input, elementType, mediaType, (ReactiveHttpOutputMessage)response, allHints);
    }

    private Map<String, Object> getEncodeHints(ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        if (this.encoder instanceof HttpMessageEncoder) {
            HttpMessageEncoder encoder = (HttpMessageEncoder)this.encoder;
            return encoder.getEncodeHints(actualType, elementType, mediaType, request, response);
        }
        return Hints.none();
    }
}

