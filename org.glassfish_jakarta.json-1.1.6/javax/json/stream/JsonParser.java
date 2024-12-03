/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonLocation;

public interface JsonParser
extends Closeable {
    public boolean hasNext();

    public Event next();

    public String getString();

    public boolean isIntegralNumber();

    public int getInt();

    public long getLong();

    public BigDecimal getBigDecimal();

    public JsonLocation getLocation();

    default public JsonObject getObject() {
        throw new UnsupportedOperationException();
    }

    default public JsonValue getValue() {
        throw new UnsupportedOperationException();
    }

    default public JsonArray getArray() {
        throw new UnsupportedOperationException();
    }

    default public Stream<JsonValue> getArrayStream() {
        throw new UnsupportedOperationException();
    }

    default public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        throw new UnsupportedOperationException();
    }

    default public Stream<JsonValue> getValueStream() {
        throw new UnsupportedOperationException();
    }

    default public void skipArray() {
        throw new UnsupportedOperationException();
    }

    default public void skipObject() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close();

    public static enum Event {
        START_ARRAY,
        START_OBJECT,
        KEY_NAME,
        VALUE_STRING,
        VALUE_NUMBER,
        VALUE_TRUE,
        VALUE_FALSE,
        VALUE_NULL,
        END_OBJECT,
        END_ARRAY;

    }
}

