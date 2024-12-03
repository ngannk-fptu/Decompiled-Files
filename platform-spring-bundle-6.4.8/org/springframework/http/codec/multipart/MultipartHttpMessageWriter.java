/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartWriterSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultipartHttpMessageWriter
extends MultipartWriterSupport
implements HttpMessageWriter<MultiValueMap<String, ?>> {
    private static final Map<String, Object> DEFAULT_HINTS = Hints.from(Hints.SUPPRESS_LOGGING_HINT, true);
    private final List<HttpMessageWriter<?>> partWriters;
    @Nullable
    private final HttpMessageWriter<MultiValueMap<String, String>> formWriter;

    public MultipartHttpMessageWriter() {
        this(Arrays.asList(new EncoderHttpMessageWriter<CharSequence>(CharSequenceEncoder.textPlainOnly()), new ResourceHttpMessageWriter()));
    }

    public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters) {
        this(partWriters, new FormHttpMessageWriter());
    }

    public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters, @Nullable HttpMessageWriter<MultiValueMap<String, String>> formWriter) {
        super(MultipartHttpMessageWriter.initMediaTypes(formWriter));
        this.partWriters = partWriters;
        this.formWriter = formWriter;
    }

    private static List<MediaType> initMediaTypes(@Nullable HttpMessageWriter<?> formWriter) {
        ArrayList<MediaType> result = new ArrayList<MediaType>(MultipartHttpMessageReader.MIME_TYPES);
        if (formWriter != null) {
            result.addAll(formWriter.getWritableMediaTypes());
        }
        return Collections.unmodifiableList(result);
    }

    public List<HttpMessageWriter<?>> getPartWriters() {
        return Collections.unmodifiableList(this.partWriters);
    }

    @Nullable
    public HttpMessageWriter<MultiValueMap<String, String>> getFormWriter() {
        return this.formWriter;
    }

    @Override
    public Mono<Void> write(Publisher<? extends MultiValueMap<String, ?>> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
        return Mono.from(inputStream).flatMap(map -> {
            if (this.formWriter == null || this.isMultipart((MultiValueMap<String, ?>)map, mediaType)) {
                return this.writeMultipart((MultiValueMap<String, ?>)map, outputMessage, mediaType, hints);
            }
            Mono input = Mono.just((Object)map);
            return this.formWriter.write((Publisher<MultiValueMap<String, String>>)input, elementType, mediaType, outputMessage, hints);
        });
    }

    private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
        if (contentType != null) {
            return contentType.getType().equalsIgnoreCase("multipart");
        }
        for (List values : map.values()) {
            for (Object value : values) {
                if (value == null || value instanceof String) continue;
                return true;
            }
        }
        return false;
    }

    private Mono<Void> writeMultipart(MultiValueMap<String, ?> map, ReactiveHttpOutputMessage outputMessage, @Nullable MediaType mediaType, Map<String, Object> hints) {
        byte[] boundary = this.generateMultipartBoundary();
        mediaType = this.getMultipartMediaType(mediaType, boundary);
        outputMessage.getHeaders().setContentType(mediaType);
        LogFormatUtils.traceDebug(this.logger, traceOn -> Hints.getLogPrefix(hints) + "Encoding " + (this.isEnableLoggingRequestDetails() ? LogFormatUtils.formatValue(map, traceOn == false) : "parts " + map.keySet() + " (content masked)"));
        DataBufferFactory bufferFactory = outputMessage.bufferFactory();
        Flux body2 = Flux.fromIterable(map.entrySet()).concatMap(entry -> this.encodePartValues(boundary, (String)entry.getKey(), (List)entry.getValue(), bufferFactory)).concatWith(this.generateLastLine(boundary, bufferFactory)).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
        if (this.logger.isDebugEnabled()) {
            body2 = body2.doOnNext(buffer -> Hints.touchDataBuffer(buffer, hints, this.logger));
        }
        return outputMessage.writeWith((Publisher<? extends DataBuffer>)body2);
    }

    private Flux<DataBuffer> encodePartValues(byte[] boundary, String name, List<?> values, DataBufferFactory bufferFactory) {
        return Flux.fromIterable(values).concatMap(value -> this.encodePart(boundary, name, value, bufferFactory));
    }

    private <T> Flux<DataBuffer> encodePart(byte[] boundary, String name, T value, DataBufferFactory factory) {
        Object body2;
        MultipartHttpOutputMessage message = new MultipartHttpOutputMessage(factory);
        HttpHeaders headers = message.getHeaders();
        ResolvableType resolvableType = null;
        if (value instanceof HttpEntity) {
            HttpEntity httpEntity = (HttpEntity)value;
            headers.putAll(httpEntity.getHeaders());
            body2 = httpEntity.getBody();
            Assert.state(body2 != null, "MultipartHttpMessageWriter only supports HttpEntity with body");
            if (httpEntity instanceof ResolvableTypeProvider) {
                resolvableType = ((ResolvableTypeProvider)((Object)httpEntity)).getResolvableType();
            }
        } else {
            body2 = value;
        }
        if (resolvableType == null) {
            resolvableType = ResolvableType.forClass(body2.getClass());
        }
        if (!headers.containsKey("Content-Disposition")) {
            if (body2 instanceof Resource) {
                headers.setContentDispositionFormData(name, ((Resource)body2).getFilename());
            } else if (resolvableType.resolve() == Resource.class) {
                body2 = Mono.from((Publisher)((Publisher)body2)).doOnNext(o -> headers.setContentDispositionFormData(name, ((Resource)o).getFilename()));
            } else {
                headers.setContentDispositionFormData(name, null);
            }
        }
        MediaType contentType = headers.getContentType();
        ResolvableType finalBodyType = resolvableType;
        Optional<HttpMessageWriter> writer = this.partWriters.stream().filter(partWriter -> partWriter.canWrite(finalBodyType, contentType)).findFirst();
        if (!writer.isPresent()) {
            return Flux.error((Throwable)new CodecException("No suitable writer found for part: " + name));
        }
        Publisher bodyPublisher = body2 instanceof Publisher ? (Publisher)body2 : Mono.just(body2);
        Mono<Void> partContentReady = writer.get().write(bodyPublisher, resolvableType, contentType, message, DEFAULT_HINTS);
        Flux partContent = partContentReady.thenMany((Publisher)Flux.defer(message::getBody));
        return Flux.concat((Publisher[])new Publisher[]{this.generateBoundaryLine(boundary, factory), partContent, this.generateNewLine(factory)});
    }

    private class MultipartHttpOutputMessage
    implements ReactiveHttpOutputMessage {
        private final DataBufferFactory bufferFactory;
        private final HttpHeaders headers = new HttpHeaders();
        private final AtomicBoolean committed = new AtomicBoolean();
        @Nullable
        private Flux<DataBuffer> body;

        public MultipartHttpOutputMessage(DataBufferFactory bufferFactory) {
            this.bufferFactory = bufferFactory;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.body != null ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
        }

        @Override
        public DataBufferFactory bufferFactory() {
            return this.bufferFactory;
        }

        @Override
        public void beforeCommit(Supplier<? extends Mono<Void>> action) {
            this.committed.set(true);
        }

        @Override
        public boolean isCommitted() {
            return this.committed.get();
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body2) {
            if (this.body != null) {
                return Mono.error((Throwable)new IllegalStateException("Multiple calls to writeWith() not supported"));
            }
            this.body = MultipartHttpMessageWriter.this.generatePartHeaders(this.headers, this.bufferFactory).concatWith(body2);
            return Mono.empty();
        }

        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body2) {
            return Mono.error((Throwable)new UnsupportedOperationException());
        }

        public Flux<DataBuffer> getBody() {
            return this.body != null ? this.body : Flux.error((Throwable)new IllegalStateException("Body has not been written yet"));
        }

        @Override
        public Mono<Void> setComplete() {
            return Mono.error((Throwable)new UnsupportedOperationException());
        }
    }
}

