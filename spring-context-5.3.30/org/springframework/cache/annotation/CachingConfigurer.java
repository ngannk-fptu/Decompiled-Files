/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;

public interface CachingConfigurer {
    @Nullable
    default public CacheManager cacheManager() {
        return null;
    }

    @Nullable
    default public CacheResolver cacheResolver() {
        return null;
    }

    @Nullable
    default public KeyGenerator keyGenerator() {
        return null;
    }

    @Nullable
    default public CacheErrorHandler errorHandler() {
        return null;
    }
}

