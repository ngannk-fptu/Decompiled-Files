/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.TargetSource
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.target.LazyInitTargetSource
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.impl.spring;

import java.util.Objects;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public final class LazyProxyFactory {
    private final BeanFactory beanFactory;

    public LazyProxyFactory(@Autowired BeanFactory beanFactory) {
        this.beanFactory = Objects.requireNonNull(beanFactory);
    }

    public <T> T proxy(Class<T> targetType, String targetBeanName) {
        LazyInitTargetSource targetSource = new LazyInitTargetSource();
        targetSource.setTargetBeanName(targetBeanName);
        targetSource.setTargetClass(targetType);
        targetSource.setBeanFactory(this.beanFactory);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource((TargetSource)targetSource);
        return (T)proxyFactory.getProxy();
    }
}

