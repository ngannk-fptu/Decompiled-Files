/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

public interface Stack<T> {
    public void push(T var1);

    public T pop();

    public T getCurrent();

    public int depth();

    public boolean isEmpty();

    public void clear();
}

