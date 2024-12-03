/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy2;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializationVisitor;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.ObjectConstructor;
import com.google.gson.ObjectNavigator;
import com.google.gson.ObjectTypePair;
import com.google.gson.Pair;
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.Primitives;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class JsonObjectDeserializationVisitor<T>
extends JsonDeserializationVisitor<T> {
    JsonObjectDeserializationVisitor(JsonElement json, Type type, ObjectNavigator objectNavigator, FieldNamingStrategy2 fieldNamingPolicy, ObjectConstructor objectConstructor, ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers, JsonDeserializationContext context) {
        super(json, type, objectNavigator, fieldNamingPolicy, objectConstructor, deserializers, context);
    }

    @Override
    protected T constructTarget() {
        return this.objectConstructor.construct(this.targetType);
    }

    @Override
    public void startVisitingObject(Object node) {
    }

    @Override
    public void visitArray(Object array, Type componentType) {
        throw new JsonParseException("Expecting object but found array: " + array);
    }

    @Override
    public void visitObjectField(FieldAttributes f, Type typeOfF, Object obj) {
        try {
            String fName;
            if (!this.json.isJsonObject()) {
                throw new JsonParseException("Expecting object found: " + this.json);
            }
            JsonObject jsonObject = this.json.getAsJsonObject();
            JsonElement jsonChild = jsonObject.get(fName = this.getFieldName(f));
            if (jsonChild != null) {
                Object child = this.visitChildAsObject(typeOfF, jsonChild);
                f.set(obj, child);
            } else {
                f.set(obj, null);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visitArrayField(FieldAttributes f, Type typeOfF, Object obj) {
        try {
            String fName;
            if (!this.json.isJsonObject()) {
                throw new JsonParseException("Expecting object found: " + this.json);
            }
            JsonObject jsonObject = this.json.getAsJsonObject();
            JsonArray jsonChild = (JsonArray)jsonObject.get(fName = this.getFieldName(f));
            if (jsonChild != null) {
                Object array = this.visitChildAsArray(typeOfF, jsonChild);
                f.set(obj, array);
            } else {
                f.set(obj, null);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFieldName(FieldAttributes f) {
        return this.fieldNamingPolicy.translateName(f);
    }

    @Override
    public boolean visitFieldUsingCustomHandler(FieldAttributes f, Type declaredTypeOfField, Object parent) {
        try {
            String fName = this.getFieldName(f);
            if (!this.json.isJsonObject()) {
                throw new JsonParseException("Expecting object found: " + this.json);
            }
            JsonElement child = this.json.getAsJsonObject().get(fName);
            boolean isPrimitive = Primitives.isPrimitive(declaredTypeOfField);
            if (child == null) {
                return true;
            }
            if (child.isJsonNull()) {
                if (!isPrimitive) {
                    f.set(parent, null);
                }
                return true;
            }
            ObjectTypePair objTypePair = new ObjectTypePair(null, declaredTypeOfField, false);
            Pair<JsonDeserializer<?>, ObjectTypePair> pair = objTypePair.getMatchingHandler(this.deserializers);
            if (pair == null) {
                return false;
            }
            Object value = this.invokeCustomDeserializer(child, pair);
            if (value != null || !isPrimitive) {
                f.set(parent, value);
            }
            return true;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void visitPrimitive(Object primitive) {
        if (!this.json.isJsonPrimitive()) {
            throw new JsonParseException("Type information is unavailable, and the target object is not a primitive: " + this.json);
        }
        JsonPrimitive prim = this.json.getAsJsonPrimitive();
        this.target = prim.getAsObject();
    }
}

