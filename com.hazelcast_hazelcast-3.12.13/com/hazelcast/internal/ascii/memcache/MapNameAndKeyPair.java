/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.util.Preconditions;

public final class MapNameAndKeyPair {
    private final String mapName;
    private final String key;

    public MapNameAndKeyPair(String mapName, String key) {
        Preconditions.checkNotNull(mapName);
        Preconditions.checkNotNull(key);
        this.mapName = mapName;
        this.key = key;
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getKey() {
        return this.key;
    }
}

