/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElementVisitor;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.$Gson$Preconditions;
import java.io.IOException;

final class DelegatingJsonElementVisitor
implements JsonElementVisitor {
    private final JsonElementVisitor delegate;

    protected DelegatingJsonElementVisitor(JsonElementVisitor delegate) {
        this.delegate = $Gson$Preconditions.checkNotNull(delegate);
    }

    public void endArray(JsonArray array) throws IOException {
        this.delegate.endArray(array);
    }

    public void endObject(JsonObject object) throws IOException {
        this.delegate.endObject(object);
    }

    public void startArray(JsonArray array) throws IOException {
        this.delegate.startArray(array);
    }

    public void startObject(JsonObject object) throws IOException {
        this.delegate.startObject(object);
    }

    public void visitArrayMember(JsonArray parent, JsonPrimitive member, boolean isFirst) throws IOException {
        this.delegate.visitArrayMember(parent, member, isFirst);
    }

    public void visitArrayMember(JsonArray parent, JsonArray member, boolean isFirst) throws IOException {
        this.delegate.visitArrayMember(parent, member, isFirst);
    }

    public void visitArrayMember(JsonArray parent, JsonObject member, boolean isFirst) throws IOException {
        this.delegate.visitArrayMember(parent, member, isFirst);
    }

    public void visitObjectMember(JsonObject parent, String memberName, JsonPrimitive member, boolean isFirst) throws IOException {
        this.delegate.visitObjectMember(parent, memberName, member, isFirst);
    }

    public void visitObjectMember(JsonObject parent, String memberName, JsonArray member, boolean isFirst) throws IOException {
        this.delegate.visitObjectMember(parent, memberName, member, isFirst);
    }

    public void visitObjectMember(JsonObject parent, String memberName, JsonObject member, boolean isFirst) throws IOException {
        this.delegate.visitObjectMember(parent, memberName, member, isFirst);
    }

    public void visitNullObjectMember(JsonObject parent, String memberName, boolean isFirst) throws IOException {
        this.delegate.visitNullObjectMember(parent, memberName, isFirst);
    }

    public void visitPrimitive(JsonPrimitive primitive) throws IOException {
        this.delegate.visitPrimitive(primitive);
    }

    public void visitNull() throws IOException {
        this.delegate.visitNull();
    }

    public void visitNullArrayMember(JsonArray parent, boolean isFirst) throws IOException {
        this.delegate.visitNullArrayMember(parent, isFirst);
    }
}

