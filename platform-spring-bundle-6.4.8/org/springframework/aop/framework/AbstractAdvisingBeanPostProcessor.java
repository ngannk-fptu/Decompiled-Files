/*
 * Decompiled with CFR 0.152.
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

    @Override
    public Object postProcessBeforeInitialization(Object bean2, String beanName) {
        return bean2;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean2, String beanName) {
        Advised advised;
        if (this.advisor == null || bean2 instanceof AopInfrastructureBean) {
            return bean2;
        }
        if (bean2 instanceof Advised && !(advised = (Advised)bean2).isFrozen() && this.isEligible(AopUtils.getTargetClass(bean2))) {
            if (this.beforeExistingAdvisors) {
                advised.addAdvisor(0, this.advisor);
            } else {
                advised.addAdvisor(this.advisor);
            }
            return bean2;
        }
        if (this.isEligible(bean2, beanName)) {
            ProxyFactory proxyFactory = this.prepareProxyFactory(bean2, beanName);
            if (!proxyFactory.isProxyTargetClass()) {
                this.evaluateProxyInterfaces(bean2.getClass(), proxyFactory);
            }
            proxyFactory.addAdvisor(this.advisor);
            this.customizeProxyFactory(proxyFactory);
            ClassLoader classLoader = this.getProxyClassLoader();
            if (classLoader instanceof SmartClassLoader && classLoader != bean2.getClass().getClassLoader()) {
                classLoader = ((SmartClassLoader)((Object)classLoader)).getOriginalClassLoader();
            }
            return proxyFactory.getProxy(classLoader);
        }
        return bean2;
    }

    protected boolean isEligible(Object bean2, String beanName) {
        return this.isEligible(bean2.getClass());
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

    protected ProxyFactory prepareProxyFactory(Object bean2, String beanName) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.copyFrom(this);
        proxyFactory.setTarget(bean2);
        return proxyFactory;
    }

    protected void customizeProxyFactory(ProxyFactory proxyFactory) {
    }
}

