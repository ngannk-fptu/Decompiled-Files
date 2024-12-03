/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializationContextDefault;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializationContextDefault;
import com.google.gson.JsonSerializer;
import com.google.gson.ObjectConstructor;
import java.lang.reflect.Type;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class BaseMapTypeAdapter
implements JsonSerializer<Map<?, ?>>,
JsonDeserializer<Map<?, ?>> {
    BaseMapTypeAdapter() {
    }

    protected static final JsonElement serialize(JsonSerializationContext context, Object src, Type srcType) {
        JsonSerializationContextDefault contextImpl = (JsonSerializationContextDefault)context;
        return contextImpl.serialize(src, srcType, false);
    }

    protected static final Map<Object, Object> constructMapType(Type mapType, JsonDeserializationContext context) {
        JsonDeserializationContextDefault contextImpl = (JsonDeserializationContextDefault)context;
        ObjectConstructor objectConstructor = contextImpl.getObjectConstructor();
        return (Map)objectConstructor.construct(mapType);
    }
}

