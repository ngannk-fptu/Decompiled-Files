/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.cache.CacheManager
 *  org.springframework.cache.interceptor.AbstractCacheResolver
 *  org.springframework.cache.interceptor.BasicOperation
 *  org.springframework.cache.interceptor.CacheOperationInvocationContext
 */
package org.springframework.cache.jcache.interceptor;

import java.util.Collection;
import java.util.Collections;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.BasicOperation;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.jcache.interceptor.CacheResultOperation;

public class SimpleExceptionCacheResolver
extends AbstractCacheResolver {
    public SimpleExceptionCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        BasicOperation operation = context.getOperation();
        if (!(operation instanceof CacheResultOperation)) {
            throw new IllegalStateException("Could not extract exception cache name from " + operation);
        }
        CacheResultOperation cacheResultOperation = (CacheResultOperation)operation;
        String exceptionCacheName = cacheResultOperation.getExceptionCacheName();
        if (exceptionCacheName != null) {
            return Collections.singleton(exceptionCacheName);
        }
        return null;
    }
}

