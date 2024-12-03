/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.management;

import javax.management.MXBean;

@MXBean
public interface CacheStatisticsMXBean {
    public void clear();

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

