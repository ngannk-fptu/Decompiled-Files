/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  net.sf.ehcache.config.CacheConfiguration
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.ehcache.ConfluenceEhCache;
import com.atlassian.confluence.cache.ehcache.ValueWrapper;
import com.atlassian.instrumentation.caches.CacheCollector;
import java.util.Collection;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.config.CacheConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class DefaultConfluenceEhCache<K, V>
implements ConfluenceEhCache<K, V> {
    private final Cache<K, ValueWrapper<V>> atlassianCacheDelegate;
    private final ManagedCache managedDelegate;
    private final CacheConfiguration ehCacheConfig;

    public DefaultConfluenceEhCache(Cache<K, V> atlassianCacheDelegate, CacheConfiguration ehCacheConfig) {
        this.atlassianCacheDelegate = Objects.requireNonNull(atlassianCacheDelegate);
        this.managedDelegate = (ManagedCache)atlassianCacheDelegate;
        this.ehCacheConfig = Objects.requireNonNull(ehCacheConfig);
    }

    public V get(@NonNull K key) {
        return this.unwrapValue(this.atlassianCacheDelegate.get(key));
    }

    public @NonNull V get(@NonNull K key, @NonNull Supplier<? extends V> supplier) {
        return (V)((ValueWrapper)this.atlassianCacheDelegate.get(key, (Supplier)new ValueWrapperSupplier(this.getName(), supplier))).getValue();
    }

    public boolean containsKey(@NonNull K k) {
        return this.atlassianCacheDelegate.containsKey(k);
    }

    public @NonNull Collection<K> getKeys() {
        return this.atlassianCacheDelegate.getKeys();
    }

    public void clear() {
        this.managedDelegate.clear();
    }

    public @NonNull String getName() {
        return this.atlassianCacheDelegate.getName();
    }

    public boolean isFlushable() {
        return this.managedDelegate.isFlushable();
    }

    public @Nullable Integer currentMaxEntries() {
        return this.managedDelegate.currentMaxEntries();
    }

    public boolean updateMaxEntries(int newValue) {
        return this.managedDelegate.updateMaxEntries(newValue);
    }

    public @Nullable Long currentExpireAfterAccessMillis() {
        return this.managedDelegate.currentExpireAfterAccessMillis();
    }

    public boolean updateExpireAfterAccess(long expireAfter, @NonNull TimeUnit timeUnit) {
        return this.managedDelegate.updateExpireAfterAccess(expireAfter, timeUnit);
    }

    public @Nullable Long currentExpireAfterWriteMillis() {
        return this.managedDelegate.currentExpireAfterWriteMillis();
    }

    public boolean updateExpireAfterWrite(long expireAfter, @NonNull TimeUnit timeUnit) {
        return this.managedDelegate.updateExpireAfterWrite(expireAfter, timeUnit);
    }

    public boolean isLocal() {
        return this.managedDelegate.isLocal();
    }

    public boolean isReplicateAsynchronously() {
        return this.managedDelegate.isReplicateAsynchronously();
    }

    public boolean isReplicateViaCopy() {
        return this.managedDelegate.isReplicateAsynchronously();
    }

    public boolean isStatisticsEnabled() {
        return this.managedDelegate.isStatisticsEnabled();
    }

    public void setStatistics(boolean b) {
        this.managedDelegate.setStatistics(b);
    }

    public @NonNull SortedMap<CacheStatisticsKey, java.util.function.Supplier<Long>> getStatistics() {
        return this.managedDelegate.getStatistics();
    }

    public @Nullable CacheCollector getCacheCollector() {
        return this.managedDelegate.getCacheCollector();
    }

    public void put(@NonNull K key, @NonNull V value) {
        this.atlassianCacheDelegate.put(key, this.wrapValue(value));
    }

    public void remove(@NonNull K key) {
        this.atlassianCacheDelegate.remove(key);
    }

    public void removeAll() {
        this.atlassianCacheDelegate.removeAll();
    }

    @Override
    public boolean updateMaxEntriesLocalHeap(long max) {
        this.ehCacheConfig.setMaxEntriesLocalHeap(max);
        return true;
    }

    public V putIfAbsent(@NonNull K key, @NonNull V value) {
        return this.unwrapValue(this.atlassianCacheDelegate.putIfAbsent(key, this.wrapValue(value)));
    }

    public boolean replace(@NonNull K key, @NonNull V oldValue, @NonNull V newValue) {
        return this.atlassianCacheDelegate.replace(key, this.wrapValue(oldValue), this.wrapValue(newValue));
    }

    public void addListener(@NonNull CacheEntryListener<K, V> listener, boolean required) {
        this.atlassianCacheDelegate.addListener(listener, required);
    }

    public void removeListener(@NonNull CacheEntryListener<K, V> listener) {
        this.atlassianCacheDelegate.removeListener(listener);
    }

    public boolean remove(@NonNull K key, @NonNull V value) {
        return this.atlassianCacheDelegate.remove(key, this.wrapValue(value));
    }

    private ValueWrapper<V> wrapValue(V value) {
        return new ValueWrapper<V>(this.getName(), value);
    }

    private V unwrapValue(@Nullable Object value) {
        if (value instanceof ValueWrapper) {
            ValueWrapper wrappedValue = (ValueWrapper)value;
            return (V)wrappedValue.getValue();
        }
        return (V)value;
    }

    @Override
    public CacheConfiguration getEhCacheConfiguration() {
        return this.ehCacheConfig;
    }

    private class ValueWrapperSupplier
    implements Supplier<ValueWrapper<V>> {
        private final Supplier<? extends V> delegate;
        private final String cacheName;

        public ValueWrapperSupplier(String cacheName, Supplier<? extends V> supplier) {
            this.delegate = Objects.requireNonNull(supplier);
            this.cacheName = Objects.requireNonNull(cacheName);
        }

        public ValueWrapper<V> get() {
            return new ValueWrapper<Object>(this.cacheName, this.delegate.get());
        }
    }
}

