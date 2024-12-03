/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package com.atlassian.plugins.rest.module.util;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugins.rest.module.util.ConstructorAndArgs;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import net.sf.cglib.proxy.Callback;

public class ProxyUtils {
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static LoadingCache<Class<?>, ConstructorAndArgs> generatorCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, ConstructorAndArgs>(){

        public ConstructorAndArgs load(Class<?> from) throws Exception {
            return new ConstructorAndArgs(from);
        }
    });

    public static <T> T create(Class<T> clazz, Callback callback) {
        try {
            return (T)((ConstructorAndArgs)generatorCache.get(clazz)).create(callback);
        }
        catch (ExecutionException e) {
            throw new RuntimeException("Failed to construct class: ", e);
        }
    }
}

