/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.json.internal;

import com.hazelcast.json.internal.JsonSchemaNode;
import com.hazelcast.json.internal.JsonSchemaStructNode;

public class JsonSchemaTerminalNode
extends JsonSchemaNode {
    private int valueStartLocation;

    public JsonSchemaTerminalNode(JsonSchemaStructNode parent) {
        super(parent);
    }

    public int getValueStartLocation() {
        return this.valueStartLocation;
    }

    public void setValueStartLocation(int valueStartLocation) {
        this.valueStartLocation = valueStartLocation;
    }

    @Override
    public boolean isTerminal() {
        return true;
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
        JsonSchemaTerminalNode that = (JsonSchemaTerminalNode)o;
        return this.valueStartLocation == that.valueStartLocation;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.valueStartLocation;
        return result;
    }

    public String toString() {
        return "JsonSchemaTerminalNode{valueStartLocation=" + this.valueStartLocation + '}';
    }
}

