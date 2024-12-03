/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.Supplier
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.ConfluenceCache;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.instrumentation.caches.CacheCollector;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class ConfluenceMonitoringCache<K, V>
implements ConfluenceCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceMonitoringCache.class);
    private final ConfluenceCache<K, V> delegate;
    private final ConfluenceMonitoring confluenceMonitoring;

    public ConfluenceMonitoringCache(ConfluenceCache<K, V> cache, ConfluenceMonitoring confluenceMonitoring) {
        this.delegate = Objects.requireNonNull(cache);
        this.confluenceMonitoring = Objects.requireNonNull(confluenceMonitoring);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V get(@NonNull K key) {
        Split split = this.createReadSplit();
        try {
            Object object = this.delegate.get(key);
            return (V)object;
        }
        finally {
            split.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public @NonNull V get(@NonNull K k, @NonNull Supplier<? extends V> supplier) {
        Split split = this.createReadSplit();
        try {
            Object object = this.delegate.get(k, supplier);
            return (V)object;
        }
        finally {
            split.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsKey(@NonNull K k) {
        Split split = this.createReadSplit();
        try {
            boolean bl = this.delegate.containsKey(k);
            return bl;
        }
        finally {
            split.stop();
        }
    }

    public @NonNull Collection<K> getKeys() {
        Split split = this.createReadSplit();
        try {
            Collection collection = this.delegate.getKeys();
            return collection;
        }
        finally {
            split.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(@NonNull K key, @NonNull V value) {
        Split split = this.createWriteSplit();
        try {
            this.delegate.put(key, value);
        }
        finally {
            split.stop();
        }
    }

    public void remove(@NonNull K key) {
        Split split = this.createWriteSplit();
        try {
            this.delegate.remove(key);
        }
        finally {
            split.stop();
        }
    }

    public void removeAll() {
        Split split = this.createWriteSplit();
        try {
            this.delegate.removeAll();
        }
        finally {
            split.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V putIfAbsent(@NonNull K key, @NonNull V value) {
        Split split = this.createWriteSplit();
        try {
            Object object = this.delegate.putIfAbsent(key, value);
            return (V)object;
        }
        finally {
            split.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean replace(@NonNull K key, @NonNull V oldValue, @NonNull V newValue) {
        Split split = this.createWriteSplit();
        try {
            boolean bl = this.delegate.replace(key, oldValue, newValue);
            return bl;
        }
        finally {
            split.stop();
        }
    }

    public void addListener(@NonNull CacheEntryListener<K, V> kvCacheEntryListener, boolean b) {
        this.delegate.addListener(kvCacheEntryListener, b);
    }

    public void removeListener(@NonNull CacheEntryListener<K, V> kvCacheEntryListener) {
        this.delegate.removeListener(kvCacheEntryListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(@NonNull K key, @NonNull V value) {
        Split split = this.createWriteSplit();
        try {
            boolean bl = this.delegate.remove(key, value);
            return bl;
        }
        finally {
            split.stop();
        }
    }

    public void clear() {
        Split split = this.createWriteSplit();
        try {
            this.delegate.removeAll();
        }
        finally {
            split.stop();
        }
    }

    public @NonNull String getName() {
        return this.delegate.getName();
    }

    public boolean isFlushable() {
        return this.delegate.isFlushable();
    }

    public @Nullable Integer currentMaxEntries() {
        return this.delegate.currentMaxEntries();
    }

    public boolean updateMaxEntries(int newValue) {
        return this.delegate.updateMaxEntries(newValue);
    }

    public @Nullable Long currentExpireAfterAccessMillis() {
        return this.delegate.currentExpireAfterAccessMillis();
    }

    public boolean updateExpireAfterAccess(long expireAfter, @NonNull TimeUnit timeUnit) {
        return this.delegate.updateExpireAfterAccess(expireAfter, timeUnit);
    }

    public @Nullable Long currentExpireAfterWriteMillis() {
        return this.delegate.currentExpireAfterWriteMillis();
    }

    public boolean updateExpireAfterWrite(long expireAfter, @NonNull TimeUnit timeUnit) {
        return this.delegate.updateExpireAfterWrite(expireAfter, timeUnit);
    }

    public boolean isLocal() {
        return this.delegate.isLocal();
    }

    public boolean isReplicateAsynchronously() {
        return this.delegate.isReplicateAsynchronously();
    }

    public boolean isReplicateViaCopy() {
        return this.delegate.isReplicateViaCopy();
    }

    public boolean isStatisticsEnabled() {
        return this.delegate.isStatisticsEnabled();
    }

    public void setStatistics(boolean b) {
        this.delegate.setStatistics(b);
    }

    public @NonNull SortedMap<CacheStatisticsKey, java.util.function.Supplier<Long>> getStatistics() {
        return this.delegate.getStatistics();
    }

    public @Nullable CacheCollector getCacheCollector() {
        return this.delegate.getCacheCollector();
    }

    protected Split createSplit(String operation) {
        log.trace("Creating timer split for {} operation on cache [{}]", (Object)operation, (Object)this.getName());
        return this.confluenceMonitoring.startSplit("CACHE", (Map<String, String>)ImmutableMap.of((Object)"cacheName", (Object)this.getName(), (Object)"cacheOperation", (Object)operation));
    }

    protected Split createReadSplit() {
        return this.createSplit("READ");
    }

    protected Split createWriteSplit() {
        return this.createSplit("WRITE");
    }

    protected Split createLockSplit() {
        return this.createSplit("LOCK");
    }

    protected Split createUnlockSplit() {
        return this.createSplit("UNLOCK");
    }
}

