/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task;

import java.util.Collection;

public interface FifoBuffer<T> {
    public T remove();

    public void add(T var1);

    public int size();

    public Collection<T> getItems();

    public void clear();
}

