/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.CircularReferenceException;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy2;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.MemoryRefStack;
import com.google.gson.ObjectNavigator;
import com.google.gson.ObjectTypePair;
import com.google.gson.Pair;
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class JsonSerializationVisitor
implements ObjectNavigator.Visitor {
    private final ObjectNavigator objectNavigator;
    private final FieldNamingStrategy2 fieldNamingPolicy;
    private final ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers;
    private final boolean serializeNulls;
    private final JsonSerializationContext context;
    private final MemoryRefStack ancestors;
    private JsonElement root;

    JsonSerializationVisitor(ObjectNavigator objectNavigator, FieldNamingStrategy2 fieldNamingPolicy, boolean serializeNulls, ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers, JsonSerializationContext context, MemoryRefStack ancestors) {
        this.objectNavigator = objectNavigator;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.serializeNulls = serializeNulls;
        this.serializers = serializers;
        this.context = context;
        this.ancestors = ancestors;
    }

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public void start(ObjectTypePair node) {
        if (node == null) {
            return;
        }
        if (this.ancestors.contains(node)) {
            throw new CircularReferenceException(node);
        }
        this.ancestors.push(node);
    }

    @Override
    public void end(ObjectTypePair node) {
        if (node != null) {
            this.ancestors.pop();
        }
    }

    @Override
    public void startVisitingObject(Object node) {
        this.assignToRoot(new JsonObject());
    }

    @Override
    public void visitArray(Object array, Type arrayType) {
        this.assignToRoot(new JsonArray());
        int length = Array.getLength(array);
        Type componentType = $Gson$Types.getArrayComponentType(arrayType);
        for (int i = 0; i < length; ++i) {
            Object child = Array.get(array, i);
            this.addAsArrayElement(new ObjectTypePair(child, componentType, false));
        }
    }

    @Override
    public void visitArrayField(FieldAttributes f, Type typeOfF, Object obj) {
        try {
            if (this.isFieldNull(f, obj)) {
                if (this.serializeNulls) {
                    this.addChildAsElement(f, JsonNull.createJsonNull());
                }
            } else {
                Object array = this.getFieldValue(f, obj);
                this.addAsChildOfObject(f, new ObjectTypePair(array, typeOfF, false));
            }
        }
        catch (CircularReferenceException e) {
            throw e.createDetailedException(f);
        }
    }

    @Override
    public void visitObjectField(FieldAttributes f, Type typeOfF, Object obj) {
        try {
            if (this.isFieldNull(f, obj)) {
                if (this.serializeNulls) {
                    this.addChildAsElement(f, JsonNull.createJsonNull());
                }
            } else {
                Object fieldValue = this.getFieldValue(f, obj);
                this.addAsChildOfObject(f, new ObjectTypePair(fieldValue, typeOfF, false));
            }
        }
        catch (CircularReferenceException e) {
            throw e.createDetailedException(f);
        }
    }

    @Override
    public void visitPrimitive(Object obj) {
        JsonElement json = obj == null ? JsonNull.createJsonNull() : new JsonPrimitive(obj);
        this.assignToRoot(json);
    }

    private void addAsChildOfObject(FieldAttributes f, ObjectTypePair fieldValuePair) {
        JsonElement childElement = this.getJsonElementForChild(fieldValuePair);
        this.addChildAsElement(f, childElement);
    }

    private void addChildAsElement(FieldAttributes f, JsonElement childElement) {
        this.root.getAsJsonObject().add(this.fieldNamingPolicy.translateName(f), childElement);
    }

    private void addAsArrayElement(ObjectTypePair elementTypePair) {
        if (elementTypePair.getObject() == null) {
            this.root.getAsJsonArray().add(JsonNull.createJsonNull());
        } else {
            JsonElement childElement = this.getJsonElementForChild(elementTypePair);
            this.root.getAsJsonArray().add(childElement);
        }
    }

    private JsonElement getJsonElementForChild(ObjectTypePair fieldValueTypePair) {
        JsonSerializationVisitor childVisitor = new JsonSerializationVisitor(this.objectNavigator, this.fieldNamingPolicy, this.serializeNulls, this.serializers, this.context, this.ancestors);
        this.objectNavigator.accept(fieldValueTypePair, childVisitor);
        return childVisitor.getJsonElement();
    }

    @Override
    public boolean visitUsingCustomHandler(ObjectTypePair objTypePair) {
        try {
            Object obj = objTypePair.getObject();
            if (obj == null) {
                if (this.serializeNulls) {
                    this.assignToRoot(JsonNull.createJsonNull());
                }
                return true;
            }
            JsonElement element = this.findAndInvokeCustomSerializer(objTypePair);
            if (element != null) {
                this.assignToRoot(element);
                return true;
            }
            return false;
        }
        catch (CircularReferenceException e) {
            throw e.createDetailedException(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonElement findAndInvokeCustomSerializer(ObjectTypePair objTypePair) {
        Pair<JsonSerializer<?>, ObjectTypePair> pair = objTypePair.getMatchingHandler(this.serializers);
        if (pair == null) {
            return null;
        }
        JsonSerializer serializer = (JsonSerializer)pair.first;
        objTypePair = (ObjectTypePair)pair.second;
        this.start(objTypePair);
        try {
            JsonElement element = serializer.serialize(objTypePair.getObject(), objTypePair.getType(), this.context);
            JsonElement jsonElement = element == null ? JsonNull.createJsonNull() : element;
            return jsonElement;
        }
        finally {
            this.end(objTypePair);
        }
    }

    @Override
    public boolean visitFieldUsingCustomHandler(FieldAttributes f, Type declaredTypeOfField, Object parent) {
        try {
            $Gson$Preconditions.checkState(this.root.isJsonObject());
            Object obj = f.get(parent);
            if (obj == null) {
                if (this.serializeNulls) {
                    this.addChildAsElement(f, JsonNull.createJsonNull());
                }
                return true;
            }
            ObjectTypePair objTypePair = new ObjectTypePair(obj, declaredTypeOfField, false);
            JsonElement child = this.findAndInvokeCustomSerializer(objTypePair);
            if (child != null) {
                this.addChildAsElement(f, child);
                return true;
            }
            return false;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
        catch (CircularReferenceException e) {
            throw e.createDetailedException(f);
        }
    }

    private void assignToRoot(JsonElement newRoot) {
        this.root = $Gson$Preconditions.checkNotNull(newRoot);
    }

    private boolean isFieldNull(FieldAttributes f, Object obj) {
        return this.getFieldValue(f, obj) == null;
    }

    private Object getFieldValue(FieldAttributes f, Object obj) {
        try {
            return f.get(obj);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonElement getJsonElement() {
        return this.root;
    }
}

