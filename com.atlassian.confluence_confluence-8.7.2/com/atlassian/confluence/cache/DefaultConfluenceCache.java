/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.ConfluenceCache;
import com.atlassian.instrumentation.caches.CacheCollector;
import java.util.Collection;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Internal
public class DefaultConfluenceCache<K, V>
implements ConfluenceCache<K, V> {
    private final Cache<K, V> delegate;
    private final ManagedCache managedDelegate;

    public DefaultConfluenceCache(Cache<K, V> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.managedDelegate = (ManagedCache)delegate;
    }

    public boolean containsKey(@NonNull K key) {
        return this.delegate.containsKey(key);
    }

    public @NonNull Collection<K> getKeys() {
        return this.delegate.getKeys();
    }

    public @Nullable V get(@NonNull K key) {
        return (V)this.delegate.get(key);
    }

    public @NonNull V get(@NonNull K key, @NonNull Supplier<? extends V> supplier) {
        return (V)this.delegate.get(key, supplier);
    }

    public void put(@NonNull K key, @NonNull V value) {
        this.delegate.put(key, value);
    }

    public @Nullable V putIfAbsent(@NonNull K key, @NonNull V value) {
        return (V)this.delegate.putIfAbsent(key, value);
    }

    public void remove(@NonNull K key) {
        this.delegate.remove(key);
    }

    public boolean remove(@NonNull K key, @NonNull V value) {
        return this.delegate.remove(key, value);
    }

    public void removeAll() {
        this.delegate.removeAll();
    }

    public boolean replace(@NonNull K key, @NonNull V oldValue, @NonNull V newValue) {
        return this.delegate.replace(key, oldValue, newValue);
    }

    public void addListener(@NonNull CacheEntryListener<K, V> listener, boolean required) {
        this.delegate.addListener(listener, required);
    }

    public void removeListener(@NonNull CacheEntryListener<K, V> listener) {
        this.delegate.removeListener(listener);
    }

    public void clear() {
        this.managedDelegate.clear();
    }

    public @NonNull String getName() {
        return this.delegate.getName();
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
        return this.managedDelegate.isReplicateViaCopy();
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

    protected Cache<K, V> getDelegate() {
        return this.delegate;
    }

    protected ManagedCache getManagedDelegate() {
        return this.managedDelegate;
    }
}

