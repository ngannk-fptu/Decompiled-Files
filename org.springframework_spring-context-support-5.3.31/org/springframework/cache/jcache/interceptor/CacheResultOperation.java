/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheMethodDetails
 *  javax.cache.annotation.CacheResult
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.cache.interceptor.KeyGenerator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ExceptionTypeFilter
 *  org.springframework.util.StringUtils
 */
package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResult;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.interceptor.AbstractJCacheKeyOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.ExceptionTypeFilter;
import org.springframework.util.StringUtils;

class CacheResultOperation
extends AbstractJCacheKeyOperation<CacheResult> {
    private final ExceptionTypeFilter exceptionTypeFilter;
    @Nullable
    private final CacheResolver exceptionCacheResolver;
    @Nullable
    private final String exceptionCacheName;

    public CacheResultOperation(CacheMethodDetails<CacheResult> methodDetails, CacheResolver cacheResolver, KeyGenerator keyGenerator, @Nullable CacheResolver exceptionCacheResolver) {
        super(methodDetails, cacheResolver, keyGenerator);
        CacheResult ann = (CacheResult)methodDetails.getCacheAnnotation();
        this.exceptionTypeFilter = this.createExceptionTypeFilter(ann.cachedExceptions(), ann.nonCachedExceptions());
        this.exceptionCacheResolver = exceptionCacheResolver;
        this.exceptionCacheName = StringUtils.hasText((String)ann.exceptionCacheName()) ? ann.exceptionCacheName() : null;
    }

    @Override
    public ExceptionTypeFilter getExceptionTypeFilter() {
        return this.exceptionTypeFilter;
    }

    public boolean isAlwaysInvoked() {
        return ((CacheResult)this.getCacheAnnotation()).skipGet();
    }

    @Nullable
    public CacheResolver getExceptionCacheResolver() {
        return this.exceptionCacheResolver;
    }

    @Nullable
    public String getExceptionCacheName() {
        return this.exceptionCacheName;
    }
}

