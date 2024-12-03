/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CloseableThreadLocal<T>
implements Closeable {
    private ThreadLocal<WeakReference<T>> t = new ThreadLocal();
    private Map<Thread, T> hardRefs = new WeakHashMap<Thread, T>();
    private static int PURGE_MULTIPLIER = 20;
    private final AtomicInteger countUntilPurge = new AtomicInteger(PURGE_MULTIPLIER);

    protected T initialValue() {
        return null;
    }

    public T get() {
        WeakReference<T> weakRef = this.t.get();
        if (weakRef == null) {
            T iv = this.initialValue();
            if (iv != null) {
                this.set(iv);
                return iv;
            }
            return null;
        }
        this.maybePurge();
        return weakRef.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(T object) {
        this.t.set(new WeakReference<T>(object));
        Map<Thread, T> map = this.hardRefs;
        synchronized (map) {
            this.hardRefs.put(Thread.currentThread(), object);
            this.maybePurge();
        }
    }

    private void maybePurge() {
        if (this.countUntilPurge.getAndDecrement() == 0) {
            this.purge();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void purge() {
        Map<Thread, T> map = this.hardRefs;
        synchronized (map) {
            int stillAliveCount = 0;
            Iterator<Thread> it = this.hardRefs.keySet().iterator();
            while (it.hasNext()) {
                Thread t = it.next();
                if (!t.isAlive()) {
                    it.remove();
                    continue;
                }
                ++stillAliveCount;
            }
            int nextCount = (1 + stillAliveCount) * PURGE_MULTIPLIER;
            if (nextCount <= 0) {
                nextCount = 1000000;
            }
            this.countUntilPurge.set(nextCount);
        }
    }

    @Override
    public void close() {
        this.hardRefs = null;
        if (this.t != null) {
            this.t.remove();
        }
        this.t = null;
    }
}

