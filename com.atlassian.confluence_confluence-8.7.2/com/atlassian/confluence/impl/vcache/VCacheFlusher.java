/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalWriteOperationsBuffered
 *  com.atlassian.vcache.ExternalWriteOperationsUnbuffered
 *  com.atlassian.vcache.LocalCacheOperations
 *  com.atlassian.vcache.VCache
 *  com.atlassian.vcache.VCacheFactory
 *  org.aopalliance.aop.Advice
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.Advisor
 *  org.springframework.aop.AfterReturningAdvice
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.confluence.impl.cache.CacheFlusher;
import com.atlassian.confluence.impl.concurrency.CompletionStageUtils;
import com.atlassian.vcache.ExternalWriteOperationsBuffered;
import com.atlassian.vcache.ExternalWriteOperationsUnbuffered;
import com.atlassian.vcache.LocalCacheOperations;
import com.atlassian.vcache.VCache;
import com.atlassian.vcache.VCacheFactory;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

public class VCacheFlusher
implements CacheFlusher {
    private static final Logger log = LoggerFactory.getLogger(VCacheFlusher.class);
    private final ConcurrentMap<String, VCache> caches = new ConcurrentHashMap<String, VCache>();

    private <T extends VCache> T record(T cache) {
        this.caches.putIfAbsent(Objects.requireNonNull(cache.getName()), Objects.requireNonNull(cache));
        return cache;
    }

    <T> T wrap(T target) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(this.createVCacheFactoryAdvisor());
        return (T)proxyFactory.getProxy();
    }

    private Advisor createVCacheFactoryAdvisor() {
        AfterReturningAdvice advice = (returnValue, method, args, target) -> this.record((VCache)returnValue);
        return new StaticMethodMatcherPointcutAdvisor((Advice)advice){

            public boolean matches(Method method, Class<?> targetClass) {
                return method.getDeclaringClass() == VCacheFactory.class && VCache.class.isAssignableFrom(method.getReturnType());
            }
        };
    }

    @Override
    public void flushCaches() {
        log.info("Flushing {} caches", (Object)this.caches.size());
        this.caches.values().forEach(this::flush);
    }

    private void flush(VCache cache) {
        log.debug("Flushing cache '{}'", (Object)cache.getName());
        if (cache instanceof LocalCacheOperations) {
            ((LocalCacheOperations)cache).removeAll();
        } else if (cache instanceof ExternalWriteOperationsUnbuffered) {
            CompletionStageUtils.joinResult(((ExternalWriteOperationsUnbuffered)cache).removeAll(), ex -> log.error("Failed to flush cache '{}': {}", (Object)cache.getName(), (Object)ex.getMessage()));
        } else if (cache instanceof ExternalWriteOperationsBuffered) {
            ((ExternalWriteOperationsBuffered)cache).removeAll();
        } else {
            log.error("Don't know how to flush cache '{}' of {}", (Object)cache.getName(), cache.getClass());
        }
    }
}

