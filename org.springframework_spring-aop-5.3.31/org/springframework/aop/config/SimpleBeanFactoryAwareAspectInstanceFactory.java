/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.aop.config;

import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class SimpleBeanFactoryAwareAspectInstanceFactory
implements AspectInstanceFactory,
BeanFactoryAware {
    @Nullable
    private String aspectBeanName;
    @Nullable
    private BeanFactory beanFactory;

    public void setAspectBeanName(String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Assert.notNull((Object)this.aspectBeanName, (String)"'aspectBeanName' is required");
    }

    @Override
    public Object getAspectInstance() {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory set");
        Assert.state((this.aspectBeanName != null ? 1 : 0) != 0, (String)"No 'aspectBeanName' set");
        return this.beanFactory.getBean(this.aspectBeanName);
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    public int getOrder() {
        if (this.beanFactory != null && this.aspectBeanName != null && this.beanFactory.isSingleton(this.aspectBeanName) && this.beanFactory.isTypeMatch(this.aspectBeanName, Ordered.class)) {
            return ((Ordered)this.beanFactory.getBean(this.aspectBeanName)).getOrder();
        }
        return Integer.MAX_VALUE;
    }
}

