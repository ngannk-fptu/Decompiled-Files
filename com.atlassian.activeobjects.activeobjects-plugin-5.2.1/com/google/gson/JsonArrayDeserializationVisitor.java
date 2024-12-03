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
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class JsonArrayDeserializationVisitor<T>
extends JsonDeserializationVisitor<T> {
    JsonArrayDeserializationVisitor(JsonArray jsonArray, Type arrayType, ObjectNavigator objectNavigator, FieldNamingStrategy2 fieldNamingPolicy, ObjectConstructor objectConstructor, ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers, JsonDeserializationContext context) {
        super(jsonArray, arrayType, objectNavigator, fieldNamingPolicy, objectConstructor, deserializers, context);
    }

    @Override
    protected T constructTarget() {
        if (!this.json.isJsonArray()) {
            throw new JsonParseException("Expecting array found: " + this.json);
        }
        JsonArray jsonArray = this.json.getAsJsonArray();
        if ($Gson$Types.isArray(this.targetType)) {
            return (T)this.objectConstructor.constructArray($Gson$Types.getArrayComponentType(this.targetType), jsonArray.size());
        }
        return this.objectConstructor.construct($Gson$Types.getRawType(this.targetType));
    }

    @Override
    public void visitArray(Object array, Type arrayType) {
        if (!this.json.isJsonArray()) {
            throw new JsonParseException("Expecting array found: " + this.json);
        }
        JsonArray jsonArray = this.json.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); ++i) {
            Object child;
            JsonElement jsonChild = jsonArray.get(i);
            if (jsonChild == null || jsonChild.isJsonNull()) {
                child = null;
            } else if (jsonChild instanceof JsonObject) {
                child = this.visitChildAsObject($Gson$Types.getArrayComponentType(arrayType), jsonChild);
            } else if (jsonChild instanceof JsonArray) {
                child = this.visitChildAsArray($Gson$Types.getArrayComponentType(arrayType), jsonChild.getAsJsonArray());
            } else if (jsonChild instanceof JsonPrimitive) {
                child = this.visitChildAsObject($Gson$Types.getArrayComponentType(arrayType), jsonChild.getAsJsonPrimitive());
            } else {
                throw new IllegalStateException();
            }
            Array.set(array, i, child);
        }
    }

    @Override
    public void startVisitingObject(Object node) {
        throw new JsonParseException("Expecting array but found object: " + node);
    }

    @Override
    public void visitArrayField(FieldAttributes f, Type typeOfF, Object obj) {
        throw new JsonParseException("Expecting array but found array field " + f.getName() + ": " + obj);
    }

    @Override
    public void visitObjectField(FieldAttributes f, Type typeOfF, Object obj) {
        throw new JsonParseException("Expecting array but found object field " + f.getName() + ": " + obj);
    }

    @Override
    public boolean visitFieldUsingCustomHandler(FieldAttributes f, Type actualTypeOfField, Object parent) {
        throw new JsonParseException("Expecting array but found field " + f.getName() + ": " + parent);
    }

    @Override
    public void visitPrimitive(Object primitive) {
        throw new JsonParseException("Type information is unavailable, and the target is not a primitive: " + this.json);
    }
}

