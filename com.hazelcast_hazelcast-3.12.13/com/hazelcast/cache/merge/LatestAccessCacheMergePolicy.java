/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.merge;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.StorageTypeAwareCacheMergePolicy;
import com.hazelcast.nio.serialization.BinaryInterface;

@BinaryInterface
public class LatestAccessCacheMergePolicy
implements StorageTypeAwareCacheMergePolicy {
    @Override
    public Object merge(String cacheName, CacheEntryView mergingEntry, CacheEntryView existingEntry) {
        if (existingEntry == null || mergingEntry.getLastAccessTime() >= existingEntry.getLastAccessTime()) {
            return mergingEntry.getValue();
        }
        return existingEntry.getValue();
    }
}

