/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.aop.aspectj.annotation.AspectMetadata;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class BeanFactoryAspectInstanceFactory
implements MetadataAwareAspectInstanceFactory,
Serializable {
    private final BeanFactory beanFactory;
    private final String name;
    private final AspectMetadata aspectMetadata;

    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name) {
        this(beanFactory, name, null);
    }

    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name, @Nullable Class<?> type) {
        Assert.notNull((Object)beanFactory, "BeanFactory must not be null");
        Assert.notNull((Object)name, "Bean name must not be null");
        this.beanFactory = beanFactory;
        this.name = name;
        Class<?> resolvedType = type;
        if (type == null) {
            resolvedType = beanFactory.getType(name);
            Assert.notNull(resolvedType, "Unresolvable bean type - explicitly specify the aspect class");
        }
        this.aspectMetadata = new AspectMetadata(resolvedType, name);
    }

    @Override
    public Object getAspectInstance() {
        return this.beanFactory.getBean(this.name);
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return this.beanFactory instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader() : ClassUtils.getDefaultClassLoader();
    }

    @Override
    public AspectMetadata getAspectMetadata() {
        return this.aspectMetadata;
    }

    @Override
    @Nullable
    public Object getAspectCreationMutex() {
        if (this.beanFactory.isSingleton(this.name)) {
            return null;
        }
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.beanFactory).getSingletonMutex();
        }
        return this;
    }

    @Override
    public int getOrder() {
        Class<?> type = this.beanFactory.getType(this.name);
        if (type != null) {
            if (Ordered.class.isAssignableFrom(type) && this.beanFactory.isSingleton(this.name)) {
                return ((Ordered)this.beanFactory.getBean(this.name)).getOrder();
            }
            return OrderUtils.getOrder(type, Integer.MAX_VALUE);
        }
        return Integer.MAX_VALUE;
    }

    public String toString() {
        return this.getClass().getSimpleName() + ": bean name '" + this.name + "'";
    }
}

