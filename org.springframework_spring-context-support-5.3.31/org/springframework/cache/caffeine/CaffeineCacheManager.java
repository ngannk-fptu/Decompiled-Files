/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.benmanes.caffeine.cache.Cache
 *  com.github.benmanes.caffeine.cache.CacheLoader
 *  com.github.benmanes.caffeine.cache.Caffeine
 *  com.github.benmanes.caffeine.cache.CaffeineSpec
 *  org.springframework.cache.Cache
 *  org.springframework.cache.CacheManager
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.cache.caffeine;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class CaffeineCacheManager
implements CacheManager {
    private Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
    @Nullable
    private CacheLoader<Object, Object> cacheLoader;
    private boolean allowNullValues = true;
    private boolean dynamic = true;
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);
    private final Collection<String> customCacheNames = new CopyOnWriteArrayList<String>();

    public CaffeineCacheManager() {
    }

    public CaffeineCacheManager(String ... cacheNames) {
        this.setCacheNames(Arrays.asList(cacheNames));
    }

    public void setCacheNames(@Nullable Collection<String> cacheNames) {
        if (cacheNames != null) {
            for (String name : cacheNames) {
                this.cacheMap.put(name, this.createCaffeineCache(name));
            }
            this.dynamic = false;
        } else {
            this.dynamic = true;
        }
    }

    public void setCaffeine(Caffeine<Object, Object> caffeine) {
        Assert.notNull(caffeine, (String)"Caffeine must not be null");
        this.doSetCaffeine(caffeine);
    }

    public void setCaffeineSpec(CaffeineSpec caffeineSpec) {
        this.doSetCaffeine((Caffeine<Object, Object>)Caffeine.from((CaffeineSpec)caffeineSpec));
    }

    public void setCacheSpecification(String cacheSpecification) {
        this.doSetCaffeine((Caffeine<Object, Object>)Caffeine.from((String)cacheSpecification));
    }

    private void doSetCaffeine(Caffeine<Object, Object> cacheBuilder) {
        if (!ObjectUtils.nullSafeEquals(this.cacheBuilder, cacheBuilder)) {
            this.cacheBuilder = cacheBuilder;
            this.refreshCommonCaches();
        }
    }

    public void setCacheLoader(CacheLoader<Object, Object> cacheLoader) {
        if (!ObjectUtils.nullSafeEquals(this.cacheLoader, cacheLoader)) {
            this.cacheLoader = cacheLoader;
            this.refreshCommonCaches();
        }
    }

    public void setAllowNullValues(boolean allowNullValues) {
        if (this.allowNullValues != allowNullValues) {
            this.allowNullValues = allowNullValues;
            this.refreshCommonCaches();
        }
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    @Nullable
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            cache = this.cacheMap.computeIfAbsent(name, this::createCaffeineCache);
        }
        return cache;
    }

    public void registerCustomCache(String name, com.github.benmanes.caffeine.cache.Cache<Object, Object> cache) {
        this.customCacheNames.add(name);
        this.cacheMap.put(name, this.adaptCaffeineCache(name, cache));
    }

    protected Cache adaptCaffeineCache(String name, com.github.benmanes.caffeine.cache.Cache<Object, Object> cache) {
        return new CaffeineCache(name, cache, this.isAllowNullValues());
    }

    protected Cache createCaffeineCache(String name) {
        return this.adaptCaffeineCache(name, this.createNativeCaffeineCache(name));
    }

    protected com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(String name) {
        return this.cacheLoader != null ? this.cacheBuilder.build(this.cacheLoader) : this.cacheBuilder.build();
    }

    private void refreshCommonCaches() {
        for (Map.Entry<String, Cache> entry : this.cacheMap.entrySet()) {
            if (this.customCacheNames.contains(entry.getKey())) continue;
            entry.setValue(this.createCaffeineCache(entry.getKey()));
        }
    }
}

