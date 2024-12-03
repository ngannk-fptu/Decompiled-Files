/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheMethodDetails
 *  javax.cache.annotation.CacheRemoveAll
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.util.ExceptionTypeFilter
 */
package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheRemoveAll;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.interceptor.AbstractJCacheOperation;
import org.springframework.util.ExceptionTypeFilter;

class CacheRemoveAllOperation
extends AbstractJCacheOperation<CacheRemoveAll> {
    private final ExceptionTypeFilter exceptionTypeFilter;

    public CacheRemoveAllOperation(CacheMethodDetails<CacheRemoveAll> methodDetails, CacheResolver cacheResolver) {
        super(methodDetails, cacheResolver);
        CacheRemoveAll ann = (CacheRemoveAll)methodDetails.getCacheAnnotation();
        this.exceptionTypeFilter = this.createExceptionTypeFilter(ann.evictFor(), ann.noEvictFor());
    }

    @Override
    public ExceptionTypeFilter getExceptionTypeFilter() {
        return this.exceptionTypeFilter;
    }

    public boolean isEarlyRemove() {
        return !((CacheRemoveAll)this.getCacheAnnotation()).afterInvocation();
    }
}

