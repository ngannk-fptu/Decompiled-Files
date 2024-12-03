/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.BaseMapTypeAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class MapAsArrayTypeAdapter
extends BaseMapTypeAdapter
implements JsonSerializer<Map<?, ?>>,
JsonDeserializer<Map<?, ?>> {
    MapAsArrayTypeAdapter() {
    }

    @Override
    public Map<?, ?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<Object, Object> result = MapAsArrayTypeAdapter.constructMapType(typeOfT, context);
        Type[] keyAndValueType = this.typeToTypeArguments(typeOfT);
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            for (int i = 0; i < array.size(); ++i) {
                JsonArray entryArray = array.get(i).getAsJsonArray();
                Object k = context.deserialize(entryArray.get(0), keyAndValueType[0]);
                Object v = context.deserialize(entryArray.get(1), keyAndValueType[1]);
                result.put(k, v);
            }
            this.checkSize(array, array.size(), result, result.size());
        } else {
            JsonObject object = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                Object k = context.deserialize(new JsonPrimitive(entry.getKey()), keyAndValueType[0]);
                Object v = context.deserialize(entry.getValue(), keyAndValueType[1]);
                result.put(k, v);
            }
            this.checkSize(object, object.entrySet().size(), result, result.size());
        }
        return result;
    }

    @Override
    public JsonElement serialize(Map<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement result;
        Type[] keyAndValueType = this.typeToTypeArguments(typeOfSrc);
        boolean serializeAsArray = false;
        ArrayList<JsonElement> keysAndValues = new ArrayList<JsonElement>();
        for (Map.Entry<?, ?> entry : src.entrySet()) {
            JsonElement key = MapAsArrayTypeAdapter.serialize(context, entry.getKey(), keyAndValueType[0]);
            serializeAsArray |= key.isJsonObject() || key.isJsonArray();
            keysAndValues.add(key);
            keysAndValues.add(MapAsArrayTypeAdapter.serialize(context, entry.getValue(), keyAndValueType[1]));
        }
        if (serializeAsArray) {
            result = new JsonArray();
            for (int i = 0; i < keysAndValues.size(); i += 2) {
                JsonArray entryArray = new JsonArray();
                entryArray.add((JsonElement)keysAndValues.get(i));
                entryArray.add((JsonElement)keysAndValues.get(i + 1));
                ((JsonArray)result).add(entryArray);
            }
            return result;
        }
        result = new JsonObject();
        for (int i = 0; i < keysAndValues.size(); i += 2) {
            ((JsonObject)result).add(((JsonElement)keysAndValues.get(i)).getAsString(), (JsonElement)keysAndValues.get(i + 1));
        }
        this.checkSize(src, src.size(), result, ((JsonObject)result).entrySet().size());
        return result;
    }

    private Type[] typeToTypeArguments(Type typeOfT) {
        if (typeOfT instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType)typeOfT).getActualTypeArguments();
            if (typeArguments.length != 2) {
                throw new IllegalArgumentException("MapAsArrayTypeAdapter cannot handle " + typeOfT);
            }
            return typeArguments;
        }
        return new Type[]{Object.class, Object.class};
    }

    private void checkSize(Object input, int inputSize, Object output, int outputSize) {
        if (inputSize != outputSize) {
            throw new JsonSyntaxException("Input size " + inputSize + " != output size " + outputSize + " for input " + input + " and output " + output);
        }
    }
}

