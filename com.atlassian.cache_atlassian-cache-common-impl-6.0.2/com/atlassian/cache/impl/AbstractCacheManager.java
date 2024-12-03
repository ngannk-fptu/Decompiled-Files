/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ManagedLock
 *  io.atlassian.util.concurrent.ManagedLocks
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.impl.metrics.CacheManagerMetricEmitter;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ManagedLock;
import io.atlassian.util.concurrent.ManagedLocks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractCacheManager
implements CacheManager {
    private final CacheManagerMetricEmitter cacheManagerMetricEmitter;
    protected final ConcurrentMap<String, java.util.function.Supplier<ManagedCache>> caches = new ConcurrentHashMap<String, java.util.function.Supplier<ManagedCache>>();
    protected final Function<String, ManagedLock> cacheCreationLocks = ManagedLocks.weakManagedLockFactory();
    @Nullable
    protected final CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider;

    protected AbstractCacheManager(@Nullable CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, @Nonnull CacheManagerMetricEmitter cacheManagerMetricEmitter) {
        this.cacheSettingsDefaultsProvider = cacheSettingsDefaultsProvider;
        this.cacheManagerMetricEmitter = Objects.requireNonNull(cacheManagerMetricEmitter, "cacheManagerMetricEmitter");
    }

    @Nonnull
    public Collection<Cache<?, ?>> getCaches() {
        ArrayList managedCaches = new ArrayList(64);
        for (java.util.function.Supplier cacheRef : this.caches.values()) {
            ManagedCache managedCache = (ManagedCache)cacheRef.get();
            if (!(managedCache instanceof Cache)) continue;
            managedCaches.add((Cache)managedCache);
        }
        return managedCaches;
    }

    @Nonnull
    public Collection<ManagedCache> getManagedCaches() {
        ArrayList<ManagedCache> managedCaches = new ArrayList<ManagedCache>(64);
        for (java.util.function.Supplier cacheRef : this.caches.values()) {
            ManagedCache managedCache = (ManagedCache)cacheRef.get();
            if (managedCache == null) continue;
            managedCaches.add(managedCache);
        }
        return managedCaches;
    }

    @Nullable
    public ManagedCache getManagedCache(@Nonnull String name) {
        java.util.function.Supplier cacheRef = (java.util.function.Supplier)this.caches.get(name);
        return null == cacheRef ? null : (ManagedCache)cacheRef.get();
    }

    public void flushCaches() {
        this.cacheManagerMetricEmitter.emitCacheManagerFlushAll(this.getClass().getName());
        for (java.util.function.Supplier cacheRef : this.caches.values()) {
            ManagedCache managedCache = (ManagedCache)cacheRef.get();
            if (managedCache == null || !managedCache.isFlushable()) continue;
            managedCache.clear();
        }
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name) {
        return this.getCache(name, null);
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull Class<?> owningClass, @Nonnull String name) {
        return this.getCache(AbstractCacheManager.cacheName(owningClass, name));
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name, @Nonnull Class<K> keyType, @Nonnull Class<V> valueType) {
        return this.getCache(name);
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name, @Nullable CacheLoader<K, V> loader) {
        return this.getCache(name, loader, new CacheSettingsBuilder().build());
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull String name, @Nonnull Supplier<V> supplier) {
        return this.getCachedReference(name, supplier, new CacheSettingsBuilder().build());
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull Class<?> owningClass, @Nonnull String name, @Nonnull Supplier<V> supplier) {
        return this.getCachedReference(owningClass, name, supplier, new CacheSettingsBuilder().build());
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull Class<?> owningClass, @Nonnull String name, @Nonnull Supplier<V> supplier, @Nonnull CacheSettings settings) {
        return this.getCachedReference(AbstractCacheManager.cacheName(owningClass, name), supplier, settings);
    }

    private static String cacheName(Class<?> owningClass, String name) {
        Preconditions.checkNotNull((Object)name, (Object)"name cannot be null");
        return owningClass.getName() + '.' + name;
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name, @Nullable CacheLoader<K, V> loader, @Nonnull CacheSettings settings) {
        if (null == loader) {
            return (Cache)this.createSimpleCache(name, this.mergeSettings(name, settings));
        }
        return (Cache)this.createComputingCache(name, this.mergeSettings(name, settings), loader);
    }

    protected abstract <K, V> ManagedCache createComputingCache(@Nonnull String var1, @Nonnull CacheSettings var2, @Nullable CacheLoader<K, V> var3);

    protected abstract ManagedCache createSimpleCache(@Nonnull String var1, @Nonnull CacheSettings var2);

    protected CacheSettings mergeSettings(String name, CacheSettings settings) {
        if (this.cacheSettingsDefaultsProvider == null) {
            return settings;
        }
        return this.cacheSettingsDefaultsProvider.getDefaults(name).override(settings);
    }

    public void shutdown() {
    }
}

