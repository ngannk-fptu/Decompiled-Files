/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public final class ContextMutexFactory {
    final Map<Object, Mutex> mutexMap = new HashMap<Object, Mutex>();
    private final Object mainMutex = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Mutex mutexFor(Object mutexKey) {
        Mutex mutex;
        Object object = this.mainMutex;
        synchronized (object) {
            mutex = this.mutexMap.get(mutexKey);
            if (mutex == null) {
                mutex = new Mutex(mutexKey);
                this.mutexMap.put(mutexKey, mutex);
            }
            mutex.referenceCount++;
        }
        return mutex;
    }

    public final class Mutex
    implements Closeable {
        private final Object key;
        private int referenceCount;

        private Mutex(Object key) {
            this.key = key;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            Object object = ContextMutexFactory.this.mainMutex;
            synchronized (object) {
                --this.referenceCount;
                if (this.referenceCount == 0) {
                    ContextMutexFactory.this.mutexMap.remove(this.key);
                }
            }
        }
    }
}

