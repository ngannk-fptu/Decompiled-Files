/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.queue.operations.AddAllOperation;
import com.hazelcast.collection.impl.queue.operations.ClearOperation;
import com.hazelcast.collection.impl.queue.operations.CompareAndRemoveOperation;
import com.hazelcast.collection.impl.queue.operations.ContainsOperation;
import com.hazelcast.collection.impl.queue.operations.DrainOperation;
import com.hazelcast.collection.impl.queue.operations.IsEmptyOperation;
import com.hazelcast.collection.impl.queue.operations.IteratorOperation;
import com.hazelcast.collection.impl.queue.operations.OfferOperation;
import com.hazelcast.collection.impl.queue.operations.PeekOperation;
import com.hazelcast.collection.impl.queue.operations.PollOperation;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.collection.impl.queue.operations.RemainingCapacityOperation;
import com.hazelcast.collection.impl.queue.operations.RemoveOperation;
import com.hazelcast.collection.impl.queue.operations.SizeOperation;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ItemListener;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.SerializableList;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.List;

abstract class QueueProxySupport
extends AbstractDistributedObject<QueueService>
implements InitializingObject {
    final String name;
    final int partitionId;
    final QueueConfig config;

    QueueProxySupport(String name, QueueService queueService, NodeEngine nodeEngine, QueueConfig config) {
        super(nodeEngine, queueService);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
        this.config = config;
    }

    @Override
    public void initialize() {
        NodeEngine nodeEngine = this.getNodeEngine();
        List<ItemListenerConfig> itemListenerConfigs = this.config.getItemListenerConfigs();
        for (ItemListenerConfig itemListenerConfig : itemListenerConfigs) {
            ItemListener listener = itemListenerConfig.getImplementation();
            if (listener == null && itemListenerConfig.getClassName() != null) {
                try {
                    listener = (ItemListener)ClassLoaderUtil.newInstance(nodeEngine.getConfigClassLoader(), itemListenerConfig.getClassName());
                }
                catch (Exception e) {
                    throw ExceptionUtil.rethrow(e);
                }
            }
            if (listener == null) continue;
            if (listener instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)listener)).setHazelcastInstance(nodeEngine.getHazelcastInstance());
            }
            this.addItemListener(listener, itemListenerConfig.isIncludeValue());
        }
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    boolean offerInternal(Data data, long timeout) throws InterruptedException {
        this.checkObjectNotNull(data);
        OfferOperation operation = new OfferOperation(this.name, timeout, data);
        return (Boolean)this.invokeAndGet(operation, InterruptedException.class);
    }

    public boolean isEmpty() {
        IsEmptyOperation operation = new IsEmptyOperation(this.name);
        return (Boolean)this.invokeAndGet(operation);
    }

    public int size() {
        SizeOperation operation = new SizeOperation(this.name);
        return (Integer)this.invokeAndGet(operation);
    }

    public int remainingCapacity() {
        RemainingCapacityOperation operation = new RemainingCapacityOperation(this.name);
        return (Integer)this.invokeAndGet(operation);
    }

    public void clear() {
        ClearOperation operation = new ClearOperation(this.name);
        this.invokeAndGet(operation);
    }

    Object peekInternal() {
        PeekOperation operation = new PeekOperation(this.name);
        return this.invokeAndGetData(operation);
    }

    Object pollInternal(long timeout) throws InterruptedException {
        PollOperation operation = new PollOperation(this.name, timeout);
        return this.invokeAndGet(operation, InterruptedException.class);
    }

    boolean removeInternal(Data data) {
        this.checkObjectNotNull(data);
        RemoveOperation operation = new RemoveOperation(this.name, data);
        return (Boolean)this.invokeAndGet(operation);
    }

    boolean containsInternal(Collection<Data> dataList) {
        ContainsOperation operation = new ContainsOperation(this.name, dataList);
        return (Boolean)this.invokeAndGet(operation);
    }

    List<Data> listInternal() {
        IteratorOperation operation = new IteratorOperation(this.name);
        SerializableList collectionContainer = (SerializableList)this.invokeAndGet(operation);
        return collectionContainer.getCollection();
    }

    Collection<Data> drainInternal(int maxSize) {
        DrainOperation operation = new DrainOperation(this.name, maxSize);
        SerializableList collectionContainer = (SerializableList)this.invokeAndGet(operation);
        return collectionContainer.getCollection();
    }

    boolean addAllInternal(Collection<Data> dataList) {
        AddAllOperation operation = new AddAllOperation(this.name, dataList);
        return (Boolean)this.invokeAndGet(operation);
    }

    boolean compareAndRemove(Collection<Data> dataList, boolean retain) {
        CompareAndRemoveOperation operation = new CompareAndRemoveOperation(this.name, dataList, retain);
        return (Boolean)this.invokeAndGet(operation);
    }

    protected void checkObjectNotNull(Object o) {
        Preconditions.checkNotNull(o, "Object is null");
    }

    private <T> T invokeAndGet(QueueOperation operation) {
        return this.invokeAndGet(operation, RuntimeException.class);
    }

    private <T, E extends Throwable> T invokeAndGet(QueueOperation operation, Class<E> allowedException) throws E {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            InternalCompletableFuture f = this.invoke(operation);
            return nodeEngine.toObject(f.get());
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable, allowedException);
        }
    }

    private InternalCompletableFuture invoke(Operation operation) {
        NodeEngine nodeEngine = this.getNodeEngine();
        OperationService operationService = nodeEngine.getOperationService();
        return operationService.invokeOnPartition("hz:impl:queueService", operation, this.getPartitionId());
    }

    private Object invokeAndGetData(QueueOperation operation) {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            OperationService operationService = nodeEngine.getOperationService();
            InternalCompletableFuture f = operationService.invokeOnPartition("hz:impl:queueService", operation, this.partitionId);
            return f.get();
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public final String getName() {
        return this.name;
    }

    public String addItemListener(ItemListener listener, boolean includeValue) {
        return ((QueueService)this.getService()).addItemListener(this.name, listener, includeValue, false);
    }

    public boolean removeItemListener(String registrationId) {
        return ((QueueService)this.getService()).removeItemListener(this.name, registrationId);
    }
}

