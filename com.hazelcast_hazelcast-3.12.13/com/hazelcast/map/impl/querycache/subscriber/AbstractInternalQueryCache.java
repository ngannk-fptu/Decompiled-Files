/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMap;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.eviction.EvictionListener;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.LazyMapEntry;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.subscriber.DefaultQueryCacheRecordStore;
import com.hazelcast.map.impl.querycache.subscriber.EventPublisherHelper;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRecordStore;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.CachedQueryEntry;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.getters.Extractors;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class AbstractInternalQueryCache<K, V>
implements InternalQueryCache<K, V> {
    protected final boolean includeValue;
    protected final String mapName;
    protected final String cacheId;
    protected final String cacheName;
    protected final IMap delegate;
    protected final Indexes indexes;
    protected final QueryCacheContext context;
    protected final QueryCacheConfig queryCacheConfig;
    protected final QueryCacheRecordStore recordStore;
    protected final PartitioningStrategy partitioningStrategy;
    protected final InternalSerializationService serializationService;
    protected final Extractors extractors;
    protected volatile String publisherListenerId;

    public AbstractInternalQueryCache(String cacheId, String cacheName, QueryCacheConfig queryCacheConfig, IMap delegate, QueryCacheContext context) {
        this.cacheId = cacheId;
        this.cacheName = cacheName;
        this.queryCacheConfig = queryCacheConfig;
        this.mapName = delegate.getName();
        this.delegate = delegate;
        this.context = context;
        this.serializationService = context.getSerializationService();
        this.indexes = Indexes.newBuilder(this.serializationService, IndexCopyBehavior.COPY_ON_READ).build();
        this.includeValue = this.isIncludeValue();
        this.partitioningStrategy = this.getPartitioningStrategy();
        this.extractors = Extractors.newBuilder(this.serializationService).build();
        this.recordStore = new DefaultQueryCacheRecordStore(this.serializationService, this.indexes, queryCacheConfig, this.getEvictionListener(), this.extractors);
        for (MapIndexConfig indexConfig : queryCacheConfig.getIndexConfigs()) {
            this.indexes.addOrGetIndex(indexConfig.getAttribute(), indexConfig.isOrdered());
        }
    }

    public QueryCacheContext getContext() {
        return this.context;
    }

    @Override
    public String getPublisherListenerId() {
        return this.publisherListenerId;
    }

    @Override
    public void setPublisherListenerId(String publisherListenerId) {
        this.publisherListenerId = publisherListenerId;
    }

    @Override
    public String getCacheId() {
        return this.cacheId;
    }

    protected Predicate getPredicate() {
        return this.queryCacheConfig.getPredicateConfig().getImplementation();
    }

    @Override
    public boolean reachedMaxCapacity() {
        EvictionConfig evictionConfig = this.queryCacheConfig.getEvictionConfig();
        EvictionConfig.MaxSizePolicy maximumSizePolicy = evictionConfig.getMaximumSizePolicy();
        return maximumSizePolicy == EvictionConfig.MaxSizePolicy.ENTRY_COUNT && this.size() == evictionConfig.getSize();
    }

    private EvictionListener getEvictionListener() {
        return new EvictionListener<Data, QueryCacheRecord>(){

            @Override
            public void onEvict(Data dataKey, QueryCacheRecord record, boolean wasExpired) {
                EventPublisherHelper.publishEntryEvent(AbstractInternalQueryCache.this.context, AbstractInternalQueryCache.this.mapName, AbstractInternalQueryCache.this.cacheId, dataKey, null, record, EntryEventType.EVICTED, AbstractInternalQueryCache.this.extractors);
            }
        };
    }

    PartitioningStrategy getPartitioningStrategy() {
        if (this.delegate instanceof MapProxyImpl) {
            return ((MapProxyImpl)this.delegate).getPartitionStrategy();
        }
        return null;
    }

    protected void doFullKeyScan(Predicate predicate, Set<K> resultingSet) {
        InternalSerializationService serializationService = this.serializationService;
        CachedQueryEntry queryEntry = new CachedQueryEntry();
        Set<Map.Entry<Data, QueryCacheRecord>> entries = this.recordStore.entrySet();
        for (Map.Entry<Data, QueryCacheRecord> entry : entries) {
            Data keyData = entry.getKey();
            QueryCacheRecord record = entry.getValue();
            Object value = record.getValue();
            queryEntry.init(serializationService, keyData, value, this.extractors);
            boolean valid = predicate.apply(queryEntry);
            if (!valid) continue;
            resultingSet.add(queryEntry.getKey());
        }
    }

    protected void doFullEntryScan(Predicate predicate, Set<Map.Entry<K, V>> resultingSet) {
        InternalSerializationService serializationService = this.serializationService;
        CachedQueryEntry queryEntry = new CachedQueryEntry();
        Set<Map.Entry<Data, QueryCacheRecord>> entries = this.recordStore.entrySet();
        for (Map.Entry<Data, QueryCacheRecord> entry : entries) {
            Data keyData = entry.getKey();
            QueryCacheRecord record = entry.getValue();
            Object value = record.getValue();
            queryEntry.init(serializationService, keyData, value, this.extractors);
            boolean valid = predicate.apply(queryEntry);
            if (!valid) continue;
            LazyMapEntry simpleEntry = new LazyMapEntry(queryEntry.getKeyData(), queryEntry.getValueData(), serializationService);
            resultingSet.add(simpleEntry);
        }
    }

    protected void doFullValueScan(Predicate predicate, List<Data> resultingSet) {
        InternalSerializationService serializationService = this.serializationService;
        CachedQueryEntry queryEntry = new CachedQueryEntry();
        Set<Map.Entry<Data, QueryCacheRecord>> entries = this.recordStore.entrySet();
        for (Map.Entry<Data, QueryCacheRecord> entry : entries) {
            Data keyData = entry.getKey();
            QueryCacheRecord record = entry.getValue();
            Object value = record.getValue();
            queryEntry.init(serializationService, keyData, value, this.extractors);
            boolean valid = predicate.apply(queryEntry);
            if (!valid) continue;
            resultingSet.add(queryEntry.getValueData());
        }
    }

    private boolean isIncludeValue() {
        return this.queryCacheConfig.isIncludeValue();
    }

    protected QueryCacheEventService getEventService() {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        return subscriberContext.getEventService();
    }

    protected <T> T toObject(Object valueInRecord) {
        return this.serializationService.toObject(valueInRecord);
    }

    protected Data toData(Object key) {
        return this.serializationService.toData(key, this.partitioningStrategy);
    }

    @Override
    public Extractors getExtractors() {
        return this.extractors;
    }

    @Override
    public void clear() {
        this.recordStore.clear();
        this.indexes.destroyIndexes();
    }
}

