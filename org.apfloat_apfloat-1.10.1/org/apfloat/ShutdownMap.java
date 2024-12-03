/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import org.apfloat.ApfloatRuntimeException;

class ShutdownMap<K, V>
extends AbstractMap<K, V> {
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new ApfloatRuntimeException("Shutdown in progress");
    }

    @Override
    public V put(K key, V value) {
        throw new ApfloatRuntimeException("Shutdown in progress");
    }
}

