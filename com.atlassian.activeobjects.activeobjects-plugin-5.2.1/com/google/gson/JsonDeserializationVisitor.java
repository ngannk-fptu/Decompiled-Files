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
import com.google.gson.JsonObjectDeserializationVisitor;
import com.google.gson.ObjectConstructor;
import com.google.gson.ObjectNavigator;
import com.google.gson.ObjectTypePair;
import com.google.gson.Pair;
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.internal.$Gson$Preconditions;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class JsonDeserializationVisitor<T>
implements ObjectNavigator.Visitor {
    protected final ObjectNavigator objectNavigator;
    protected final FieldNamingStrategy2 fieldNamingPolicy;
    protected final ObjectConstructor objectConstructor;
    protected final ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers;
    protected T target;
    protected final JsonElement json;
    protected final Type targetType;
    protected final JsonDeserializationContext context;
    protected boolean constructed;

    JsonDeserializationVisitor(JsonElement json, Type targetType, ObjectNavigator objectNavigator, FieldNamingStrategy2 fieldNamingPolicy, ObjectConstructor objectConstructor, ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers, JsonDeserializationContext context) {
        this.targetType = targetType;
        this.objectNavigator = objectNavigator;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.objectConstructor = objectConstructor;
        this.deserializers = deserializers;
        this.json = $Gson$Preconditions.checkNotNull(json);
        this.context = context;
        this.constructed = false;
    }

    public T getTarget() {
        if (!this.constructed) {
            this.target = this.constructTarget();
            this.constructed = true;
        }
        return this.target;
    }

    protected abstract T constructTarget();

    @Override
    public void start(ObjectTypePair node) {
    }

    @Override
    public void end(ObjectTypePair node) {
    }

    @Override
    public final boolean visitUsingCustomHandler(ObjectTypePair objTypePair) {
        Pair<JsonDeserializer<?>, ObjectTypePair> pair = objTypePair.getMatchingHandler(this.deserializers);
        if (pair == null) {
            return false;
        }
        Object value = this.invokeCustomDeserializer(this.json, pair);
        this.target = value;
        this.constructed = true;
        return true;
    }

    protected Object invokeCustomDeserializer(JsonElement element, Pair<JsonDeserializer<?>, ObjectTypePair> pair) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        Type objType = ((ObjectTypePair)pair.second).type;
        return ((JsonDeserializer)pair.first).deserialize(element, objType, this.context);
    }

    final Object visitChildAsObject(Type childType, JsonElement jsonChild) {
        JsonObjectDeserializationVisitor childVisitor = new JsonObjectDeserializationVisitor(jsonChild, childType, this.objectNavigator, this.fieldNamingPolicy, this.objectConstructor, this.deserializers, this.context);
        return this.visitChild(childType, childVisitor);
    }

    final Object visitChildAsArray(Type childType, JsonArray jsonChild) {
        JsonArrayDeserializationVisitor childVisitor = new JsonArrayDeserializationVisitor(jsonChild.getAsJsonArray(), childType, this.objectNavigator, this.fieldNamingPolicy, this.objectConstructor, this.deserializers, this.context);
        return this.visitChild(childType, childVisitor);
    }

    private Object visitChild(Type type, JsonDeserializationVisitor<?> childVisitor) {
        this.objectNavigator.accept(new ObjectTypePair(null, type, false), childVisitor);
        return childVisitor.getTarget();
    }
}

