/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.map.impl.querycache.InvokerWrapper;
import com.hazelcast.map.impl.querycache.NodeInvokerWrapper;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.subscriber.AbstractInternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.EventPublisherHelper;
import com.hazelcast.map.impl.querycache.subscriber.MapSubscriberRegistry;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndProvider;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRequest;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberAccumulator;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContextSupport;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberRegistry;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.query.impl.CachedQueryEntry;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryEntry;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.impl.UnmodifiableLazyList;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class DefaultQueryCache<K, V>
extends AbstractInternalQueryCache<K, V> {
    public DefaultQueryCache(String cacheId, String cacheName, QueryCacheConfig queryCacheConfig, IMap delegate, QueryCacheContext context) {
        super(cacheId, cacheName, queryCacheConfig, delegate, context);
    }

    @Override
    public void set(K key, V value, EntryEventType eventType) {
        this.setInternal(key, value, eventType, true);
    }

    @Override
    public void prepopulate(K key, V value) {
        this.setInternal(key, value, EntryEventType.ADDED, false);
    }

    private void setInternal(K key, V value, EntryEventType eventType, boolean doEvictionCheck) {
        QueryCacheRecord oldRecord;
        Data keyData = this.toData(key);
        Data valueData = this.toData(value);
        QueryCacheRecord queryCacheRecord = oldRecord = doEvictionCheck ? this.recordStore.add(keyData, valueData) : this.recordStore.addWithoutEvictionCheck(keyData, valueData);
        if (eventType != null) {
            EventPublisherHelper.publishEntryEvent(this.context, this.mapName, this.cacheId, keyData, valueData, oldRecord, eventType, this.extractors);
        }
    }

    @Override
    public void delete(Object key, EntryEventType eventType) {
        Preconditions.checkNotNull(key, "key cannot be null");
        Data keyData = this.toData(key);
        QueryCacheRecord oldRecord = this.recordStore.remove(keyData);
        if (oldRecord == null) {
            return;
        }
        if (eventType != null) {
            EventPublisherHelper.publishEntryEvent(this.context, this.mapName, this.cacheId, keyData, null, oldRecord, eventType, this.extractors);
        }
    }

    @Override
    public boolean tryRecover() {
        SubscriberAccumulator subscriberAccumulator = this.getOrNullSubscriberAccumulator();
        if (subscriberAccumulator == null) {
            return false;
        }
        ConcurrentMap<Integer, Long> brokenSequences = subscriberAccumulator.getBrokenSequences();
        if (brokenSequences.isEmpty()) {
            return true;
        }
        return this.isTryRecoverSucceeded(brokenSequences);
    }

    private boolean isTryRecoverSucceeded(ConcurrentMap<Integer, Long> brokenSequences) {
        int numberOfBrokenSequences = brokenSequences.size();
        InvokerWrapper invokerWrapper = this.context.getInvokerWrapper();
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        SubscriberContextSupport subscriberContextSupport = subscriberContext.getSubscriberContextSupport();
        ArrayList futures = new ArrayList(numberOfBrokenSequences);
        for (Map.Entry entry : brokenSequences.entrySet()) {
            Integer partitionId = (Integer)entry.getKey();
            Long sequence = (Long)entry.getValue();
            Object recoveryOperation = subscriberContextSupport.createRecoveryOperation(this.mapName, this.cacheId, sequence, partitionId);
            Future future = invokerWrapper.invokeOnPartitionOwner(recoveryOperation, partitionId);
            futures.add(future);
        }
        Collection results = FutureUtil.returnWithDeadline(futures, 1L, TimeUnit.MINUTES);
        int successCount = 0;
        for (Object object : results) {
            Boolean resolvedResponse = subscriberContextSupport.resolveResponseForRecoveryOperation(object);
            if (!Boolean.TRUE.equals(resolvedResponse)) continue;
            ++successCount;
        }
        return successCount == numberOfBrokenSequences;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void destroy() {
        this.removeAccumulatorInfo();
        this.removeSubscriberRegistry();
        this.removeInternalQueryCache();
        ContextMutexFactory.Mutex mutex = this.context.getLifecycleMutexFactory().mutexFor(this.mapName);
        try {
            ContextMutexFactory.Mutex mutex2 = mutex;
            synchronized (mutex2) {
                this.destroyRemoteResources();
                this.removeAllUserDefinedListeners();
            }
        }
        finally {
            IOUtil.closeResource(mutex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void destroyRemoteResources() {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        SubscriberContextSupport subscriberContextSupport = subscriberContext.getSubscriberContextSupport();
        InvokerWrapper invokerWrapper = this.context.getInvokerWrapper();
        if (invokerWrapper instanceof NodeInvokerWrapper) {
            subscriberContext.getEventService().removePublisherListener(this.mapName, this.cacheId, this.publisherListenerId);
            Collection<Member> memberList = this.context.getMemberList();
            ArrayList<Future> futures = new ArrayList<Future>(memberList.size());
            for (Member member : memberList) {
                Address address = member.getAddress();
                Object removePublisher = subscriberContextSupport.createDestroyQueryCacheOperation(this.mapName, this.cacheId);
                Future future = invokerWrapper.invokeOnTarget(removePublisher, address);
                futures.add(future);
            }
            FutureUtil.waitWithDeadline(futures, 5L, TimeUnit.MINUTES);
        } else {
            try {
                subscriberContext.getEventService().removePublisherListener(this.mapName, this.cacheId, this.publisherListenerId);
            }
            finally {
                Object removePublisher = subscriberContextSupport.createDestroyQueryCacheOperation(this.mapName, this.cacheId);
                invokerWrapper.invoke(removePublisher);
            }
        }
    }

    private void removeAllUserDefinedListeners() {
        this.context.getQueryCacheEventService().removeAllListeners(this.mapName, this.cacheId);
    }

    private boolean removeSubscriberRegistry() {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        MapSubscriberRegistry mapSubscriberRegistry = subscriberContext.getMapSubscriberRegistry();
        SubscriberRegistry subscriberRegistry = mapSubscriberRegistry.getOrNull(this.mapName);
        if (subscriberRegistry == null) {
            return true;
        }
        subscriberRegistry.remove(this.cacheId);
        return false;
    }

    private void removeAccumulatorInfo() {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        AccumulatorInfoSupplier accumulatorInfoSupplier = subscriberContext.getAccumulatorInfoSupplier();
        accumulatorInfoSupplier.remove(this.mapName, this.cacheId);
    }

    private boolean removeInternalQueryCache() {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        QueryCacheEndToEndProvider cacheProvider = subscriberContext.getEndToEndQueryCacheProvider();
        cacheProvider.removeSingleQueryCache(this.mapName, this.cacheName);
        this.clear();
        return subscriberContext.getQueryCacheFactory().remove(this);
    }

    @Override
    public boolean containsKey(Object key) {
        Preconditions.checkNotNull(key, "key cannot be null");
        Data keyData = this.toData(key);
        return this.recordStore.containsKey(keyData);
    }

    @Override
    public boolean containsValue(Object value) {
        Preconditions.checkNotNull(value, "value cannot be null");
        return this.recordStore.containsValue(value);
    }

    @Override
    public V get(Object key) {
        Preconditions.checkNotNull(key, "key cannot be null");
        Data keyData = this.toData(key);
        QueryCacheRecord record = this.recordStore.get(keyData);
        if (record == null) {
            return null;
        }
        if (this.includeValue) {
            Object valueInRecord = record.getValue();
            return (V)this.toObject(valueInRecord);
        }
        return this.getDelegate().get(keyData);
    }

    @Override
    public Map<K, V> getAll(Set<K> keys) {
        Preconditions.checkNotNull(keys, "keys cannot be null");
        Preconditions.checkNoNullInside(keys, "supplied key-set cannot contain null key");
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        if (!this.includeValue) {
            return this.getDelegate().getAll(keys);
        }
        Map map = MapUtil.createHashMap(keys.size());
        for (K key : keys) {
            Data keyData = this.toData(key);
            QueryCacheRecord record = this.recordStore.get(keyData);
            if (record == null) continue;
            Object value = this.toObject(record.getValue());
            map.put(key, value);
        }
        return map;
    }

    @Override
    public Set<K> keySet() {
        return this.keySet(TruePredicate.INSTANCE);
    }

    @Override
    public Collection<V> values() {
        return this.values(TruePredicate.INSTANCE);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.entrySet(TruePredicate.INSTANCE);
    }

    @Override
    public Set<K> keySet(Predicate predicate) {
        Preconditions.checkNotNull(predicate, "Predicate cannot be null!");
        HashSet resultingSet = new HashSet();
        Set<QueryableEntry> query = this.indexes.query(predicate, -1);
        if (query != null) {
            for (QueryableEntry entry : query) {
                Object key = this.toObject(entry.getKeyData());
                resultingSet.add(key);
            }
        } else {
            this.doFullKeyScan(predicate, resultingSet);
        }
        return resultingSet;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet(Predicate predicate) {
        Preconditions.checkNotNull(predicate, "Predicate cannot be null!");
        HashSet resultingSet = new HashSet();
        Set<QueryableEntry> query = this.indexes.query(predicate, -1);
        if (query != null) {
            for (QueryableEntry entry : query) {
                CachedQueryEntry copyEntry = new CachedQueryEntry(this.serializationService, entry.getKeyData(), entry.getValueData(), null);
                resultingSet.add(copyEntry);
            }
        } else {
            this.doFullEntryScan(predicate, resultingSet);
        }
        return resultingSet;
    }

    @Override
    public Collection<V> values(Predicate predicate) {
        Preconditions.checkNotNull(predicate, "Predicate cannot be null!");
        if (!this.includeValue) {
            return Collections.emptySet();
        }
        ArrayList<Data> resultingList = new ArrayList<Data>();
        Set<QueryableEntry> query = this.indexes.query(predicate, -1);
        if (query != null) {
            for (QueryableEntry entry : query) {
                resultingList.add(entry.getValueData());
            }
        } else {
            this.doFullValueScan(predicate, resultingList);
        }
        return new UnmodifiableLazyList(resultingList, this.serializationService);
    }

    @Override
    public boolean isEmpty() {
        return this.recordStore.isEmpty();
    }

    @Override
    public int size() {
        return this.recordStore.size();
    }

    @Override
    public String addEntryListener(MapListener listener, boolean includeValue) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        return this.addEntryListenerInternal(listener, null, includeValue);
    }

    @Override
    public String addEntryListener(MapListener listener, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        return this.addEntryListenerInternal(listener, key, includeValue);
    }

    private String addEntryListenerInternal(MapListener listener, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        Data keyData = this.toData(key);
        EntryEventFilter filter = new EntryEventFilter(includeValue, keyData);
        QueryCacheEventService eventService = this.getEventService();
        String mapName = this.delegate.getName();
        return eventService.addListener(mapName, this.cacheId, listener, filter);
    }

    @Override
    public String addEntryListener(MapListener listener, Predicate<K, V> predicate, boolean includeValue) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        Preconditions.checkNotNull(predicate, "predicate cannot be null");
        QueryCacheEventService eventService = this.getEventService();
        QueryEventFilter filter = new QueryEventFilter(includeValue, null, predicate);
        String mapName = this.delegate.getName();
        return eventService.addListener(mapName, this.cacheId, listener, filter);
    }

    @Override
    public String addEntryListener(MapListener listener, Predicate<K, V> predicate, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        Preconditions.checkNotNull(predicate, "predicate cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        QueryCacheEventService eventService = this.getEventService();
        QueryEventFilter filter = new QueryEventFilter(includeValue, this.toData(key), predicate);
        String mapName = this.delegate.getName();
        return eventService.addListener(mapName, this.cacheId, listener, filter);
    }

    @Override
    public boolean removeEntryListener(String id) {
        Preconditions.checkNotNull(id, "listener ID cannot be null");
        QueryCacheEventService eventService = this.getEventService();
        return eventService.removeListener(this.mapName, this.cacheId, id);
    }

    @Override
    public void addIndex(String attribute, boolean ordered) {
        Preconditions.checkNotNull(attribute, "attribute cannot be null");
        this.getIndexes().addOrGetIndex(attribute, ordered);
        InternalSerializationService serializationService = this.context.getSerializationService();
        Set<Map.Entry<Data, QueryCacheRecord>> entries = this.recordStore.entrySet();
        for (Map.Entry<Data, QueryCacheRecord> entry : entries) {
            Data keyData = entry.getKey();
            QueryCacheRecord record = entry.getValue();
            Object value = record.getValue();
            QueryEntry queryable = new QueryEntry(serializationService, keyData, value, this.extractors);
            this.indexes.putEntry(queryable, null, Index.OperationSource.USER);
        }
    }

    @Override
    public String getName() {
        return this.cacheName;
    }

    @Override
    public IMap getDelegate() {
        return this.delegate;
    }

    @Override
    public Indexes getIndexes() {
        return this.indexes;
    }

    @Override
    public void recreate() {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        SubscriberAccumulator subscriberAccumulator = this.getOrNullSubscriberAccumulator();
        if (subscriberAccumulator == null) {
            return;
        }
        subscriberAccumulator.reset();
        QueryCacheRequest request = QueryCacheRequest.newQueryCacheRequest().withCacheName(this.cacheName).forMap(this.delegate).withContext(this.context);
        QueryCacheEndToEndProvider queryCacheEndToEndProvider = subscriberContext.getEndToEndQueryCacheProvider();
        queryCacheEndToEndProvider.tryCreateQueryCache(this.mapName, this.cacheName, subscriberContext.newEndToEndConstructor(request));
    }

    private SubscriberAccumulator getOrNullSubscriberAccumulator() {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        MapSubscriberRegistry mapSubscriberRegistry = subscriberContext.getMapSubscriberRegistry();
        SubscriberRegistry subscriberRegistry = mapSubscriberRegistry.getOrNull(this.mapName);
        if (subscriberRegistry == null) {
            return null;
        }
        Accumulator accumulator = subscriberRegistry.getOrNull(this.cacheId);
        if (accumulator == null) {
            return null;
        }
        return (SubscriberAccumulator)accumulator;
    }

    @Override
    public int removeEntriesOf(int partitionId) {
        int removedEntryCount = 0;
        Set<Data> keys = this.recordStore.keySet();
        for (Data keyData : keys) {
            if (this.context.getPartitionId(keyData) != partitionId || this.recordStore.remove(keyData) == null) continue;
            ++removedEntryCount;
        }
        return removedEntryCount;
    }

    public String toString() {
        return "DefaultQueryCache{mapName='" + this.mapName + '\'' + ", cacheId='" + this.cacheId + '\'' + ", cacheName='" + this.cacheName + '\'' + '}';
    }
}

