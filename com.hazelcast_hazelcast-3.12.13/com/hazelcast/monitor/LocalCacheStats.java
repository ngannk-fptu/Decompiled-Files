/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalCacheStats
extends LocalInstanceStats {
    public long getLastAccessTime();

    public long getLastUpdateTime();

    public long getOwnedEntryCount();

    public long getCacheHits();

    public float getCacheHitPercentage();

    public long getCacheMisses();

    public float getCacheMissPercentage();

    public long getCacheGets();

    public long getCachePuts();

    public long getCacheRemovals();

    public long getCacheEvictions();

    public float getAverageGetTime();

    public float getAveragePutTime();

    public float getAverageRemoveTime();
}

