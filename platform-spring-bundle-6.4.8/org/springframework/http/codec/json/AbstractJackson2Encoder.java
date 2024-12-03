/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonEncoding
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.util.ByteArrayBuilder
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectWriter
 *  com.fasterxml.jackson.databind.SequenceWriter
 *  com.fasterxml.jackson.databind.exc.InvalidDefinitionException
 *  com.fasterxml.jackson.databind.ser.FilterProvider
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.http.codec.json.Jackson2CodecSupport;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractJackson2Encoder
extends Jackson2CodecSupport
implements HttpMessageEncoder<Object> {
    private static final byte[] NEWLINE_SEPARATOR = new byte[]{10};
    private static final Map<String, JsonEncoding> ENCODINGS = CollectionUtils.newHashMap(JsonEncoding.values().length);
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
        Charset charset;
        if (!this.supportsMimeType(mimeType)) {
            return false;
        }
        if (mimeType != null && mimeType.getCharset() != null && !ENCODINGS.containsKey((charset = mimeType.getCharset()).name())) {
            return false;
        }
        ObjectMapper mapper = this.selectObjectMapper(elementType, mimeType);
        if (mapper == null) {
            return false;
        }
        Class<?> clazz = elementType.toClass();
        if (String.class.isAssignableFrom(elementType.resolve(clazz))) {
            return false;
        }
        if (Object.class == clazz) {
            return true;
        }
        if (!this.logger.isDebugEnabled()) {
            return mapper.canSerialize(clazz);
        }
        AtomicReference causeRef = new AtomicReference();
        if (mapper.canSerialize(clazz, causeRef)) {
            return true;
        }
        this.logWarningIfNecessary(clazz, (Throwable)causeRef.get());
        return false;
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Assert.notNull(inputStream, "'inputStream' must not be null");
        Assert.notNull((Object)bufferFactory, "'bufferFactory' must not be null");
        Assert.notNull((Object)elementType, "'elementType' must not be null");
        if (inputStream instanceof Mono) {
            return Mono.from(inputStream).map(value -> this.encodeValue(value, bufferFactory, elementType, mimeType, hints)).flux();
        }
        byte[] separator = this.getStreamingMediaTypeSeparator(mimeType);
        if (separator != null) {
            try {
                ObjectMapper mapper = this.selectObjectMapper(elementType, mimeType);
                if (mapper == null) {
                    throw new IllegalStateException("No ObjectMapper for " + elementType);
                }
                ObjectWriter writer = this.createObjectWriter(mapper, elementType, mimeType, null, hints);
                ByteArrayBuilder byteBuilder = new ByteArrayBuilder(writer.getFactory()._getBufferRecycler());
                JsonEncoding encoding = this.getJsonEncoding(mimeType);
                JsonGenerator generator = mapper.getFactory().createGenerator((OutputStream)byteBuilder, encoding);
                SequenceWriter sequenceWriter = writer.writeValues(generator);
                return Flux.from(inputStream).map(value -> this.encodeStreamingValue(value, bufferFactory, hints, sequenceWriter, byteBuilder, separator)).doAfterTerminate(() -> {
                    try {
                        byteBuilder.release();
                        generator.close();
                    }
                    catch (IOException ex) {
                        this.logger.error((Object)"Could not close Encoder resources", (Throwable)ex);
                    }
                });
            }
            catch (IOException ex) {
                return Flux.error((Throwable)ex);
            }
        }
        ResolvableType listType = ResolvableType.forClassWithGenerics(List.class, elementType);
        return Flux.from(inputStream).collectList().map(list -> this.encodeValue(list, bufferFactory, listType, mimeType, hints)).flux();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        ObjectMapper mapper;
        Class<?> jsonView = null;
        FilterProvider filters = null;
        if (value instanceof MappingJacksonValue) {
            MappingJacksonValue container = (MappingJacksonValue)value;
            value = container.getValue();
            valueType = ResolvableType.forInstance(value);
            jsonView = container.getSerializationView();
            filters = container.getFilters();
        }
        if ((mapper = this.selectObjectMapper(valueType, mimeType)) == null) {
            throw new IllegalStateException("No ObjectMapper for " + valueType);
        }
        ObjectWriter writer = this.createObjectWriter(mapper, valueType, mimeType, jsonView, hints);
        if (filters != null) {
            writer = writer.with(filters);
        }
        ByteArrayBuilder byteBuilder = new ByteArrayBuilder(writer.getFactory()._getBufferRecycler());
        try {
            JsonEncoding encoding = this.getJsonEncoding(mimeType);
            this.logValue(hints, value);
            try (JsonGenerator generator = mapper.getFactory().createGenerator((OutputStream)byteBuilder, encoding);){
                writer.writeValue(generator, value);
                generator.flush();
            }
            catch (InvalidDefinitionException ex) {
                throw new CodecException("Type definition error: " + ex.getType(), ex);
            }
            catch (JsonProcessingException ex) {
                throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
            }
            catch (IOException ex) {
                throw new IllegalStateException("Unexpected I/O error while writing to byte array builder", ex);
            }
            byte[] bytes = byteBuilder.toByteArray();
            DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
            buffer.write(bytes);
            Hints.touchDataBuffer(buffer, hints, this.logger);
            DataBuffer dataBuffer = buffer;
            return dataBuffer;
        }
        finally {
            byteBuilder.release();
        }
    }

    private DataBuffer encodeStreamingValue(Object value, DataBufferFactory bufferFactory, @Nullable Map<String, Object> hints, SequenceWriter sequenceWriter, ByteArrayBuilder byteArrayBuilder, byte[] separator) {
        int length;
        int offset;
        this.logValue(hints, value);
        try {
            sequenceWriter.write(value);
            sequenceWriter.flush();
        }
        catch (InvalidDefinitionException ex) {
            throw new CodecException("Type definition error: " + ex.getType(), ex);
        }
        catch (JsonProcessingException ex) {
            throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unexpected I/O error while writing to byte array builder", ex);
        }
        byte[] bytes = byteArrayBuilder.toByteArray();
        byteArrayBuilder.reset();
        if (bytes.length > 0 && bytes[0] == 32) {
            offset = 1;
            length = bytes.length - 1;
        } else {
            offset = 0;
            length = bytes.length;
        }
        DataBuffer buffer = bufferFactory.allocateBuffer(length + separator.length);
        buffer.write(bytes, offset, length);
        buffer.write(separator);
        Hints.touchDataBuffer(buffer, hints, this.logger);
        return buffer;
    }

    private void logValue(@Nullable Map<String, Object> hints, Object value) {
        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, traceOn == false);
                return Hints.getLogPrefix(hints) + "Encoding [" + formatted + "]";
            });
        }
    }

    private ObjectWriter createObjectWriter(ObjectMapper mapper, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Class<?> jsonView, @Nullable Map<String, Object> hints) {
        ObjectWriter writer;
        JavaType javaType = this.getJavaType(valueType.getType(), null);
        if (jsonView == null && hints != null) {
            jsonView = (Class)hints.get(Jackson2CodecSupport.JSON_VIEW_HINT);
        }
        ObjectWriter objectWriter = writer = jsonView != null ? mapper.writerWithView(jsonView) : mapper.writer();
        if (javaType.isContainerType()) {
            writer = writer.forType(javaType);
        }
        return this.customizeWriter(writer, mimeType, valueType, hints);
    }

    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        return writer;
    }

    @Nullable
    protected byte[] getStreamingMediaTypeSeparator(@Nullable MimeType mimeType) {
        for (MediaType streamingMediaType : this.streamingMediaTypes) {
            if (!streamingMediaType.isCompatibleWith(mimeType)) continue;
            return NEWLINE_SEPARATOR;
        }
        return null;
    }

    protected JsonEncoding getJsonEncoding(@Nullable MimeType mimeType) {
        Charset charset;
        JsonEncoding result;
        if (mimeType != null && mimeType.getCharset() != null && (result = ENCODINGS.get((charset = mimeType.getCharset()).name())) != null) {
            return result;
        }
        return JsonEncoding.UTF8;
    }

    @Override
    public List<MimeType> getEncodableMimeTypes() {
        return this.getMimeTypes();
    }

    @Override
    public List<MimeType> getEncodableMimeTypes(ResolvableType elementType) {
        return this.getMimeTypes(elementType);
    }

    @Override
    public List<MediaType> getStreamingMediaTypes() {
        return Collections.unmodifiableList(this.streamingMediaTypes);
    }

    @Override
    public Map<String, Object> getEncodeHints(@Nullable ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response) {
        return actualType != null ? this.getHints(actualType) : Hints.none();
    }

    @Override
    protected <A extends Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType) {
        return parameter.getMethodAnnotation(annotType);
    }

    static {
        for (JsonEncoding encoding : JsonEncoding.values()) {
            ENCODINGS.put(encoding.getJavaName(), encoding);
        }
        ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
    }
}

