/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.MultiMap;
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
import com.hazelcast.monitor.LocalMultiMapStats;
import com.hazelcast.multimap.impl.MultiMapProxySupport;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.EntrySetResponse;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ObjectMultiMapProxy<K, V>
extends MultiMapProxySupport
implements MultiMap<K, V>,
InitializingObject {
    protected static final String NULL_KEY_IS_NOT_ALLOWED = "Null key is not allowed!";
    protected static final String NULL_VALUE_IS_NOT_ALLOWED = "Null value is not allowed!";

    public ObjectMultiMapProxy(MultiMapConfig config, MultiMapService service, NodeEngine nodeEngine, String name) {
        super(config, service, nodeEngine, name);
    }

    @Override
    public void initialize() {
        NodeEngine nodeEngine = this.getNodeEngine();
        List<EntryListenerConfig> listenerConfigs = this.config.getEntryListenerConfigs();
        for (EntryListenerConfig listenerConfig : listenerConfigs) {
            EntryListener listener = null;
            if (listenerConfig.getImplementation() != null) {
                listener = listenerConfig.getImplementation();
            } else if (listenerConfig.getClassName() != null) {
                try {
                    listener = (EntryListener)ClassLoaderUtil.newInstance(nodeEngine.getConfigClassLoader(), listenerConfig.getClassName());
                }
                catch (Exception e) {
                    throw ExceptionUtil.rethrow(e);
                }
            }
            if (listener == null) continue;
            if (listener instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)listener)).setHazelcastInstance(nodeEngine.getHazelcastInstance());
            }
            if (listenerConfig.isLocal()) {
                this.addLocalEntryListener(listener);
                continue;
            }
            this.addEntryListener(listener, listenerConfig.isIncludeValue());
        }
    }

    @Override
    public boolean put(K key, V value) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        Preconditions.checkNotNull(value, NULL_VALUE_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        Data dataValue = nodeEngine.toData(value);
        return this.putInternal(dataKey, dataValue, -1);
    }

    @Override
    public Collection<V> get(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        MultiMapResponse result = this.getAllInternal(dataKey);
        return result.getObjectCollection(nodeEngine);
    }

    @Override
    public boolean remove(Object key, Object value) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        Preconditions.checkNotNull(value, NULL_VALUE_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        Data dataValue = nodeEngine.toData(value);
        return this.removeInternal(dataKey, dataValue);
    }

    @Override
    public Collection<V> remove(Object key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        MultiMapResponse result = this.removeInternal(dataKey);
        return result.getObjectCollection(nodeEngine);
    }

    @Override
    public void delete(Object key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        this.deleteInternal(dataKey);
    }

    @Override
    public Set<K> localKeySet() {
        this.ensureQuorumPresent(QuorumType.READ);
        Set<Data> dataKeySet = this.localKeySetInternal();
        return this.toObjectSet(dataKeySet);
    }

    @Override
    public Set<K> keySet() {
        Set<Data> dataKeySet = this.keySetInternal();
        return this.toObjectSet(dataKeySet);
    }

    @Override
    public Collection<V> values() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Map map = this.valuesInternal();
        LinkedList values = new LinkedList();
        for (Object obj : map.values()) {
            if (obj == null) continue;
            MultiMapResponse response = (MultiMapResponse)nodeEngine.toObject(obj);
            values.addAll(response.getObjectCollection(nodeEngine));
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Map map = this.entrySetInternal();
        HashSet<Map.Entry<K, V>> entrySet = new HashSet<Map.Entry<K, V>>();
        for (Object obj : map.values()) {
            if (obj == null) continue;
            EntrySetResponse response = (EntrySetResponse)nodeEngine.toObject(obj);
            Set entries = response.getObjectEntrySet(nodeEngine);
            entrySet.addAll(entries);
        }
        return entrySet;
    }

    @Override
    public boolean containsKey(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        return this.containsInternal(dataKey, null);
    }

    @Override
    public boolean containsValue(Object value) {
        Preconditions.checkNotNull(value, NULL_VALUE_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data valueKey = nodeEngine.toData(value);
        return this.containsInternal(null, valueKey);
    }

    @Override
    public boolean containsEntry(K key, V value) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        Preconditions.checkNotNull(value, NULL_VALUE_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        Data valueKey = nodeEngine.toData(value);
        return this.containsInternal(dataKey, valueKey);
    }

    @Override
    public int valueCount(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        return this.countInternal(dataKey);
    }

    @Override
    public String addLocalEntryListener(EntryListener<K, V> listener) {
        return ((MultiMapService)this.getService()).addListener(this.name, listener, null, false, true);
    }

    @Override
    public String addEntryListener(EntryListener<K, V> listener, boolean includeValue) {
        return ((MultiMapService)this.getService()).addListener(this.name, listener, null, includeValue, false);
    }

    @Override
    public boolean removeEntryListener(String registrationId) {
        return ((MultiMapService)this.getService()).removeListener(this.name, registrationId);
    }

    @Override
    public String addEntryListener(EntryListener<K, V> listener, K key, boolean includeValue) {
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        return ((MultiMapService)this.getService()).addListener(this.name, listener, dataKey, includeValue, false);
    }

    @Override
    public void lock(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        this.lockSupport.lock(nodeEngine, dataKey);
    }

    @Override
    public void lock(K key, long leaseTime, TimeUnit timeUnit) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        Preconditions.checkPositive(leaseTime, "leaseTime should be positive");
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        this.lockSupport.lock(nodeEngine, dataKey, timeUnit.toMillis(leaseTime));
    }

    @Override
    public boolean isLocked(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        return this.lockSupport.isLocked(nodeEngine, dataKey);
    }

    @Override
    public boolean tryLock(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        return this.lockSupport.tryLock(nodeEngine, dataKey);
    }

    @Override
    public boolean tryLock(K key, long time, TimeUnit timeunit) throws InterruptedException {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        return this.lockSupport.tryLock(nodeEngine, dataKey, time, timeunit);
    }

    @Override
    public boolean tryLock(K key, long time, TimeUnit timeunit, long leaseTime, TimeUnit leaseUnit) throws InterruptedException {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        return this.lockSupport.tryLock(nodeEngine, dataKey, time, timeunit, leaseTime, leaseUnit);
    }

    @Override
    public void unlock(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        this.lockSupport.unlock(nodeEngine, dataKey);
    }

    @Override
    public void forceUnlock(K key) {
        Preconditions.checkNotNull(key, NULL_KEY_IS_NOT_ALLOWED);
        NodeEngine nodeEngine = this.getNodeEngine();
        Data dataKey = nodeEngine.toData(key);
        this.lockSupport.forceUnlock(nodeEngine, dataKey);
    }

    @Override
    public LocalMultiMapStats getLocalMultiMapStats() {
        return ((MultiMapService)this.getService()).createStats(this.name);
    }

    @Override
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> supplier, Aggregation<K, SuppliedValue, Result> aggregation) {
        HazelcastInstance hazelcastInstance = this.getNodeEngine().getHazelcastInstance();
        JobTracker jobTracker = hazelcastInstance.getJobTracker("hz::aggregation-multimap-" + this.getName());
        return this.aggregate(supplier, aggregation, jobTracker);
    }

    @Override
    public <SuppliedValue, Result> Result aggregate(Supplier<K, V, SuppliedValue> supplier, Aggregation<K, SuppliedValue, Result> aggregation, JobTracker jobTracker) {
        try {
            Preconditions.isNotNull(jobTracker, "jobTracker");
            KeyValueSource keyValueSource = KeyValueSource.fromMultiMap(this);
            Job job = jobTracker.newJob(keyValueSource);
            Mapper mapper = aggregation.getMapper(supplier);
            CombinerFactory combinerFactory = aggregation.getCombinerFactory();
            ReducerFactory reducerFactory = aggregation.getReducerFactory();
            Collator<Map.Entry, Result> collator = aggregation.getCollator();
            MappingJob mappingJob = job.mapper(mapper);
            ReducingSubmittableJob reducingJob = combinerFactory != null ? mappingJob.combiner(combinerFactory).reducer(reducerFactory) : mappingJob.reducer(reducerFactory);
            JobCompletableFuture<Result> future = reducingJob.submit(collator);
            return (Result)future.get();
        }
        catch (Exception e) {
            throw new HazelcastException(e);
        }
    }

    private Set<K> toObjectSet(Set<Data> dataSet) {
        NodeEngine nodeEngine = this.getNodeEngine();
        Set keySet = SetUtil.createHashSet(dataSet.size());
        for (Data dataKey : dataSet) {
            keySet.add(nodeEngine.toObject(dataKey));
        }
        return keySet;
    }

    private void ensureQuorumPresent(QuorumType requiredQuorumPermissionType) {
        ((MultiMapService)this.getService()).ensureQuorumPresent(this.name, requiredQuorumPermissionType);
    }
}

