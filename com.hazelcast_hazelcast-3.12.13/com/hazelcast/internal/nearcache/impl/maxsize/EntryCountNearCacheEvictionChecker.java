/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.maxsize;

import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.internal.nearcache.impl.SampleableNearCacheRecordMap;

public class EntryCountNearCacheEvictionChecker
implements EvictionChecker {
    private final SampleableNearCacheRecordMap nearCacheRecordMap;
    private final int maxSize;

    public EntryCountNearCacheEvictionChecker(int size, SampleableNearCacheRecordMap nearCacheRecordMap) {
        this.maxSize = size;
        this.nearCacheRecordMap = nearCacheRecordMap;
    }

    @Override
    public boolean isEvictionRequired() {
        return this.nearCacheRecordMap.size() >= this.maxSize;
    }
}

