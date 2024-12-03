/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.function.SingletonSupplier;

public abstract class AbstractCacheInvoker {
    protected SingletonSupplier<CacheErrorHandler> errorHandler;

    protected AbstractCacheInvoker() {
        this.errorHandler = SingletonSupplier.of(SimpleCacheErrorHandler::new);
    }

    protected AbstractCacheInvoker(CacheErrorHandler errorHandler) {
        this.errorHandler = SingletonSupplier.of(errorHandler);
    }

    public void setErrorHandler(CacheErrorHandler errorHandler) {
        this.errorHandler = SingletonSupplier.of(errorHandler);
    }

    public CacheErrorHandler getErrorHandler() {
        return this.errorHandler.obtain();
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

    protected void doPut(Cache cache, Object key, @Nullable Object value) {
        try {
            cache.put(key, value);
        }
        catch (RuntimeException ex) {
            this.getErrorHandler().handleCachePutError(ex, cache, key, value);
        }
    }

    protected void doEvict(Cache cache, Object key, boolean immediate) {
        try {
            if (immediate) {
                cache.evictIfPresent(key);
            } else {
                cache.evict(key);
            }
        }
        catch (RuntimeException ex) {
            this.getErrorHandler().handleCacheEvictError(ex, cache, key);
        }
    }

    protected void doClear(Cache cache, boolean immediate) {
        try {
            if (immediate) {
                cache.invalidate();
            } else {
                cache.clear();
            }
        }
        catch (RuntimeException ex) {
            this.getErrorHandler().handleCacheClearError(ex, cache);
        }
    }
}

