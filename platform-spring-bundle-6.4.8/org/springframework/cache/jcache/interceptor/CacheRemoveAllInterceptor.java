/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheRemoveAll
 */
package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheRemoveAll;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.jcache.interceptor.AbstractCacheInterceptor;
import org.springframework.cache.jcache.interceptor.CacheRemoveAllOperation;

class CacheRemoveAllInterceptor
extends AbstractCacheInterceptor<CacheRemoveAllOperation, CacheRemoveAll> {
    protected CacheRemoveAllInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    protected Object invoke(CacheOperationInvocationContext<CacheRemoveAllOperation> context, CacheOperationInvoker invoker) {
        CacheRemoveAllOperation operation = context.getOperation();
        boolean earlyRemove = operation.isEarlyRemove();
        if (earlyRemove) {
            this.removeAll(context);
        }
        try {
            Object result = invoker.invoke();
            if (!earlyRemove) {
                this.removeAll(context);
            }
            return result;
        }
        catch (CacheOperationInvoker.ThrowableWrapper ex) {
            Throwable original = ex.getOriginal();
            if (!earlyRemove && operation.getExceptionTypeFilter().match(original.getClass())) {
                this.removeAll(context);
            }
            throw ex;
        }
    }

    protected void removeAll(CacheOperationInvocationContext<CacheRemoveAllOperation> context) {
        Cache cache = this.resolveCache(context);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Invalidating entire cache '" + cache.getName() + "' for operation " + context.getOperation()));
        }
        this.doClear(cache, context.getOperation().isEarlyRemove());
    }
}

