/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class DelegatingCacheManager
implements CacheManager {
    protected abstract CacheManager getDelegate();

    public @NonNull Collection<Cache<?, ?>> getCaches() {
        return this.getDelegate().getCaches();
    }

    public @NonNull Collection<ManagedCache> getManagedCaches() {
        return this.getDelegate().getManagedCaches();
    }

    public void flushCaches() {
        this.getDelegate().flushCaches();
    }

    public @Nullable ManagedCache getManagedCache(@NonNull String name) {
        return this.getDelegate().getManagedCache(name);
    }

    public void shutdown() {
        this.getDelegate().shutdown();
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull String name, @NonNull Supplier<V> supplier) {
        return this.getDelegate().getCachedReference(name, supplier);
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull String name, @NonNull Supplier<V> supplier, @NonNull CacheSettings required) {
        return this.getDelegate().getCachedReference(name, supplier, required);
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull Class<?> owningClass, @NonNull String name, @NonNull Supplier<V> supplier) {
        return this.getDelegate().getCachedReference(owningClass, name, supplier);
    }

    public <V> @NonNull CachedReference<V> getCachedReference(@NonNull Class<?> owningClass, @NonNull String name, @NonNull Supplier<V> supplier, @NonNull CacheSettings required) {
        return this.getDelegate().getCachedReference(owningClass, name, supplier, required);
    }

    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name) {
        return this.getDelegate().getCache(name);
    }

    public <K, V> @NonNull Cache<K, V> getCache(@NonNull Class<?> owningClass, @NonNull String name) {
        return this.getDelegate().getCache(owningClass, name);
    }

    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name, @Nullable CacheLoader<K, V> loader) {
        return this.getDelegate().getCache(name, loader);
    }

    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name, @Nullable CacheLoader<K, V> loader, @NonNull CacheSettings required) {
        return this.getDelegate().getCache(name, loader, required);
    }

    public <K, V> @NonNull Cache<K, V> getCache(@NonNull String name, @NonNull Class<K> keyType, @NonNull Class<V> valueType) {
        return this.getDelegate().getCache(name, keyType, valueType);
    }
}

