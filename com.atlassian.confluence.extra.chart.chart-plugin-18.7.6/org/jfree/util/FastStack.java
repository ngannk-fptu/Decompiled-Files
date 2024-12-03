/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EmptyStackException;

public final class FastStack
implements Serializable,
Cloneable {
    private Object[] contents;
    private int size;
    private int initialSize;

    public FastStack() {
        this.initialSize = 10;
    }

    public FastStack(int size) {
        this.initialSize = Math.max(1, size);
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public void push(Object o) {
        if (this.contents == null) {
            this.contents = new Object[this.initialSize];
            this.contents[0] = o;
            this.size = 1;
            return;
        }
        int oldSize = this.size++;
        if (this.contents.length == this.size) {
            Object[] newContents = new Object[this.size + this.initialSize];
            System.arraycopy(this.contents, 0, newContents, 0, this.size);
            this.contents = newContents;
        }
        this.contents[oldSize] = o;
    }

    public Object peek() {
        if (this.size == 0) {
            throw new EmptyStackException();
        }
        return this.contents[this.size - 1];
    }

    public Object pop() {
        if (this.size == 0) {
            throw new EmptyStackException();
        }
        --this.size;
        Object retval = this.contents[this.size];
        this.contents[this.size] = null;
        return retval;
    }

    public Object clone() {
        try {
            FastStack stack = (FastStack)super.clone();
            if (this.contents != null) {
                stack.contents = (Object[])this.contents.clone();
            }
            return stack;
        }
        catch (CloneNotSupportedException cne) {
            throw new IllegalStateException("Clone not supported? Why?");
        }
    }

    public void clear() {
        this.size = 0;
        if (this.contents != null) {
            Arrays.fill(this.contents, null);
        }
    }

    public Object get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        return this.contents[index];
    }
}

