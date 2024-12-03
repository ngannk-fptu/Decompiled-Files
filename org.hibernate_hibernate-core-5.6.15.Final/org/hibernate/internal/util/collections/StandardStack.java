/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.ArrayDeque;
import java.util.Deque;
import org.hibernate.internal.util.collections.Stack;

public final class StandardStack<T>
implements Stack<T> {
    private ArrayDeque internalStack;
    private static final Object NULL_TOKEN = new Object();

    @Override
    public void push(T newCurrent) {
        Object toStore = newCurrent;
        if (newCurrent == null) {
            toStore = NULL_TOKEN;
        }
        this.stackInstanceExpected().addFirst(toStore);
    }

    private Deque stackInstanceExpected() {
        if (this.internalStack == null) {
            this.internalStack = new ArrayDeque(7);
        }
        return this.internalStack;
    }

    @Override
    public T pop() {
        return this.convert(this.stackInstanceExpected().removeFirst());
    }

    private T convert(Object internalStoredObject) {
        if (internalStoredObject == NULL_TOKEN) {
            return null;
        }
        return (T)internalStoredObject;
    }

    @Override
    public T getCurrent() {
        if (this.internalStack == null) {
            return null;
        }
        return this.convert(this.internalStack.peek());
    }

    @Override
    public int depth() {
        if (this.internalStack == null) {
            return 0;
        }
        return this.internalStack.size();
    }

    @Override
    public boolean isEmpty() {
        if (this.internalStack == null) {
            return true;
        }
        return this.internalStack.isEmpty();
    }

    @Override
    public void clear() {
        if (this.internalStack != null) {
            this.internalStack.clear();
        }
    }
}

