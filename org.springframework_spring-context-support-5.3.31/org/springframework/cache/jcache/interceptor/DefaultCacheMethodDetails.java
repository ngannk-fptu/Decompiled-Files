/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.annotation.CacheMethodDetails
 */
package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.cache.annotation.CacheMethodDetails;

class DefaultCacheMethodDetails<A extends Annotation>
implements CacheMethodDetails<A> {
    private final Method method;
    private final Set<Annotation> annotations;
    private final A cacheAnnotation;
    private final String cacheName;

    public DefaultCacheMethodDetails(Method method, A cacheAnnotation, String cacheName) {
        this.method = method;
        this.annotations = Collections.unmodifiableSet(new LinkedHashSet<Annotation>(Arrays.asList(method.getAnnotations())));
        this.cacheAnnotation = cacheAnnotation;
        this.cacheName = cacheName;
    }

    public Method getMethod() {
        return this.method;
    }

    public Set<Annotation> getAnnotations() {
        return this.annotations;
    }

    public A getCacheAnnotation() {
        return this.cacheAnnotation;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CacheMethodDetails[");
        sb.append("method=").append(this.method);
        sb.append(", cacheAnnotation=").append(this.cacheAnnotation);
        sb.append(", cacheName='").append(this.cacheName).append('\'');
        sb.append(']');
        return sb.toString();
    }
}

