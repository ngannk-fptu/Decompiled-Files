/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.apache.commons.lang3.ClassUtils
 *  org.springframework.aop.Advisor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.support.DelegatingIntroductionInterceptor
 */
package com.atlassian.confluence.util;

import java.util.Arrays;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

public abstract class AopUtils {
    public static Object adaptInterface(Object target, Class newInterface, Class oldInterface, Object delegate) {
        if (!ClassUtils.isAssignable(delegate.getClass(), (Class)newInterface, (boolean)true)) {
            throw new IllegalArgumentException("Delegate does not implement the new interface (" + newInterface + ")");
        }
        if (!ClassUtils.isAssignable(target.getClass(), (Class)oldInterface, (boolean)true)) {
            return target;
        }
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.removeInterface(oldInterface);
        proxyFactory.addInterface(newInterface);
        DelegatingIntroductionInterceptor delegatingInterceptor = new DelegatingIntroductionInterceptor(delegate);
        proxyFactory.addAdvice((Advice)delegatingInterceptor);
        return proxyFactory.getProxy();
    }

    public static <T> T createAdvisedProxy(Object target, Advisor advisor, Class<T> advisedInterface) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(advisor);
        proxyFactory.addInterface(advisedInterface);
        return advisedInterface.cast(proxyFactory.getProxy());
    }

    public static <T> T createAdvisedProxy(Object target, Class<T> advisedInterface, Advisor ... advisors) {
        return AopUtils.createAdvisedProxy(target, advisedInterface, Arrays.asList(advisors));
    }

    public static <T> T createAdvisedProxy(Object target, Class<T> advisedInterface, List<Advisor> advisors) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        for (Advisor advisor : advisors) {
            proxyFactory.addAdvisor(advisor);
        }
        proxyFactory.addInterface(advisedInterface);
        return advisedInterface.cast(proxyFactory.getProxy());
    }

    public static Object createAdvisedDynamicProxy(Object target, Advisor advisor) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(advisor);
        boolean hasInterfaces = false;
        for (Class<?> advisedInterface : target.getClass().getInterfaces()) {
            proxyFactory.addInterface(advisedInterface);
            hasInterfaces = true;
        }
        if (!hasInterfaces) {
            throw new IllegalArgumentException(target + " with class : " + target.getClass().getName() + " requires an interface to create a dynamic proxy");
        }
        return proxyFactory.getProxy();
    }
}

