/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  com.google.common.collect.ImmutableMap
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.Advisor
 *  org.springframework.aop.MethodBeforeAdvice
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor
 */
package com.atlassian.confluence.impl.cache.whitelist;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.confluence.cache.ConfluenceCache;
import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelist;
import com.atlassian.confluence.impl.cache.whitelist.CacheOperationsWhitelistService;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Method;
import java.util.Map;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

final class AopCacheOperationsWhitelistService
implements CacheOperationsWhitelistService {
    private final CacheOperationsWhitelist whitelist;
    private final Map<Method, CacheOperationsWhitelist.Operation> methodOperations;

    AopCacheOperationsWhitelistService(CacheOperationsWhitelist whitelist) throws NoSuchMethodException {
        this.whitelist = whitelist;
        this.methodOperations = ImmutableMap.of((Object)Cache.class.getMethod("put", Object.class, Object.class), (Object)((Object)CacheOperationsWhitelist.Operation.PUT), (Object)Cache.class.getMethod("putIfAbsent", Object.class, Object.class), (Object)((Object)CacheOperationsWhitelist.Operation.PUT), (Object)Cache.class.getMethod("replace", Object.class, Object.class, Object.class), (Object)((Object)CacheOperationsWhitelist.Operation.REPLACE), (Object)Cache.class.getMethod("remove", Object.class, Object.class), (Object)((Object)CacheOperationsWhitelist.Operation.REMOVE_CONDITIONAL), (Object)Cache.class.getMethod("addListener", CacheEntryListener.class, Boolean.TYPE), (Object)((Object)CacheOperationsWhitelist.Operation.LISTENER));
    }

    @Override
    public <K, V> ConfluenceCache<K, V> wrap(ConfluenceCache<K, V> cache) {
        return new WhitelistProxyFactory<K, V>().createProxy(cache);
    }

    class WhitelistProxyFactory<K, V> {
        WhitelistProxyFactory() {
        }

        public ConfluenceCache<K, V> createProxy(ConfluenceCache<K, V> target) {
            ProxyFactory proxyFactory = new ProxyFactory(target);
            proxyFactory.addAdvisor(this.advisor(target));
            Object proxy = proxyFactory.getProxy(this.getClass().getClassLoader());
            return (ConfluenceCache)proxy;
        }

        private Advisor advisor(ConfluenceCache<K, V> cache) {
            return new StaticMethodMatcherPointcutAdvisor((Advice)this.assertIsPermitted(cache)){

                public boolean matches(Method method, Class<?> targetClass) {
                    return WhitelistProxyFactory.this.isMethodOn(method, Cache.class);
                }
            };
        }

        private boolean isMethodOn(Method method, Class<?> type) {
            return method.getDeclaringClass() == type;
        }

        private MethodBeforeAdvice assertIsPermitted(ConfluenceCache<K, V> cache) {
            return (method, args, target) -> this.assertIsPermitted(cache, method);
        }

        private void assertIsPermitted(ConfluenceCache<K, V> cache, Method method) {
            CacheOperationsWhitelist.Operation operation = AopCacheOperationsWhitelistService.this.methodOperations.get(method);
            if (operation != null) {
                AopCacheOperationsWhitelistService.this.whitelist.assertPermitted(operation, cache);
            }
        }
    }
}

