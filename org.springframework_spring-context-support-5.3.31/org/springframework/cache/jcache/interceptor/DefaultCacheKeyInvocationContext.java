/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheInvocationParameter
 *  javax.cache.annotation.CacheKeyInvocationContext
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKeyInvocationContext;
import org.springframework.cache.jcache.interceptor.AbstractJCacheKeyOperation;
import org.springframework.cache.jcache.interceptor.CachePutOperation;
import org.springframework.cache.jcache.interceptor.DefaultCacheInvocationContext;
import org.springframework.lang.Nullable;

class DefaultCacheKeyInvocationContext<A extends Annotation>
extends DefaultCacheInvocationContext<A>
implements CacheKeyInvocationContext<A> {
    private final CacheInvocationParameter[] keyParameters;
    @Nullable
    private final CacheInvocationParameter valueParameter;

    public DefaultCacheKeyInvocationContext(AbstractJCacheKeyOperation<A> operation, Object target, Object[] args) {
        super(operation, target, args);
        this.keyParameters = operation.getKeyParameters(args);
        this.valueParameter = operation instanceof CachePutOperation ? ((CachePutOperation)operation).getValueParameter(args) : null;
    }

    public CacheInvocationParameter[] getKeyParameters() {
        return (CacheInvocationParameter[])this.keyParameters.clone();
    }

    @Nullable
    public CacheInvocationParameter getValueParameter() {
        return this.valueParameter;
    }
}

