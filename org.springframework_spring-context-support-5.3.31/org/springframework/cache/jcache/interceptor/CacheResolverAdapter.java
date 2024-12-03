/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  javax.cache.annotation.CacheInvocationContext
 *  javax.cache.annotation.CacheResolver
 *  org.springframework.cache.Cache
 *  org.springframework.cache.interceptor.CacheOperationInvocationContext
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.util.Assert
 */
package org.springframework.cache.jcache.interceptor;

import java.util.Collection;
import java.util.Collections;
import javax.cache.annotation.CacheInvocationContext;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.util.Assert;

class CacheResolverAdapter
implements CacheResolver {
    private final javax.cache.annotation.CacheResolver target;

    public CacheResolverAdapter(javax.cache.annotation.CacheResolver target) {
        Assert.notNull((Object)target, (String)"JSR-107 CacheResolver is required");
        this.target = target;
    }

    protected javax.cache.annotation.CacheResolver getTarget() {
        return this.target;
    }

    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        if (!(context instanceof CacheInvocationContext)) {
            throw new IllegalStateException("Unexpected context " + context);
        }
        CacheInvocationContext cacheInvocationContext = (CacheInvocationContext)context;
        javax.cache.Cache cache = this.target.resolveCache(cacheInvocationContext);
        if (cache == null) {
            throw new IllegalStateException("Could not resolve cache for " + context + " using " + this.target);
        }
        return Collections.singleton(new JCacheCache((javax.cache.Cache<Object, Object>)cache));
    }
}

