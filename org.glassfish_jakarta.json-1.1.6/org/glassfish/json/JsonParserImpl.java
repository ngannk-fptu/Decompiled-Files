/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import org.glassfish.json.JsonArrayBuilderImpl;
import org.glassfish.json.JsonMessages;
import org.glassfish.json.JsonNumberImpl;
import org.glassfish.json.JsonObjectBuilderImpl;
import org.glassfish.json.JsonStringImpl;
import org.glassfish.json.JsonTokenizer;
import org.glassfish.json.UnicodeDetectingInputStream;
import org.glassfish.json.api.BufferPool;

public class JsonParserImpl
implements JsonParser {
    private final BufferPool bufferPool;
    private Context currentContext = new NoneContext();
    private JsonParser.Event currentEvent;
    private final Stack stack = new Stack();
    private final JsonTokenizer tokenizer;

    public JsonParserImpl(Reader reader, BufferPool bufferPool) {
        this.bufferPool = bufferPool;
        this.tokenizer = new JsonTokenizer(reader, bufferPool);
    }

    public JsonParserImpl(InputStream in, BufferPool bufferPool) {
        this.bufferPool = bufferPool;
        UnicodeDetectingInputStream uin = new UnicodeDetectingInputStream(in);
        this.tokenizer = new JsonTokenizer(new InputStreamReader((InputStream)uin, uin.getCharset()), bufferPool);
    }

    public JsonParserImpl(InputStream in, Charset encoding, BufferPool bufferPool) {
        this.bufferPool = bufferPool;
        this.tokenizer = new JsonTokenizer(new InputStreamReader(in, encoding), bufferPool);
    }

    @Override
    public String getString() {
        if (this.currentEvent == JsonParser.Event.KEY_NAME || this.currentEvent == JsonParser.Event.VALUE_STRING || this.currentEvent == JsonParser.Event.VALUE_NUMBER) {
            return this.tokenizer.getValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETSTRING_ERR(this.currentEvent));
    }

    @Override
    public boolean isIntegralNumber() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_ISINTEGRALNUMBER_ERR(this.currentEvent));
        }
        return this.tokenizer.isIntegral();
    }

    @Override
    public int getInt() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_GETINT_ERR(this.currentEvent));
        }
        return this.tokenizer.getInt();
    }

    boolean isDefinitelyInt() {
        return this.tokenizer.isDefinitelyInt();
    }

    boolean isDefinitelyLong() {
        return this.tokenizer.isDefinitelyLong();
    }

    @Override
    public long getLong() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_GETLONG_ERR(this.currentEvent));
        }
        return this.tokenizer.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (this.currentEvent != JsonParser.Event.VALUE_NUMBER) {
            throw new IllegalStateException(JsonMessages.PARSER_GETBIGDECIMAL_ERR(this.currentEvent));
        }
        return this.tokenizer.getBigDecimal();
    }

    @Override
    public JsonArray getArray() {
        if (this.currentEvent != JsonParser.Event.START_ARRAY) {
            throw new IllegalStateException(JsonMessages.PARSER_GETARRAY_ERR(this.currentEvent));
        }
        return this.getArray(new JsonArrayBuilderImpl(this.bufferPool));
    }

    @Override
    public JsonObject getObject() {
        if (this.currentEvent != JsonParser.Event.START_OBJECT) {
            throw new IllegalStateException(JsonMessages.PARSER_GETOBJECT_ERR(this.currentEvent));
        }
        return this.getObject(new JsonObjectBuilderImpl(this.bufferPool));
    }

    @Override
    public JsonValue getValue() {
        switch (this.currentEvent) {
            case START_ARRAY: {
                return this.getArray(new JsonArrayBuilderImpl(this.bufferPool));
            }
            case START_OBJECT: {
                return this.getObject(new JsonObjectBuilderImpl(this.bufferPool));
            }
            case KEY_NAME: 
            case VALUE_STRING: {
                return new JsonStringImpl(this.getString());
            }
            case VALUE_NUMBER: {
                if (this.isDefinitelyInt()) {
                    return JsonNumberImpl.getJsonNumber(this.getInt());
                }
                if (this.isDefinitelyLong()) {
                    return JsonNumberImpl.getJsonNumber(this.getLong());
                }
                return JsonNumberImpl.getJsonNumber(this.getBigDecimal());
            }
            case VALUE_TRUE: {
                return JsonValue.TRUE;
            }
            case VALUE_FALSE: {
                return JsonValue.FALSE;
            }
            case VALUE_NULL: {
                return JsonValue.NULL;
            }
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETVALUE_ERR(this.currentEvent));
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        if (this.currentEvent != JsonParser.Event.START_ARRAY) {
            throw new IllegalStateException(JsonMessages.PARSER_GETARRAY_ERR(this.currentEvent));
        }
        Spliterators.AbstractSpliterator<JsonValue> spliterator = new Spliterators.AbstractSpliterator<JsonValue>(Long.MAX_VALUE, 16){

            @Override
            public Spliterator<JsonValue> trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super JsonValue> action) {
                if (action == null) {
                    throw new NullPointerException();
                }
                if (!JsonParserImpl.this.hasNext()) {
                    return false;
                }
                if (JsonParserImpl.this.next() == JsonParser.Event.END_ARRAY) {
                    return false;
                }
                action.accept(JsonParserImpl.this.getValue());
                return true;
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        if (this.currentEvent != JsonParser.Event.START_OBJECT) {
            throw new IllegalStateException(JsonMessages.PARSER_GETOBJECT_ERR(this.currentEvent));
        }
        Spliterators.AbstractSpliterator<Map.Entry<String, JsonValue>> spliterator = new Spliterators.AbstractSpliterator<Map.Entry<String, JsonValue>>(Long.MAX_VALUE, 16){

            @Override
            public Spliterator<Map.Entry<String, JsonValue>> trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super Map.Entry<String, JsonValue>> action) {
                if (action == null) {
                    throw new NullPointerException();
                }
                if (!JsonParserImpl.this.hasNext()) {
                    return false;
                }
                JsonParser.Event e = JsonParserImpl.this.next();
                if (e == JsonParser.Event.END_OBJECT) {
                    return false;
                }
                if (e != JsonParser.Event.KEY_NAME) {
                    throw new JsonException(JsonMessages.INTERNAL_ERROR());
                }
                String key = JsonParserImpl.this.getString();
                if (!JsonParserImpl.this.hasNext()) {
                    throw new JsonException(JsonMessages.INTERNAL_ERROR());
                }
                JsonParserImpl.this.next();
                JsonValue value = JsonParserImpl.this.getValue();
                action.accept(new AbstractMap.SimpleImmutableEntry<String, JsonValue>(key, value));
                return true;
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        if (!(this.currentContext instanceof NoneContext)) {
            throw new IllegalStateException(JsonMessages.PARSER_GETVALUESTREAM_ERR());
        }
        Spliterators.AbstractSpliterator<JsonValue> spliterator = new Spliterators.AbstractSpliterator<JsonValue>(Long.MAX_VALUE, 16){

            @Override
            public Spliterator<JsonValue> trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super JsonValue> action) {
                if (action == null) {
                    throw new NullPointerException();
                }
                if (!JsonParserImpl.this.hasNext()) {
                    return false;
                }
                JsonParserImpl.this.next();
                action.accept(JsonParserImpl.this.getValue());
                return true;
            }
        };
        return StreamSupport.stream(spliterator, false);
    }

    @Override
    public void skipArray() {
        if (this.currentEvent == JsonParser.Event.START_ARRAY) {
            this.currentContext.skip();
            this.currentContext = this.stack.pop();
            this.currentEvent = JsonParser.Event.END_ARRAY;
        }
    }

    @Override
    public void skipObject() {
        if (this.currentEvent == JsonParser.Event.START_OBJECT) {
            this.currentContext.skip();
            this.currentContext = this.stack.pop();
            this.currentEvent = JsonParser.Event.END_OBJECT;
        }
    }

    private JsonArray getArray(JsonArrayBuilder builder) {
        while (this.hasNext()) {
            JsonParser.Event e = this.next();
            if (e == JsonParser.Event.END_ARRAY) {
                return builder.build();
            }
            builder.add(this.getValue());
        }
        throw this.parsingException(JsonTokenizer.JsonToken.EOF, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL, SQUARECLOSE]");
    }

    private JsonObject getObject(JsonObjectBuilder builder) {
        while (this.hasNext()) {
            JsonParser.Event e = this.next();
            if (e == JsonParser.Event.END_OBJECT) {
                return builder.build();
            }
            String key = this.getString();
            this.next();
            builder.add(key, this.getValue());
        }
        throw this.parsingException(JsonTokenizer.JsonToken.EOF, "[STRING, CURLYCLOSE]");
    }

    @Override
    public JsonLocation getLocation() {
        return this.tokenizer.getLocation();
    }

    public JsonLocation getLastCharLocation() {
        return this.tokenizer.getLastCharLocation();
    }

    @Override
    public boolean hasNext() {
        if (this.stack.isEmpty() && this.currentEvent != null && this.currentEvent.compareTo(JsonParser.Event.KEY_NAME) > 0) {
            JsonTokenizer.JsonToken token = this.tokenizer.nextToken();
            if (token != JsonTokenizer.JsonToken.EOF) {
                throw new JsonParsingException(JsonMessages.PARSER_EXPECTED_EOF(token), this.getLastCharLocation());
            }
            return false;
        }
        if (!this.stack.isEmpty() && !this.tokenizer.hasNextToken()) {
            this.currentEvent = this.currentContext.getNextEvent();
            return false;
        }
        return true;
    }

    @Override
    public JsonParser.Event next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.currentEvent = this.currentContext.getNextEvent();
        return this.currentEvent;
    }

    @Override
    public void close() {
        try {
            this.tokenizer.close();
        }
        catch (IOException e) {
            throw new JsonException(JsonMessages.PARSER_TOKENIZER_CLOSE_IO(), e);
        }
    }

    private JsonParsingException parsingException(JsonTokenizer.JsonToken token, String expectedTokens) {
        JsonLocation location = this.getLastCharLocation();
        return new JsonParsingException(JsonMessages.PARSER_INVALID_TOKEN(token, location, expectedTokens), location);
    }

    private final class ArrayContext
    extends Context {
        private boolean firstValue;

        private ArrayContext() {
            this.firstValue = true;
        }

        @Override
        public JsonParser.Event getNextEvent() {
            JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
            if (token == JsonTokenizer.JsonToken.EOF) {
                switch (JsonParserImpl.this.currentEvent) {
                    case START_ARRAY: {
                        throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
                    }
                }
                throw JsonParserImpl.this.parsingException(token, "[COMMA, CURLYCLOSE]");
            }
            if (token == JsonTokenizer.JsonToken.SQUARECLOSE) {
                JsonParserImpl.this.currentContext = JsonParserImpl.this.stack.pop();
                return JsonParser.Event.END_ARRAY;
            }
            if (this.firstValue) {
                this.firstValue = false;
            } else {
                if (token != JsonTokenizer.JsonToken.COMMA) {
                    throw JsonParserImpl.this.parsingException(token, "[COMMA]");
                }
                token = JsonParserImpl.this.tokenizer.nextToken();
            }
            if (token.isValue()) {
                return token.getEvent();
            }
            if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
                JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                JsonParserImpl.this.currentContext = new ObjectContext();
                return JsonParser.Event.START_OBJECT;
            }
            if (token == JsonTokenizer.JsonToken.SQUAREOPEN) {
                JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                JsonParserImpl.this.currentContext = new ArrayContext();
                return JsonParser.Event.START_ARRAY;
            }
            throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
        }

        @Override
        void skip() {
            JsonTokenizer.JsonToken token;
            int depth = 1;
            do {
                token = JsonParserImpl.this.tokenizer.nextToken();
                switch (token) {
                    case SQUARECLOSE: {
                        --depth;
                        break;
                    }
                    case SQUAREOPEN: {
                        ++depth;
                    }
                }
            } while (token != JsonTokenizer.JsonToken.SQUARECLOSE || depth != 0);
        }
    }

    private final class ObjectContext
    extends Context {
        private boolean firstValue;

        private ObjectContext() {
            this.firstValue = true;
        }

        @Override
        public JsonParser.Event getNextEvent() {
            JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
            if (token == JsonTokenizer.JsonToken.EOF) {
                switch (JsonParserImpl.this.currentEvent) {
                    case START_OBJECT: {
                        throw JsonParserImpl.this.parsingException(token, "[STRING, CURLYCLOSE]");
                    }
                    case KEY_NAME: {
                        throw JsonParserImpl.this.parsingException(token, "[COLON]");
                    }
                }
                throw JsonParserImpl.this.parsingException(token, "[COMMA, CURLYCLOSE]");
            }
            if (JsonParserImpl.this.currentEvent == JsonParser.Event.KEY_NAME) {
                if (token != JsonTokenizer.JsonToken.COLON) {
                    throw JsonParserImpl.this.parsingException(token, "[COLON]");
                }
                token = JsonParserImpl.this.tokenizer.nextToken();
                if (token.isValue()) {
                    return token.getEvent();
                }
                if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
                    JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                    JsonParserImpl.this.currentContext = new ObjectContext();
                    return JsonParser.Event.START_OBJECT;
                }
                if (token == JsonTokenizer.JsonToken.SQUAREOPEN) {
                    JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                    JsonParserImpl.this.currentContext = new ArrayContext();
                    return JsonParser.Event.START_ARRAY;
                }
                throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
            }
            if (token == JsonTokenizer.JsonToken.CURLYCLOSE) {
                JsonParserImpl.this.currentContext = JsonParserImpl.this.stack.pop();
                return JsonParser.Event.END_OBJECT;
            }
            if (this.firstValue) {
                this.firstValue = false;
            } else {
                if (token != JsonTokenizer.JsonToken.COMMA) {
                    throw JsonParserImpl.this.parsingException(token, "[COMMA]");
                }
                token = JsonParserImpl.this.tokenizer.nextToken();
            }
            if (token == JsonTokenizer.JsonToken.STRING) {
                return JsonParser.Event.KEY_NAME;
            }
            throw JsonParserImpl.this.parsingException(token, "[STRING]");
        }

        @Override
        void skip() {
            JsonTokenizer.JsonToken token;
            int depth = 1;
            do {
                token = JsonParserImpl.this.tokenizer.nextToken();
                switch (token) {
                    case CURLYCLOSE: {
                        --depth;
                        break;
                    }
                    case CURLYOPEN: {
                        ++depth;
                    }
                }
            } while (token != JsonTokenizer.JsonToken.CURLYCLOSE || depth != 0);
        }
    }

    private final class NoneContext
    extends Context {
        private NoneContext() {
        }

        @Override
        public JsonParser.Event getNextEvent() {
            JsonTokenizer.JsonToken token = JsonParserImpl.this.tokenizer.nextToken();
            if (token == JsonTokenizer.JsonToken.CURLYOPEN) {
                JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                JsonParserImpl.this.currentContext = new ObjectContext();
                return JsonParser.Event.START_OBJECT;
            }
            if (token == JsonTokenizer.JsonToken.SQUAREOPEN) {
                JsonParserImpl.this.stack.push(JsonParserImpl.this.currentContext);
                JsonParserImpl.this.currentContext = new ArrayContext();
                return JsonParser.Event.START_ARRAY;
            }
            if (token.isValue()) {
                return token.getEvent();
            }
            throw JsonParserImpl.this.parsingException(token, "[CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]");
        }

        @Override
        void skip() {
        }
    }

    private abstract class Context {
        Context next;

        private Context() {
        }

        abstract JsonParser.Event getNextEvent();

        abstract void skip();
    }

    private static final class Stack {
        private Context head;

        private Stack() {
        }

        private void push(Context context) {
            context.next = this.head;
            this.head = context;
        }

        private Context pop() {
            if (this.head == null) {
                throw new NoSuchElementException();
            }
            Context temp = this.head;
            this.head = this.head.next;
            return temp;
        }

        private Context peek() {
            return this.head;
        }

        private boolean isEmpty() {
            return this.head == null;
        }
    }
}

