/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.framework.autoproxy.target;

import org.springframework.aop.framework.autoproxy.target.AbstractBeanFactoryBasedTargetSourceCreator;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;

public class LazyInitTargetSourceCreator
extends AbstractBeanFactoryBasedTargetSourceCreator {
    @Override
    protected boolean isPrototypeBased() {
        return false;
    }

    @Override
    @Nullable
    protected AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> beanClass, String beanName) {
        BeanDefinition definition;
        if (this.getBeanFactory() instanceof ConfigurableListableBeanFactory && (definition = ((ConfigurableListableBeanFactory)this.getBeanFactory()).getBeanDefinition(beanName)).isLazyInit()) {
            return new LazyInitTargetSource();
        }
        return null;
    }
}

