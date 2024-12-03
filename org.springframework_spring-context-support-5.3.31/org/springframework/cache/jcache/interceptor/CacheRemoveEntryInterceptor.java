/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheRemove
 *  org.springframework.cache.Cache
 *  org.springframework.cache.interceptor.CacheErrorHandler
 *  org.springframework.cache.interceptor.CacheOperationInvocationContext
 *  org.springframework.cache.interceptor.CacheOperationInvoker
 *  org.springframework.cache.interceptor.CacheOperationInvoker$ThrowableWrapper
 */
package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheRemove;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.jcache.interceptor.AbstractKeyCacheInterceptor;
import org.springframework.cache.jcache.interceptor.CacheRemoveOperation;

class CacheRemoveEntryInterceptor
extends AbstractKeyCacheInterceptor<CacheRemoveOperation, CacheRemove> {
    protected CacheRemoveEntryInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    protected Object invoke(CacheOperationInvocationContext<CacheRemoveOperation> context, CacheOperationInvoker invoker) {
        CacheRemoveOperation operation = (CacheRemoveOperation)context.getOperation();
        boolean earlyRemove = operation.isEarlyRemove();
        if (earlyRemove) {
            this.removeValue(context);
        }
        try {
            Object result = invoker.invoke();
            if (!earlyRemove) {
                this.removeValue(context);
            }
            return result;
        }
        catch (CacheOperationInvoker.ThrowableWrapper wrapperException) {
            Throwable ex = wrapperException.getOriginal();
            if (!earlyRemove && operation.getExceptionTypeFilter().match(ex.getClass())) {
                this.removeValue(context);
            }
            throw wrapperException;
        }
    }

    private void removeValue(CacheOperationInvocationContext<CacheRemoveOperation> context) {
        Object key = this.generateKey(context);
        Cache cache = this.resolveCache(context);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Invalidating key [" + key + "] on cache '" + cache.getName() + "' for operation " + context.getOperation()));
        }
        this.doEvict(cache, key, ((CacheRemoveOperation)context.getOperation()).isEarlyRemove());
    }
}

