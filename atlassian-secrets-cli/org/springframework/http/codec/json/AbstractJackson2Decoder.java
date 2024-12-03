/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
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
    private final JsonFactory jsonFactory;

    protected AbstractJackson2Decoder(ObjectMapper mapper, MimeType ... mimeTypes) {
        super(mapper, mimeTypes);
        this.jsonFactory = mapper.getFactory().copy().disable(JsonFactory.Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING);
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        JavaType javaType = this.getObjectMapper().getTypeFactory().constructType(elementType.getType());
        return !CharSequence.class.isAssignableFrom(elementType.resolve(Object.class)) && this.getObjectMapper().canDeserialize(javaType) && this.supportsMimeType(mimeType);
    }

    @Override
    public Flux<Object> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux<TokenBuffer> tokens = Jackson2Tokenizer.tokenize((Flux<DataBuffer>)Flux.from(input), this.jsonFactory, true);
        return this.decodeInternal(tokens, elementType, mimeType, hints);
    }

    @Override
    public Mono<Object> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux<TokenBuffer> tokens = Jackson2Tokenizer.tokenize((Flux<DataBuffer>)Flux.from(input), this.jsonFactory, false);
        return this.decodeInternal(tokens, elementType, mimeType, hints).singleOrEmpty();
    }

    private Flux<Object> decodeInternal(Flux<TokenBuffer> tokens, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Assert.notNull(tokens, "'tokens' must not be null");
        Assert.notNull((Object)elementType, "'elementType' must not be null");
        MethodParameter param = this.getParameter(elementType);
        Class<?> contextClass = param != null ? param.getContainingClass() : null;
        JavaType javaType = this.getJavaType(elementType.getType(), contextClass);
        Class jsonView = hints != null ? (Class)hints.get(Jackson2CodecSupport.JSON_VIEW_HINT) : null;
        ObjectReader reader = jsonView != null ? this.getObjectMapper().readerWithView(jsonView).forType(javaType) : this.getObjectMapper().readerFor(javaType);
        return tokens.map(tokenBuffer -> {
            try {
                return reader.readValue(tokenBuffer.asParser(this.getObjectMapper()));
            }
            catch (InvalidDefinitionException ex) {
                throw new CodecException("Type definition error: " + ex.getType(), ex);
            }
            catch (JsonProcessingException ex) {
                throw new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex);
            }
            catch (IOException ex) {
                throw new DecodingException("I/O error while parsing input stream", ex);
            }
        });
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
    protected <A extends Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType) {
        return parameter.getParameterAnnotation(annotType);
    }
}

