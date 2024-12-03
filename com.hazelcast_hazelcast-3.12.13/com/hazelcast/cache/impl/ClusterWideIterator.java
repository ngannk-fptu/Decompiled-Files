/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache$Entry
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractClusterWideIterator;
import com.hazelcast.cache.impl.CacheEntryIterationResult;
import com.hazelcast.cache.impl.CacheKeyIterationResult;
import com.hazelcast.cache.impl.CacheProxy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Iterator;
import java.util.List;
import javax.cache.Cache;

public class ClusterWideIterator<K, V>
extends AbstractClusterWideIterator<K, V>
implements Iterator<Cache.Entry<K, V>> {
    private final SerializationService serializationService;
    private final CacheProxy<K, V> cacheProxy;

    public ClusterWideIterator(CacheProxy<K, V> cache, boolean prefetchValues) {
        this(cache, 100, prefetchValues);
    }

    public ClusterWideIterator(CacheProxy<K, V> cache, int fetchSize, boolean prefetchValues) {
        super(cache, cache.getNodeEngine().getPartitionService().getPartitionCount(), fetchSize, prefetchValues);
        this.cacheProxy = cache;
        this.serializationService = cache.getNodeEngine().getSerializationService();
        this.advance();
    }

    public ClusterWideIterator(CacheProxy<K, V> cache, int fetchSize, int partitionId, boolean prefetchValues) {
        super(cache, cache.getNodeEngine().getPartitionService().getPartitionCount(), fetchSize, prefetchValues);
        this.cacheProxy = cache;
        this.serializationService = cache.getNodeEngine().getSerializationService();
        this.partitionIndex = partitionId;
        this.advance();
    }

    @Override
    protected List fetch() {
        OperationService operationService = this.cacheProxy.getNodeEngine().getOperationService();
        if (this.prefetchValues) {
            Operation operation = this.cacheProxy.operationProvider.createEntryIteratorOperation(this.lastTableIndex, this.fetchSize);
            InternalCompletableFuture f = operationService.invokeOnPartition("hz:impl:cacheService", operation, this.partitionIndex);
            CacheEntryIterationResult iteratorResult = (CacheEntryIterationResult)f.join();
            if (iteratorResult != null) {
                this.setLastTableIndex(iteratorResult.getEntries(), iteratorResult.getTableIndex());
                return iteratorResult.getEntries();
            }
        } else {
            Operation operation = this.cacheProxy.operationProvider.createKeyIteratorOperation(this.lastTableIndex, this.fetchSize);
            InternalCompletableFuture f = operationService.invokeOnPartition("hz:impl:cacheService", operation, this.partitionIndex);
            CacheKeyIterationResult iteratorResult = (CacheKeyIterationResult)f.join();
            if (iteratorResult != null) {
                this.setLastTableIndex(iteratorResult.getKeys(), iteratorResult.getTableIndex());
                return iteratorResult.getKeys();
            }
        }
        return null;
    }

    @Override
    protected Data toData(Object obj) {
        return this.serializationService.toData(obj);
    }

    @Override
    protected <T> T toObject(Object data) {
        return this.serializationService.toObject(data);
    }
}

