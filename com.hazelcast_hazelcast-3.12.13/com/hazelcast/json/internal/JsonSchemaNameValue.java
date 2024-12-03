/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.json.internal;

import com.hazelcast.json.internal.JsonSchemaNode;

public class JsonSchemaNameValue {
    private final int nameStart;
    private final JsonSchemaNode value;

    public JsonSchemaNameValue(int nameStart, JsonSchemaNode value) {
        this.nameStart = nameStart;
        this.value = value;
    }

    public int getNameStart() {
        return this.nameStart;
    }

    public boolean isArrayItem() {
        return this.nameStart == -1;
    }

    public boolean isObjectItem() {
        return this.nameStart > 0;
    }

    public JsonSchemaNode getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JsonSchemaNameValue that = (JsonSchemaNameValue)o;
        if (this.nameStart != that.nameStart) {
            return false;
        }
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    public int hashCode() {
        int result = this.nameStart;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "JsonSchemaNameValue{nameStart=" + this.nameStart + ", value=" + this.value + '}';
    }
}

