/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionEventFilter;
import com.hazelcast.collection.impl.collection.operations.CollectionAddAllOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionAddOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionClearOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionCompareAndRemoveOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionContainsOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionGetAllOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionIsEmptyOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionRemoveOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionSizeOperation;
import com.hazelcast.config.CollectionConfig;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ItemListener;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.SerializableList;
import com.hazelcast.spi.impl.UnmodifiableLazyList;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractCollectionProxyImpl<S extends RemoteService, E>
extends AbstractDistributedObject<S>
implements InitializingObject {
    protected final String name;
    protected final int partitionId;

    protected AbstractCollectionProxyImpl(String name, NodeEngine nodeEngine, S service) {
        super(nodeEngine, service);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
    }

    @Override
    public void initialize() {
        NodeEngine nodeEngine = this.getNodeEngine();
        CollectionConfig config = this.getConfig(nodeEngine);
        ConfigValidator.checkCollectionConfig(config, nodeEngine.getSplitBrainMergePolicyProvider());
        List<ItemListenerConfig> itemListenerConfigs = config.getItemListenerConfigs();
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

    protected abstract CollectionConfig getConfig(NodeEngine var1);

    @Override
    public String getName() {
        return this.name;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public boolean add(E e) {
        this.checkObjectNotNull(e);
        Data value = this.getNodeEngine().toData(e);
        CollectionAddOperation operation = new CollectionAddOperation(this.name, value);
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    public boolean remove(Object o) {
        this.checkObjectNotNull(o);
        Data value = this.getNodeEngine().toData(o);
        CollectionRemoveOperation operation = new CollectionRemoveOperation(this.name, value);
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    public int size() {
        CollectionSizeOperation operation = new CollectionSizeOperation(this.name);
        Integer result = (Integer)this.invoke(operation);
        return result;
    }

    public boolean isEmpty() {
        CollectionIsEmptyOperation operation = new CollectionIsEmptyOperation(this.name);
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    public boolean contains(Object o) {
        this.checkObjectNotNull(o);
        CollectionContainsOperation operation = new CollectionContainsOperation(this.name, Collections.singleton(this.getNodeEngine().toData(o)));
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    public boolean containsAll(Collection<?> c) {
        this.checkObjectNotNull(c);
        Set<Data> valueSet = SetUtil.createHashSet(c.size());
        NodeEngine nodeEngine = this.getNodeEngine();
        for (Object o : c) {
            this.checkObjectNotNull(o);
            valueSet.add(nodeEngine.toData(o));
        }
        CollectionContainsOperation operation = new CollectionContainsOperation(this.name, valueSet);
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    public boolean addAll(Collection<? extends E> c) {
        this.checkObjectNotNull(c);
        ArrayList<Data> valueList = new ArrayList<Data>(c.size());
        NodeEngine nodeEngine = this.getNodeEngine();
        for (E e : c) {
            this.checkObjectNotNull(e);
            valueList.add(nodeEngine.toData(e));
        }
        CollectionAddAllOperation operation = new CollectionAddAllOperation(this.name, valueList);
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    public boolean retainAll(Collection<?> c) {
        return this.compareAndRemove(true, c);
    }

    public boolean removeAll(Collection<?> c) {
        return this.compareAndRemove(false, c);
    }

    private boolean compareAndRemove(boolean retain, Collection<?> c) {
        this.checkObjectNotNull(c);
        Set<Data> valueSet = SetUtil.createHashSet(c.size());
        NodeEngine nodeEngine = this.getNodeEngine();
        for (Object o : c) {
            this.checkObjectNotNull(o);
            valueSet.add(nodeEngine.toData(o));
        }
        CollectionCompareAndRemoveOperation operation = new CollectionCompareAndRemoveOperation(this.name, retain, valueSet);
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    public void clear() {
        CollectionClearOperation operation = new CollectionClearOperation(this.name);
        this.invoke(operation);
    }

    public Iterator<E> iterator() {
        return Collections.unmodifiableCollection(this.getAll()).iterator();
    }

    public Object[] toArray() {
        return this.getAll().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return this.getAll().toArray(a);
    }

    private Collection<E> getAll() {
        CollectionGetAllOperation operation = new CollectionGetAllOperation(this.name);
        SerializableList result = (SerializableList)this.invoke(operation);
        List<Data> collection = result.getCollection();
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        return new UnmodifiableLazyList(collection, serializationService);
    }

    public String addItemListener(ItemListener<E> listener, boolean includeValue) {
        EventService eventService = this.getNodeEngine().getEventService();
        CollectionEventFilter filter = new CollectionEventFilter(includeValue);
        EventRegistration registration = eventService.registerListener(this.getServiceName(), this.name, filter, listener);
        return registration.getId();
    }

    public boolean removeItemListener(String registrationId) {
        EventService eventService = this.getNodeEngine().getEventService();
        return eventService.deregisterListener(this.getServiceName(), this.name, registrationId);
    }

    protected <T> T invoke(CollectionOperation operation) {
        NodeEngine nodeEngine = this.getNodeEngine();
        try {
            InternalCompletableFuture f = nodeEngine.getOperationService().invokeOnPartition(this.getServiceName(), operation, this.partitionId);
            return nodeEngine.toObject(f.get());
        }
        catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    protected void checkObjectNotNull(Object o) {
        Preconditions.checkNotNull(o, "Object is null");
    }

    protected void checkIndexNotNegative(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index is negative");
        }
    }
}

