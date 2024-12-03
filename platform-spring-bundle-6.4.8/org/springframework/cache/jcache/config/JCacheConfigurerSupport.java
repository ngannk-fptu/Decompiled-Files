/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.jcache.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.config.JCacheConfigurer;
import org.springframework.lang.Nullable;

public class JCacheConfigurerSupport
extends CachingConfigurerSupport
implements JCacheConfigurer {
    @Override
    @Nullable
    public CacheResolver exceptionCacheResolver() {
        return null;
    }
}

