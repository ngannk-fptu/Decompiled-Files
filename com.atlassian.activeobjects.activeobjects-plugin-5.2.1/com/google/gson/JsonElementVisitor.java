/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;

interface JsonElementVisitor {
    public void visitPrimitive(JsonPrimitive var1) throws IOException;

    public void visitNull() throws IOException;

    public void startArray(JsonArray var1) throws IOException;

    public void visitArrayMember(JsonArray var1, JsonPrimitive var2, boolean var3) throws IOException;

    public void visitArrayMember(JsonArray var1, JsonArray var2, boolean var3) throws IOException;

    public void visitArrayMember(JsonArray var1, JsonObject var2, boolean var3) throws IOException;

    public void visitNullArrayMember(JsonArray var1, boolean var2) throws IOException;

    public void endArray(JsonArray var1) throws IOException;

    public void startObject(JsonObject var1) throws IOException;

    public void visitObjectMember(JsonObject var1, String var2, JsonPrimitive var3, boolean var4) throws IOException;

    public void visitObjectMember(JsonObject var1, String var2, JsonArray var3, boolean var4) throws IOException;

    public void visitObjectMember(JsonObject var1, String var2, JsonObject var3, boolean var4) throws IOException;

    public void visitNullObjectMember(JsonObject var1, String var2, boolean var3) throws IOException;

    public void endObject(JsonObject var1) throws IOException;
}

