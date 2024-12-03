/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.util;

import org.apache.felix.resolver.util.CopyOnWriteSet;
import org.apache.felix.resolver.util.OpenHashMap;

public class OpenHashMapSet<K, V>
extends OpenHashMap<K, CopyOnWriteSet<V>> {
    private static final long serialVersionUID = 1L;

    public OpenHashMapSet() {
    }

    public OpenHashMapSet(int initialCapacity) {
        super(initialCapacity);
    }

    public OpenHashMapSet<K, V> deepClone() {
        OpenHashMapSet copy = (OpenHashMapSet)super.clone();
        Object[] values = copy.value;
        int i = values.length;
        while (i-- > 0) {
            if (values[i] == null) continue;
            values[i] = new CopyOnWriteSet((CopyOnWriteSet)values[i]);
        }
        return copy;
    }

    @Override
    protected CopyOnWriteSet<V> compute(K key) {
        return new CopyOnWriteSet();
    }
}

