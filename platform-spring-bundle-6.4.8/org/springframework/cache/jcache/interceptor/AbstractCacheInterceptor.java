/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.cache.jcache.interceptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.AbstractCacheInvoker;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.jcache.interceptor.AbstractJCacheOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

abstract class AbstractCacheInterceptor<O extends AbstractJCacheOperation<A>, A extends Annotation>
extends AbstractCacheInvoker
implements Serializable {
    protected final Log logger = LogFactory.getLog(this.getClass());

    protected AbstractCacheInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Nullable
    protected abstract Object invoke(CacheOperationInvocationContext<O> var1, CacheOperationInvoker var2) throws Throwable;

    protected Cache resolveCache(CacheOperationInvocationContext<O> context) {
        Collection<? extends Cache> caches = ((AbstractJCacheOperation)context.getOperation()).getCacheResolver().resolveCaches(context);
        Cache cache = AbstractCacheInterceptor.extractFrom(caches);
        if (cache == null) {
            throw new IllegalStateException("Cache could not have been resolved for " + context.getOperation());
        }
        return cache;
    }

    @Nullable
    static Cache extractFrom(Collection<? extends Cache> caches) {
        if (CollectionUtils.isEmpty(caches)) {
            return null;
        }
        if (caches.size() == 1) {
            return caches.iterator().next();
        }
        throw new IllegalStateException("Unsupported cache resolution result " + caches + ": JSR-107 only supports a single cache.");
    }
}

