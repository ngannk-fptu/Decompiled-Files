/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.Supplier
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.CacheOperations;
import com.atlassian.confluence.cache.DefaultConfluenceCache;
import com.atlassian.confluence.cache.Deferred;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredOperationsCache<K, V>
extends DefaultConfluenceCache<K, V>
implements Deferred {
    private static final Logger log = LoggerFactory.getLogger(DeferredOperationsCache.class);
    private final CacheLoader<K, V> loader;
    private final CacheOperations<K, V> cacheOperations = new CacheOperations();

    public static <K, V> DeferredOperationsCache<K, V> create(Cache<K, V> delegate, CacheLoader<K, V> loader) {
        return new DeferredOperationsCache<K, V>(delegate, loader);
    }

    private DeferredOperationsCache(Cache<K, V> delegate, CacheLoader<K, V> loader) {
        super(delegate);
        this.loader = loader;
    }

    @Override
    public V get(@NonNull K key) {
        return this.getOrLoad(key, this.loader == null ? null : () -> this.loader.load(key));
    }

    @Override
    public @NonNull V get(@NonNull K key, @NonNull Supplier<? extends V> supplier) {
        V loadedValue = this.getOrLoad(key, supplier);
        if (loadedValue == null) {
            throw new IllegalArgumentException(String.format("Got Null value when attempting to load key: %s from cache: %s using supplier %s", key, this.getName(), supplier.toString()));
        }
        return loadedValue;
    }

    private V getOrLoad(@NonNull K key, Supplier<? extends V> supplier) {
        log.trace("Retrieving key [{}] from cache [{}]", key, (Object)this.getName());
        V currentTransactionCacheValue = this.cacheOperations.get(key);
        if (currentTransactionCacheValue != null) {
            log.trace("Found thread bound value for key [{}] in session cache [{}]", key, (Object)this.getName());
            return currentTransactionCacheValue;
        }
        if (this.cacheOperations.isRemoved(key)) {
            log.debug("Value has been removed in current transaction for key [{}] in cache [{}]", key, (Object)this.getName());
            if (supplier != null) {
                log.debug("Deferring loading of key [{}] to cache [{}]", key, (Object)this.getName());
                return this.cacheOperations.get(key, supplier);
            }
            return null;
        }
        Object globalCacheValue = super.get(key);
        if (globalCacheValue != null) {
            this.cacheOperations.cache(key, globalCacheValue);
            return globalCacheValue;
        }
        if (supplier != null) {
            log.debug("Deferring loading of key [{}] to cache [{}]", key, (Object)this.getName());
            return this.cacheOperations.get(key, supplier);
        }
        return null;
    }

    @Override
    public boolean containsKey(@NonNull K key) {
        V currentTransactionCacheValue = this.cacheOperations.get(key);
        if (currentTransactionCacheValue != null) {
            return true;
        }
        if (this.cacheOperations.isRemoved(key)) {
            return false;
        }
        Object globalCacheValue = super.get(key);
        if (globalCacheValue != null) {
            this.cacheOperations.cache(key, globalCacheValue);
            return true;
        }
        return false;
    }

    @Override
    public @NonNull Collection<K> getKeys() {
        return this.cacheOperations.filter(super.getKeys());
    }

    @Override
    public void put(@NonNull K key, @NonNull V value) {
        log.debug("Deferring addition of key [{}] to cache [{}]", key, (Object)this.getName());
        this.cacheOperations.put(key, value);
    }

    @Override
    public void remove(@NonNull K key) {
        log.debug("Deferring removal of key [{}] from cache [{}]", key, (Object)this.getName());
        this.cacheOperations.remove(key);
    }

    @Override
    public void removeAll() {
        Collection<K> keys = this.getKeys();
        log.debug("Deferring removal of all {} keys from cache [{}]", (Object)keys.size(), (Object)this.getName());
        this.cacheOperations.removeAll(keys);
    }

    @Override
    public boolean hasDeferredOperations() {
        return this.cacheOperations.operationCount() > 0;
    }

    @Override
    public String getType() {
        return "cache";
    }

    @Override
    public void clear() {
        this.cacheOperations.clear();
    }

    @Override
    public void sync() {
        if (log.isInfoEnabled()) {
            log.info("Synchronising deferred operations ({} putIfAbsent, {} put, {} remove, {} get) to cache [{}]", new Object[]{this.cacheOperations.putIfAbsentCount(), this.cacheOperations.putCount(), this.cacheOperations.removeCount(), this.cacheOperations.valueCount(), this.getName()});
        }
        this.cacheOperations.perform(this.getDelegate());
        this.cacheOperations.clear();
    }
}

