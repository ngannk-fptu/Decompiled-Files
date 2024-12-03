/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonFactory
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.JsonToken
 *  com.fasterxml.jackson.core.async.ByteArrayFeeder
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.deser.DefaultDeserializationContext
 *  com.fasterxml.jackson.databind.util.TokenBuffer
 *  org.springframework.core.codec.DecodingException
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferLimitException
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  reactor.core.Exceptions
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

final class Jackson2Tokenizer {
    private final JsonParser parser;
    private final DeserializationContext deserializationContext;
    private final boolean tokenizeArrayElements;
    private final boolean forceUseOfBigDecimal;
    private final int maxInMemorySize;
    private int objectDepth;
    private int arrayDepth;
    private int byteCount;
    private TokenBuffer tokenBuffer;
    private final ByteArrayFeeder inputFeeder;

    private Jackson2Tokenizer(JsonParser parser, DeserializationContext deserializationContext, boolean tokenizeArrayElements, boolean forceUseOfBigDecimal, int maxInMemorySize) {
        this.parser = parser;
        this.deserializationContext = deserializationContext;
        this.tokenizeArrayElements = tokenizeArrayElements;
        this.forceUseOfBigDecimal = forceUseOfBigDecimal;
        this.inputFeeder = (ByteArrayFeeder)this.parser.getNonBlockingInputFeeder();
        this.maxInMemorySize = maxInMemorySize;
        this.tokenBuffer = this.createToken();
    }

    private List<TokenBuffer> tokenize(DataBuffer dataBuffer) {
        int bufferSize = dataBuffer.readableByteCount();
        byte[] bytes = new byte[bufferSize];
        dataBuffer.read(bytes);
        DataBufferUtils.release((DataBuffer)dataBuffer);
        try {
            this.inputFeeder.feedInput(bytes, 0, bytes.length);
            List<TokenBuffer> result = this.parseTokenBufferFlux();
            this.assertInMemorySize(bufferSize, result);
            return result;
        }
        catch (JsonProcessingException ex) {
            throw new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), (Throwable)ex);
        }
        catch (IOException ex) {
            throw Exceptions.propagate((Throwable)ex);
        }
    }

    private Flux<TokenBuffer> endOfInput() {
        return Flux.defer(() -> {
            this.inputFeeder.endOfInput();
            try {
                return Flux.fromIterable(this.parseTokenBufferFlux());
            }
            catch (JsonProcessingException ex) {
                throw new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), (Throwable)ex);
            }
            catch (IOException ex) {
                throw Exceptions.propagate((Throwable)ex);
            }
        });
    }

    private List<TokenBuffer> parseTokenBufferFlux() throws IOException {
        JsonToken token;
        ArrayList<TokenBuffer> result = new ArrayList<TokenBuffer>();
        boolean previousNull = false;
        while (!(this.parser.isClosed() || (token = this.parser.nextToken()) == JsonToken.NOT_AVAILABLE || token == null && previousNull)) {
            if (token == null) {
                previousNull = true;
                continue;
            }
            previousNull = false;
            this.updateDepth(token);
            if (!this.tokenizeArrayElements) {
                this.processTokenNormal(token, result);
                continue;
            }
            this.processTokenArray(token, result);
        }
        return result;
    }

    private void updateDepth(JsonToken token) {
        switch (token) {
            case START_OBJECT: {
                ++this.objectDepth;
                break;
            }
            case END_OBJECT: {
                --this.objectDepth;
                break;
            }
            case START_ARRAY: {
                ++this.arrayDepth;
                break;
            }
            case END_ARRAY: {
                --this.arrayDepth;
            }
        }
    }

    private void processTokenNormal(JsonToken token, List<TokenBuffer> result) throws IOException {
        this.tokenBuffer.copyCurrentEvent(this.parser);
        if ((token.isStructEnd() || token.isScalarValue()) && this.objectDepth == 0 && this.arrayDepth == 0) {
            result.add(this.tokenBuffer);
            this.tokenBuffer = this.createToken();
        }
    }

    private void processTokenArray(JsonToken token, List<TokenBuffer> result) throws IOException {
        if (!this.isTopLevelArrayToken(token)) {
            this.tokenBuffer.copyCurrentEvent(this.parser);
        }
        if (!(this.objectDepth != 0 || this.arrayDepth != 0 && this.arrayDepth != 1 || token != JsonToken.END_OBJECT && !token.isScalarValue())) {
            result.add(this.tokenBuffer);
            this.tokenBuffer = this.createToken();
        }
    }

    private TokenBuffer createToken() {
        TokenBuffer tokenBuffer = new TokenBuffer(this.parser, this.deserializationContext);
        tokenBuffer.forceUseOfBigDecimal(this.forceUseOfBigDecimal);
        return tokenBuffer;
    }

    private boolean isTopLevelArrayToken(JsonToken token) {
        return this.objectDepth == 0 && (token == JsonToken.START_ARRAY && this.arrayDepth == 1 || token == JsonToken.END_ARRAY && this.arrayDepth == 0);
    }

    private void assertInMemorySize(int currentBufferSize, List<TokenBuffer> result) {
        if (this.maxInMemorySize >= 0) {
            if (!result.isEmpty()) {
                this.byteCount = 0;
            } else if (currentBufferSize > Integer.MAX_VALUE - this.byteCount) {
                this.raiseLimitException();
            } else {
                this.byteCount += currentBufferSize;
                if (this.byteCount > this.maxInMemorySize) {
                    this.raiseLimitException();
                }
            }
        }
    }

    private void raiseLimitException() {
        throw new DataBufferLimitException("Exceeded limit on max bytes per JSON object: " + this.maxInMemorySize);
    }

    public static Flux<TokenBuffer> tokenize(Flux<DataBuffer> dataBuffers, JsonFactory jsonFactory, ObjectMapper objectMapper, boolean tokenizeArrays, boolean forceUseOfBigDecimal, int maxInMemorySize) {
        try {
            JsonParser parser = jsonFactory.createNonBlockingByteArrayParser();
            DeserializationContext context = objectMapper.getDeserializationContext();
            if (context instanceof DefaultDeserializationContext) {
                context = ((DefaultDeserializationContext)context).createInstance(objectMapper.getDeserializationConfig(), parser, objectMapper.getInjectableValues());
            }
            Jackson2Tokenizer tokenizer = new Jackson2Tokenizer(parser, context, tokenizeArrays, forceUseOfBigDecimal, maxInMemorySize);
            return dataBuffers.concatMapIterable(tokenizer::tokenize).concatWith(tokenizer.endOfInput());
        }
        catch (IOException ex) {
            return Flux.error((Throwable)ex);
        }
    }
}

