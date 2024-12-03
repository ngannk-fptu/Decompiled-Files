/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.framework.autoproxy;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;

public class InfrastructureAdvisorAutoProxyCreator
extends AbstractAdvisorAutoProxyCreator {
    @Nullable
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    protected void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.initBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    @Override
    protected boolean isEligibleAdvisorBean(String beanName) {
        return this.beanFactory != null && this.beanFactory.containsBeanDefinition(beanName) && this.beanFactory.getBeanDefinition(beanName).getRole() == 2;
    }
}

