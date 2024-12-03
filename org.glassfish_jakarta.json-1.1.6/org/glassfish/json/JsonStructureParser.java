/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;
import javax.json.stream.JsonParser;
import org.glassfish.json.JsonLocationImpl;
import org.glassfish.json.JsonMessages;

class JsonStructureParser
implements JsonParser {
    private Scope current;
    private JsonParser.Event state;
    private final Deque<Scope> scopeStack = new ArrayDeque<Scope>();

    JsonStructureParser(JsonArray array) {
        this.current = new ArrayScope(array);
    }

    JsonStructureParser(JsonObject object) {
        this.current = new ObjectScope(object);
    }

    @Override
    public String getString() {
        switch (this.state) {
            case KEY_NAME: {
                return ((ObjectScope)this.current).key;
            }
            case VALUE_STRING: {
                return ((JsonString)this.current.getJsonValue()).getString();
            }
            case VALUE_NUMBER: {
                return ((JsonNumber)this.current.getJsonValue()).toString();
            }
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETSTRING_ERR(this.state));
    }

    @Override
    public boolean isIntegralNumber() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber)this.current.getJsonValue()).isIntegral();
        }
        throw new IllegalStateException(JsonMessages.PARSER_ISINTEGRALNUMBER_ERR(this.state));
    }

    @Override
    public int getInt() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber)this.current.getJsonValue()).intValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETINT_ERR(this.state));
    }

    @Override
    public long getLong() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber)this.current.getJsonValue()).longValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETLONG_ERR(this.state));
    }

    @Override
    public BigDecimal getBigDecimal() {
        if (this.state == JsonParser.Event.VALUE_NUMBER) {
            return ((JsonNumber)this.current.getJsonValue()).bigDecimalValue();
        }
        throw new IllegalStateException(JsonMessages.PARSER_GETBIGDECIMAL_ERR(this.state));
    }

    @Override
    public JsonLocation getLocation() {
        return JsonLocationImpl.UNKNOWN;
    }

    @Override
    public boolean hasNext() {
        return this.state != JsonParser.Event.END_OBJECT && this.state != JsonParser.Event.END_ARRAY || !this.scopeStack.isEmpty();
    }

    @Override
    public JsonParser.Event next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.transition();
        return this.state;
    }

    private void transition() {
        if (this.state == null) {
            this.state = this.current instanceof ArrayScope ? JsonParser.Event.START_ARRAY : JsonParser.Event.START_OBJECT;
        } else {
            if (this.state == JsonParser.Event.END_OBJECT || this.state == JsonParser.Event.END_ARRAY) {
                this.current = this.scopeStack.pop();
            }
            if (this.current instanceof ArrayScope) {
                if (this.current.hasNext()) {
                    this.current.next();
                    this.state = JsonStructureParser.getState(this.current.getJsonValue());
                    if (this.state == JsonParser.Event.START_ARRAY || this.state == JsonParser.Event.START_OBJECT) {
                        this.scopeStack.push(this.current);
                        this.current = Scope.createScope(this.current.getJsonValue());
                    }
                } else {
                    this.state = JsonParser.Event.END_ARRAY;
                }
            } else if (this.state == JsonParser.Event.KEY_NAME) {
                this.state = JsonStructureParser.getState(this.current.getJsonValue());
                if (this.state == JsonParser.Event.START_ARRAY || this.state == JsonParser.Event.START_OBJECT) {
                    this.scopeStack.push(this.current);
                    this.current = Scope.createScope(this.current.getJsonValue());
                }
            } else if (this.current.hasNext()) {
                this.current.next();
                this.state = JsonParser.Event.KEY_NAME;
            } else {
                this.state = JsonParser.Event.END_OBJECT;
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void skipObject() {
        if (this.current instanceof ObjectScope) {
            int depth = 1;
            do {
                if (this.state == JsonParser.Event.KEY_NAME) {
                    this.state = JsonStructureParser.getState(this.current.getJsonValue());
                    switch (this.state) {
                        case START_OBJECT: {
                            ++depth;
                            break;
                        }
                        case END_OBJECT: {
                            --depth;
                            break;
                        }
                    }
                    continue;
                }
                if (this.current.hasNext()) {
                    this.current.next();
                    this.state = JsonParser.Event.KEY_NAME;
                    continue;
                }
                this.state = JsonParser.Event.END_OBJECT;
                --depth;
            } while (this.state != JsonParser.Event.END_OBJECT && depth > 0);
        }
    }

    @Override
    public void skipArray() {
        if (this.current instanceof ArrayScope) {
            int depth = 1;
            do {
                if (this.current.hasNext()) {
                    this.current.next();
                    this.state = JsonStructureParser.getState(this.current.getJsonValue());
                    switch (this.state) {
                        case START_ARRAY: {
                            ++depth;
                            break;
                        }
                        case END_ARRAY: {
                            --depth;
                            break;
                        }
                    }
                    continue;
                }
                this.state = JsonParser.Event.END_ARRAY;
                --depth;
            } while (this.state != JsonParser.Event.END_ARRAY || depth != 0);
        }
    }

    private static JsonParser.Event getState(JsonValue value) {
        switch (value.getValueType()) {
            case ARRAY: {
                return JsonParser.Event.START_ARRAY;
            }
            case OBJECT: {
                return JsonParser.Event.START_OBJECT;
            }
            case STRING: {
                return JsonParser.Event.VALUE_STRING;
            }
            case NUMBER: {
                return JsonParser.Event.VALUE_NUMBER;
            }
            case TRUE: {
                return JsonParser.Event.VALUE_TRUE;
            }
            case FALSE: {
                return JsonParser.Event.VALUE_FALSE;
            }
            case NULL: {
                return JsonParser.Event.VALUE_NULL;
            }
        }
        throw new JsonException(JsonMessages.PARSER_STATE_ERR(value.getValueType()));
    }

    private static class ObjectScope
    extends Scope {
        private final Iterator<Map.Entry<String, JsonValue>> it;
        private JsonValue value;
        private String key;

        ObjectScope(JsonObject object) {
            this.it = object.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }

        public Map.Entry<String, JsonValue> next() {
            Map.Entry<String, JsonValue> next = this.it.next();
            this.key = next.getKey();
            this.value = next.getValue();
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        JsonValue getJsonValue() {
            return this.value;
        }
    }

    private static class ArrayScope
    extends Scope {
        private final Iterator<JsonValue> it;
        private JsonValue value;

        ArrayScope(JsonArray array) {
            this.it = array.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }

        public JsonValue next() {
            this.value = this.it.next();
            return this.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        JsonValue getJsonValue() {
            return this.value;
        }
    }

    private static abstract class Scope
    implements Iterator {
        private Scope() {
        }

        abstract JsonValue getJsonValue();

        static Scope createScope(JsonValue value) {
            if (value instanceof JsonArray) {
                return new ArrayScope((JsonArray)value);
            }
            if (value instanceof JsonObject) {
                return new ObjectScope((JsonObject)value);
            }
            throw new JsonException(JsonMessages.PARSER_SCOPE_ERR(value));
        }
    }
}

