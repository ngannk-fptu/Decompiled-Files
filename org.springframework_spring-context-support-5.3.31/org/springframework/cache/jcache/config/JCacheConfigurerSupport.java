/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.cache.annotation.CachingConfigurerSupport
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.lang.Nullable
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

