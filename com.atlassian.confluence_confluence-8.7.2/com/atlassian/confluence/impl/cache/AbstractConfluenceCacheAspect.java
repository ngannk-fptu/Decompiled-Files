/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.cache.Cache;
import com.atlassian.confluence.cache.ConfluenceCache;
import com.atlassian.confluence.cache.DefaultConfluenceCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public abstract class AbstractConfluenceCacheAspect {
    private static final Logger log = LoggerFactory.getLogger(AbstractConfluenceCacheAspect.class);

    @Around(value="bean(cacheManager) && execution(com.atlassian.cache.Cache com.atlassian.cache.CacheFactory.getCache(..))")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        Object value = joinPoint.proceed();
        if (this.isEnabled()) {
            log.debug("Wrapping {} result of {}", (Object)value.getClass().getName(), (Object)joinPoint.toShortString());
            return this.wrapCache(AbstractConfluenceCacheAspect.asConfluenceCache(value));
        }
        return value;
    }

    protected abstract <K, V> ConfluenceCache<K, V> wrapCache(ConfluenceCache<K, V> var1);

    private static <K, V> ConfluenceCache<K, V> asConfluenceCache(Object cache) {
        return cache instanceof ConfluenceCache ? (ConfluenceCache)cache : new DefaultConfluenceCache((Cache)cache);
    }

    protected abstract boolean isEnabled();
}

