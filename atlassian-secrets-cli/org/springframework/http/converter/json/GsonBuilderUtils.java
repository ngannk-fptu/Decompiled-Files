/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.springframework.util.Base64Utils;

public abstract class GsonBuilderUtils {
    public static GsonBuilder gsonBuilderWithBase64EncodedByteArrays() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(byte[].class, new Base64TypeAdapter());
        return builder;
    }

    private static class Base64TypeAdapter
    implements JsonSerializer<byte[]>,
    JsonDeserializer<byte[]> {
        private Base64TypeAdapter() {
        }

        @Override
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64Utils.encodeToString(src));
        }

        @Override
        public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext cxt) {
            return Base64Utils.decodeFromString(json.getAsString());
        }
    }
}

