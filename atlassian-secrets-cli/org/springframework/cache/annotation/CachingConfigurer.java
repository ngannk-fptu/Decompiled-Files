/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;

public interface CachingConfigurer {
    @Nullable
    public CacheManager cacheManager();

    @Nullable
    public CacheResolver cacheResolver();

    @Nullable
    public KeyGenerator keyGenerator();

    @Nullable
    public CacheErrorHandler errorHandler();
}

