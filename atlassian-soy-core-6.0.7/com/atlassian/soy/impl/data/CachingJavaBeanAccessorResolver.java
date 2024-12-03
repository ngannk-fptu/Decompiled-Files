/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nonnull
 */
package com.atlassian.soy.impl.data;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.soy.impl.data.IntrospectorJavaBeanAccessorResolver;
import com.atlassian.soy.impl.data.JavaBeanAccessorResolver;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.lang.reflect.Method;
import java.util.Map;
import javax.annotation.Nonnull;

public class CachingJavaBeanAccessorResolver
implements JavaBeanAccessorResolver {
    @TenantAware(value=TenancyScope.TENANTLESS, comment="It caches the results of IntrospectorJavaBeanAccessorResolver it's the same for all tenants.")
    private final LoadingCache<Class<?>, Map<String, Method>> accessorCache;

    public CachingJavaBeanAccessorResolver() {
        this(new IntrospectorJavaBeanAccessorResolver());
    }

    public CachingJavaBeanAccessorResolver(final JavaBeanAccessorResolver delegate) {
        this.accessorCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Map<String, Method>>(){

            public Map<String, Method> load(@Nonnull Class<?> targetClass) throws Exception {
                return delegate.resolveAccessors(targetClass);
            }
        });
    }

    @Override
    public void clearCaches() {
        this.accessorCache.invalidateAll();
    }

    @Override
    public Map<String, Method> resolveAccessors(Class<?> targetClass) {
        return (Map)this.accessorCache.getUnchecked(targetClass);
    }
}

