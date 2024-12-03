/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.impl.hibernate.JCacheProxyFactory;
import java.util.Objects;
import javax.cache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
final class JCacheManagerAspect {
    private final CacheManager delegate;

    JCacheManagerAspect(CacheManager delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Around(value="execution(javax.cache.Cache javax.cache.CacheManager.createCache(..)) && args(cacheName, ..)")
    Cache<?, ?> createCache(String cacheName) {
        this.delegate.addCache(cacheName);
        return this.getCache(cacheName);
    }

    @Around(value="execution(javax.cache.Cache javax.cache.CacheManager.getCache(..)) && args(cacheName)")
    Cache<?, ?> getCache(String cacheName) {
        Ehcache ehcache = this.delegate.getEhcache(cacheName);
        return ehcache == null ? null : JCacheProxyFactory.createProxy(ehcache);
    }
}

