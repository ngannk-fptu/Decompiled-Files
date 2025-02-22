/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.querycache.subscriber.EvictionOperator;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRecordHashMap;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRecordStore;
import com.hazelcast.map.impl.querycache.subscriber.record.DataQueryCacheRecordFactory;
import com.hazelcast.map.impl.querycache.subscriber.record.ObjectQueryCacheRecordFactory;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecordFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.util.Clock;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

class DefaultQueryCacheRecordStore
implements QueryCacheRecordStore {
    private static final int DEFAULT_CACHE_CAPACITY = 1000;
    private final Indexes indexes;
    private final QueryCacheRecordHashMap cache;
    private final EvictionOperator evictionOperator;
    private final QueryCacheRecordFactory recordFactory;
    private final InternalSerializationService serializationService;
    private final Extractors extractors;

    public DefaultQueryCacheRecordStore(InternalSerializationService serializationService, Indexes indexes, QueryCacheConfig config, EvictionListener listener, Extractors extractors) {
        this.cache = new QueryCacheRecordHashMap(serializationService, 1000);
        this.serializationService = serializationService;
        this.recordFactory = this.getRecordFactory(config.getInMemoryFormat());
        this.indexes = indexes;
        this.evictionOperator = new EvictionOperator(this.cache, config, listener, serializationService.getClassLoader());
        this.extractors = extractors;
    }

    private QueryCacheRecord accessRecord(QueryCacheRecord record) {
        if (record == null) {
            return null;
        }
        record.incrementAccessHit();
        record.setAccessTime(Clock.currentTimeMillis());
        return record;
    }

    private QueryCacheRecordFactory getRecordFactory(InMemoryFormat inMemoryFormat) {
        switch (inMemoryFormat) {
            case BINARY: {
                return new DataQueryCacheRecordFactory(this.serializationService);
            }
            case OBJECT: {
                return new ObjectQueryCacheRecordFactory(this.serializationService);
            }
        }
        throw new IllegalArgumentException("Not a known format [" + (Object)((Object)inMemoryFormat) + "]");
    }

    @Override
    public QueryCacheRecord add(Data keyData, Data valueData) {
        this.evictionOperator.evictIfRequired();
        return this.addWithoutEvictionCheck(keyData, valueData);
    }

    @Override
    public QueryCacheRecord addWithoutEvictionCheck(Data keyData, Data valueData) {
        QueryCacheRecord newRecord = this.recordFactory.createRecord(valueData);
        QueryCacheRecord oldRecord = this.cache.put(keyData, newRecord);
        this.saveIndex(keyData, newRecord, oldRecord);
        return oldRecord;
    }

    private void saveIndex(Data keyData, QueryCacheRecord currentRecord, QueryCacheRecord oldRecord) {
        if (this.indexes.haveAtLeastOneIndex()) {
            Object currentValue = currentRecord.getValue();
            QueryEntry queryEntry = new QueryEntry(this.serializationService, keyData, currentValue, this.extractors);
            Object oldValue = oldRecord == null ? null : oldRecord.getValue();
            this.indexes.putEntry(queryEntry, oldValue, Index.OperationSource.USER);
        }
    }

    @Override
    public QueryCacheRecord get(Data keyData) {
        QueryCacheRecord record = (QueryCacheRecord)this.cache.get(keyData);
        return this.accessRecord(record);
    }

    @Override
    public QueryCacheRecord remove(Data keyData) {
        QueryCacheRecord oldRecord = (QueryCacheRecord)this.cache.remove(keyData);
        if (oldRecord != null) {
            this.removeIndex(keyData, oldRecord.getValue());
        }
        return oldRecord;
    }

    private void removeIndex(Data keyData, Object value) {
        if (this.indexes.haveAtLeastOneIndex()) {
            this.indexes.removeEntry(keyData, value, Index.OperationSource.USER);
        }
    }

    @Override
    public boolean containsKey(Data keyData) {
        QueryCacheRecord record = this.get(keyData);
        return record != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Collection values = this.cache.values();
        for (QueryCacheRecord cacheRecord : values) {
            Object cacheRecordValue = cacheRecord.getValue();
            if (!this.recordFactory.isEquals(cacheRecordValue, value)) continue;
            this.accessRecord(cacheRecord);
            return true;
        }
        return false;
    }

    @Override
    public Set<Data> keySet() {
        return this.cache.keySet();
    }

    @Override
    public Set<Map.Entry<Data, QueryCacheRecord>> entrySet() {
        return this.cache.entrySet();
    }

    @Override
    public int clear() {
        int removedEntryCount = this.cache.size();
        this.cache.clear();
        this.indexes.clearAll();
        return removedEntryCount;
    }

    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    @Override
    public int size() {
        return this.cache.size();
    }
}

