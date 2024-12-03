/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory instanceof ConfigurableListableBeanFactory ? (ConfigurableListableBeanFactory)beanFactory : null;
    }

    @Override
    protected ProxyFactory prepareProxyFactory(Object bean2, String beanName) {
        ProxyFactory proxyFactory;
        if (this.beanFactory != null) {
            AutoProxyUtils.exposeTargetClass(this.beanFactory, beanName, bean2.getClass());
        }
        if (!(proxyFactory = super.prepareProxyFactory(bean2, beanName)).isProxyTargetClass() && this.beanFactory != null && AutoProxyUtils.shouldProxyTargetClass(this.beanFactory, beanName)) {
            proxyFactory.setProxyTargetClass(true);
        }
        return proxyFactory;
    }
}

