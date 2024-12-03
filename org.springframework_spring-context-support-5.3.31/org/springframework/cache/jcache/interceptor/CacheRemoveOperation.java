/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheMethodDetails
 *  javax.cache.annotation.CacheRemove
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.cache.interceptor.KeyGenerator
 *  org.springframework.util.ExceptionTypeFilter
 */
package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheRemove;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.interceptor.AbstractJCacheKeyOperation;
import org.springframework.util.ExceptionTypeFilter;

class CacheRemoveOperation
extends AbstractJCacheKeyOperation<CacheRemove> {
    private final ExceptionTypeFilter exceptionTypeFilter;

    public CacheRemoveOperation(CacheMethodDetails<CacheRemove> methodDetails, CacheResolver cacheResolver, KeyGenerator keyGenerator) {
        super(methodDetails, cacheResolver, keyGenerator);
        CacheRemove ann = (CacheRemove)methodDetails.getCacheAnnotation();
        this.exceptionTypeFilter = this.createExceptionTypeFilter(ann.evictFor(), ann.noEvictFor());
    }

    @Override
    public ExceptionTypeFilter getExceptionTypeFilter() {
        return this.exceptionTypeFilter;
    }

    public boolean isEarlyRemove() {
        return !((CacheRemove)this.getCacheAnnotation()).afterInvocation();
    }
}

