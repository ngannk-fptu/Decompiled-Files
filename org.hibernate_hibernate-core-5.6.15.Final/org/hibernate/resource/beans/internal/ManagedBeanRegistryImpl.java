/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.internal;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.container.spi.FallbackContainedBean;
import org.hibernate.resource.beans.internal.FallbackBeanInstanceProducer;
import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.service.spi.Stoppable;

public class ManagedBeanRegistryImpl
implements ManagedBeanRegistry,
BeanContainer.LifecycleOptions,
Stoppable {
    private Map<String, ManagedBean<?>> registrations = new HashMap();
    private final BeanContainer beanContainer;

    public ManagedBeanRegistryImpl(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    @Override
    public BeanContainer getBeanContainer() {
        return this.beanContainer;
    }

    @Override
    public boolean canUseCachedReferences() {
        return true;
    }

    @Override
    public boolean useJpaCompliantCreation() {
        return true;
    }

    @Override
    public <T> ManagedBean<T> getBean(Class<T> beanClass) {
        ContainedBean<T> containedBean;
        ManagedBean<?> existing = this.registrations.get(beanClass.getName());
        if (existing != null) {
            return existing;
        }
        ManagedBean<Object> bean = this.beanContainer == null ? new FallbackContainedBean<T>(beanClass, FallbackBeanInstanceProducer.INSTANCE) : ((containedBean = this.beanContainer.getBean(beanClass, this, FallbackBeanInstanceProducer.INSTANCE)) instanceof ManagedBean ? (ManagedBean)((Object)containedBean) : new ContainedBeanManagedBeanAdapter(beanClass, containedBean));
        this.registrations.put(beanClass.getName(), bean);
        return bean;
    }

    @Override
    public <T> ManagedBean<T> getBean(String beanName, Class<T> beanContract) {
        ContainedBean<T> containedBean;
        String key = beanContract.getName() + ':' + beanName;
        ManagedBean<?> existing = this.registrations.get(key);
        if (existing != null) {
            return existing;
        }
        ManagedBean<Object> bean = this.beanContainer == null ? new FallbackContainedBean<T>(beanName, beanContract, FallbackBeanInstanceProducer.INSTANCE) : ((containedBean = this.beanContainer.getBean(beanName, beanContract, this, FallbackBeanInstanceProducer.INSTANCE)) instanceof ManagedBean ? (ManagedBean)((Object)containedBean) : new ContainedBeanManagedBeanAdapter(beanContract, containedBean));
        this.registrations.put(key, bean);
        return bean;
    }

    @Override
    public void stop() {
        if (this.beanContainer != null) {
            this.beanContainer.stop();
        }
        this.registrations.clear();
    }

    private static class ContainedBeanManagedBeanAdapter<B>
    implements ManagedBean<B> {
        private final Class<B> beanClass;
        private final ContainedBean<B> containedBean;

        private ContainedBeanManagedBeanAdapter(Class<B> beanClass, ContainedBean<B> containedBean) {
            this.beanClass = beanClass;
            this.containedBean = containedBean;
        }

        @Override
        public Class<B> getBeanClass() {
            return this.beanClass;
        }

        @Override
        public B getBeanInstance() {
            return this.containedBean.getBeanInstance();
        }
    }
}

