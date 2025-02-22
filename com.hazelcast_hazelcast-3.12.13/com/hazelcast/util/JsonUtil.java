/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static int getInt(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asInt();
    }

    public static int getInt(JsonObject object, String field, int defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asInt();
    }

    public static long getLong(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asLong();
    }

    public static long getLong(JsonObject object, String field, long defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asLong();
    }

    public static double getDouble(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asDouble();
    }

    public static double getDouble(JsonObject object, String field, double defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asDouble();
    }

    public static float getFloat(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asFloat();
    }

    public static float getFloat(JsonObject object, String field, float defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asFloat();
    }

    public static String getString(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asString();
    }

    public static String getString(JsonObject object, String field, String defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asString();
    }

    public static boolean getBoolean(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asBoolean();
    }

    public static boolean getBoolean(JsonObject object, String field, boolean defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asBoolean();
    }

    public static JsonArray getArray(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asArray();
    }

    public static JsonArray getArray(JsonObject object, String field, JsonArray defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asArray();
    }

    public static JsonObject getObject(JsonObject object, String field) {
        JsonValue value = object.get(field);
        JsonUtil.throwExceptionIfNull(value, field);
        return value.asObject();
    }

    public static JsonObject getObject(JsonObject object, String field, JsonObject defaultValue) {
        JsonValue value = object.get(field);
        if (value == null || value.isNull()) {
            return defaultValue;
        }
        return value.asObject();
    }

    public static Map<String, Comparable> fromJsonObject(JsonObject object) {
        if (object == null || object.isNull() || object.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Comparable> map = MapUtil.createHashMap(object.size());
        for (String propertyName : object.names()) {
            map.put(propertyName, (Comparable)((Object)object.get(propertyName).asString()));
        }
        return map;
    }

    public static JsonObject toJsonObject(Map<String, ?> map) {
        JsonObject properties = new JsonObject();
        for (Map.Entry<String, ?> property : map.entrySet()) {
            properties.add(property.getKey(), Json.value(property.getValue().toString()));
        }
        return properties;
    }

    private static void throwExceptionIfNull(JsonValue value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("No field found: " + field);
        }
    }

    public static String toJson(Object value) {
        if (value instanceof String) {
            return '\"' + (String)value + '\"';
        }
        if (value instanceof Collection) {
            return "[" + JsonUtil.toJsonCollection((Collection)value) + "]";
        }
        if (value instanceof JsonValue) {
            return value.toString();
        }
        throw new IllegalArgumentException("Unable to convert " + value + " to JSON");
    }

    private static String toJsonCollection(Collection objects) {
        Iterator iterator = objects.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return JsonUtil.toJson(first);
        }
        StringBuilder buf = new StringBuilder();
        if (first != null) {
            buf.append(JsonUtil.toJson(first));
        }
        while (iterator.hasNext()) {
            buf.append(',');
            Object obj = iterator.next();
            if (obj == null) continue;
            buf.append(JsonUtil.toJson(obj));
        }
        return buf.toString();
    }
}

