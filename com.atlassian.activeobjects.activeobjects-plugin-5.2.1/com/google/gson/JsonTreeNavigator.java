/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonElementVisitor;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Map;

final class JsonTreeNavigator {
    private final JsonElementVisitor visitor;
    private final boolean visitNulls;

    JsonTreeNavigator(JsonElementVisitor visitor, boolean visitNulls) {
        this.visitor = visitor;
        this.visitNulls = visitNulls;
    }

    public void navigate(JsonElement element) throws IOException {
        if (element.isJsonNull()) {
            this.visitor.visitNull();
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            this.visitor.startArray(array);
            boolean isFirst = true;
            for (JsonElement child : array) {
                this.visitChild(array, child, isFirst);
                if (!isFirst) continue;
                isFirst = false;
            }
            this.visitor.endArray(array);
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            this.visitor.startObject(object);
            boolean isFirst = true;
            for (Map.Entry<String, JsonElement> member : object.entrySet()) {
                boolean visited = this.visitChild(object, member.getKey(), member.getValue(), isFirst);
                if (!visited || !isFirst) continue;
                isFirst = false;
            }
            this.visitor.endObject(object);
        } else {
            this.visitor.visitPrimitive(element.getAsJsonPrimitive());
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean visitChild(JsonObject parent, String childName, JsonElement child, boolean isFirst) throws IOException {
        if (child.isJsonNull()) {
            if (!this.visitNulls) return false;
            this.visitor.visitNullObjectMember(parent, childName, isFirst);
            this.navigate(child.getAsJsonNull());
            return true;
        } else if (child.isJsonArray()) {
            JsonArray childAsArray = child.getAsJsonArray();
            this.visitor.visitObjectMember(parent, childName, childAsArray, isFirst);
            this.navigate(childAsArray);
            return true;
        } else if (child.isJsonObject()) {
            JsonObject childAsObject = child.getAsJsonObject();
            this.visitor.visitObjectMember(parent, childName, childAsObject, isFirst);
            this.navigate(childAsObject);
            return true;
        } else {
            this.visitor.visitObjectMember(parent, childName, child.getAsJsonPrimitive(), isFirst);
        }
        return true;
    }

    private void visitChild(JsonArray parent, JsonElement child, boolean isFirst) throws IOException {
        if (child.isJsonNull()) {
            this.visitor.visitNullArrayMember(parent, isFirst);
            this.navigate(child);
        } else if (child.isJsonArray()) {
            JsonArray childAsArray = child.getAsJsonArray();
            this.visitor.visitArrayMember(parent, childAsArray, isFirst);
            this.navigate(childAsArray);
        } else if (child.isJsonObject()) {
            JsonObject childAsObject = child.getAsJsonObject();
            this.visitor.visitArrayMember(parent, childAsObject, isFirst);
            this.navigate(childAsObject);
        } else {
            this.visitor.visitArrayMember(parent, child.getAsJsonPrimitive(), isFirst);
        }
    }
}

