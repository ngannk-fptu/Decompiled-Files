/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.collections;

import java.util.Arrays;
import java.util.function.Function;

public abstract class LazyIndexedMap<K, V> {
    private volatile Object[] values;
    private static final Object NOT_INITIALIZED = new Object();

    protected LazyIndexedMap(int size) {
        Object[] vs = new Object[size];
        Arrays.fill(vs, NOT_INITIALIZED);
        this.values = vs;
    }

    protected <K1 extends K> V computeIfAbsent(int index, K1 originalKey, Function<K1, V> valueGenerator) {
        Object value = this.values[index];
        if (value != NOT_INITIALIZED) {
            return (V)value;
        }
        return this.lockedComputeIfAbsent(index, originalKey, valueGenerator);
    }

    private synchronized <K1 extends K> V lockedComputeIfAbsent(int index, K1 originalKey, Function<K1, V> valueGenerator) {
        Object[] values = this.values;
        Object value = values[index];
        if (value != NOT_INITIALIZED) {
            return (V)value;
        }
        V generated = valueGenerator.apply(originalKey);
        values[index] = generated;
        this.values = values;
        return generated;
    }
}

