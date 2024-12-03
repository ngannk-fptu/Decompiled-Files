/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheKeyInvocationContext
 *  javax.cache.annotation.CachePut
 *  org.springframework.cache.Cache
 *  org.springframework.cache.interceptor.CacheErrorHandler
 *  org.springframework.cache.interceptor.CacheOperationInvocationContext
 *  org.springframework.cache.interceptor.CacheOperationInvoker
 *  org.springframework.cache.interceptor.CacheOperationInvoker$ThrowableWrapper
 */
package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.CachePut;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.jcache.interceptor.AbstractKeyCacheInterceptor;
import org.springframework.cache.jcache.interceptor.CachePutOperation;

class CachePutInterceptor
extends AbstractKeyCacheInterceptor<CachePutOperation, CachePut> {
    public CachePutInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    protected Object invoke(CacheOperationInvocationContext<CachePutOperation> context, CacheOperationInvoker invoker) {
        CachePutOperation operation = (CachePutOperation)context.getOperation();
        CacheKeyInvocationContext invocationContext = this.createCacheKeyInvocationContext(context);
        boolean earlyPut = operation.isEarlyPut();
        Object value = invocationContext.getValueParameter().getValue();
        if (earlyPut) {
            this.cacheValue(context, value);
        }
        try {
            Object result = invoker.invoke();
            if (!earlyPut) {
                this.cacheValue(context, value);
            }
            return result;
        }
        catch (CacheOperationInvoker.ThrowableWrapper ex) {
            Throwable original = ex.getOriginal();
            if (!earlyPut && operation.getExceptionTypeFilter().match(original.getClass())) {
                this.cacheValue(context, value);
            }
            throw ex;
        }
    }

    protected void cacheValue(CacheOperationInvocationContext<CachePutOperation> context, Object value) {
        Object key = this.generateKey(context);
        Cache cache = this.resolveCache(context);
        this.doPut(cache, key, value);
    }
}

