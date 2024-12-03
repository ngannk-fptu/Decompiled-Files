/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl;

import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.util.ResultSet;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapEntries;
import com.hazelcast.replicatedmap.impl.operation.ClearOperationFactory;
import com.hazelcast.replicatedmap.impl.operation.PutAllOperation;
import com.hazelcast.replicatedmap.impl.operation.PutOperation;
import com.hazelcast.replicatedmap.impl.operation.RemoveOperation;
import com.hazelcast.replicatedmap.impl.operation.RequestMapDataOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.replicatedmap.impl.record.ReplicatedEntryEventFilter;
import com.hazelcast.replicatedmap.impl.record.ReplicatedQueryEventFilter;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ReplicatedMapProxy<K, V>
extends AbstractDistributedObject<ReplicatedMapService>
implements ReplicatedMap<K, V>,
InitializingObject {
    private static final int WAIT_INTERVAL_MILLIS = 1000;
    private static final int RETRY_INTERVAL_COUNT = 3;
    private static final int KEY_SET_MIN_SIZE = 16;
    private static final int KEY_SET_STORE_MULTIPLE = 4;
    private final String name;
    private final NodeEngine nodeEngine;
    private final ReplicatedMapService service;
    private final ReplicatedMapEventPublishingService eventPublishingService;
    private final SerializationService serializationService;
    private final InternalPartitionServiceImpl partitionService;
    private final ReplicatedMapConfig config;
    private int retryCount;

    ReplicatedMapProxy(NodeEngine nodeEngine, String name, ReplicatedMapService service, ReplicatedMapConfig config) {
        super(nodeEngine, service);
        this.name = name;
        this.nodeEngine = nodeEngine;
        this.service = service;
        this.eventPublishingService = service.getEventPublishingService();
        this.serializationService = nodeEngine.getSerializationService();
        this.partitionService = (InternalPartitionServiceImpl)nodeEngine.getPartitionService();
        this.config = config;
    }

    @Override
    public void initialize() {
        this.service.initializeListeners(this.name);
        if (this.nodeEngine.getClusterService().getSize() == 1) {
            return;
        }
        this.fireMapDataLoadingTasks();
        if (!this.config.isAsyncFillup()) {
            for (int i = 0; i < this.nodeEngine.getPartitionService().getPartitionCount(); ++i) {
                ReplicatedRecordStore store = this.service.getReplicatedRecordStore(this.name, false, i);
                while (store == null || !store.isLoaded()) {
                    if (this.retryCount++ % 3 == 0) {
                        this.requestDataForPartition(i);
                    }
                    this.sleep();
                    if (store != null) continue;
                    store = this.service.getReplicatedRecordStore(this.name, false, i);
                }
            }
        }
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000L);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void fireMapDataLoadingTasks() {
        for (int i = 0; i < this.nodeEngine.getPartitionService().getPartitionCount(); ++i) {
            this.requestDataForPartition(i);
        }
    }

    private void requestDataForPartition(int partitionId) {
        RequestMapDataOperation requestMapDataOperation = new RequestMapDataOperation(this.name);
        OperationService operationService = this.nodeEngine.getOperationService();
        operationService.createInvocationBuilder("hz:impl:replicatedMapService", (Operation)requestMapDataOperation, partitionId).setTryCount(3).invoke();
    }

    @Override
    protected boolean preDestroy() {
        if (super.preDestroy()) {
            this.eventPublishingService.fireMapClearedEvent(this.size(), this.name);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPartitionKey() {
        return this.getName();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public int size() {
        this.ensureQuorumPresent(QuorumType.READ);
        Collection<ReplicatedRecordStore> stores = this.service.getAllReplicatedRecordStores(this.getName());
        int size = 0;
        for (ReplicatedRecordStore store : stores) {
            size += store.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        this.ensureQuorumPresent(QuorumType.READ);
        Collection<ReplicatedRecordStore> stores = this.service.getAllReplicatedRecordStores(this.getName());
        for (ReplicatedRecordStore store : stores) {
            if (store.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        this.ensureQuorumPresent(QuorumType.READ);
        Preconditions.isNotNull(key, "key");
        int partitionId = this.partitionService.getPartitionId(key);
        ReplicatedRecordStore store = this.service.getReplicatedRecordStore(this.name, false, partitionId);
        return store != null && store.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        this.ensureQuorumPresent(QuorumType.READ);
        Preconditions.isNotNull(value, "value");
        Collection<ReplicatedRecordStore> stores = this.service.getAllReplicatedRecordStores(this.getName());
        for (ReplicatedRecordStore store : stores) {
            if (!store.containsValue(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        this.ensureQuorumPresent(QuorumType.READ);
        Preconditions.isNotNull(key, "key");
        int partitionId = this.partitionService.getPartitionId(key);
        ReplicatedRecordStore store = this.service.getReplicatedRecordStore(this.getName(), false, partitionId);
        if (store == null) {
            return null;
        }
        return (V)store.get(key);
    }

    @Override
    public V put(K key, V value) {
        Preconditions.isNotNull(key, "key");
        Preconditions.isNotNull(value, "value");
        Data dataKey = this.nodeEngine.toData(key);
        Data dataValue = this.nodeEngine.toData(value);
        int partitionId = this.nodeEngine.getPartitionService().getPartitionId(dataKey);
        PutOperation putOperation = new PutOperation(this.getName(), dataKey, dataValue);
        InternalCompletableFuture future = this.getOperationService().invokeOnPartition(this.getServiceName(), putOperation, partitionId);
        VersionResponsePair result = (VersionResponsePair)future.join();
        return (V)this.nodeEngine.toObject(result.getResponse());
    }

    @Override
    public V put(K key, V value, long ttl, TimeUnit timeUnit) {
        Preconditions.isNotNull(key, "key");
        Preconditions.isNotNull(value, "value");
        Preconditions.isNotNull(timeUnit, "timeUnit");
        if (ttl < 0L) {
            throw new IllegalArgumentException("ttl must be a positive integer");
        }
        long ttlMillis = timeUnit.toMillis(ttl);
        Data dataKey = this.nodeEngine.toData(key);
        Data dataValue = this.nodeEngine.toData(value);
        int partitionId = this.partitionService.getPartitionId(dataKey);
        PutOperation putOperation = new PutOperation(this.getName(), dataKey, dataValue, ttlMillis);
        InternalCompletableFuture future = this.getOperationService().invokeOnPartition(this.getServiceName(), putOperation, partitionId);
        VersionResponsePair result = (VersionResponsePair)future.join();
        return (V)this.nodeEngine.toObject(result.getResponse());
    }

    @Override
    public V remove(Object key) {
        Preconditions.isNotNull(key, "key");
        Data dataKey = this.nodeEngine.toData(key);
        int partitionId = this.partitionService.getPartitionId(key);
        RemoveOperation removeOperation = new RemoveOperation(this.getName(), dataKey);
        InternalCompletableFuture future = this.getOperationService().invokeOnPartition(this.getServiceName(), removeOperation, partitionId);
        VersionResponsePair result = (VersionResponsePair)future.join();
        return (V)this.nodeEngine.toObject(result.getResponse());
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        Preconditions.checkNotNull(entries, "entries cannot be null");
        int mapSize = entries.size();
        if (mapSize == 0) {
            return;
        }
        int partitionCount = this.partitionService.getPartitionCount();
        int initialSize = this.getPutAllInitialSize(mapSize, partitionCount);
        try {
            ArrayList<Future> futures = new ArrayList<Future>(partitionCount);
            ReplicatedMapEntries[] entrySetPerPartition = new ReplicatedMapEntries[partitionCount];
            for (Map.Entry<K, V> entry : entries.entrySet()) {
                Preconditions.isNotNull(entry.getKey(), "key");
                Preconditions.isNotNull(entry.getValue(), "value");
                int partitionId = this.partitionService.getPartitionId(entry.getKey());
                ReplicatedMapEntries mapEntries = entrySetPerPartition[partitionId];
                if (mapEntries == null) {
                    entrySetPerPartition[partitionId] = mapEntries = new ReplicatedMapEntries(initialSize);
                }
                Object keyData = this.serializationService.toData(entry.getKey());
                Object valueData = this.serializationService.toData(entry.getValue());
                mapEntries.add((Data)keyData, (Data)valueData);
            }
            for (int partitionId = 0; partitionId < partitionCount; ++partitionId) {
                ReplicatedMapEntries entrySet = entrySetPerPartition[partitionId];
                if (entrySet == null) continue;
                Future future = this.createPutAllOperationFuture(this.name, entrySet, partitionId);
                futures.add(future);
            }
            for (Future future : futures) {
                future.get();
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private int getPutAllInitialSize(int mapSize, int partitionCount) {
        if (mapSize == 1) {
            return 1;
        }
        return (int)Math.ceil((double)(20.0f * (float)mapSize / (float)partitionCount) / Math.log10(mapSize));
    }

    private Future createPutAllOperationFuture(String name, ReplicatedMapEntries entrySet, int partitionId) {
        OperationService operationService = this.nodeEngine.getOperationService();
        PutAllOperation op = new PutAllOperation(name, entrySet);
        return operationService.invokeOnPartition("hz:impl:replicatedMapService", op, partitionId);
    }

    @Override
    public void clear() {
        OperationService operationService = this.nodeEngine.getOperationService();
        try {
            Map<Integer, Object> results = operationService.invokeOnAllPartitions("hz:impl:replicatedMapService", new ClearOperationFactory(this.name));
            int deletedEntrySize = 0;
            for (Object deletedEntryPerPartition : results.values()) {
                deletedEntrySize += ((Integer)deletedEntryPerPartition).intValue();
            }
            this.eventPublishingService.fireMapClearedEvent(deletedEntrySize, this.name);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    @Override
    public boolean removeEntryListener(String id) {
        return this.eventPublishingService.removeEventListener(this.name, id);
    }

    @Override
    public String addEntryListener(EntryListener<K, V> listener) {
        Preconditions.isNotNull(listener, "listener");
        return this.eventPublishingService.addEventListener(listener, TrueEventFilter.INSTANCE, this.name);
    }

    @Override
    public String addEntryListener(EntryListener<K, V> listener, K key) {
        Preconditions.isNotNull(listener, "listener");
        ReplicatedEntryEventFilter eventFilter = new ReplicatedEntryEventFilter((Data)this.serializationService.toData(key));
        return this.eventPublishingService.addEventListener(listener, eventFilter, this.name);
    }

    @Override
    public String addEntryListener(EntryListener<K, V> listener, Predicate<K, V> predicate) {
        Preconditions.isNotNull(listener, "listener");
        Preconditions.isNotNull(predicate, "predicate");
        ReplicatedQueryEventFilter eventFilter = new ReplicatedQueryEventFilter(null, predicate);
        return this.eventPublishingService.addEventListener(listener, eventFilter, this.name);
    }

    @Override
    public String addEntryListener(EntryListener<K, V> listener, Predicate<K, V> predicate, K key) {
        Preconditions.isNotNull(listener, "listener");
        Preconditions.isNotNull(predicate, "predicate");
        ReplicatedQueryEventFilter eventFilter = new ReplicatedQueryEventFilter((Data)this.serializationService.toData(key), predicate);
        return this.eventPublishingService.addEventListener(listener, eventFilter, this.name);
    }

    @Override
    public Set<K> keySet() {
        this.ensureQuorumPresent(QuorumType.READ);
        Collection<ReplicatedRecordStore> stores = this.service.getAllReplicatedRecordStores(this.getName());
        Set keySet = SetUtil.createHashSet(Math.max(16, stores.size() * 4));
        for (ReplicatedRecordStore store : stores) {
            keySet.addAll(store.keySet(true));
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        this.ensureQuorumPresent(QuorumType.READ);
        Collection<ReplicatedRecordStore> stores = this.service.getAllReplicatedRecordStores(this.getName());
        ArrayList values = new ArrayList();
        for (ReplicatedRecordStore store : stores) {
            values.addAll(store.values(true));
        }
        return values;
    }

    @Override
    public Collection<V> values(Comparator<V> comparator) {
        this.ensureQuorumPresent(QuorumType.READ);
        Collection<ReplicatedRecordStore> stores = this.service.getAllReplicatedRecordStores(this.getName());
        ArrayList values = new ArrayList();
        for (ReplicatedRecordStore store : stores) {
            values.addAll(store.values(comparator));
        }
        Collections.sort(values, comparator);
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        this.ensureQuorumPresent(QuorumType.READ);
        Collection<ReplicatedRecordStore> stores = this.service.getAllReplicatedRecordStores(this.getName());
        ArrayList<Map.Entry> entries = new ArrayList<Map.Entry>();
        for (ReplicatedRecordStore store : stores) {
            entries.addAll(store.entrySet(true));
        }
        ResultSet result = new ResultSet(entries, IterationType.ENTRY);
        return result;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " -> " + this.name;
    }

    @Override
    public LocalReplicatedMapStats getReplicatedMapStats() {
        return this.service.createReplicatedMapStats(this.name);
    }

    private void ensureQuorumPresent(QuorumType requiredQuorumPermissionType) {
        this.service.ensureQuorumPresent(this.name, requiredQuorumPermissionType);
    }
}

