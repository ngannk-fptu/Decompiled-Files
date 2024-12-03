/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.MapLoader;
import java.util.Properties;

public interface MapStoreFactory<K, V> {
    public MapLoader<K, V> newMapStore(String var1, Properties var2);
}

