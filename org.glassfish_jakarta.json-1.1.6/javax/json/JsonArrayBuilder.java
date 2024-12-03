/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public interface JsonArrayBuilder {
    public JsonArrayBuilder add(JsonValue var1);

    public JsonArrayBuilder add(String var1);

    public JsonArrayBuilder add(BigDecimal var1);

    public JsonArrayBuilder add(BigInteger var1);

    public JsonArrayBuilder add(int var1);

    public JsonArrayBuilder add(long var1);

    public JsonArrayBuilder add(double var1);

    public JsonArrayBuilder add(boolean var1);

    public JsonArrayBuilder addNull();

    public JsonArrayBuilder add(JsonObjectBuilder var1);

    public JsonArrayBuilder add(JsonArrayBuilder var1);

    default public JsonArrayBuilder addAll(JsonArrayBuilder builder) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, JsonValue value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, String value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, BigInteger value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, int value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, long value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, double value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, boolean value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder addNull(int index) {
        return this.add(index, JsonValue.NULL);
    }

    default public JsonArrayBuilder add(int index, JsonObjectBuilder builder) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder add(int index, JsonArrayBuilder builder) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, JsonValue value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, String value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, BigInteger value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, int value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, long value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, double value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, boolean value) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder setNull(int index) {
        return this.set(index, JsonValue.NULL);
    }

    default public JsonArrayBuilder set(int index, JsonObjectBuilder builder) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder set(int index, JsonArrayBuilder builder) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder remove(int index) {
        throw new UnsupportedOperationException();
    }

    public JsonArray build();
}

