/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.jcache.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;

public interface JCacheConfigurer
extends CachingConfigurer {
    @Nullable
    default public CacheResolver exceptionCacheResolver() {
        return null;
    }
}

