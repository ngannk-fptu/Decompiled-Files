/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.FieldNamingStrategy2;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializationVisitor;
import com.google.gson.JsonSerializer;
import com.google.gson.MemoryRefStack;
import com.google.gson.ObjectNavigator;
import com.google.gson.ObjectTypePair;
import com.google.gson.ParameterizedTypeHandlerMap;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class JsonSerializationContextDefault
implements JsonSerializationContext {
    private final ObjectNavigator objectNavigator;
    private final FieldNamingStrategy2 fieldNamingPolicy;
    private final ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers;
    private final boolean serializeNulls;
    private final MemoryRefStack ancestors;

    JsonSerializationContextDefault(ObjectNavigator objectNavigator, FieldNamingStrategy2 fieldNamingPolicy, boolean serializeNulls, ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers) {
        this.objectNavigator = objectNavigator;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.serializeNulls = serializeNulls;
        this.serializers = serializers;
        this.ancestors = new MemoryRefStack();
    }

    @Override
    public JsonElement serialize(Object src) {
        if (src == null) {
            return JsonNull.createJsonNull();
        }
        return this.serialize(src, src.getClass(), false);
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc) {
        return this.serialize(src, typeOfSrc, true);
    }

    JsonElement serialize(Object src, Type typeOfSrc, boolean preserveType) {
        if (src == null) {
            return JsonNull.createJsonNull();
        }
        JsonSerializationVisitor visitor = new JsonSerializationVisitor(this.objectNavigator, this.fieldNamingPolicy, this.serializeNulls, this.serializers, this, this.ancestors);
        this.objectNavigator.accept(new ObjectTypePair(src, typeOfSrc, preserveType), visitor);
        return visitor.getJsonElement();
    }
}

