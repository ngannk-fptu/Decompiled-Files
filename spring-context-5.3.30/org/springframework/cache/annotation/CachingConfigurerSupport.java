/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;

public class CachingConfigurerSupport
implements CachingConfigurer {
    @Override
    @Nullable
    public CacheManager cacheManager() {
        return null;
    }

    @Override
    @Nullable
    public CacheResolver cacheResolver() {
        return null;
    }

    @Override
    @Nullable
    public KeyGenerator keyGenerator() {
        return null;
    }

    @Override
    @Nullable
    public CacheErrorHandler errorHandler() {
        return null;
    }
}

