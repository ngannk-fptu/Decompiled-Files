/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheMergePolicy;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.CacheSplitBrainHandlerService;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.merge.entry.DefaultCacheEntryView;
import com.hazelcast.cache.impl.operation.CacheLegacyMergeOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.merge.AbstractMergeRunnable;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.function.BiConsumer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class CacheMergeRunnable
extends AbstractMergeRunnable<Data, Data, ICacheRecordStore, SplitBrainMergeTypes.CacheMergeTypes> {
    private final CacheService cacheService;
    private final ConcurrentMap<String, CacheConfig> configs;

    CacheMergeRunnable(Collection<ICacheRecordStore> mergingStores, CacheSplitBrainHandlerService splitBrainHandlerService, NodeEngine nodeEngine) {
        super("hz:impl:cacheService", mergingStores, splitBrainHandlerService, nodeEngine);
        this.cacheService = (CacheService)nodeEngine.getService("hz:impl:cacheService");
        this.configs = new ConcurrentHashMap<String, CacheConfig>(this.cacheService.getConfigs());
    }

    @Override
    protected void onRunStart() {
        super.onRunStart();
        for (CacheConfig cacheConfig : this.configs.values()) {
            this.cacheService.putCacheConfigIfAbsent(cacheConfig);
        }
    }

    @Override
    protected void onMerge(String cacheName) {
        this.cacheService.sendInvalidationEvent(cacheName, null, "<NA>");
    }

    @Override
    protected void mergeStore(ICacheRecordStore store, BiConsumer<Integer, SplitBrainMergeTypes.CacheMergeTypes> consumer) {
        int partitionId = store.getPartitionId();
        for (Map.Entry<Data, CacheRecord> entry : store.getReadOnlyRecords().entrySet()) {
            Data key = this.toHeapData(entry.getKey());
            CacheRecord record = entry.getValue();
            Data dataValue = this.toHeapData(record.getValue());
            consumer.accept(partitionId, MergingValueFactory.createMergingEntry((SerializationService)this.getSerializationService(), key, dataValue, record));
        }
    }

    @Override
    protected void mergeStoreLegacy(ICacheRecordStore recordStore, BiConsumer<Integer, Operation> consumer) {
        int partitionId = recordStore.getPartitionId();
        String name = recordStore.getName();
        CacheMergePolicy mergePolicy = (CacheMergePolicy)this.getMergePolicy(name);
        for (Map.Entry<Data, CacheRecord> entry : recordStore.getReadOnlyRecords().entrySet()) {
            Data key = entry.getKey();
            CacheRecord record = entry.getValue();
            DefaultCacheEntryView entryView = new DefaultCacheEntryView(key, this.toData(record.getValue()), record.getCreationTime(), record.getExpirationTime(), record.getLastAccessTime(), record.getAccessHit(), this.toData(record.getExpiryPolicy()));
            consumer.accept(partitionId, new CacheLegacyMergeOperation(name, key, entryView, mergePolicy));
        }
    }

    @Override
    protected InMemoryFormat getInMemoryFormat(String dataStructureName) {
        return ((CacheConfig)this.cacheService.getConfigs().get(dataStructureName)).getInMemoryFormat();
    }

    @Override
    protected int getBatchSize(String dataStructureName) {
        return 100;
    }

    @Override
    protected Object getMergePolicy(String dataStructureName) {
        return this.cacheService.getMergePolicy(dataStructureName);
    }

    @Override
    protected String getDataStructureName(ICacheRecordStore iCacheRecordStore) {
        return iCacheRecordStore.getName();
    }

    @Override
    protected int getPartitionId(ICacheRecordStore store) {
        return store.getPartitionId();
    }

    @Override
    protected OperationFactory createMergeOperationFactory(String dataStructureName, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> mergePolicy, int[] partitions, List<SplitBrainMergeTypes.CacheMergeTypes>[] entries) {
        CacheConfig cacheConfig = this.cacheService.getCacheConfig(dataStructureName);
        CacheOperationProvider operationProvider = this.cacheService.getCacheOperationProvider(dataStructureName, cacheConfig.getInMemoryFormat());
        return operationProvider.createMergeOperationFactory(dataStructureName, partitions, entries, mergePolicy);
    }
}

