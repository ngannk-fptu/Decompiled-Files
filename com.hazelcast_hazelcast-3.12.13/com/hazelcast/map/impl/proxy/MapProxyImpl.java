/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.proxy;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.internal.journal.EventJournalReader;
import com.hazelcast.internal.util.SimpleCompletedFuture;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.SimpleEntryView;
import com.hazelcast.map.impl.iterator.MapPartitionIterator;
import com.hazelcast.map.impl.iterator.MapQueryPartitionIterator;
import com.hazelcast.map.impl.journal.MapEventJournalReadOperation;
import com.hazelcast.map.impl.journal.MapEventJournalSubscribeOperation;
import com.hazelcast.map.impl.proxy.MapProxySupport;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultUtils;
import com.hazelcast.map.impl.query.Target;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndProvider;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRequest;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.map.journal.EventJournalMapEvent;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.MappingJob;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.ReducingSubmittableJob;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import com.hazelcast.util.executor.DelegatingFuture;
import com.hazelcast.util.function.Function;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MapProxyImpl<K, V>
extends MapProxySupport<K, V>
implements EventJournalReader<EventJournalMapEvent<K, V>> {
    public MapProxyImpl(String name, MapService mapService, NodeEngine nodeEngine, MapConfig mapConfig) {
        super(name, mapService, nodeEngine, mapConfig);
    }

    @Override
    public V get(Object key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        return (V)this.toObject(this.getInternal(key));
    }

    @Override
    public V put(K key, V value) {
        return this.put(key, value, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public V put(K key, V value, long ttl, TimeUnit timeunit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        Data result = this.putInternal(key, valueData, ttl, timeunit, -1L, TimeUnit.MILLISECONDS);
        return (V)this.toObject(result);
    }

    @Override
    public V put(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("put with Max-Idle operation is available when cluster version is 3.11 or higher");
        }
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        Data result = this.putInternal(key, valueData, ttl, ttlUnit, maxIdle, maxIdleUnit);
        return (V)this.toObject(result);
    }

    @Override
    public boolean tryPut(K key, V value, long timeout, TimeUnit timeunit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return this.tryPutInternal(key, valueData, timeout, timeunit);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return this.putIfAbsent(key, value, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public V putIfAbsent(K key, V value, long ttl, TimeUnit timeunit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        Data result = this.putIfAbsentInternal(key, valueData, ttl, timeunit, -1L, TimeUnit.MILLISECONDS);
        return (V)this.toObject(result);
    }

    @Override
    public V putIfAbsent(K key, V value, long ttl, TimeUnit timeunit, long maxIdle, TimeUnit maxIdleUnit) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("putIfAbsent with Max-Idle operation is available when cluster version is 3.11 or higher");
        }
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        Data result = this.putIfAbsentInternal(key, valueData, ttl, timeunit, maxIdle, maxIdleUnit);
        return (V)this.toObject(result);
    }

    @Override
    public void putTransient(K key, V value, long ttl, TimeUnit timeunit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        this.putTransientInternal(key, valueData, ttl, timeunit, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void putTransient(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("putTransient with Max-Idle operation is available when cluster version is 3.11 or higher");
        }
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        this.putTransientInternal(key, valueData, ttl, ttlUnit, maxIdle, maxIdleUnit);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(oldValue, "Null value is not allowed!");
        Preconditions.checkNotNull(newValue, "Null value is not allowed!");
        Data oldValueData = this.toData(oldValue);
        Data newValueData = this.toData(newValue);
        return this.replaceInternal(key, oldValueData, newValueData);
    }

    @Override
    public V replace(K key, V value) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return (V)this.toObject(this.replaceInternal(key, valueData));
    }

    @Override
    public void set(K key, V value) {
        this.set(key, value, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void set(K key, V value, long ttl, TimeUnit ttlUnit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        this.setInternal(key, valueData, ttl, ttlUnit, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void set(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("set with Max-Idle operation is available when cluster version is 3.11 or higher");
        }
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        this.setInternal(key, valueData, ttl, ttlUnit, maxIdle, maxIdleUnit);
    }

    @Override
    public V remove(Object key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data result = this.removeInternal(key);
        return (V)this.toObject(result);
    }

    @Override
    public boolean remove(Object key, Object value) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return this.removeInternal(key, valueData);
    }

    @Override
    public void removeAll(Predicate<K, V> predicate) {
        Preconditions.checkNotNull(predicate, "predicate cannot be null");
        this.handleHazelcastInstanceAwareParams(predicate);
        this.removeAllInternal(predicate);
    }

    @Override
    public void delete(Object key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        this.deleteInternal(key);
    }

    @Override
    public boolean containsKey(Object key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        return this.containsKeyInternal(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return this.containsValueInternal(valueData);
    }

    @Override
    public void lock(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data keyData = this.toDataWithStrategy(key);
        this.lockSupport.lock(this.getNodeEngine(), keyData);
    }

    @Override
    public void lock(Object key, long leaseTime, TimeUnit timeUnit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkPositive(leaseTime, "leaseTime should be positive");
        Data keyData = this.toDataWithStrategy(key);
        this.lockSupport.lock(this.getNodeEngine(), keyData, timeUnit.toMillis(leaseTime));
    }

    @Override
    public void unlock(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data keyData = this.toDataWithStrategy(key);
        this.lockSupport.unlock(this.getNodeEngine(), keyData);
    }

    @Override
    public boolean tryRemove(K key, long timeout, TimeUnit timeunit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        return this.tryRemoveInternal(key, timeout, timeunit);
    }

    @Override
    public ICompletableFuture<V> getAsync(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        return new DelegatingFuture(this.getAsyncInternal(key), this.serializationService);
    }

    @Override
    public boolean isLocked(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data keyData = this.toDataWithStrategy(key);
        return this.lockSupport.isLocked(this.getNodeEngine(), keyData);
    }

    @Override
    public ICompletableFuture<V> putAsync(K key, V value) {
        return this.putAsync((Object)key, (Object)value, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public ICompletableFuture<V> putAsync(K key, V value, long ttl, TimeUnit timeunit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return new DelegatingFuture(this.putAsyncInternal(key, valueData, ttl, timeunit, -1L, TimeUnit.MILLISECONDS), this.serializationService);
    }

    @Override
    public ICompletableFuture<V> putAsync(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("putAsync with Max-Idle operation is available when cluster version is 3.11 or higher");
        }
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return new DelegatingFuture(this.putAsyncInternal(key, valueData, ttl, ttlUnit, maxIdle, maxIdleUnit), this.serializationService);
    }

    @Override
    public ICompletableFuture<Void> setAsync(K key, V value) {
        return this.setAsync((Object)key, (Object)value, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public ICompletableFuture<Void> setAsync(K key, V value, long ttl, TimeUnit timeunit) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return new DelegatingFuture<Void>(this.setAsyncInternal(key, valueData, ttl, timeunit, -1L, TimeUnit.MILLISECONDS), this.serializationService);
    }

    @Override
    public ICompletableFuture<Void> setAsync(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("setAsync with Max-Idle operation is available when cluster version is 3.11 or higher");
        }
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Preconditions.checkNotNull(value, "Null value is not allowed!");
        Data valueData = this.toData(value);
        return new DelegatingFuture<Void>(this.setAsyncInternal(key, valueData, ttl, ttlUnit, maxIdle, maxIdleUnit), this.serializationService);
    }

    @Override
    public ICompletableFuture<V> removeAsync(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        return new DelegatingFuture(this.removeAsyncInternal(key), this.serializationService);
    }

    @Override
    public Map<K, V> getAll(Set<K> keys) {
        if (CollectionUtil.isEmpty(keys)) {
            return Collections.emptyMap();
        }
        int keysSize = keys.size();
        LinkedList<Data> dataKeys = new LinkedList<Data>();
        ArrayList<Object> resultingKeyValuePairs = new ArrayList<Object>(keysSize * 2);
        this.getAllInternal(keys, dataKeys, resultingKeyValuePairs);
        Map result = MapUtil.createHashMap(keysSize);
        int i = 0;
        while (i < resultingKeyValuePairs.size()) {
            Object key = this.toObject(resultingKeyValuePairs.get(i++));
            Object value = this.toObject(resultingKeyValuePairs.get(i++));
            result.put(key, value);
        }
        return result;
    }

    @Override
    public boolean setTtl(K key, long ttl, TimeUnit timeunit) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(timeunit);
        return this.setTtlInternal(key, ttl, timeunit);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        Preconditions.checkNotNull(map, "Null argument map is not allowed");
        this.putAllInternal(map);
    }

    @Override
    public boolean tryLock(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data keyData = this.toDataWithStrategy(key);
        return this.lockSupport.tryLock(this.getNodeEngine(), keyData);
    }

    @Override
    public boolean tryLock(K key, long time, TimeUnit timeunit) throws InterruptedException {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data keyData = this.toDataWithStrategy(key);
        return this.lockSupport.tryLock(this.getNodeEngine(), keyData, time, timeunit);
    }

    @Override
    public boolean tryLock(K key, long time, TimeUnit timeunit, long leaseTime, TimeUnit leaseTimeUnit) throws InterruptedException {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data keyData = this.toDataWithStrategy(key);
        return this.lockSupport.tryLock(this.getNodeEngine(), keyData, time, timeunit, leaseTime, leaseTimeUnit);
    }

    @Override
    public void forceUnlock(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        Data keyData = this.toDataWithStrategy(key);
        this.lockSupport.forceUnlock(this.getNodeEngine(), keyData);
    }

    @Override
    public String addInterceptor(MapInterceptor interceptor) {
        Preconditions.checkNotNull(interceptor, "Interceptor should not be null!");
        this.handleHazelcastInstanceAwareParams(interceptor);
        return this.addMapInterceptorInternal(interceptor);
    }

    @Override
    public void removeInterceptor(String id) {
        Preconditions.checkNotNull(id, "Interceptor ID should not be null!");
        this.removeMapInterceptorInternal(id);
    }

    @Override
    public String addLocalEntryListener(MapListener listener) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        this.handleHazelcastInstanceAwareParams(listener);
        return this.addLocalEntryListenerInternal(listener);
    }

    @Override
    public String addLocalEntryListener(EntryListener listener) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        this.handleHazelcastInstanceAwareParams(listener);
        return this.addLocalEntryListenerInternal(listener);
    }

    @Override
    public String addLocalEntryListener(MapListener listener, Predicate<K, V> predicate, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addLocalEntryListenerInternal(listener, predicate, null, includeValue);
    }

    @Override
    public String addLocalEntryListener(EntryListener listener, Predicate<K, V> predicate, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addLocalEntryListenerInternal(listener, predicate, null, includeValue);
    }

    @Override
    public String addLocalEntryListener(MapListener listener, Predicate<K, V> predicate, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addLocalEntryListenerInternal(listener, predicate, this.toDataWithStrategy(key), includeValue);
    }

    @Override
    public String addLocalEntryListener(EntryListener listener, Predicate<K, V> predicate, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addLocalEntryListenerInternal(listener, predicate, this.toDataWithStrategy(key), includeValue);
    }

    @Override
    public String addEntryListener(MapListener listener, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        this.handleHazelcastInstanceAwareParams(listener);
        return this.addEntryListenerInternal(listener, null, includeValue);
    }

    @Override
    public String addEntryListener(EntryListener listener, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        this.handleHazelcastInstanceAwareParams(listener);
        return this.addEntryListenerInternal(listener, null, includeValue);
    }

    @Override
    public String addEntryListener(MapListener listener, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        this.handleHazelcastInstanceAwareParams(listener);
        return this.addEntryListenerInternal(listener, this.toDataWithStrategy(key), includeValue);
    }

    @Override
    public String addEntryListener(EntryListener listener, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        this.handleHazelcastInstanceAwareParams(listener);
        return this.addEntryListenerInternal(listener, this.toDataWithStrategy(key), includeValue);
    }

    @Override
    public String addEntryListener(MapListener listener, Predicate<K, V> predicate, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addEntryListenerInternal(listener, predicate, this.toDataWithStrategy(key), includeValue);
    }

    @Override
    public String addEntryListener(EntryListener listener, Predicate<K, V> predicate, K key, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addEntryListenerInternal(listener, predicate, this.toDataWithStrategy(key), includeValue);
    }

    @Override
    public String addEntryListener(MapListener listener, Predicate<K, V> predicate, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addEntryListenerInternal(listener, predicate, null, includeValue);
    }

    @Override
    public String addEntryListener(EntryListener listener, Predicate<K, V> predicate, boolean includeValue) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.addEntryListenerInternal(listener, predicate, null, includeValue);
    }

    @Override
    public boolean removeEntryListener(String id) {
        Preconditions.checkNotNull(id, "Listener ID should not be null!");
        return this.removeEntryListenerInternal(id);
    }

    @Override
    public String addPartitionLostListener(MapPartitionLostListener listener) {
        Preconditions.checkNotNull(listener, "Null listener is not allowed!");
        this.handleHazelcastInstanceAwareParams(listener);
        return this.addPartitionLostListenerInternal(listener);
    }

    @Override
    public boolean removePartitionLostListener(String id) {
        Preconditions.checkNotNull(id, "Listener ID should not be null!");
        return this.removePartitionLostListenerInternal(id);
    }

    @Override
    public EntryView<K, V> getEntryView(K key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        SimpleEntryView entryViewInternal = (SimpleEntryView)this.getEntryViewInternal(this.toDataWithStrategy(key));
        if (entryViewInternal == null) {
            return null;
        }
        Data value = (Data)entryViewInternal.getValue();
        entryViewInternal.setKey(key);
        entryViewInternal.setValue(this.toObject(value));
        return entryViewInternal;
    }

    @Override
    public boolean evict(Object key) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        return this.evictInternal(key);
    }

    @Override
    public void evictAll() {
        this.evictAllInternal();
    }

    @Override
    public void loadAll(boolean replaceExistingValues) {
        Preconditions.checkTrue(this.isMapStoreEnabled(), "First you should configure a map store");
        this.loadAllInternal(replaceExistingValues);
    }

    @Override
    public void loadAll(Set<K> keys, boolean replaceExistingValues) {
        Preconditions.checkTrue(this.isMapStoreEnabled(), "First you should configure a map store");
        Preconditions.checkNotNull(keys, "Null keys collection is not allowed!");
        Preconditions.checkNoNullInside(keys, "Null key is not allowed!");
        this.loadInternal(keys, null, replaceExistingValues);
    }

    @Override
    public void clear() {
        this.clearInternal();
    }

    @Override
    public Set<K> keySet() {
        return this.keySet(TruePredicate.INSTANCE);
    }

    @Override
    public Set<K> keySet(Predicate predicate) {
        return this.executePredicate(predicate, IterationType.KEY, true);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.entrySet(TruePredicate.INSTANCE);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet(Predicate predicate) {
        return this.executePredicate(predicate, IterationType.ENTRY, true);
    }

    @Override
    public Collection<V> values() {
        return this.values(TruePredicate.INSTANCE);
    }

    @Override
    public Collection<V> values(Predicate predicate) {
        return this.executePredicate(predicate, IterationType.VALUE, false);
    }

    private Set executePredicate(Predicate predicate, IterationType iterationType, boolean uniqueResult) {
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        QueryResult result = (QueryResult)this.executeQueryInternal(predicate, iterationType, Target.ALL_NODES);
        this.incrementOtherOperationsStat();
        return QueryResultUtils.transformToSet(this.serializationService, result, predicate, iterationType, uniqueResult, false);
    }

    @Override
    public Set<K> localKeySet() {
        return this.localKeySet(TruePredicate.INSTANCE);
    }

    @Override
    public Set<K> localKeySet(Predicate predicate) {
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        QueryResult result = (QueryResult)this.executeQueryInternal(predicate, IterationType.KEY, Target.LOCAL_NODE);
        this.incrementOtherOperationsStat();
        return QueryResultUtils.transformToSet(this.serializationService, result, predicate, IterationType.KEY, false, false);
    }

    @Override
    public Object executeOnKey(K key, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        this.handleHazelcastInstanceAwareParams(entryProcessor);
        Data result = this.executeOnKeyInternal(key, entryProcessor);
        return this.toObject(result);
    }

    @Override
    public Map<K, Object> executeOnKeys(Set<K> keys, EntryProcessor entryProcessor) {
        try {
            return (Map)this.submitToKeys(keys, entryProcessor).get();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    public ICompletableFuture<Map<K, Object>> submitToKeys(Set<K> keys, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(keys, "Null keys collection is not allowed!");
        if (keys.isEmpty()) {
            return new SimpleCompletedFuture<Map<K, Object>>(Collections.emptyMap());
        }
        this.handleHazelcastInstanceAwareParams(entryProcessor);
        Set dataKeys = SetUtil.createHashSet(keys.size());
        return this.submitToKeysInternal((Set)keys, dataKeys, entryProcessor);
    }

    @Override
    public void submitToKey(K key, EntryProcessor entryProcessor, ExecutionCallback callback) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        this.handleHazelcastInstanceAwareParams(entryProcessor, callback);
        this.executeOnKeyInternal((Object)key, entryProcessor, callback);
    }

    @Override
    public ICompletableFuture submitToKey(K key, EntryProcessor entryProcessor) {
        Preconditions.checkNotNull(key, "Null key is not allowed!");
        this.handleHazelcastInstanceAwareParams(entryProcessor);
        InternalCompletableFuture future = this.executeOnKeyInternal((Object)key, entryProcessor, (ExecutionCallback)null);
        return new DelegatingFuture(future, this.serializationService);
    }

    @Override
    public Map<K, Object> executeOnEntries(EntryProcessor entryProcessor) {
        return this.executeOnEntries(entryProcessor, TruePredicate.INSTANCE);
    }

    @Override
    public Map<K, Object> executeOnEntries(EntryProcessor entryProcessor, Predicate predicate) {
        this.handleHazelcastInstanceAwareParams(entryProcessor, predicate);
        ArrayList result = new ArrayList();
        this.executeOnEntriesInternal(entryProcessor, predicate, (List)result);
        if (result.isEmpty()) {
            return Collections.emptyMap();
        }
        Map resultingMap = MapUtil.createHashMap(result.size() / 2);
        int i = 0;
        while (i < result.size()) {
            Data key = (Data)result.get(i++);
            Data value = (Data)result.get(i++);
            resultingMap.put(this.toObject(key), this.toObject(value));
        }
        return resultingMap;
    }

    @Override
    public <R> R aggregate(Aggregator<Map.Entry<K, V>, R> aggregator) {
        return this.aggregate(aggregator, TruePredicate.truePredicate());
    }

    @Override
    public <R> R aggregate(Aggregator<Map.Entry<K, V>, R> aggregator, Predicate<K, V> predicate) {
        Preconditions.checkNotNull(aggregator, "Aggregator should not be null!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        MapProxyImpl.checkNotPagingPredicate(predicate, "aggregate");
        aggregator = (Aggregator)this.serializationService.toObject(this.serializationService.toData(aggregator));
        AggregationResult result = (AggregationResult)this.executeQueryInternal(predicate, aggregator, null, IterationType.ENTRY, Target.ALL_NODES);
        return result.getAggregator().aggregate();
    }

    @Override
    public <R> Collection<R> project(Projection<Map.Entry<K, V>, R> projection) {
        return this.project(projection, TruePredicate.INSTANCE);
    }

    @Override
    public <R> Collection<R> project(Projection<Map.Entry<K, V>, R> projection, Predicate<K, V> predicate) {
        Preconditions.checkNotNull(projection, "Projection should not be null!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        MapProxyImpl.checkNotPagingPredicate(predicate, "project");
        projection = (Projection)this.serializationService.toObject(this.serializationService.toData(projection));
        QueryResult result = (QueryResult)this.executeQueryInternal(predicate, null, projection, IterationType.VALUE, Target.ALL_NODES);
        return QueryResultUtils.transformToSet(this.serializationService, result, predicate, IterationType.VALUE, false, false);
    }

    @Override
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> supplier, Aggregation<K, SuppliedValue, Result> aggregation) {
        Preconditions.checkTrue(InMemoryFormat.NATIVE != this.mapConfig.getInMemoryFormat(), "NATIVE storage format is not supported for MapReduce");
        HazelcastInstance hazelcastInstance = this.getNodeEngine().getHazelcastInstance();
        JobTracker jobTracker = hazelcastInstance.getJobTracker("hz::aggregation-map-" + this.getName());
        return this.aggregate(supplier, aggregation, jobTracker);
    }

    @Override
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> supplier, Aggregation<K, SuppliedValue, Result> aggregation, JobTracker jobTracker) {
        Preconditions.checkTrue(InMemoryFormat.NATIVE != this.mapConfig.getInMemoryFormat(), "NATIVE storage format is not supported for MapReduce");
        try {
            Preconditions.isNotNull(jobTracker, "jobTracker");
            KeyValueSource keyValueSource = KeyValueSource.fromMap(this);
            Job job = jobTracker.newJob(keyValueSource);
            Mapper mapper = aggregation.getMapper(supplier);
            CombinerFactory combinerFactory = aggregation.getCombinerFactory();
            ReducerFactory reducerFactory = aggregation.getReducerFactory();
            Collator<Map.Entry, Result> collator = aggregation.getCollator();
            MappingJob mappingJob = job.mapper(mapper);
            ReducingSubmittableJob reducingJob = combinerFactory == null ? mappingJob.reducer(reducerFactory) : mappingJob.combiner(combinerFactory).reducer(reducerFactory);
            JobCompletableFuture<Result> future = reducingJob.submit(collator);
            return (Result)future.get();
        }
        catch (Exception e) {
            throw new HazelcastException(e);
        }
    }

    protected Object invoke(Operation operation, int partitionId) throws Throwable {
        InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
        Object response = future.get();
        Object result = this.toObject(response);
        if (result instanceof Throwable) {
            throw (Throwable)result;
        }
        return result;
    }

    public Iterator<Map.Entry<K, V>> iterator(int fetchSize, int partitionId, boolean prefetchValues) {
        return new MapPartitionIterator(this, fetchSize, partitionId, prefetchValues);
    }

    public <R> Iterator<R> iterator(int fetchSize, int partitionId, Projection<Map.Entry<K, V>, R> projection, Predicate<K, V> predicate) {
        if (predicate instanceof PagingPredicate) {
            throw new IllegalArgumentException("Paging predicate is not allowed when iterating map by query");
        }
        Preconditions.checkNotNull(projection, "Projection should not be null!");
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        projection = (Projection)this.serializationService.toObject(this.serializationService.toData(projection));
        this.handleHazelcastInstanceAwareParams(predicate);
        return new MapQueryPartitionIterator(this, fetchSize, partitionId, predicate, projection);
    }

    @Override
    public ICompletableFuture<EventJournalInitialSubscriberState> subscribeToEventJournal(int partitionId) {
        MapEventJournalSubscribeOperation op = new MapEventJournalSubscribeOperation(this.name);
        op.setPartitionId(partitionId);
        return this.operationService.invokeOnPartition(op);
    }

    @Override
    public <T> ICompletableFuture<ReadResultSet<T>> readFromEventJournal(long startSequence, int minSize, int maxSize, int partitionId, com.hazelcast.util.function.Predicate<? super EventJournalMapEvent<K, V>> predicate, Function<? super EventJournalMapEvent<K, V>, ? extends T> projection) {
        if (maxSize < minSize) {
            throw new IllegalArgumentException("maxSize " + maxSize + " must be greater or equal to minSize " + minSize);
        }
        ManagedContext context = this.serializationService.getManagedContext();
        context.initialize(predicate);
        context.initialize(projection);
        MapEventJournalReadOperation op = new MapEventJournalReadOperation(this.name, startSequence, minSize, maxSize, predicate, projection);
        op.setPartitionId(partitionId);
        return this.operationService.invokeOnPartition(op);
    }

    @Override
    public String toString() {
        return "IMap{name='" + this.name + '\'' + '}';
    }

    @Override
    public QueryCache<K, V> getQueryCache(String name) {
        Preconditions.checkNotNull(name, "name cannot be null");
        return this.getQueryCacheInternal(name, null, null, null, this);
    }

    @Override
    public QueryCache<K, V> getQueryCache(String name, Predicate<K, V> predicate, boolean includeValue) {
        Preconditions.checkNotNull(name, "name cannot be null");
        Preconditions.checkNotNull(predicate, "predicate cannot be null");
        Preconditions.checkNotInstanceOf(PagingPredicate.class, predicate, "predicate");
        this.handleHazelcastInstanceAwareParams(predicate);
        return this.getQueryCacheInternal(name, null, predicate, includeValue, this);
    }

    @Override
    public QueryCache<K, V> getQueryCache(String name, MapListener listener, Predicate<K, V> predicate, boolean includeValue) {
        Preconditions.checkNotNull(name, "name cannot be null");
        Preconditions.checkNotNull(predicate, "predicate cannot be null");
        Preconditions.checkNotInstanceOf(PagingPredicate.class, predicate, "predicate");
        this.handleHazelcastInstanceAwareParams(listener, predicate);
        return this.getQueryCacheInternal(name, listener, predicate, includeValue, this);
    }

    private QueryCache<K, V> getQueryCacheInternal(String name, MapListener listener, Predicate<K, V> predicate, Boolean includeValue, IMap<K, V> map) {
        QueryCacheContext queryCacheContext = this.mapServiceContext.getQueryCacheContext();
        QueryCacheRequest request = QueryCacheRequest.newQueryCacheRequest().forMap(map).withCacheName(name).withListener(listener).withPredicate(predicate).withIncludeValue(includeValue).withContext(queryCacheContext);
        return this.createQueryCache(request);
    }

    private QueryCache<K, V> createQueryCache(QueryCacheRequest request) {
        QueryCacheContext queryCacheContext = request.getContext();
        SubscriberContext subscriberContext = queryCacheContext.getSubscriberContext();
        QueryCacheEndToEndProvider queryCacheEndToEndProvider = subscriberContext.getEndToEndQueryCacheProvider();
        return queryCacheEndToEndProvider.getOrCreateQueryCache(request.getMapName(), request.getCacheName(), subscriberContext.newEndToEndConstructor(request));
    }

    private static void checkNotPagingPredicate(Predicate predicate, String method) {
        if (predicate instanceof PagingPredicate) {
            throw new IllegalArgumentException("PagingPredicate not supported in " + method + " method");
        }
    }
}

