/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.framework.autoproxy;

import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;

public abstract class AbstractBeanFactoryAwareAdvisingPostProcessor
extends AbstractAdvisingBeanPostProcessor
implements BeanFactoryAware {
    @Nullable
    private ConfigurableListableBeanFactory beanFactory;

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory instanceof ConfigurableListableBeanFactory ? (ConfigurableListableBeanFactory)beanFactory : null;
    }

    @Override
    protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
        ProxyFactory proxyFactory;
        if (this.beanFactory != null) {
            AutoProxyUtils.exposeTargetClass(this.beanFactory, beanName, bean.getClass());
        }
        if (!(proxyFactory = super.prepareProxyFactory(bean, beanName)).isProxyTargetClass() && this.beanFactory != null && AutoProxyUtils.shouldProxyTargetClass(this.beanFactory, beanName)) {
            proxyFactory.setProxyTargetClass(true);
        }
        return proxyFactory;
    }

    @Override
    protected boolean isEligible(Object bean, String beanName) {
        return !AutoProxyUtils.isOriginalInstance(beanName, bean.getClass()) && super.isEligible(bean, beanName);
    }
}

