/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.maxsize.impl;

import com.hazelcast.cache.impl.record.CacheRecordMap;
import com.hazelcast.internal.eviction.EvictionChecker;

public class EntryCountCacheEvictionChecker
implements EvictionChecker {
    private static final int MAX_ENTRY_COUNT_FOR_THRESHOLD_USAGE = 1000000;
    private static final int STD_DEV_OF_5_THRESHOLD = 4000;
    private static final int STD_DEV_MULTIPLIER_5 = 5;
    private static final int STD_DEV_MULTIPLIER_3 = 3;
    private final CacheRecordMap cacheRecordMap;
    private final int maxPartitionSize;

    public EntryCountCacheEvictionChecker(int size, CacheRecordMap cacheRecordMap, int partitionCount) {
        this.cacheRecordMap = cacheRecordMap;
        this.maxPartitionSize = EntryCountCacheEvictionChecker.calculateMaxPartitionSize(size, partitionCount);
    }

    public static int calculateMaxPartitionSize(int maxEntryCount, int partitionCount) {
        double balancedPartitionSize = (double)maxEntryCount / (double)partitionCount;
        double approximatedStdDev = Math.sqrt(balancedPartitionSize);
        int stdDevMultiplier = maxEntryCount <= 4000 ? 5 : (maxEntryCount > 4000 && maxEntryCount <= 1000000 ? 3 : 0);
        return (int)(approximatedStdDev * (double)stdDevMultiplier + balancedPartitionSize);
    }

    @Override
    public boolean isEvictionRequired() {
        return this.cacheRecordMap.size() >= this.maxPartitionSize;
    }
}

