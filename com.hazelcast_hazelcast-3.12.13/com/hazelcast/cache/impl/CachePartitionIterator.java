/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheProxy;
import com.hazelcast.cache.impl.ClusterWideIterator;

public class CachePartitionIterator<K, V>
extends ClusterWideIterator<K, V> {
    public CachePartitionIterator(CacheProxy<K, V> cache, int fetchSize, int partitionId, boolean prefetchValues) {
        super(cache, fetchSize, partitionId, prefetchValues);
    }

    @Override
    protected boolean advance() {
        if (this.lastTableIndex < 0) {
            this.lastTableIndex = Integer.MAX_VALUE;
            return false;
        }
        this.result = this.fetch();
        if (this.result != null && this.result.size() > 0) {
            this.index = 0;
            return true;
        }
        return false;
    }
}

