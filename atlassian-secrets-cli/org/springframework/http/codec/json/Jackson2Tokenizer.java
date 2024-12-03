/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

class Jackson2Tokenizer {
    private final JsonParser parser;
    private final boolean tokenizeArrayElements;
    private TokenBuffer tokenBuffer;
    private int objectDepth;
    private int arrayDepth;
    private final ByteArrayFeeder inputFeeder;

    private Jackson2Tokenizer(JsonParser parser, boolean tokenizeArrayElements) {
        this.parser = parser;
        this.tokenizeArrayElements = tokenizeArrayElements;
        this.tokenBuffer = new TokenBuffer(parser);
        this.inputFeeder = (ByteArrayFeeder)this.parser.getNonBlockingInputFeeder();
    }

    private Flux<TokenBuffer> tokenize(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        try {
            this.inputFeeder.feedInput(bytes, 0, bytes.length);
            return this.parseTokenBufferFlux();
        }
        catch (JsonProcessingException ex) {
            return Flux.error((Throwable)new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex));
        }
        catch (IOException ex) {
            return Flux.error((Throwable)ex);
        }
    }

    private Flux<TokenBuffer> endOfInput() {
        this.inputFeeder.endOfInput();
        try {
            return this.parseTokenBufferFlux();
        }
        catch (JsonProcessingException ex) {
            return Flux.error((Throwable)new DecodingException("JSON decoding error: " + ex.getOriginalMessage(), ex));
        }
        catch (IOException ex) {
            return Flux.error((Throwable)ex);
        }
    }

    private Flux<TokenBuffer> parseTokenBufferFlux() throws IOException {
        JsonToken token;
        ArrayList<TokenBuffer> result = new ArrayList<TokenBuffer>();
        while ((token = this.parser.nextToken()) != JsonToken.NOT_AVAILABLE && (token != null || (token = this.parser.nextToken()) != null)) {
            this.updateDepth(token);
            if (!this.tokenizeArrayElements) {
                this.processTokenNormal(token, result);
                continue;
            }
            this.processTokenArray(token, result);
        }
        return Flux.fromIterable(result);
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
            this.tokenBuffer = new TokenBuffer(this.parser);
        }
    }

    private void processTokenArray(JsonToken token, List<TokenBuffer> result) throws IOException {
        if (!this.isTopLevelArrayToken(token)) {
            this.tokenBuffer.copyCurrentEvent(this.parser);
        }
        if (!(this.objectDepth != 0 || this.arrayDepth != 0 && this.arrayDepth != 1 || token != JsonToken.END_OBJECT && !token.isScalarValue())) {
            result.add(this.tokenBuffer);
            this.tokenBuffer = new TokenBuffer(this.parser);
        }
    }

    private boolean isTopLevelArrayToken(JsonToken token) {
        return this.objectDepth == 0 && (token == JsonToken.START_ARRAY && this.arrayDepth == 1 || token == JsonToken.END_ARRAY && this.arrayDepth == 0);
    }

    public static Flux<TokenBuffer> tokenize(Flux<DataBuffer> dataBuffers, JsonFactory jsonFactory, boolean tokenizeArrayElements) {
        try {
            JsonParser parser = jsonFactory.createNonBlockingByteArrayParser();
            Jackson2Tokenizer tokenizer = new Jackson2Tokenizer(parser, tokenizeArrayElements);
            return dataBuffers.flatMap(tokenizer::tokenize, Flux::error, tokenizer::endOfInput);
        }
        catch (IOException ex) {
            return Flux.error((Throwable)ex);
        }
    }
}

