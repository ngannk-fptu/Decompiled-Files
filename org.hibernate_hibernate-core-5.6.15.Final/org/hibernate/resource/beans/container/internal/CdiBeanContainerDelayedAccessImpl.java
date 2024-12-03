/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.BeanManager
 */
package org.hibernate.resource.beans.container.internal;

import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.resource.beans.container.spi.AbstractCdiBeanContainer;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;

public class CdiBeanContainerDelayedAccessImpl
extends AbstractCdiBeanContainer {
    private final BeanManager beanManager;

    private CdiBeanContainerDelayedAccessImpl(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public BeanManager getUsableBeanManager() {
        return this.beanManager;
    }

    @Override
    protected <B> ContainedBeanImplementor<B> createBean(Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
        return new BeanImpl(beanType, lifecycleStrategy, fallbackProducer);
    }

    @Override
    protected <B> ContainedBeanImplementor<B> createBean(String name, Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
        return new NamedBeanImpl(name, beanType, lifecycleStrategy, fallbackProducer);
    }

    private class NamedBeanImpl<B>
    implements ContainedBeanImplementor<B> {
        private final String name;
        private final Class<B> beanType;
        private final BeanLifecycleStrategy lifecycleStrategy;
        private final BeanInstanceProducer fallbackProducer;
        private ContainedBeanImplementor<B> delegateBean;

        private NamedBeanImpl(String name, Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
            this.name = name;
            this.beanType = beanType;
            this.lifecycleStrategy = lifecycleStrategy;
            this.fallbackProducer = fallbackProducer;
        }

        @Override
        public void initialize() {
            if (this.delegateBean == null) {
                this.delegateBean = this.lifecycleStrategy.createBean(this.name, this.beanType, this.fallbackProducer, CdiBeanContainerDelayedAccessImpl.this);
            }
        }

        @Override
        public B getBeanInstance() {
            if (this.delegateBean == null) {
                this.initialize();
            }
            return this.delegateBean.getBeanInstance();
        }

        @Override
        public void release() {
            this.delegateBean.release();
        }
    }

    private class BeanImpl<B>
    implements ContainedBeanImplementor<B> {
        private final Class<B> beanType;
        private final BeanLifecycleStrategy lifecycleStrategy;
        private final BeanInstanceProducer fallbackProducer;
        private ContainedBeanImplementor<B> delegateBean;

        private BeanImpl(Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
            this.beanType = beanType;
            this.lifecycleStrategy = lifecycleStrategy;
            this.fallbackProducer = fallbackProducer;
        }

        @Override
        public void initialize() {
            if (this.delegateBean == null) {
                this.delegateBean = this.lifecycleStrategy.createBean(this.beanType, this.fallbackProducer, CdiBeanContainerDelayedAccessImpl.this);
            }
        }

        @Override
        public B getBeanInstance() {
            if (this.delegateBean == null) {
                this.initialize();
            }
            return this.delegateBean.getBeanInstance();
        }

        @Override
        public void release() {
            this.delegateBean.release();
        }
    }
}

