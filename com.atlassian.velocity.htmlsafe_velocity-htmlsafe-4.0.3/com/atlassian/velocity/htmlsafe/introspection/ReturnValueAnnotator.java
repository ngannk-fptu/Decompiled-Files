/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.velocity.htmlsafe.annotations.ReturnValueAnnotation;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ReturnValueAnnotator
implements MethodAnnotator {
    private static final Logger log = LoggerFactory.getLogger(ReturnValueAnnotator.class);
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LoadingCache<Annotation, Boolean> annotationCache = CacheBuilder.newBuilder().weakKeys().build((CacheLoader)new CacheLoader<Annotation, Boolean>(){

        public Boolean load(Annotation annotation) {
            return annotation.annotationType().isAnnotationPresent(ReturnValueAnnotation.class);
        }
    });

    ReturnValueAnnotator() {
    }

    @Override
    public Collection<Annotation> getAnnotationsForMethod(Method method) {
        HashSet<Annotation> returnValueAnnotations = new HashSet<Annotation>();
        for (Annotation annotation : method.getAnnotations()) {
            if (!((Boolean)this.annotationCache.getUnchecked((Object)annotation)).booleanValue()) continue;
            returnValueAnnotations.add(annotation);
        }
        return Collections.unmodifiableCollection(returnValueAnnotations);
    }
}

