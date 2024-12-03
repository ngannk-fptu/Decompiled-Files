/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.core.SmartClassLoader
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.framework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyProcessorSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;

public abstract class AbstractAdvisingBeanPostProcessor
extends ProxyProcessorSupport
implements BeanPostProcessor {
    @Nullable
    protected Advisor advisor;
    protected boolean beforeExistingAdvisors = false;
    private final Map<Class<?>, Boolean> eligibleBeans = new ConcurrentHashMap(256);

    public void setBeforeExistingAdvisors(boolean beforeExistingAdvisors) {
        this.beforeExistingAdvisors = beforeExistingAdvisors;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Advised advised;
        if (this.advisor == null || bean instanceof AopInfrastructureBean) {
            return bean;
        }
        if (bean instanceof Advised && !(advised = (Advised)bean).isFrozen() && this.isEligible(AopUtils.getTargetClass(bean))) {
            if (this.beforeExistingAdvisors) {
                advised.addAdvisor(0, this.advisor);
            } else {
                advised.addAdvisor(this.advisor);
            }
            return bean;
        }
        if (this.isEligible(bean, beanName)) {
            ProxyFactory proxyFactory = this.prepareProxyFactory(bean, beanName);
            if (!proxyFactory.isProxyTargetClass()) {
                this.evaluateProxyInterfaces(bean.getClass(), proxyFactory);
            }
            proxyFactory.addAdvisor(this.advisor);
            this.customizeProxyFactory(proxyFactory);
            ClassLoader classLoader = this.getProxyClassLoader();
            if (classLoader instanceof SmartClassLoader && classLoader != bean.getClass().getClassLoader()) {
                classLoader = ((SmartClassLoader)classLoader).getOriginalClassLoader();
            }
            return proxyFactory.getProxy(classLoader);
        }
        return bean;
    }

    protected boolean isEligible(Object bean, String beanName) {
        return this.isEligible(bean.getClass());
    }

    protected boolean isEligible(Class<?> targetClass) {
        Boolean eligible = this.eligibleBeans.get(targetClass);
        if (eligible != null) {
            return eligible;
        }
        if (this.advisor == null) {
            return false;
        }
        eligible = AopUtils.canApply(this.advisor, targetClass);
        this.eligibleBeans.put(targetClass, eligible);
        return eligible;
    }

    protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.copyFrom(this);
        proxyFactory.setTarget(bean);
        return proxyFactory;
    }

    protected void customizeProxyFactory(ProxyFactory proxyFactory) {
    }
}

