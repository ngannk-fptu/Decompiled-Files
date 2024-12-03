/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class ConcurrencyUtil {
    public static final Executor CALLER_RUNS = new Executor(){

        @Override
        public void execute(Runnable command) {
            command.run();
        }

        public String toString() {
            return "CALLER_RUNS";
        }
    };

    private ConcurrencyUtil() {
    }

    public static <E> void setMax(E obj, AtomicLongFieldUpdater<E> updater, long value) {
        long current;
        do {
            if ((current = updater.get(obj)) < value) continue;
            return;
        } while (!updater.compareAndSet(obj, current, value));
    }

    public static boolean setIfEqualOrGreaterThan(AtomicLong oldValue, long newValue) {
        long local;
        do {
            if (newValue >= (local = oldValue.get())) continue;
            return false;
        } while (!oldValue.compareAndSet(local, newValue));
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <K, V> V getOrPutSynchronized(ConcurrentMap<K, V> map, K key, Object mutex, ConstructorFunction<K, V> func) {
        if (mutex == null) {
            throw new NullPointerException();
        }
        Object value = map.get(key);
        if (value == null) {
            Object object = mutex;
            synchronized (object) {
                value = map.get(key);
                if (value == null) {
                    value = func.createNew(key);
                    map.put(key, value);
                }
            }
        }
        return value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <K, V> V getOrPutSynchronized(ConcurrentMap<K, V> map, K key, ContextMutexFactory contextMutexFactory, ConstructorFunction<K, V> func) {
        if (contextMutexFactory == null) {
            throw new NullPointerException();
        }
        Object value = map.get(key);
        if (value == null) {
            try (ContextMutexFactory.Mutex mutex = contextMutexFactory.mutexFor(key);){
                ContextMutexFactory.Mutex mutex2 = mutex;
                synchronized (mutex2) {
                    value = map.get(key);
                    if (value == null) {
                        value = func.createNew(key);
                        map.put(key, value);
                    }
                }
            }
        }
        return value;
    }

    public static <K, V> V getOrPutIfAbsent(ConcurrentMap<K, V> map, K key, ConstructorFunction<K, V> func) {
        Object value = map.get(key);
        if (value == null) {
            value = func.createNew(key);
            V current = map.putIfAbsent(key, value);
            value = current == null ? value : current;
        }
        return value;
    }
}

