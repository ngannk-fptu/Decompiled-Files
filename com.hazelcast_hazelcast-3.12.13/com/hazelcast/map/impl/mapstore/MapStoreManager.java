/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore;

import com.hazelcast.map.impl.mapstore.MapDataStore;

public interface MapStoreManager {
    public void start();

    public void stop();

    public MapDataStore getMapDataStore(String var1, int var2);
}

