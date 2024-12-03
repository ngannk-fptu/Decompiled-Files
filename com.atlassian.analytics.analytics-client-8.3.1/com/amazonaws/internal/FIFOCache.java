/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal;

import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.internal.BoundedLinkedHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public final class FIFOCache<T> {
    private final BoundedLinkedHashMap<String, T> map;
    private final ReentrantReadWriteLock.ReadLock rlock;
    private final ReentrantReadWriteLock.WriteLock wlock;

    public FIFOCache(int maxSize) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("maxSize " + maxSize + " must be at least 1");
        }
        this.map = new BoundedLinkedHashMap(maxSize);
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.rlock = lock.readLock();
        this.wlock = lock.writeLock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T add(String key, T value) {
        this.wlock.lock();
        try {
            T t = this.map.put(key, value);
            return t;
        }
        finally {
            this.wlock.unlock();
        }
    }

    public T get(String key) {
        this.rlock.lock();
        try {
            Object v = this.map.get(key);
            return (T)v;
        }
        finally {
            this.rlock.unlock();
        }
    }

    public int size() {
        this.rlock.lock();
        try {
            int n = this.map.size();
            return n;
        }
        finally {
            this.rlock.unlock();
        }
    }

    public int getMaxSize() {
        return this.map.getMaxSize();
    }

    public String toString() {
        this.rlock.lock();
        try {
            String string = this.map.toString();
            return string;
        }
        finally {
            this.rlock.unlock();
        }
    }
}

