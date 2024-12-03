/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ServerSentEventHttpMessageReader
implements HttpMessageReader<Object> {
    private static final ResolvableType STRING_TYPE = ResolvableType.forClass(String.class);
    private static final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    private static final StringDecoder stringDecoder = StringDecoder.textPlainOnly();
    @Nullable
    private final Decoder<?> decoder;

    public ServerSentEventHttpMessageReader() {
        this(null);
    }

    public ServerSentEventHttpMessageReader(@Nullable Decoder<?> decoder) {
        this.decoder = decoder;
    }

    @Nullable
    public Decoder<?> getDecoder() {
        return this.decoder;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.TEXT_EVENT_STREAM);
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return MediaType.TEXT_EVENT_STREAM.includes(mediaType) || this.isServerSentEvent(elementType);
    }

    private boolean isServerSentEvent(ResolvableType elementType) {
        Class<?> rawClass = elementType.getRawClass();
        return rawClass != null && ServerSentEvent.class.isAssignableFrom(rawClass);
    }

    @Override
    public Flux<Object> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        boolean shouldWrap = this.isServerSentEvent(elementType);
        ResolvableType valueType = shouldWrap ? elementType.getGeneric(new int[0]) : elementType;
        return stringDecoder.decode((Publisher<DataBuffer>)message.getBody(), STRING_TYPE, null, Collections.emptyMap()).bufferUntil(line -> line.equals("")).concatMap(lines -> this.buildEvent((List<String>)lines, valueType, shouldWrap, hints));
    }

    private Mono<?> buildEvent(List<String> lines, ResolvableType valueType, boolean shouldWrap, Map<String, Object> hints) {
        Mono<?> decodedData;
        ServerSentEvent.Builder sseBuilder = shouldWrap ? ServerSentEvent.builder() : null;
        StringBuilder data = null;
        StringBuilder comment = null;
        for (String line : lines) {
            if (line.startsWith("data:")) {
                data = data != null ? data : new StringBuilder();
                data.append(line.substring(5)).append("\n");
            }
            if (!shouldWrap) continue;
            if (line.startsWith("id:")) {
                sseBuilder.id(line.substring(3));
                continue;
            }
            if (line.startsWith("event:")) {
                sseBuilder.event(line.substring(6));
                continue;
            }
            if (line.startsWith("retry:")) {
                sseBuilder.retry(Duration.ofMillis(Long.parseLong(line.substring(6))));
                continue;
            }
            if (!line.startsWith(":")) continue;
            comment = comment != null ? comment : new StringBuilder();
            comment.append(line.substring(1)).append("\n");
        }
        Mono<?> mono = decodedData = data != null ? this.decodeData(data.toString(), valueType, hints) : Mono.empty();
        if (shouldWrap) {
            if (comment != null) {
                sseBuilder.comment(comment.substring(0, comment.length() - 1));
            }
            return decodedData.map(o -> {
                sseBuilder.data(o);
                return sseBuilder.build();
            });
        }
        return decodedData;
    }

    private Mono<?> decodeData(String data, ResolvableType dataType, Map<String, Object> hints) {
        if (String.class == dataType.resolve()) {
            return Mono.just((Object)data.substring(0, data.length() - 1));
        }
        if (this.decoder == null) {
            return Mono.error((Throwable)new CodecException("No SSE decoder configured and the data is not String."));
        }
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        Mono input = Mono.just((Object)bufferFactory.wrap(bytes));
        return this.decoder.decodeToMono((Publisher<DataBuffer>)input, dataType, MediaType.TEXT_EVENT_STREAM, hints);
    }

    @Override
    public Mono<Object> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        if (String.class.equals(elementType.getRawClass())) {
            Flux<DataBuffer> body = message.getBody();
            return stringDecoder.decodeToMono((Publisher<DataBuffer>)body, elementType, null, null).cast(Object.class);
        }
        return Mono.error((Throwable)new UnsupportedOperationException("ServerSentEventHttpMessageReader only supports reading stream of events as a Flux"));
    }
}

