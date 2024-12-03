/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheInvocationParameter
 *  javax.cache.annotation.CacheMethodDetails
 *  javax.cache.annotation.CachePut
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.cache.interceptor.KeyGenerator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ExceptionTypeFilter
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import java.util.List;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CachePut;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.jcache.interceptor.AbstractJCacheKeyOperation;
import org.springframework.cache.jcache.interceptor.AbstractJCacheOperation;
import org.springframework.lang.Nullable;
import org.springframework.util.ExceptionTypeFilter;

class CachePutOperation
extends AbstractJCacheKeyOperation<CachePut> {
    private final ExceptionTypeFilter exceptionTypeFilter;
    private final AbstractJCacheOperation.CacheParameterDetail valueParameterDetail;

    public CachePutOperation(CacheMethodDetails<CachePut> methodDetails, CacheResolver cacheResolver, KeyGenerator keyGenerator) {
        super(methodDetails, cacheResolver, keyGenerator);
        CachePut ann = (CachePut)methodDetails.getCacheAnnotation();
        this.exceptionTypeFilter = this.createExceptionTypeFilter(ann.cacheFor(), ann.noCacheFor());
        AbstractJCacheOperation.CacheParameterDetail valueParameterDetail = CachePutOperation.initializeValueParameterDetail(methodDetails.getMethod(), this.allParameterDetails);
        if (valueParameterDetail == null) {
            throw new IllegalArgumentException("No parameter annotated with @CacheValue was found for " + methodDetails.getMethod());
        }
        this.valueParameterDetail = valueParameterDetail;
    }

    @Override
    public ExceptionTypeFilter getExceptionTypeFilter() {
        return this.exceptionTypeFilter;
    }

    public boolean isEarlyPut() {
        return !((CachePut)this.getCacheAnnotation()).afterInvocation();
    }

    public CacheInvocationParameter getValueParameter(Object ... values) {
        int parameterPosition = this.valueParameterDetail.getParameterPosition();
        if (parameterPosition >= values.length) {
            throw new IllegalStateException("Values mismatch, value parameter at position " + parameterPosition + " cannot be matched against " + values.length + " value(s)");
        }
        return this.valueParameterDetail.toCacheInvocationParameter(values[parameterPosition]);
    }

    @Nullable
    private static AbstractJCacheOperation.CacheParameterDetail initializeValueParameterDetail(Method method, List<AbstractJCacheOperation.CacheParameterDetail> allParameters) {
        AbstractJCacheOperation.CacheParameterDetail result = null;
        for (AbstractJCacheOperation.CacheParameterDetail parameter : allParameters) {
            if (!parameter.isValue()) continue;
            if (result == null) {
                result = parameter;
                continue;
            }
            throw new IllegalArgumentException("More than one @CacheValue found on " + method + "");
        }
        return result;
    }
}

