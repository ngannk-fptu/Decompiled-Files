/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Memoizer<K, V> {
    public static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<K, V> cache = new ConcurrentHashMap();
    private final ContextMutexFactory cacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<K, V> constructorFunction;

    public Memoizer(ConstructorFunction<K, V> calculationFunction) {
        this.constructorFunction = calculationFunction;
    }

    public V getOrCalculate(K key) {
        V value = ConcurrencyUtil.getOrPutSynchronized(this.cache, key, this.cacheMutexFactory, this.constructorFunction);
        return value == NULL_OBJECT ? null : (V)value;
    }

    public void remove(K key) {
        this.cache.remove(key);
    }
}

