/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.json.internal;

import com.hazelcast.json.internal.JsonSchemaStructNode;

public abstract class JsonSchemaNode {
    private JsonSchemaStructNode parent;

    public JsonSchemaNode(JsonSchemaStructNode parent) {
        this.parent = parent;
    }

    public JsonSchemaStructNode getParent() {
        return this.parent;
    }

    public void setParent(JsonSchemaStructNode parent) {
        this.parent = parent;
    }

    public boolean isTerminal() {
        return false;
    }
}

