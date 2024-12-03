/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueIterator;
import com.hazelcast.collection.impl.queue.QueueProxySupport;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.IQueue;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.Preconditions;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class QueueProxyImpl<E>
extends QueueProxySupport
implements IQueue<E>,
InitializingObject {
    public QueueProxyImpl(String name, QueueService queueService, NodeEngine nodeEngine, QueueConfig config) {
        super(name, queueService, nodeEngine, config);
    }

    @Override
    public LocalQueueStats getLocalQueueStats() {
        return ((QueueService)this.getService()).createLocalQueueStats(this.name, this.partitionId);
    }

    @Override
    public boolean add(E e) {
        if (this.offer(e)) {
            return true;
        }
        throw new IllegalStateException("Queue is full!");
    }

    @Override
    public boolean offer(E e) {
        try {
            return this.offer(e, 0L, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void put(E e) throws InterruptedException {
        this.offer(e, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit timeUnit) throws InterruptedException {
        NodeEngine nodeEngine = this.getNodeEngine();
        Data data = nodeEngine.toData(e);
        return this.offerInternal(data, timeUnit.toMillis(timeout));
    }

    @Override
    public E take() throws InterruptedException {
        return this.poll(-1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public E poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        NodeEngine nodeEngine = this.getNodeEngine();
        Object data = this.pollInternal(timeUnit.toMillis(timeout));
        return (E)nodeEngine.toObject(data);
    }

    @Override
    public boolean remove(Object o) {
        NodeEngine nodeEngine = this.getNodeEngine();
        Data data = nodeEngine.toData(o);
        return this.removeInternal(data);
    }

    @Override
    public boolean contains(Object o) {
        NodeEngine nodeEngine = this.getNodeEngine();
        Data data = nodeEngine.toData(o);
        ArrayList<Data> dataSet = new ArrayList<Data>(1);
        dataSet.add(data);
        return this.containsInternal(dataSet);
    }

    @Override
    public int drainTo(Collection<? super E> objects) {
        return this.drainTo(objects, -1);
    }

    @Override
    public int drainTo(Collection<? super E> objects, int i) {
        Preconditions.checkNotNull(objects, "Collection is null");
        Preconditions.checkFalse(this.equals(objects), "Can not drain to same Queue");
        NodeEngine nodeEngine = this.getNodeEngine();
        Collection<Data> dataList = this.drainInternal(i);
        for (Data data : dataList) {
            Object e = nodeEngine.toObject(data);
            objects.add(e);
        }
        return dataList.size();
    }

    @Override
    public E remove() {
        E res = this.poll();
        if (res == null) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return res;
    }

    @Override
    public E poll() {
        try {
            return this.poll(0L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public E element() {
        E res = this.peek();
        if (res == null) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return res;
    }

    @Override
    public E peek() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Object data = this.peekInternal();
        return (E)nodeEngine.toObject(data);
    }

    @Override
    public Iterator<E> iterator() {
        NodeEngine nodeEngine = this.getNodeEngine();
        return new QueueIterator(this.listInternal().iterator(), nodeEngine.getSerializationService(), false);
    }

    @Override
    public Object[] toArray() {
        NodeEngine nodeEngine = this.getNodeEngine();
        List<Data> list = this.listInternal();
        int size = list.size();
        Object[] array = new Object[size];
        for (int i = 0; i < size; ++i) {
            array[i] = nodeEngine.toObject(list.get(i));
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        Object[] tsParam = ts;
        NodeEngine nodeEngine = this.getNodeEngine();
        List<Data> list = this.listInternal();
        int size = list.size();
        if (tsParam.length < size) {
            tsParam = (Object[])Array.newInstance(tsParam.getClass().getComponentType(), size);
        }
        for (int i = 0; i < size; ++i) {
            tsParam[i] = nodeEngine.toObject(list.get(i));
        }
        return tsParam;
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return this.containsInternal(this.getDataList(objects));
    }

    @Override
    public boolean addAll(Collection<? extends E> es) {
        return this.addAllInternal(this.toDataList(es));
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return this.compareAndRemove(this.getDataList(objects), false);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return this.compareAndRemove(this.getDataList(objects), true);
    }

    private List<Data> getDataList(Collection<?> objects) {
        NodeEngine nodeEngine = this.getNodeEngine();
        ArrayList<Data> dataList = new ArrayList<Data>(objects.size());
        for (Object o : objects) {
            dataList.add(nodeEngine.toData(o));
        }
        return dataList;
    }

    private List<Data> toDataList(Collection<?> objects) {
        NodeEngine nodeEngine = this.getNodeEngine();
        ArrayList<Data> dataList = new ArrayList<Data>(objects.size());
        for (Object o : objects) {
            Preconditions.checkNotNull(o, "Object is null");
            dataList.add(nodeEngine.toData(o));
        }
        return dataList;
    }

    @Override
    public String toString() {
        return "IQueue{name='" + this.name + '\'' + '}';
    }
}

