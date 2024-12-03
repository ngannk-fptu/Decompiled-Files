/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheMergeRunnable;
import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.merge.AbstractSplitBrainHandlerService;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.util.ThreadUtil;
import java.util.Collection;
import java.util.Iterator;

class CacheSplitBrainHandlerService
extends AbstractSplitBrainHandlerService<ICacheRecordStore> {
    private final CacheService cacheService;
    private final CachePartitionSegment[] segments;

    CacheSplitBrainHandlerService(NodeEngine nodeEngine, CachePartitionSegment[] segments) {
        super(nodeEngine);
        this.segments = segments;
        this.cacheService = (CacheService)nodeEngine.getService("hz:impl:cacheService");
    }

    @Override
    protected Runnable newMergeRunnable(Collection<ICacheRecordStore> mergingStores) {
        return new CacheMergeRunnable(mergingStores, this, this.cacheService.nodeEngine);
    }

    @Override
    protected Iterator<ICacheRecordStore> storeIterator(int partitionId) {
        return this.segments[partitionId].recordStoreIterator();
    }

    @Override
    protected void destroyStore(ICacheRecordStore store) {
        ThreadUtil.assertRunningOnPartitionThread();
        store.destroyInternals();
    }

    @Override
    protected boolean hasEntries(ICacheRecordStore store) {
        ThreadUtil.assertRunningOnPartitionThread();
        return store.size() > 0;
    }

    @Override
    protected boolean hasMergeablePolicy(ICacheRecordStore store) {
        Object mergePolicy = this.cacheService.getMergePolicy(store.getName());
        return !(mergePolicy instanceof DiscardMergePolicy);
    }
}

