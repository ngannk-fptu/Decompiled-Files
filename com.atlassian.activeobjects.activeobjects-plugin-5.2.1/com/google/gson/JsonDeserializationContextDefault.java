/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.FieldNamingStrategy2;
import com.google.gson.JsonArray;
import com.google.gson.JsonArrayDeserializationVisitor;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonObjectDeserializationVisitor;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.MappedObjectConstructor;
import com.google.gson.ObjectConstructor;
import com.google.gson.ObjectNavigator;
import com.google.gson.ObjectTypePair;
import com.google.gson.ParameterizedTypeHandlerMap;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class JsonDeserializationContextDefault
implements JsonDeserializationContext {
    private final ObjectNavigator objectNavigator;
    private final FieldNamingStrategy2 fieldNamingPolicy;
    private final ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers;
    private final MappedObjectConstructor objectConstructor;

    JsonDeserializationContextDefault(ObjectNavigator objectNavigator, FieldNamingStrategy2 fieldNamingPolicy, ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers, MappedObjectConstructor objectConstructor) {
        this.objectNavigator = objectNavigator;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.deserializers = deserializers;
        this.objectConstructor = objectConstructor;
    }

    ObjectConstructor getObjectConstructor() {
        return this.objectConstructor;
    }

    @Override
    public <T> T deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        if (json.isJsonArray()) {
            return this.fromJsonArray(typeOfT, json.getAsJsonArray(), this);
        }
        if (json.isJsonObject()) {
            return this.fromJsonObject(typeOfT, json.getAsJsonObject(), this);
        }
        if (json.isJsonPrimitive()) {
            return this.fromJsonPrimitive(typeOfT, json.getAsJsonPrimitive(), this);
        }
        throw new JsonParseException("Failed parsing JSON source: " + json + " to Json");
    }

    private <T> T fromJsonArray(Type arrayType, JsonArray jsonArray, JsonDeserializationContext context) throws JsonParseException {
        JsonArrayDeserializationVisitor visitor = new JsonArrayDeserializationVisitor(jsonArray, arrayType, this.objectNavigator, this.fieldNamingPolicy, (ObjectConstructor)this.objectConstructor, this.deserializers, context);
        this.objectNavigator.accept(new ObjectTypePair(null, arrayType, true), visitor);
        return visitor.getTarget();
    }

    private <T> T fromJsonObject(Type typeOfT, JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        JsonObjectDeserializationVisitor visitor = new JsonObjectDeserializationVisitor(jsonObject, typeOfT, this.objectNavigator, this.fieldNamingPolicy, this.objectConstructor, this.deserializers, context);
        this.objectNavigator.accept(new ObjectTypePair(null, typeOfT, true), visitor);
        return visitor.getTarget();
    }

    private <T> T fromJsonPrimitive(Type typeOfT, JsonPrimitive json, JsonDeserializationContext context) throws JsonParseException {
        JsonObjectDeserializationVisitor visitor = new JsonObjectDeserializationVisitor(json, typeOfT, this.objectNavigator, this.fieldNamingPolicy, this.objectConstructor, this.deserializers, context);
        this.objectNavigator.accept(new ObjectTypePair(json.getAsObject(), typeOfT, true), visitor);
        Object target = visitor.getTarget();
        return target;
    }
}

