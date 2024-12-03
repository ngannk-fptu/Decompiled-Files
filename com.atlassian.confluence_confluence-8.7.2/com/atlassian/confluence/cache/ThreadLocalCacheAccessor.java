/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cache;

import com.atlassian.confluence.cache.ThreadLocalCache;
import com.google.common.base.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ThreadLocalCacheAccessor<K, V> {
    public static <K, V> ThreadLocalCacheAccessor<K, V> newInstance() {
        return new ThreadLocalCacheAccessor<K, V>();
    }

    private ThreadLocalCacheAccessor() {
    }

    public void put(K key, V value) {
        ThreadLocalCache.put(key, value);
    }

    public V get(K key) {
        return (V)ThreadLocalCache.get(key);
    }

    @Deprecated
    public @NonNull V get(@NonNull K key, @NonNull Supplier<V> supplier) {
        Object value = this.get(key);
        if (value == null) {
            value = supplier.get();
            this.put(key, value);
        }
        return value;
    }

    public @NonNull V getOrCompute(@NonNull K key, @NonNull java.util.function.Supplier<V> supplier) {
        return this.get(key, supplier::get);
    }

    public void init() {
        ThreadLocalCache.init();
    }

    public boolean isInit() {
        return ThreadLocalCache.isInit();
    }

    public void flush() {
        ThreadLocalCache.flush();
    }
}

