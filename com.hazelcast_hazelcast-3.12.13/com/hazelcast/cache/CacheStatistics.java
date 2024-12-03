/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache;

import com.hazelcast.monitor.NearCacheStats;

public interface CacheStatistics {
    public long getCreationTime();

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

    public NearCacheStats getNearCacheStatistics();
}

