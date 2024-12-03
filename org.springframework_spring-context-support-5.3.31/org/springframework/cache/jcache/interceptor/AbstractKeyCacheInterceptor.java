/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheKeyInvocationContext
 *  org.springframework.cache.interceptor.CacheErrorHandler
 *  org.springframework.cache.interceptor.CacheOperationInvocationContext
 *  org.springframework.cache.interceptor.KeyGenerator
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheKeyInvocationContext;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.interceptor.AbstractCacheInterceptor;
import org.springframework.cache.jcache.interceptor.AbstractJCacheKeyOperation;
import org.springframework.cache.jcache.interceptor.DefaultCacheKeyInvocationContext;

abstract class AbstractKeyCacheInterceptor<O extends AbstractJCacheKeyOperation<A>, A extends Annotation>
extends AbstractCacheInterceptor<O, A> {
    protected AbstractKeyCacheInterceptor(CacheErrorHandler errorHandler) {
        super(errorHandler);
    }

    protected Object generateKey(CacheOperationInvocationContext<O> context) {
        KeyGenerator keyGenerator = ((AbstractJCacheKeyOperation)context.getOperation()).getKeyGenerator();
        Object key = keyGenerator.generate(context.getTarget(), context.getMethod(), context.getArgs());
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Computed cache key " + key + " for operation " + context.getOperation()));
        }
        return key;
    }

    protected CacheKeyInvocationContext<A> createCacheKeyInvocationContext(CacheOperationInvocationContext<O> context) {
        return new DefaultCacheKeyInvocationContext((AbstractJCacheKeyOperation)context.getOperation(), context.getTarget(), context.getArgs());
    }
}

