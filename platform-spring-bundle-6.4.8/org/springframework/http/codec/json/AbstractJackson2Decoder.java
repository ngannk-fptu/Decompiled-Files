/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.ObjectCodec
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectReader
 *  com.fasterxml.jackson.databind.exc.InvalidDefinitionException
 *  com.fasterxml.jackson.databind.util.TokenBuffer
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.codec.HttpMessageDecoder;
import org.springframework.http.codec.json.Jackson2CodecSupport;
import org.springframework.http.codec.json.Jackson2Tokenizer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractJackson2Decoder
extends Jackson2CodecSupport
implements HttpMessageDecoder<Object> {
    private int maxInMemorySize = 262144;

    protected AbstractJackson2Decoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        ObjectMapper mapper = this.selectObjectMapper(elementType, mimeType);
        if (mapper == null) {
            return false;
        }
        JavaType javaType = mapper.constructType(elementType.getType());
        if (CharSequence.class.isAssignableFrom(elementType.toClass()) || !this.supportsMimeType(mimeType)) {
            return false;
        }
        if (!this.logger.isDebugEnabled()) {
            return mapper.canDeserialize(javaType);
        }
        AtomicReference causeRef = new AtomicReference();
        if (mapper.canDeserialize(javaType, causeRef)) {
            return true;
        }
        this.logWarningIfNecessary((Type)javaType, (Throwable)causeRef.get());
        return false;
    }

    @Override
    public Flux<Object> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        ObjectMapper mapper = this.selectObjectMapper(elementType, mimeType);
        if (mapper == null) {
            throw new IllegalStateException("No ObjectMapper for " + elementType);
        }
        boolean forceUseOfBigDecimal = mapper.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        if (BigDecimal.class.equals((Object)elementType.getType())) {
            forceUseOfBigDecimal = true;
        }
        Flux<DataBuffer> processed = this.processInput(input, elementType, mimeType, hints);
        Flux<TokenBuffer> tokens = Jackson2Tokenizer.tokenize(processed, mapper.getFactory(), mapper, true, forceUseOfBigDecimal, this.getMaxInMemorySize());
        ObjectReader reader = this.getObjectReader(mapper, elementType, hints);
        return tokens.handle((tokenBuffer, sink) -> {
            try {
                Object value = reader.readValue(tokenBuffer.asParser((ObjectCodec)mapper));
                this.logValue(value, hints);
                if (value != null) {
                    sink.next(value);
                }
            }
            catch (IOException ex) {
                sink.error((Throwable)this.processException(ex));
            }
        });
    }

    protected Flux<DataBuffer> processInput(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(input);
    }

    @Override
    public Mono<Object> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(input, this.maxInMemorySize).flatMap(dataBuffer -> Mono.justOrEmpty((Object)this.decode((DataBuffer)dataBuffer, elementType, mimeType, hints)));
    }

    @Override
    public Object decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
        ObjectMapper mapper = this.selectObjectMapper(targetType, mimeType);
        if (mapper == null) {
            throw new IllegalStateException("No ObjectMapper for " + targetType);
        }
        try {
            ObjectReader objectReader = this.getObjectReader(mapper, targetType, hints);
            Object value = objectReader.readValue(dataBuffer.asInputStream());
            this.logValue(value, hints);
            Object object = value;
            return object;
        }
        catch (IOException ex) {
            throw this.processException(ex);
        }
        finally {
            DataBufferUtils.release(dataBuffer);
        }
    }

    private ObjectReader getObjectReader(ObjectMapper mapper, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        Assert.notNull((Object)elementType, "'elementType' must not be null");
        Class<?> contextClass = this.getContextClass(elementType);
        if (contextClass == null && hints != null) {
            contextClass = this.getContextClass((ResolvableType)hints.get(ACTUAL_TYPE_HINT));
        }
        JavaType javaType = this.getJavaType(elementType.getType(), contextClass);
        Class jsonView = hints != null ? (Class)hints.get(Jackson2CodecSupport.JSON_VIEW_HINT) : null;
        return jsonView != null ? mapper.readerWithView(jsonView).forType(javaType) : mapper.readerFor(javaType);
    }

    @Nullable
    private Class<?> getContextClass(@Nullable ResolvableType elementType) {
        MethodParameter param = elementType != null ? this.getParameter(elementType) : null;
        return param != null ? param.getContainingClass() : null;
    }

    private void logValue(@Nullable Object value, @Nullable Map<String, Object> hints) {
        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, traceOn == false);
                return Hints.getLogPrefix(hints) + "Decoded [" + formatted + "]";
            });
        }
    }

    private CodecException processException(IOException ex) {
        if (ex instanceof InvalidDefinitionException) {
            JavaType type = ((InvalidDefinitionException)((Object)ex)).getType();
            return new CodecException("Type definition error: " + type, ex);
        }
        if (ex instanceof JsonProcessingException) {
            String originalMessage = ((JsonProcessingException)((Object)ex)).getOriginalMessage();
            return new DecodingException("JSON decoding error: " + originalMessage, ex);
        }
        return new DecodingException("I/O error while parsing input stream", ex);
    }

    @Override
    public Map<String, Object> getDecodeHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
        return this.getHints(actualType);
    }

    @Override
    public List<MimeType> getDecodableMimeTypes() {
        return this.getMimeTypes();
    }

    @Override
    public List<MimeType> getDecodableMimeTypes(ResolvableType targetType) {
        return this.getMimeTypes(targetType);
    }

    @Override
    protected <A extends Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType) {
        return parameter.getParameterAnnotation(annotType);
    }
}

