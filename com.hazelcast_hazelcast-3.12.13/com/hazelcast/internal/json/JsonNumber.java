/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.json;

import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.json.JsonWriter;
import com.hazelcast.nio.serialization.SerializableByConvention;
import java.io.IOException;

@SerializableByConvention
class JsonNumber
extends JsonValue {
    private final String string;

    JsonNumber(String string) {
        if (string == null) {
            throw new NullPointerException("string is null");
        }
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    void write(JsonWriter writer) throws IOException {
        writer.writeNumber(this.string);
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public int asInt() {
        return Integer.parseInt(this.string, 10);
    }

    @Override
    public long asLong() {
        return Long.parseLong(this.string, 10);
    }

    @Override
    public float asFloat() {
        return Float.parseFloat(this.string);
    }

    @Override
    public double asDouble() {
        return Double.parseDouble(this.string);
    }

    @Override
    public int hashCode() {
        return this.string.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        JsonNumber other = (JsonNumber)object;
        return this.string.equals(other.string);
    }
}

