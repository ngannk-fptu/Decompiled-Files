/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheInvocationParameter
 *  javax.cache.annotation.CacheMethodDetails
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import org.springframework.cache.interceptor.BasicOperation;
import org.springframework.cache.interceptor.CacheResolver;

public interface JCacheOperation<A extends Annotation>
extends BasicOperation,
CacheMethodDetails<A> {
    public CacheResolver getCacheResolver();

    public CacheInvocationParameter[] getAllParameters(Object ... var1);
}

