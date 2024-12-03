/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
 *  org.springframework.aop.support.DefaultPointcutAdvisor
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.SmartInitializingSingleton
 */
package org.springframework.cache.interceptor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;

public class CacheProxyFactoryBean
extends AbstractSingletonProxyFactoryBean
implements BeanFactoryAware,
SmartInitializingSingleton {
    private final CacheInterceptor cacheInterceptor = new CacheInterceptor();
    private Pointcut pointcut = Pointcut.TRUE;

    public void setCacheOperationSources(CacheOperationSource ... cacheOperationSources) {
        this.cacheInterceptor.setCacheOperationSources(cacheOperationSources);
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.cacheInterceptor.setKeyGenerator(keyGenerator);
    }

    public void setCacheResolver(CacheResolver cacheResolver) {
        this.cacheInterceptor.setCacheResolver(cacheResolver);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheInterceptor.setCacheManager(cacheManager);
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.cacheInterceptor.setBeanFactory(beanFactory);
    }

    public void afterSingletonsInstantiated() {
        this.cacheInterceptor.afterSingletonsInstantiated();
    }

    protected Object createMainInterceptor() {
        this.cacheInterceptor.afterPropertiesSet();
        return new DefaultPointcutAdvisor(this.pointcut, (Advice)this.cacheInterceptor);
    }
}

