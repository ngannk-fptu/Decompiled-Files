/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.concurrent.lock.LockProxySupport;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.CountOperation;
import com.hazelcast.multimap.impl.operations.DeleteOperation;
import com.hazelcast.multimap.impl.operations.GetAllOperation;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.multimap.impl.operations.PutOperation;
import com.hazelcast.multimap.impl.operations.RemoveAllOperation;
import com.hazelcast.multimap.impl.operations.RemoveOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.ThreadUtil;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MultiMapProxySupport
extends AbstractDistributedObject<MultiMapService> {
    protected final MultiMapConfig config;
    protected final String name;
    protected final LockProxySupport lockSupport;

    protected MultiMapProxySupport(MultiMapConfig config, MultiMapService service, NodeEngine nodeEngine, String name) {
        super(nodeEngine, service);
        this.config = config;
        this.name = name;
        this.lockSupport = new LockProxySupport(new DistributedObjectNamespace("hz:impl:multiMapService", name), LockServiceImpl.getMaxLeaseTimeInMillis(nodeEngine.getProperties()));
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected Boolean putInternal(Data dataKey, Data dataValue, int index) {
        try {
            PutOperation operation = new PutOperation(this.name, dataKey, this.getThreadId(), dataValue, index);
            return (Boolean)this.invoke(operation, dataKey);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected MultiMapResponse getAllInternal(Data dataKey) {
        try {
            GetAllOperation operation = new GetAllOperation(this.name, dataKey);
            operation.setThreadId(ThreadUtil.getThreadId());
            return (MultiMapResponse)this.invoke(operation, dataKey);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected Boolean removeInternal(Data dataKey, Data dataValue) {
        try {
            RemoveOperation operation = new RemoveOperation(this.name, dataKey, this.getThreadId(), dataValue);
            return (Boolean)this.invoke(operation, dataKey);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected MultiMapResponse removeInternal(Data dataKey) {
        try {
            RemoveAllOperation operation = new RemoveAllOperation(this.name, dataKey, this.getThreadId());
            return (MultiMapResponse)this.invoke(operation, dataKey);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected void deleteInternal(Data dataKey) {
        try {
            DeleteOperation operation = new DeleteOperation(this.name, dataKey, this.getThreadId());
            this.invoke(operation, dataKey);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected Set<Data> localKeySetInternal() {
        return ((MultiMapService)this.getService()).localKeySet(this.name);
    }

    protected Set<Data> keySetInternal() {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            Map<Integer, Object> results = nodeEngine.getOperationService().invokeOnAllPartitions("hz:impl:multiMapService", new MultiMapOperationFactory(this.name, MultiMapOperationFactory.OperationFactoryType.KEY_SET));
            HashSet<Data> keySet = new HashSet<Data>();
            for (Object result : results.values()) {
                MultiMapResponse response;
                if (result == null || (response = (MultiMapResponse)nodeEngine.toObject(result)).getCollection() == null) continue;
                keySet.addAll(response.getCollection());
            }
            return keySet;
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected Map valuesInternal() {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            Map<Integer, Object> results = nodeEngine.getOperationService().invokeOnAllPartitions("hz:impl:multiMapService", new MultiMapOperationFactory(this.name, MultiMapOperationFactory.OperationFactoryType.VALUES));
            return results;
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected Map entrySetInternal() {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            Map<Integer, Object> results = nodeEngine.getOperationService().invokeOnAllPartitions("hz:impl:multiMapService", new MultiMapOperationFactory(this.name, MultiMapOperationFactory.OperationFactoryType.ENTRY_SET));
            return results;
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected boolean containsInternal(Data key, Data value) {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            Map<Integer, Object> results = nodeEngine.getOperationService().invokeOnAllPartitions("hz:impl:multiMapService", new MultiMapOperationFactory(this.name, MultiMapOperationFactory.OperationFactoryType.CONTAINS, key, value, ThreadUtil.getThreadId()));
            for (Object obj : results.values()) {
                Boolean result;
                if (obj == null || !(result = (Boolean)nodeEngine.toObject(obj)).booleanValue()) continue;
                return true;
            }
            return false;
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    public int size() {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            Map<Integer, Object> results = nodeEngine.getOperationService().invokeOnAllPartitions("hz:impl:multiMapService", new MultiMapOperationFactory(this.name, MultiMapOperationFactory.OperationFactoryType.SIZE));
            long size = 0L;
            for (Object obj : results.values()) {
                if (obj == null) continue;
                Integer result = (Integer)nodeEngine.toObject(obj);
                size += (long)result.intValue();
            }
            return MapUtil.toIntSize(size);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    public void clear() {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            Map<Integer, Object> resultMap = nodeEngine.getOperationService().invokeOnAllPartitions("hz:impl:multiMapService", new MultiMapOperationFactory(this.name, MultiMapOperationFactory.OperationFactoryType.CLEAR));
            int numberOfAffectedEntries = 0;
            for (Object o : resultMap.values()) {
                numberOfAffectedEntries += ((Integer)o).intValue();
            }
            this.publishMultiMapEvent(numberOfAffectedEntries, EntryEventType.CLEAR_ALL);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    private void publishMultiMapEvent(int numberOfAffectedEntries, EntryEventType eventType) {
        ((MultiMapService)this.getService()).publishMultiMapEvent(this.name, eventType, numberOfAffectedEntries);
    }

    protected Integer countInternal(Data dataKey) {
        try {
            CountOperation operation = new CountOperation(this.name, dataKey);
            operation.setThreadId(ThreadUtil.getThreadId());
            return (Integer)this.invoke(operation, dataKey);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    private <T> T invoke(Operation operation, Data dataKey) {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            Object result;
            int partitionId = nodeEngine.getPartitionService().getPartitionId(dataKey);
            if (this.config.isStatisticsEnabled()) {
                long startTimeNanos = System.nanoTime();
                InternalCompletableFuture future = nodeEngine.getOperationService().invokeOnPartition("hz:impl:multiMapService", operation, partitionId);
                result = future.get();
                if (operation instanceof PutOperation) {
                    ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementPutLatencyNanos(System.nanoTime() - startTimeNanos);
                } else if (operation instanceof RemoveOperation || operation instanceof RemoveAllOperation || operation instanceof DeleteOperation) {
                    ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementRemoveLatencyNanos(System.nanoTime() - startTimeNanos);
                } else if (operation instanceof GetAllOperation) {
                    ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementGetLatencyNanos(System.nanoTime() - startTimeNanos);
                }
            } else {
                InternalCompletableFuture future = nodeEngine.getOperationService().invokeOnPartition("hz:impl:multiMapService", operation, partitionId);
                result = future.get();
            }
            return nodeEngine.toObject(result);
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    private long getThreadId() {
        return ThreadUtil.getThreadId();
    }

    @Override
    public String toString() {
        return "MultiMap{name=" + this.name + '}';
    }
}

