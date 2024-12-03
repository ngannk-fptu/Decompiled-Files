/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 */
package com.atlassian.crowd.manager.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.crowd.manager.cache.EhcacheBackedCache;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.sf.ehcache.Ehcache;

public class CacheFactoryEhcache
implements CacheFactory,
CacheManager {
    private final net.sf.ehcache.CacheManager cacheManager;

    public CacheFactoryEhcache(net.sf.ehcache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name) {
        Ehcache ehcache = this.cacheManager.addCacheIfAbsent(name);
        return new EhcacheBackedCache(ehcache);
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull Class<?> owningClass, @Nonnull String name) {
        return this.getCache(owningClass.getName() + "/" + name);
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name, @Nullable CacheLoader<K, V> loader) {
        Ehcache ehcache = this.cacheManager.addCacheIfAbsent(name);
        return new EhcacheBackedCache<K, V>(ehcache, loader);
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name, @Nullable CacheLoader<K, V> loader, @Nonnull CacheSettings required) {
        if (loader == null) {
            return this.getCache(name);
        }
        return this.getCache(name, loader);
    }

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String name, @Nonnull Class<K> keyType, @Nonnull Class<V> valueType) {
        return this.getCache(name);
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull Class<?> owningClass, @Nonnull String name, @Nonnull Supplier<V> supplier) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull Class<?> owningClass, @Nonnull String name, @Nonnull Supplier<V> supplier, @Nonnull CacheSettings required) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull String name, @Nonnull Supplier<V> supplier) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull String name, @Nonnull Supplier<V> supplier, @Nonnull CacheSettings required) {
        throw new UnsupportedOperationException();
    }

    public void flushCaches() {
        this.cacheManager.clearAll();
    }

    @Nonnull
    public Collection<Cache<?, ?>> getCaches() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public ManagedCache getManagedCache(@Nonnull String name) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public Collection<ManagedCache> getManagedCaches() {
        throw new UnsupportedOperationException();
    }

    public void shutdown() {
        this.cacheManager.shutdown();
    }
}

