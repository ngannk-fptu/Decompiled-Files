/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.ReachedMaxSizeException;
import com.hazelcast.map.impl.mapstore.writebehind.IPredicate;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class BoundedWriteBehindQueue<E>
implements WriteBehindQueue<E> {
    private final AtomicInteger writeBehindQueueItemCounter;
    private final int maxCapacity;
    private final WriteBehindQueue<E> queue;

    BoundedWriteBehindQueue(int maxCapacity, AtomicInteger writeBehindQueueItemCounter, WriteBehindQueue<E> queue) {
        this.maxCapacity = maxCapacity;
        this.writeBehindQueueItemCounter = writeBehindQueueItemCounter;
        this.queue = queue;
    }

    @Override
    public void addFirst(Collection<E> collection) {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        this.addCapacity(collection.size());
        this.queue.addFirst(collection);
    }

    @Override
    public void addLast(E e) {
        this.addCapacity(1);
        this.queue.addLast(e);
    }

    @Override
    public E peek() {
        return this.queue.peek();
    }

    @Override
    public boolean removeFirstOccurrence(E e) {
        boolean result = this.queue.removeFirstOccurrence(e);
        if (result) {
            this.addCapacity(-1);
        }
        return result;
    }

    @Override
    public int drainTo(Collection<E> collection) {
        int size = this.queue.drainTo(collection);
        this.addCapacity(-size);
        return size;
    }

    @Override
    public boolean contains(E e) {
        return this.queue.contains(e);
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public void clear() {
        int size = this.size();
        this.queue.clear();
        this.addCapacity(-size);
    }

    @Override
    public List<E> asList() {
        return this.queue.asList();
    }

    @Override
    public void filter(IPredicate<E> predicate, Collection<E> collection) {
        this.queue.filter(predicate, collection);
    }

    private void addCapacity(int capacity) {
        int maxCapacity = this.maxCapacity;
        AtomicInteger writeBehindQueueItemCounter = this.writeBehindQueueItemCounter;
        int currentCapacity = writeBehindQueueItemCounter.get();
        int newCapacity = currentCapacity + capacity;
        if (newCapacity < 0) {
            return;
        }
        if (maxCapacity < newCapacity) {
            this.throwException(currentCapacity, maxCapacity, capacity);
        }
        while (!writeBehindQueueItemCounter.compareAndSet(currentCapacity, newCapacity)) {
            currentCapacity = writeBehindQueueItemCounter.get();
            newCapacity = currentCapacity + capacity;
            if (newCapacity < 0) {
                return;
            }
            if (maxCapacity >= newCapacity) continue;
            this.throwException(currentCapacity, maxCapacity, capacity);
        }
    }

    private void throwException(int currentCapacity, int maxSize, int requiredCapacity) {
        String msg = String.format("Reached node-wide max capacity for write-behind-stores. Max allowed capacity = [%d], current capacity = [%d], required capacity = [%d]", maxSize, currentCapacity, requiredCapacity);
        throw new ReachedMaxSizeException(msg);
    }
}

