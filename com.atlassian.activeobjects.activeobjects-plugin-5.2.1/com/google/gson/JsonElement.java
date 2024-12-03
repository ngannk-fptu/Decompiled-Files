/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Escaper;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class JsonElement {
    private static final Escaper BASIC_ESCAPER = new Escaper(false);

    public boolean isJsonArray() {
        return this instanceof JsonArray;
    }

    public boolean isJsonObject() {
        return this instanceof JsonObject;
    }

    public boolean isJsonPrimitive() {
        return this instanceof JsonPrimitive;
    }

    public boolean isJsonNull() {
        return this instanceof JsonNull;
    }

    public JsonObject getAsJsonObject() {
        if (this.isJsonObject()) {
            return (JsonObject)this;
        }
        throw new IllegalStateException("This is not a JSON Object.");
    }

    public JsonArray getAsJsonArray() {
        if (this.isJsonArray()) {
            return (JsonArray)this;
        }
        throw new IllegalStateException("This is not a JSON Array.");
    }

    public JsonPrimitive getAsJsonPrimitive() {
        if (this.isJsonPrimitive()) {
            return (JsonPrimitive)this;
        }
        throw new IllegalStateException("This is not a JSON Primitive.");
    }

    public JsonNull getAsJsonNull() {
        if (this.isJsonNull()) {
            return (JsonNull)this;
        }
        throw new IllegalStateException("This is not a JSON Null.");
    }

    public boolean getAsBoolean() {
        throw new UnsupportedOperationException();
    }

    Boolean getAsBooleanWrapper() {
        throw new UnsupportedOperationException();
    }

    public Number getAsNumber() {
        throw new UnsupportedOperationException();
    }

    public String getAsString() {
        throw new UnsupportedOperationException();
    }

    public double getAsDouble() {
        throw new UnsupportedOperationException();
    }

    public float getAsFloat() {
        throw new UnsupportedOperationException();
    }

    public long getAsLong() {
        throw new UnsupportedOperationException();
    }

    public int getAsInt() {
        throw new UnsupportedOperationException();
    }

    public byte getAsByte() {
        throw new UnsupportedOperationException();
    }

    public char getAsCharacter() {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException();
    }

    public BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException();
    }

    public short getAsShort() {
        throw new UnsupportedOperationException();
    }

    Object getAsObject() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            this.toString(sb, BASIC_ESCAPER);
            return sb.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void toString(Appendable var1, Escaper var2) throws IOException;
}

