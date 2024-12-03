/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractCacheInvoker {
    private CacheErrorHandler errorHandler;

    protected AbstractCacheInvoker() {
        this.errorHandler = new SimpleCacheErrorHandler();
    }

    protected AbstractCacheInvoker(CacheErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setErrorHandler(CacheErrorHandler errorHandler) {
        Assert.notNull((Object)errorHandler, "CacheErrorHandler must not be null");
        this.errorHandler = errorHandler;
    }

    public CacheErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Nullable
    protected Cache.ValueWrapper doGet(Cache cache, Object key) {
        try {
            return cache.get(key);
        }
        catch (RuntimeException ex) {
            this.getErrorHandler().handleCacheGetError(ex, cache, key);
            return null;
        }
    }

    protected void doPut(Cache cache, Object key, @Nullable Object result) {
        try {
            cache.put(key, result);
        }
        catch (RuntimeException ex) {
            this.getErrorHandler().handleCachePutError(ex, cache, key, result);
        }
    }

    protected void doEvict(Cache cache, Object key) {
        try {
            cache.evict(key);
        }
        catch (RuntimeException ex) {
            this.getErrorHandler().handleCacheEvictError(ex, cache, key);
        }
    }

    protected void doClear(Cache cache) {
        try {
            cache.clear();
        }
        catch (RuntimeException ex) {
            this.getErrorHandler().handleCacheClearError(ex, cache);
        }
    }
}

