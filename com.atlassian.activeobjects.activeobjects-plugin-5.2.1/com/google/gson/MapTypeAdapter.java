/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.BaseMapTypeAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

final class MapTypeAdapter
extends BaseMapTypeAdapter {
    MapTypeAdapter() {
    }

    public JsonElement serialize(Map src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject map = new JsonObject();
        Class<?> childGenericType = null;
        if (typeOfSrc instanceof ParameterizedType) {
            Class<?> rawTypeOfSrc = $Gson$Types.getRawType(typeOfSrc);
            childGenericType = $Gson$Types.getMapKeyAndValueTypes(typeOfSrc, rawTypeOfSrc)[1];
        }
        for (Map.Entry entry : src.entrySet()) {
            JsonElement valueElement;
            Object value = entry.getValue();
            if (value == null) {
                valueElement = JsonNull.createJsonNull();
            } else {
                Class<?> childType = childGenericType == null ? value.getClass() : childGenericType;
                valueElement = MapTypeAdapter.serialize(context, value, childType);
            }
            map.add(String.valueOf(entry.getKey()), valueElement);
        }
        return map;
    }

    public Map deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<Object, Object> map = MapTypeAdapter.constructMapType(typeOfT, context);
        Type[] keyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes(typeOfT, $Gson$Types.getRawType(typeOfT));
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            Object key = context.deserialize(new JsonPrimitive(entry.getKey()), keyAndValueTypes[0]);
            Object value = context.deserialize(entry.getValue(), keyAndValueTypes[1]);
            map.put(key, value);
        }
        return map;
    }

    public String toString() {
        return MapTypeAdapter.class.getSimpleName();
    }
}

