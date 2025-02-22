/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.management.CacheStatisticsMXBean
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheStatisticsImpl;
import javax.cache.management.CacheStatisticsMXBean;

public class CacheStatisticsMXBeanImpl
implements CacheStatisticsMXBean {
    private CacheStatisticsImpl statistics;

    public CacheStatisticsMXBeanImpl(CacheStatisticsImpl statistics) {
        this.statistics = statistics;
    }

    public void clear() {
        this.statistics.clear();
    }

    public long getCacheHits() {
        return this.statistics.getCacheHits();
    }

    public float getCacheHitPercentage() {
        return this.statistics.getCacheHitPercentage();
    }

    public long getCacheMisses() {
        return this.statistics.getCacheMisses();
    }

    public float getCacheMissPercentage() {
        return this.statistics.getCacheMissPercentage();
    }

    public long getCacheGets() {
        return this.statistics.getCacheGets();
    }

    public long getCachePuts() {
        return this.statistics.getCachePuts();
    }

    public long getCacheRemovals() {
        return this.statistics.getCacheRemovals();
    }

    public long getCacheEvictions() {
        return this.statistics.getCacheEvictions();
    }

    public float getAverageGetTime() {
        return this.statistics.getAverageGetTime();
    }

    public float getAveragePutTime() {
        return this.statistics.getAveragePutTime();
    }

    public float getAverageRemoveTime() {
        return this.statistics.getAverageRemoveTime();
    }
}

