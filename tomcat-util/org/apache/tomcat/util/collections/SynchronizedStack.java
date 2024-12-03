/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.collections;

public class SynchronizedStack<T> {
    public static final int DEFAULT_SIZE = 128;
    private static final int DEFAULT_LIMIT = -1;
    private int size;
    private final int limit;
    private int index = -1;
    private Object[] stack;

    public SynchronizedStack() {
        this(128, -1);
    }

    public SynchronizedStack(int size, int limit) {
        this.size = limit > -1 && size > limit ? limit : size;
        this.limit = limit;
        this.stack = new Object[this.size];
    }

    public synchronized boolean push(T obj) {
        ++this.index;
        if (this.index == this.size) {
            if (this.limit == -1 || this.size < this.limit) {
                this.expand();
            } else {
                --this.index;
                return false;
            }
        }
        this.stack[this.index] = obj;
        return true;
    }

    public synchronized T pop() {
        if (this.index == -1) {
            return null;
        }
        Object result = this.stack[this.index];
        this.stack[this.index--] = null;
        return (T)result;
    }

    public synchronized void clear() {
        if (this.index > -1) {
            for (int i = 0; i < this.index + 1; ++i) {
                this.stack[i] = null;
            }
        }
        this.index = -1;
    }

    private void expand() {
        int newSize = this.size * 2;
        if (this.limit != -1 && newSize > this.limit) {
            newSize = this.limit;
        }
        Object[] newStack = new Object[newSize];
        System.arraycopy(this.stack, 0, newStack, 0, this.size);
        this.stack = newStack;
        this.size = newSize;
    }
}

