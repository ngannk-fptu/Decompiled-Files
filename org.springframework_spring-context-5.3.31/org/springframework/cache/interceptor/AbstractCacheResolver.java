/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.cache.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractCacheResolver
implements CacheResolver,
InitializingBean {
    @Nullable
    private CacheManager cacheManager;

    protected AbstractCacheResolver() {
    }

    protected AbstractCacheResolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        Assert.state((this.cacheManager != null ? 1 : 0) != 0, (String)"No CacheManager set");
        return this.cacheManager;
    }

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.cacheManager, (String)"CacheManager is required");
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = this.getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        ArrayList<Cache> result = new ArrayList<Cache>(cacheNames.size());
        for (String cacheName : cacheNames) {
            Cache cache = this.getCacheManager().getCache(cacheName);
            if (cache == null) {
                throw new IllegalArgumentException("Cannot find cache named '" + cacheName + "' for " + context.getOperation());
            }
            result.add(cache);
        }
        return result;
    }

    @Nullable
    protected abstract Collection<String> getCacheNames(CacheOperationInvocationContext<?> var1);
}

