/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.nio.serialization.SerializableByConvention;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SerializableByConvention
public final class InvalidationQueue<T>
extends ConcurrentLinkedQueue<T> {
    private final AtomicInteger elementCount = new AtomicInteger(0);
    private final AtomicBoolean flushingInProgress = new AtomicBoolean(false);

    @Override
    public int size() {
        return this.elementCount.get();
    }

    @Override
    public boolean offer(T invalidation) {
        boolean offered = super.offer(invalidation);
        if (offered) {
            this.elementCount.incrementAndGet();
        }
        return offered;
    }

    @Override
    public T poll() {
        Object invalidation = super.poll();
        if (invalidation != null) {
            this.elementCount.decrementAndGet();
        }
        return (T)invalidation;
    }

    public boolean tryAcquire() {
        return this.flushingInProgress.compareAndSet(false, true);
    }

    public void release() {
        this.flushingInProgress.set(false);
    }

    @Override
    public boolean add(T invalidation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }
}

