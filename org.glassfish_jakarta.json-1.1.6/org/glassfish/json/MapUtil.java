/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import javax.json.JsonValue;
import org.glassfish.json.JsonArrayBuilderImpl;
import org.glassfish.json.JsonNumberImpl;
import org.glassfish.json.JsonObjectBuilderImpl;
import org.glassfish.json.JsonStringImpl;
import org.glassfish.json.api.BufferPool;

public final class MapUtil {
    private MapUtil() {
    }

    static JsonValue handle(Object value, BufferPool bufferPool) {
        if (value == null) {
            return JsonValue.NULL;
        }
        if (value instanceof BigDecimal) {
            return JsonNumberImpl.getJsonNumber((BigDecimal)value);
        }
        if (value instanceof BigInteger) {
            return JsonNumberImpl.getJsonNumber((BigInteger)value);
        }
        if (value instanceof Boolean) {
            Boolean b = (Boolean)value;
            return b != false ? JsonValue.TRUE : JsonValue.FALSE;
        }
        if (value instanceof Double) {
            return JsonNumberImpl.getJsonNumber((Double)value);
        }
        if (value instanceof Integer) {
            return JsonNumberImpl.getJsonNumber((Integer)value);
        }
        if (value instanceof Long) {
            return JsonNumberImpl.getJsonNumber((Long)value);
        }
        if (value instanceof String) {
            return new JsonStringImpl((String)value);
        }
        if (value instanceof Collection) {
            Collection collection = (Collection)value;
            JsonArrayBuilderImpl jsonArrayBuilder = new JsonArrayBuilderImpl(collection, bufferPool);
            return jsonArrayBuilder.build();
        }
        if (value instanceof Map) {
            JsonObjectBuilderImpl object = new JsonObjectBuilderImpl((Map)value, bufferPool);
            return object.build();
        }
        throw new IllegalArgumentException(String.format("Type %s is not supported.", value.getClass()));
    }
}

