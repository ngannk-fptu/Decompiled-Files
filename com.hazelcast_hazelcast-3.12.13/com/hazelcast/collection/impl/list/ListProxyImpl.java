/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list;

import com.hazelcast.collection.impl.collection.AbstractCollectionProxyImpl;
import com.hazelcast.collection.impl.list.ListService;
import com.hazelcast.collection.impl.list.operations.ListAddAllOperation;
import com.hazelcast.collection.impl.list.operations.ListAddOperation;
import com.hazelcast.collection.impl.list.operations.ListGetOperation;
import com.hazelcast.collection.impl.list.operations.ListIndexOfOperation;
import com.hazelcast.collection.impl.list.operations.ListRemoveOperation;
import com.hazelcast.collection.impl.list.operations.ListSetOperation;
import com.hazelcast.collection.impl.list.operations.ListSubOperation;
import com.hazelcast.config.CollectionConfig;
import com.hazelcast.core.IList;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.SerializableList;
import com.hazelcast.spi.impl.UnmodifiableLazyList;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListProxyImpl<E>
extends AbstractCollectionProxyImpl<ListService, E>
implements IList<E> {
    protected ListProxyImpl(String name, NodeEngine nodeEngine, ListService service) {
        super(name, nodeEngine, service);
    }

    @Override
    protected CollectionConfig getConfig(NodeEngine nodeEngine) {
        return nodeEngine.getConfig().findListConfig(this.name);
    }

    @Override
    public void add(int index, E e) {
        this.checkObjectNotNull(e);
        this.checkIndexNotNegative(index);
        Data value = this.getNodeEngine().toData(e);
        ListAddOperation operation = new ListAddOperation(this.name, index, value);
        this.invoke(operation);
    }

    @Override
    public E get(int index) {
        this.checkIndexNotNegative(index);
        ListGetOperation operation = new ListGetOperation(this.name, index);
        return (E)this.invoke(operation);
    }

    @Override
    public E set(int index, E element) {
        this.checkObjectNotNull(element);
        this.checkIndexNotNegative(index);
        Data value = this.getNodeEngine().toData(element);
        ListSetOperation operation = new ListSetOperation(this.name, index, value);
        return (E)this.invoke(operation);
    }

    @Override
    public E remove(int index) {
        this.checkIndexNotNegative(index);
        ListRemoveOperation operation = new ListRemoveOperation(this.name, index);
        return (E)this.invoke(operation);
    }

    @Override
    public int indexOf(Object o) {
        return this.indexOfInternal(false, o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.indexOfInternal(true, o);
    }

    private int indexOfInternal(boolean last, Object o) {
        this.checkObjectNotNull(o);
        Data value = this.getNodeEngine().toData(o);
        ListIndexOfOperation operation = new ListIndexOfOperation(this.name, last, value);
        Integer result = (Integer)this.invoke(operation);
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        this.checkObjectNotNull(c);
        this.checkIndexNotNegative(index);
        ArrayList<Data> valueList = new ArrayList<Data>(c.size());
        NodeEngine nodeEngine = this.getNodeEngine();
        for (E e : c) {
            this.checkObjectNotNull(e);
            valueList.add(nodeEngine.toData(e));
        }
        ListAddAllOperation operation = new ListAddAllOperation(this.name, index, valueList);
        Boolean result = (Boolean)this.invoke(operation);
        return result;
    }

    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        List<E> list = this.subList(-1, -1);
        return list.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        ListSubOperation operation = new ListSubOperation(this.name, fromIndex, toIndex);
        SerializableList result = (SerializableList)this.invoke(operation);
        List<Data> collection = result.getCollection();
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        return new UnmodifiableLazyList(collection, serializationService);
    }

    @Override
    public Iterator<E> iterator() {
        return this.listIterator(0);
    }

    @Override
    public Object[] toArray() {
        return this.subList(-1, -1).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        this.checkObjectNotNull(a);
        return this.subList(-1, -1).toArray(a);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }
}

