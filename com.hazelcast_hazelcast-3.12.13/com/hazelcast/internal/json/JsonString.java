/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.json;

import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.json.JsonWriter;
import com.hazelcast.nio.serialization.SerializableByConvention;
import java.io.IOException;

@SerializableByConvention
class JsonString
extends JsonValue {
    private final String string;

    JsonString(String string) {
        if (string == null) {
            throw new NullPointerException("string is null");
        }
        this.string = string;
    }

    @Override
    void write(JsonWriter writer) throws IOException {
        writer.writeString(this.string);
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asString() {
        return this.string;
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
        JsonString other = (JsonString)object;
        return this.string.equals(other.string);
    }
}

