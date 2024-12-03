/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.HierarchicalBeanFactory
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 */
package com.atlassian.spring.container;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class AtlassianBeanFactory
extends DefaultListableBeanFactory {
    public AtlassianBeanFactory(AutowireCapableBeanFactory beanFactory) {
        super((BeanFactory)beanFactory);
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.copyConfigurationFrom((ConfigurableBeanFactory)beanFactory);
        }
    }

    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        existingBean = super.applyBeanPostProcessorsBeforeInitialization(existingBean, beanName);
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        while (parentBeanFactory != null) {
            if (parentBeanFactory instanceof AutowireCapableBeanFactory) {
                AutowireCapableBeanFactory autowireCapableParentFactory = (AutowireCapableBeanFactory)parentBeanFactory;
                existingBean = autowireCapableParentFactory.applyBeanPostProcessorsBeforeInitialization(existingBean, beanName);
            }
            if (parentBeanFactory instanceof HierarchicalBeanFactory) {
                parentBeanFactory = ((HierarchicalBeanFactory)parentBeanFactory).getParentBeanFactory();
                continue;
            }
            parentBeanFactory = null;
        }
        return existingBean;
    }

    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        existingBean = super.applyBeanPostProcessorsAfterInitialization(existingBean, beanName);
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        while (parentBeanFactory != null) {
            if (parentBeanFactory instanceof AutowireCapableBeanFactory) {
                AutowireCapableBeanFactory autowireCapableParentFactory = (AutowireCapableBeanFactory)parentBeanFactory;
                existingBean = autowireCapableParentFactory.applyBeanPostProcessorsAfterInitialization(existingBean, beanName);
            }
            if (parentBeanFactory instanceof HierarchicalBeanFactory) {
                parentBeanFactory = ((HierarchicalBeanFactory)parentBeanFactory).getParentBeanFactory();
                continue;
            }
            parentBeanFactory = null;
        }
        return existingBean;
    }

    public String toString() {
        return "toString overridden for performance reasons";
    }
}

