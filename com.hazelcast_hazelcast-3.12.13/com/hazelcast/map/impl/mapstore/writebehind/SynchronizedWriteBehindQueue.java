/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.writebehind.IPredicate;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.List;

class SynchronizedWriteBehindQueue<E>
implements WriteBehindQueue<E> {
    private final WriteBehindQueue<E> queue;
    private final Object mutex;

    SynchronizedWriteBehindQueue(WriteBehindQueue<E> queue) {
        this.queue = Preconditions.checkNotNull(queue, "queue can't be null");
        this.mutex = this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFirst(Collection<E> collection) {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        Object object = this.mutex;
        synchronized (object) {
            this.queue.addFirst(collection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLast(E e) {
        Object object = this.mutex;
        synchronized (object) {
            this.queue.addLast(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E peek() {
        Object object = this.mutex;
        synchronized (object) {
            return this.queue.peek();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeFirstOccurrence(E e) {
        Object object = this.mutex;
        synchronized (object) {
            return this.queue.removeFirstOccurrence(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean contains(E e) {
        Object object = this.mutex;
        synchronized (object) {
            return this.queue.contains(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        Object object = this.mutex;
        synchronized (object) {
            return this.queue.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Object object = this.mutex;
        synchronized (object) {
            this.queue.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int drainTo(Collection<E> collection) {
        Object object = this.mutex;
        synchronized (object) {
            return this.queue.drainTo(collection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<E> asList() {
        Object object = this.mutex;
        synchronized (object) {
            return this.queue.asList();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void filter(IPredicate<E> predicate, Collection<E> collection) {
        Object object = this.mutex;
        synchronized (object) {
            this.queue.filter(predicate, collection);
        }
    }
}

