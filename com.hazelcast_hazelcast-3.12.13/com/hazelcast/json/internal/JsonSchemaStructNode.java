/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.json.internal;

import com.hazelcast.json.internal.JsonSchemaNameValue;
import com.hazelcast.json.internal.JsonSchemaNode;
import java.util.ArrayList;
import java.util.List;

public class JsonSchemaStructNode
extends JsonSchemaNode {
    private final List<JsonSchemaNameValue> inners = new ArrayList<JsonSchemaNameValue>();

    public JsonSchemaStructNode(JsonSchemaStructNode parent) {
        super(parent);
    }

    public void addChild(JsonSchemaNameValue description) {
        this.inners.add(description);
    }

    public JsonSchemaNameValue getChild(int i) {
        return this.inners.get(i);
    }

    public int getChildCount() {
        return this.inners.size();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        JsonSchemaStructNode that = (JsonSchemaStructNode)o;
        return this.inners != null ? this.inners.equals(that.inners) : that.inners == null;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.inners != null ? this.inners.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "JsonSchemaStructNode{inners=" + this.inners + '}';
    }
}

