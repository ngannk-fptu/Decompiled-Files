/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  javax.cache.CacheManager
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  org.springframework.aop.aspectj.annotation.AspectJProxyFactory
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.impl.hibernate.JCacheAspect;
import com.atlassian.confluence.impl.hibernate.JCacheManagerAspect;
import javax.cache.Cache;
import javax.cache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

final class JCacheProxyFactory {
    JCacheProxyFactory() {
    }

    static CacheManager createProxy(net.sf.ehcache.CacheManager cacheManager) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory();
        proxyFactory.setTargetClass(CacheManager.class);
        proxyFactory.addAspect((Object)new JCacheManagerAspect(cacheManager));
        return (CacheManager)proxyFactory.getProxy();
    }

    static <K, V> Cache<K, V> createProxy(Ehcache ehcache) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory();
        proxyFactory.setTargetClass(Cache.class);
        proxyFactory.addAspect(new JCacheAspect(ehcache));
        return (Cache)proxyFactory.getProxy();
    }
}

