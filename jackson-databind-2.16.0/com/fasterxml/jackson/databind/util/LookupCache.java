/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.util;

import java.util.function.BiConsumer;

public interface LookupCache<K, V> {
    default public void contents(BiConsumer<K, V> consumer) {
        throw new UnsupportedOperationException();
    }

    default public LookupCache<K, V> emptyCopy() {
        throw new UnsupportedOperationException("LookupCache implementation " + this.getClass().getName() + " does not implement `emptyCopy()`");
    }

    public int size();

    public V get(Object var1);

    public V put(K var1, V var2);

    public V putIfAbsent(K var1, V var2);

    public void clear();
}

