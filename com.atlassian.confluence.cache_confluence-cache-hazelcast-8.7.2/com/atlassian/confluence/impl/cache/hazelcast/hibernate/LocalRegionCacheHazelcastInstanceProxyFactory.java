/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.hazelcast.config.Config
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.config.MaxSizeConfig
 *  com.hazelcast.config.MaxSizeConfig$MaxSizePolicy
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.hibernate.local.LocalRegionCache
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.springframework.aop.Advisor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.support.NameMatchMethodPointcutAdvisor
 */
package com.atlassian.confluence.impl.cache.hazelcast.hibernate;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.local.LocalRegionCache;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

final class LocalRegionCacheHazelcastInstanceProxyFactory {
    private final CacheSettingsDefaultsProvider cacheSettingsProvider;
    private final Method getMaxSizeConfig;
    private final Method getTimeToLiveSeconds;
    private final Method findMapConfig;
    private final Method getConfig;

    LocalRegionCacheHazelcastInstanceProxyFactory(CacheSettingsDefaultsProvider cacheSettingsProvider) {
        try {
            this.getMaxSizeConfig = MapConfig.class.getDeclaredMethod("getMaxSizeConfig", new Class[0]);
            this.getTimeToLiveSeconds = MapConfig.class.getDeclaredMethod("getTimeToLiveSeconds", new Class[0]);
            this.findMapConfig = Config.class.getDeclaredMethod("findMapConfig", String.class);
            this.getConfig = HazelcastInstance.class.getDeclaredMethod("getConfig", new Class[0]);
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        this.cacheSettingsProvider = Objects.requireNonNull(cacheSettingsProvider);
    }

    public HazelcastInstance createProxy(HazelcastInstance hazelcastInstance) {
        Config proxiedConfig = this.createProxy(hazelcastInstance.getConfig());
        return LocalRegionCacheHazelcastInstanceProxyFactory.createProxy(hazelcastInstance, HazelcastInstance.class, LocalRegionCacheHazelcastInstanceProxyFactory.createMethodInterceptAdvisor(invocation -> proxiedConfig, this.getConfig));
    }

    private Config createProxy(Config target) {
        return LocalRegionCacheHazelcastInstanceProxyFactory.createProxy(target, LocalRegionCacheHazelcastInstanceProxyFactory.createMethodInterceptAdvisor(invocation -> {
            String cacheName = (String)invocation.getArguments()[0];
            return this.createProxy((MapConfig)invocation.proceed(), cacheName);
        }, this.findMapConfig));
    }

    private MapConfig createProxy(MapConfig target, String cacheName) {
        return LocalRegionCacheHazelcastInstanceProxyFactory.createProxy(target, LocalRegionCacheHazelcastInstanceProxyFactory.createMethodInterceptAdvisor(invocation -> {
            if (this.getMaxSizeConfig.getName().equals(invocation.getMethod().getName())) {
                return LocalRegionCacheHazelcastInstanceProxyFactory.getMaxSizeConfig(this.getCacheSettings(cacheName));
            }
            if (this.getTimeToLiveSeconds.getName().equals(invocation.getMethod().getName())) {
                return LocalRegionCacheHazelcastInstanceProxyFactory.getTimeToLiveSeconds(this.getCacheSettings(cacheName));
            }
            return invocation.proceed();
        }, this.getMaxSizeConfig, this.getTimeToLiveSeconds));
    }

    private CacheSettings getCacheSettings(String cacheName) {
        return this.cacheSettingsProvider.getDefaults(cacheName);
    }

    private static long getTimeToLiveSeconds(CacheSettings cacheSettings) {
        return Objects.requireNonNull(cacheSettings.getExpireAfterWrite(), "CacheSettings must specify a expireAfterWrite") / 1000L;
    }

    private static MaxSizeConfig getMaxSizeConfig(CacheSettings cacheSettings) {
        return new MaxSizeConfig(LocalRegionCacheHazelcastInstanceProxyFactory.calculateCompensatedMaxSize(cacheSettings), MaxSizeConfig.MaxSizePolicy.PER_NODE);
    }

    private static int calculateCompensatedMaxSize(CacheSettings cacheSettings) {
        int configuredMaxSize = Objects.requireNonNull(cacheSettings.getMaxEntries(), "CacheSettings must specify a maxEntries");
        return (int)((float)configuredMaxSize / (1.0f - LocalRegionCacheHazelcastInstanceProxyFactory.getLocalRegionCacheUndersizingFactor()));
    }

    private static float getLocalRegionCacheUndersizingFactor() {
        try {
            Field field = LocalRegionCache.class.getDeclaredField("BASE_EVICTION_RATE");
            field.setAccessible(true);
            return ((Float)field.get(null)).floatValue();
        }
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Failed to obtain cache undersizing factor from LocalRegionCache", ex);
        }
    }

    private static Advisor createMethodInterceptAdvisor(MethodInterceptor methodInterceptor, Method ... proxiedMethodNames) {
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        Arrays.stream(proxiedMethodNames).map(Method::getName).forEach(arg_0 -> ((NameMatchMethodPointcutAdvisor)advisor).addMethodName(arg_0));
        advisor.setAdvice((Advice)methodInterceptor);
        return advisor;
    }

    private static <T> T createProxy(T target, Class<T> interfaceType, Advisor advisor) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.setInterfaces(new Class[]{interfaceType});
        proxyFactory.addAdvisor(advisor);
        return (T)proxyFactory.getProxy();
    }

    private static <T> T createProxy(T target, Advisor advisor) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setProxyTargetClass(true);
        return (T)proxyFactory.getProxy();
    }
}

