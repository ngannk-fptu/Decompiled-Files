/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.http.codec.json.Jackson2CodecSupport;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractJackson2Encoder
extends Jackson2CodecSupport
implements HttpMessageEncoder<Object> {
    private static final byte[] NEWLINE_SEPARATOR = new byte[]{10};
    private static final Map<MediaType, byte[]> STREAM_SEPARATORS = new HashMap<MediaType, byte[]>(4);
    private final List<MediaType> streamingMediaTypes = new ArrayList<MediaType>(1);

    protected AbstractJackson2Encoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
    }

    public void setStreamingMediaTypes(List<MediaType> mediaTypes) {
        this.streamingMediaTypes.clear();
        this.streamingMediaTypes.addAll(mediaTypes);
    }

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.resolve(Object.class);
        return this.supportsMimeType(mimeType) && (Object.class == clazz || !String.class.isAssignableFrom(elementType.resolve(clazz)) && this.getObjectMapper().canSerialize(clazz));
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Assert.notNull(inputStream, "'inputStream' must not be null");
        Assert.notNull((Object)bufferFactory, "'bufferFactory' must not be null");
        Assert.notNull((Object)elementType, "'elementType' must not be null");
        JsonEncoding encoding = this.getJsonEncoding(mimeType);
        if (inputStream instanceof Mono) {
            return Flux.from(inputStream).map(value -> this.encodeValue(value, mimeType, bufferFactory, elementType, hints, encoding));
        }
        for (MediaType streamingMediaType : this.streamingMediaTypes) {
            if (!streamingMediaType.isCompatibleWith(mimeType)) continue;
            byte[] separator = STREAM_SEPARATORS.getOrDefault(streamingMediaType, NEWLINE_SEPARATOR);
            return Flux.from(inputStream).map(value -> {
                DataBuffer buffer = this.encodeValue(value, mimeType, bufferFactory, elementType, hints, encoding);
                if (separator != null) {
                    buffer.write(separator);
                }
                return buffer;
            });
        }
        ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, elementType);
        return Flux.from(inputStream).collectList().map(list -> this.encodeValue(list, mimeType, bufferFactory, listType, hints, encoding)).flux();
    }

    private DataBuffer encodeValue(Object value, @Nullable MimeType mimeType, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable Map<String, Object> hints, JsonEncoding encoding) {
        ObjectWriter writer;
        JavaType javaType = this.getJavaType(elementType.getType(), null);
        Class jsonView = hints != null ? (Class)hints.get(Jackson2CodecSupport.JSON_VIEW_HINT) : null;
        ObjectWriter objectWriter = writer = jsonView != null ? this.getObjectMapper().writerWithView(jsonView) : this.getObjectMapper().writer();
        if (javaType.isContainerType()) {
            writer = writer.forType(javaType);
        }
        writer = this.customizeWriter(writer, mimeType, elementType, hints);
        DataBuffer buffer = bufferFactory.allocateBuffer();
        OutputStream outputStream = buffer.asOutputStream();
        try {
            JsonGenerator generator = this.getObjectMapper().getFactory().createGenerator(outputStream, encoding);
            writer.writeValue(generator, value);
        }
        catch (InvalidDefinitionException ex) {
            throw new CodecException("Type definition error: " + ex.getType(), ex);
        }
        catch (JsonProcessingException ex) {
            throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unexpected I/O error while writing to data buffer", ex);
        }
        return buffer;
    }

    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        return writer;
    }

    protected JsonEncoding getJsonEncoding(@Nullable MimeType mimeType) {
        if (mimeType != null && mimeType.getCharset() != null) {
            Charset charset = mimeType.getCharset();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (!charset.name().equals(encoding.getJavaName())) continue;
                return encoding;
            }
        }
        return JsonEncoding.UTF8;
    }

    @Override
    public List<MimeType> getEncodableMimeTypes() {
        return this.getMimeTypes();
    }

    @Override
    public List<MediaType> getStreamingMediaTypes() {
        return Collections.unmodifiableList(this.streamingMediaTypes);
    }

    @Override
    public Map<String, Object> getEncodeHints(@Nullable ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        return actualType != null ? this.getHints(actualType) : Collections.emptyMap();
    }

    @Override
    protected <A extends Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType) {
        return parameter.getMethodAnnotation(annotType);
    }

    static {
        STREAM_SEPARATORS.put(MediaType.APPLICATION_STREAM_JSON, NEWLINE_SEPARATOR);
        STREAM_SEPARATORS.put(MediaType.parseMediaType("application/stream+x-jackson-smile"), new byte[0]);
    }
}

