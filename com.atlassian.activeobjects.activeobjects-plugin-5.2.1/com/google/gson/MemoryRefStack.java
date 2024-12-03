/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ObjectTypePair;
import com.google.gson.internal.$Gson$Preconditions;
import java.util.Stack;

final class MemoryRefStack {
    private final Stack<ObjectTypePair> stack = new Stack();

    MemoryRefStack() {
    }

    public ObjectTypePair push(ObjectTypePair obj) {
        $Gson$Preconditions.checkNotNull(obj);
        return this.stack.push(obj);
    }

    public ObjectTypePair pop() {
        return this.stack.pop();
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public ObjectTypePair peek() {
        return this.stack.peek();
    }

    public boolean contains(ObjectTypePair obj) {
        if (obj == null) {
            return false;
        }
        for (ObjectTypePair stackObject : this.stack) {
            if (stackObject.getObject() != obj.getObject() || !stackObject.type.equals(obj.type)) continue;
            return true;
        }
        return false;
    }
}

