/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import java.util.Collection;
import java.util.Map;

public interface MapLoader<K, V> {
    public V load(K var1);

    public Map<K, V> loadAll(Collection<K> var1);

    public Iterable<K> loadAllKeys();
}

