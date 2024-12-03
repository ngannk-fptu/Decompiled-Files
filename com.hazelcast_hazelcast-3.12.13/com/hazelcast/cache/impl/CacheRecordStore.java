/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractCacheRecordStore;
import com.hazelcast.cache.impl.AbstractCacheService;
import com.hazelcast.cache.impl.CacheEntryProcessorEntry;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.cache.impl.record.CacheRecordHashMap;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.serialization.SerializationService;

public class CacheRecordStore
extends AbstractCacheRecordStore<CacheRecord, CacheRecordHashMap> {
    protected SerializationService serializationService;

    public CacheRecordStore(String cacheNameWithPrefix, int partitionId, NodeEngine nodeEngine, AbstractCacheService cacheService) {
        super(cacheNameWithPrefix, partitionId, nodeEngine, cacheService);
        this.serializationService = nodeEngine.getSerializationService();
    }

    @Override
    protected EvictionChecker createCacheEvictionChecker(int size, EvictionConfig.MaxSizePolicy maxSizePolicy) {
        if (maxSizePolicy == null) {
            throw new IllegalArgumentException("Max-Size policy cannot be null");
        }
        if (maxSizePolicy != EvictionConfig.MaxSizePolicy.ENTRY_COUNT) {
            throw new IllegalArgumentException("Invalid max-size policy (" + (Object)((Object)maxSizePolicy) + ") for " + this.getClass().getName() + "! Only " + (Object)((Object)EvictionConfig.MaxSizePolicy.ENTRY_COUNT) + " is supported.");
        }
        return super.createCacheEvictionChecker(size, maxSizePolicy);
    }

    @Override
    protected CacheRecordHashMap createRecordCacheMap() {
        return new CacheRecordHashMap(this.nodeEngine.getSerializationService(), 256, this.cacheContext);
    }

    @Override
    protected CacheEntryProcessorEntry createCacheEntryProcessorEntry(Data key, CacheRecord record, long now, int completionId) {
        return new CacheEntryProcessorEntry(key, record, this, now, completionId);
    }

    @Override
    protected CacheRecord createRecord(Object value, long creationTime, long expiryTime) {
        this.evictIfRequired();
        this.markExpirable(expiryTime);
        return this.cacheRecordFactory.newRecordWithExpiry(value, creationTime, expiryTime);
    }

    @Override
    protected Data valueToData(Object value) {
        return this.cacheService.toData(value);
    }

    @Override
    protected Object dataToValue(Data data) {
        return this.serializationService.toObject(data);
    }

    @Override
    protected Object recordToValue(CacheRecord record) {
        Object value = record.getValue();
        if (value instanceof Data) {
            switch (this.cacheConfig.getInMemoryFormat()) {
                case BINARY: {
                    return value;
                }
                case OBJECT: {
                    return this.dataToValue((Data)value);
                }
            }
            throw new IllegalStateException("Unsupported in-memory format: " + (Object)((Object)this.cacheConfig.getInMemoryFormat()));
        }
        return value;
    }

    @Override
    protected Data recordToData(CacheRecord record) {
        Object value = this.recordToValue(record);
        if (value == null) {
            return null;
        }
        if (value instanceof Data) {
            return (Data)value;
        }
        return this.valueToData(value);
    }

    @Override
    protected Data toHeapData(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Data) {
            return (Data)obj;
        }
        if (obj instanceof CacheRecord) {
            CacheRecord record = (CacheRecord)obj;
            Object value = record.getValue();
            return this.toHeapData(value);
        }
        return this.serializationService.toData(obj);
    }

    @Override
    public void disposeDeferredBlocks() {
    }
}

