/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.AbstractConcurrentArrayQueuePadding3;
import com.hazelcast.internal.util.concurrent.QueuedPipe;
import com.hazelcast.util.QuickMath;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceArray;

abstract class AbstractConcurrentArrayQueue<E>
extends AbstractConcurrentArrayQueuePadding3
implements QueuedPipe<E> {
    protected final int capacity;
    protected final AtomicReferenceArray<E> buffer;

    protected AbstractConcurrentArrayQueue(int requestedCapacity) {
        this.capacity = QuickMath.nextPowerOfTwo(requestedCapacity);
        this.buffer = new AtomicReferenceArray(this.capacity);
    }

    @Override
    public long addedCount() {
        return this.tail;
    }

    @Override
    public long removedCount() {
        return this.head;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int remainingCapacity() {
        return this.capacity() - this.size();
    }

    @Override
    public E peek() {
        return this.buffer.get(AbstractConcurrentArrayQueue.seqToArrayIndex(this.head, this.capacity - 1));
    }

    @Override
    public boolean add(E e) {
        if (this.offer(e)) {
            return true;
        }
        throw new IllegalStateException("Queue is full");
    }

    @Override
    public E remove() {
        Object e = this.poll();
        if (e == null) {
            throw new NoSuchElementException("Queue is empty");
        }
        return e;
    }

    @Override
    public E element() {
        E e = this.peek();
        if (e == null) {
            throw new NoSuchElementException("Queue is empty");
        }
        return e;
    }

    @Override
    public boolean isEmpty() {
        return this.peek() == null;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        AtomicReferenceArray<E> buffer = this.buffer;
        long mask = this.capacity - 1;
        long limit = this.tail;
        for (long i = this.head; i < limit; ++i) {
            E e = buffer.get(AbstractConcurrentArrayQueue.seqToArrayIndex(i, mask));
            if (!o.equals(e)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (this.contains(o)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            this.add(e);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        Object value;
        while ((value = this.poll()) != null) {
        }
    }

    @Override
    public int size() {
        long currentTail;
        long currentHeadBefore;
        long currentHeadAfter = this.head;
        do {
            currentHeadBefore = currentHeadAfter;
            currentTail = this.tail;
        } while ((currentHeadAfter = this.head) != currentHeadBefore);
        return (int)(currentTail - currentHeadAfter);
    }

    protected static int seqToArrayIndex(long sequence, long mask) {
        return (int)(sequence & mask);
    }
}

