/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.MapLoader;
import java.util.Collection;
import java.util.Map;

public interface MapStore<K, V>
extends MapLoader<K, V> {
    public void store(K var1, V var2);

    public void storeAll(Map<K, V> var1);

    public void delete(K var1);

    public void deleteAll(Collection<K> var1);
}

