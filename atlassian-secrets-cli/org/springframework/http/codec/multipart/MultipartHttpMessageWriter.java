/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultipartHttpMessageWriter
implements HttpMessageWriter<MultiValueMap<String, ?>> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final List<HttpMessageWriter<?>> partWriters;
    @Nullable
    private final HttpMessageWriter<MultiValueMap<String, String>> formWriter;
    private Charset charset = DEFAULT_CHARSET;
    private final List<MediaType> supportedMediaTypes;
    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

    public MultipartHttpMessageWriter() {
        this(Arrays.asList(new EncoderHttpMessageWriter<CharSequence>(CharSequenceEncoder.textPlainOnly()), new ResourceHttpMessageWriter()));
    }

    public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters) {
        this(partWriters, new FormHttpMessageWriter());
    }

    public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters, @Nullable HttpMessageWriter<MultiValueMap<String, String>> formWriter) {
        this.partWriters = partWriters;
        this.formWriter = formWriter;
        this.supportedMediaTypes = MultipartHttpMessageWriter.initMediaTypes(formWriter);
    }

    private static List<MediaType> initMediaTypes(@Nullable HttpMessageWriter<?> formWriter) {
        ArrayList<MediaType> result = new ArrayList<MediaType>();
        result.add(MediaType.MULTIPART_FORM_DATA);
        if (formWriter != null) {
            result.addAll(formWriter.getWritableMediaTypes());
        }
        return Collections.unmodifiableList(result);
    }

    public List<HttpMessageWriter<?>> getPartWriters() {
        return Collections.unmodifiableList(this.partWriters);
    }

    public void setCharset(Charset charset) {
        Assert.notNull((Object)charset, "Charset must not be null");
        this.charset = charset;
    }

    public Charset getCharset() {
        return this.charset;
    }

    @Override
    public List<MediaType> getWritableMediaTypes() {
        return this.supportedMediaTypes;
    }

    @Override
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        Class<?> rawClass = elementType.getRawClass();
        return rawClass != null && MultiValueMap.class.isAssignableFrom(rawClass) && (mediaType == null || this.supportedMediaTypes.stream().anyMatch(m -> m.isCompatibleWith(mediaType)));
    }

    @Override
    public Mono<Void> write(Publisher<? extends MultiValueMap<String, ?>> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
        return Mono.from(inputStream).flatMap(map -> {
            if (this.formWriter == null || this.isMultipart((MultiValueMap<String, ?>)map, mediaType)) {
                return this.writeMultipart((MultiValueMap<String, ?>)map, outputMessage);
            }
            MultiValueMap formData = map;
            return this.formWriter.write((Publisher<MultiValueMap<String, String>>)Mono.just((Object)formData), elementType, mediaType, outputMessage, hints);
        });
    }

    private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
        if (contentType != null) {
            return MediaType.MULTIPART_FORM_DATA.includes(contentType);
        }
        for (List values : map.values()) {
            for (Object value : values) {
                if (value == null || value instanceof String) continue;
                return true;
            }
        }
        return false;
    }

    private Mono<Void> writeMultipart(MultiValueMap<String, ?> map, ReactiveHttpOutputMessage outputMessage) {
        byte[] boundary = this.generateMultipartBoundary();
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
        params.put("charset", this.getCharset().name());
        outputMessage.getHeaders().setContentType(new MediaType(MediaType.MULTIPART_FORM_DATA, params));
        Flux body = Flux.fromIterable(map.entrySet()).concatMap(entry -> this.encodePartValues(boundary, (String)entry.getKey(), (List)entry.getValue())).concatWith((Publisher)Mono.just((Object)this.generateLastLine(boundary)));
        return outputMessage.writeWith((Publisher<? extends DataBuffer>)body);
    }

    protected byte[] generateMultipartBoundary() {
        return MimeTypeUtils.generateMultipartBoundary();
    }

    private Flux<DataBuffer> encodePartValues(byte[] boundary, String name, List<?> values) {
        return Flux.concat((Iterable)values.stream().map(v -> this.encodePart(boundary, name, v)).collect(Collectors.toList()));
    }

    private <T> Flux<DataBuffer> encodePart(byte[] boundary, String name, T value) {
        Object body;
        MultipartHttpOutputMessage outputMessage = new MultipartHttpOutputMessage(this.bufferFactory, this.getCharset());
        HttpHeaders outputHeaders = outputMessage.getHeaders();
        ResolvableType resolvableType = null;
        if (value instanceof HttpEntity) {
            HttpEntity httpEntity = (HttpEntity)value;
            outputHeaders.putAll(httpEntity.getHeaders());
            body = httpEntity.getBody();
            Assert.state(body != null, "MultipartHttpMessageWriter only supports HttpEntity with body");
            if (httpEntity instanceof MultipartBodyBuilder.PublisherEntity) {
                MultipartBodyBuilder.PublisherEntity publisherEntity = (MultipartBodyBuilder.PublisherEntity)httpEntity;
                resolvableType = publisherEntity.getResolvableType();
            }
        } else {
            body = value;
        }
        if (resolvableType == null) {
            resolvableType = ResolvableType.forClass(body.getClass());
        }
        if (!outputHeaders.containsKey("Content-Disposition")) {
            if (body instanceof Resource) {
                outputHeaders.setContentDispositionFormData(name, ((Resource)body).getFilename());
            } else if (Resource.class.equals(resolvableType.getRawClass())) {
                body = Mono.from((Publisher)((Publisher)body)).doOnNext(o -> outputHeaders.setContentDispositionFormData(name, ((Resource)o).getFilename()));
            } else {
                outputHeaders.setContentDispositionFormData(name, null);
            }
        }
        MediaType contentType = outputHeaders.getContentType();
        ResolvableType finalBodyType = resolvableType;
        Optional<HttpMessageWriter> writer = this.partWriters.stream().filter(partWriter -> partWriter.canWrite(finalBodyType, contentType)).findFirst();
        if (!writer.isPresent()) {
            return Flux.error((Throwable)new CodecException("No suitable writer found for part: " + name));
        }
        Publisher bodyPublisher = body instanceof Publisher ? (Publisher)body : Mono.just(body);
        Mono<Void> partContentReady = writer.get().write(bodyPublisher, resolvableType, contentType, outputMessage, Collections.emptyMap());
        Flux partContent = partContentReady.thenMany((Publisher)Flux.defer(outputMessage::getBody));
        return Flux.concat((Publisher[])new Publisher[]{Mono.just((Object)this.generateBoundaryLine(boundary)), partContent, Mono.just((Object)this.generateNewLine())});
    }

    private DataBuffer generateBoundaryLine(byte[] boundary) {
        DataBuffer buffer = this.bufferFactory.allocateBuffer(boundary.length + 4);
        buffer.write((byte)45);
        buffer.write((byte)45);
        buffer.write(boundary);
        buffer.write((byte)13);
        buffer.write((byte)10);
        return buffer;
    }

    private DataBuffer generateNewLine() {
        DataBuffer buffer = this.bufferFactory.allocateBuffer(2);
        buffer.write((byte)13);
        buffer.write((byte)10);
        return buffer;
    }

    private DataBuffer generateLastLine(byte[] boundary) {
        DataBuffer buffer = this.bufferFactory.allocateBuffer(boundary.length + 6);
        buffer.write((byte)45);
        buffer.write((byte)45);
        buffer.write(boundary);
        buffer.write((byte)45);
        buffer.write((byte)45);
        buffer.write((byte)13);
        buffer.write((byte)10);
        return buffer;
    }

    private static class MultipartHttpOutputMessage
    implements ReactiveHttpOutputMessage {
        private final DataBufferFactory bufferFactory;
        private final Charset charset;
        private final HttpHeaders headers = new HttpHeaders();
        private final AtomicBoolean committed = new AtomicBoolean();
        @Nullable
        private Flux<DataBuffer> body;

        public MultipartHttpOutputMessage(DataBufferFactory bufferFactory, Charset charset) {
            this.bufferFactory = bufferFactory;
            this.charset = charset;
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
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            if (this.body != null) {
                return Mono.error((Throwable)new IllegalStateException("Multiple calls to writeWith() not supported"));
            }
            this.body = Flux.just((Object)this.generateHeaders()).concatWith(body);
            return Mono.empty();
        }

        private DataBuffer generateHeaders() {
            DataBuffer buffer = this.bufferFactory.allocateBuffer();
            for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                byte[] headerName = entry.getKey().getBytes(this.charset);
                for (String headerValueString : entry.getValue()) {
                    byte[] headerValue = headerValueString.getBytes(this.charset);
                    buffer.write(headerName);
                    buffer.write((byte)58);
                    buffer.write((byte)32);
                    buffer.write(headerValue);
                    buffer.write((byte)13);
                    buffer.write((byte)10);
                }
            }
            buffer.write((byte)13);
            buffer.write((byte)10);
            return buffer;
        }

        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
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

