/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.cache.StatisticsType;
import java.util.Comparator;
import java.util.Objects;
import javax.annotation.Nonnull;

public enum CacheStatisticsKey {
    SIZE("size", StatisticsType.GAUGE),
    HEAP_SIZE("heapSize", StatisticsType.GAUGE),
    HIT_COUNT("hitCount", StatisticsType.COUNTER),
    PUT_COUNT("putCount", StatisticsType.COUNTER),
    REMOVE_COUNT("removeCount", StatisticsType.COUNTER),
    MISS_COUNT("missCount", StatisticsType.COUNTER),
    LOAD_COUNT("loadCount", StatisticsType.COUNTER),
    LOAD_SUCCESS_COUNT("loadSuccessCount", StatisticsType.COUNTER),
    LOAD_EXCEPTION_COUNT("loadExceptionCount", StatisticsType.COUNTER),
    TOTAL_MISS_TIME("totalMissTime", StatisticsType.COUNTER),
    TOTAL_LOAD_TIME("totalLoadTime", StatisticsType.COUNTER),
    EVICTION_COUNT("evictionCount", StatisticsType.COUNTER),
    REQUEST_COUNT("requestCount", StatisticsType.COUNTER);

    public static Comparator<CacheStatisticsKey> SORT_BY_LABEL;
    private final String label;
    private final StatisticsType type;

    private CacheStatisticsKey(String label, StatisticsType type) {
        this.label = Objects.requireNonNull(label, "label");
        this.type = Objects.requireNonNull(type, "type");
    }

    @Nonnull
    public String getLabel() {
        return this.label;
    }

    @Nonnull
    public StatisticsType getType() {
        return this.type;
    }

    static {
        SORT_BY_LABEL = new Comparator<CacheStatisticsKey>(){

            @Override
            public int compare(CacheStatisticsKey key1, CacheStatisticsKey key2) {
                return key1.getLabel().compareTo(key2.getLabel());
            }
        };
    }
}

