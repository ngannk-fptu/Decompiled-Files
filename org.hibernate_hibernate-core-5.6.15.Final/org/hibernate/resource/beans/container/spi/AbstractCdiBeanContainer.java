/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.hibernate.resource.beans.container.internal.CdiBasedBeanContainer;
import org.hibernate.resource.beans.container.internal.ContainerManagedLifecycleStrategy;
import org.hibernate.resource.beans.container.internal.JpaCompliantLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.internal.BeansMessageLogger;
import org.hibernate.resource.beans.internal.Helper;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;

public abstract class AbstractCdiBeanContainer
implements CdiBasedBeanContainer {
    private Map<String, ContainedBeanImplementor<?>> beanCache = new HashMap();
    private List<ContainedBeanImplementor<?>> registeredBeans = new ArrayList();

    @Override
    public <B> ContainedBean<B> getBean(Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        if (lifecycleOptions.canUseCachedReferences()) {
            return this.getCacheableBean(beanType, lifecycleOptions, fallbackProducer);
        }
        return this.createBean(beanType, lifecycleOptions, fallbackProducer);
    }

    private <B> ContainedBean<B> getCacheableBean(Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        String beanCacheKey = Helper.INSTANCE.determineBeanCacheKey(beanType);
        ContainedBeanImplementor<?> existing = this.beanCache.get(beanCacheKey);
        if (existing != null) {
            return existing;
        }
        ContainedBeanImplementor<B> bean = this.createBean(beanType, lifecycleOptions, fallbackProducer);
        this.beanCache.put(beanCacheKey, bean);
        return bean;
    }

    private <B> ContainedBeanImplementor<B> createBean(Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        ContainedBeanImplementor<B> bean = this.createBean(beanType, lifecycleOptions.useJpaCompliantCreation() ? JpaCompliantLifecycleStrategy.INSTANCE : ContainerManagedLifecycleStrategy.INSTANCE, fallbackProducer);
        this.registeredBeans.add(bean);
        return bean;
    }

    protected abstract <B> ContainedBeanImplementor<B> createBean(Class<B> var1, BeanLifecycleStrategy var2, BeanInstanceProducer var3);

    @Override
    public <B> ContainedBean<B> getBean(String beanName, Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        if (lifecycleOptions.canUseCachedReferences()) {
            return this.getCacheableBean(beanName, beanType, lifecycleOptions, fallbackProducer);
        }
        return this.createBean(beanName, beanType, lifecycleOptions, fallbackProducer);
    }

    private <B> ContainedBeanImplementor<B> getCacheableBean(String beanName, Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        String beanCacheKey = Helper.INSTANCE.determineBeanCacheKey(beanName, beanType);
        ContainedBeanImplementor<?> existing = this.beanCache.get(beanCacheKey);
        if (existing != null) {
            return existing;
        }
        ContainedBeanImplementor<B> bean = this.createBean(beanName, beanType, lifecycleOptions, fallbackProducer);
        this.beanCache.put(beanCacheKey, bean);
        return bean;
    }

    private <B> ContainedBeanImplementor<B> createBean(String beanName, Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        ContainedBeanImplementor<B> bean = this.createBean(beanName, beanType, lifecycleOptions.useJpaCompliantCreation() ? JpaCompliantLifecycleStrategy.INSTANCE : ContainerManagedLifecycleStrategy.INSTANCE, fallbackProducer);
        this.registeredBeans.add(bean);
        return bean;
    }

    protected abstract <B> ContainedBeanImplementor<B> createBean(String var1, Class<B> var2, BeanLifecycleStrategy var3, BeanInstanceProducer var4);

    protected final void forEachBean(Consumer<ContainedBeanImplementor<?>> consumer) {
        this.registeredBeans.forEach(consumer);
    }

    @Override
    public final void stop() {
        BeansMessageLogger.BEANS_LOGGER.stoppingBeanContainer(this);
        this.forEachBean(ContainedBeanImplementor::release);
        this.registeredBeans.clear();
        this.beanCache.clear();
    }
}

