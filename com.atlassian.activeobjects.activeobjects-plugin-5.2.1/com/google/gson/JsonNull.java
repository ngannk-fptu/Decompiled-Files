/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Escaper;
import com.google.gson.JsonElement;
import java.io.IOException;

public final class JsonNull
extends JsonElement {
    private static final JsonNull INSTANCE = new JsonNull();

    protected void toString(Appendable sb, Escaper escaper) throws IOException {
        sb.append("null");
    }

    public int hashCode() {
        return JsonNull.class.hashCode();
    }

    public boolean equals(Object other) {
        return this == other || other instanceof JsonNull;
    }

    static JsonNull createJsonNull() {
        return INSTANCE;
    }
}

