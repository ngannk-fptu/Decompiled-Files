/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;

public class ConcurrentMapCacheManager
implements CacheManager,
BeanClassLoaderAware {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);
    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private boolean storeByValue = false;
    @Nullable
    private SerializationDelegate serialization;

    public ConcurrentMapCacheManager() {
    }

    public ConcurrentMapCacheManager(String ... cacheNames) {
        this.setCacheNames(Arrays.asList(cacheNames));
    }

    public void setCacheNames(@Nullable Collection<String> cacheNames) {
        if (cacheNames != null) {
            for (String name : cacheNames) {
                this.cacheMap.put(name, this.createConcurrentMapCache(name));
            }
            this.dynamic = false;
        } else {
            this.dynamic = true;
        }
    }

    public void setAllowNullValues(boolean allowNullValues) {
        if (allowNullValues != this.allowNullValues) {
            this.allowNullValues = allowNullValues;
            this.recreateCaches();
        }
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    public void setStoreByValue(boolean storeByValue) {
        if (storeByValue != this.storeByValue) {
            this.storeByValue = storeByValue;
            this.recreateCaches();
        }
    }

    public boolean isStoreByValue() {
        return this.storeByValue;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.serialization = new SerializationDelegate(classLoader);
        if (this.isStoreByValue()) {
            this.recreateCaches();
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    @Override
    @Nullable
    public Cache getCache(String name) {
        Cache cache = (Cache)this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            cache = this.cacheMap.computeIfAbsent(name, this::createConcurrentMapCache);
        }
        return cache;
    }

    private void recreateCaches() {
        for (Map.Entry entry : this.cacheMap.entrySet()) {
            entry.setValue(this.createConcurrentMapCache((String)entry.getKey()));
        }
    }

    protected Cache createConcurrentMapCache(String name) {
        SerializationDelegate actualSerialization = this.isStoreByValue() ? this.serialization : null;
        return new ConcurrentMapCache(name, new ConcurrentHashMap<Object, Object>(256), this.isAllowNullValues(), actualSerialization);
    }
}

