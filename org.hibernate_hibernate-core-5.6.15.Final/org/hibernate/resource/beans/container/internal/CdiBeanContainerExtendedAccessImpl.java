/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.BeanManager
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.beans.container.internal;

import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.resource.beans.container.internal.CdiBasedBeanContainer;
import org.hibernate.resource.beans.container.spi.AbstractCdiBeanContainer;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.container.spi.ExtendedBeanManager;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.jboss.logging.Logger;

public class CdiBeanContainerExtendedAccessImpl
extends AbstractCdiBeanContainer
implements ExtendedBeanManager.LifecycleListener {
    private static final Logger log = Logger.getLogger(CdiBeanContainerExtendedAccessImpl.class);
    private BeanManager usableBeanManager;
    private final CdiBasedBeanContainer DUMMY_BEAN_CONTAINER = new CdiBasedBeanContainer(){

        @Override
        public BeanManager getUsableBeanManager() {
            return CdiBeanContainerExtendedAccessImpl.this.usableBeanManager;
        }

        @Override
        public <B> ContainedBean<B> getBean(Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
            return CdiBeanContainerExtendedAccessImpl.this.getBean(beanType, lifecycleOptions, fallbackProducer);
        }

        @Override
        public <B> ContainedBean<B> getBean(String beanName, Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
            return CdiBeanContainerExtendedAccessImpl.this.getBean(beanName, beanType, lifecycleOptions, fallbackProducer);
        }

        @Override
        public void stop() {
        }
    };

    private CdiBeanContainerExtendedAccessImpl(ExtendedBeanManager beanManager) {
        beanManager.registerLifecycleListener(this);
        log.debugf("Extended access requested to CDI BeanManager : " + beanManager, new Object[0]);
    }

    @Override
    protected <B> ContainedBeanImplementor<B> createBean(Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
        if (this.usableBeanManager == null) {
            return new BeanImpl(beanType, lifecycleStrategy, fallbackProducer);
        }
        return lifecycleStrategy.createBean(beanType, fallbackProducer, this);
    }

    @Override
    protected <B> ContainedBeanImplementor<B> createBean(String name, Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
        if (this.usableBeanManager == null) {
            return new NamedBeanImpl(name, beanType, lifecycleStrategy, fallbackProducer);
        }
        return lifecycleStrategy.createBean(name, beanType, fallbackProducer, this);
    }

    @Override
    public void beanManagerInitialized(BeanManager beanManager) {
        this.usableBeanManager = beanManager;
        this.forEachBean(ContainedBeanImplementor::initialize);
    }

    @Override
    public void beforeBeanManagerDestroyed(BeanManager beanManager) {
        this.stop();
        this.usableBeanManager = null;
    }

    @Override
    public BeanManager getUsableBeanManager() {
        if (this.usableBeanManager == null) {
            throw new IllegalStateException("ExtendedBeanManager.LifecycleListener callback not yet called: CDI not (yet) usable");
        }
        return this.usableBeanManager;
    }

    private class NamedBeanImpl<B>
    implements ContainedBeanImplementor<B> {
        private final String name;
        private final Class<B> beanType;
        private final BeanLifecycleStrategy lifecycleStrategy;
        private final BeanInstanceProducer fallbackProducer;
        private ContainedBeanImplementor<B> delegateContainedBean;

        private NamedBeanImpl(String name, Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
            this.name = name;
            this.beanType = beanType;
            this.lifecycleStrategy = lifecycleStrategy;
            this.fallbackProducer = fallbackProducer;
        }

        @Override
        public void initialize() {
            if (this.delegateContainedBean == null) {
                this.delegateContainedBean = this.lifecycleStrategy.createBean(this.name, this.beanType, this.fallbackProducer, CdiBeanContainerExtendedAccessImpl.this.DUMMY_BEAN_CONTAINER);
                this.delegateContainedBean.initialize();
            }
        }

        @Override
        public B getBeanInstance() {
            if (this.delegateContainedBean == null) {
                this.initialize();
            }
            return this.delegateContainedBean.getBeanInstance();
        }

        @Override
        public void release() {
            this.delegateContainedBean.release();
            this.delegateContainedBean = null;
        }
    }

    private class BeanImpl<B>
    implements ContainedBeanImplementor<B> {
        private final Class<B> beanType;
        private final BeanLifecycleStrategy lifecycleStrategy;
        private final BeanInstanceProducer fallbackProducer;
        private ContainedBeanImplementor<B> delegateContainedBean;

        private BeanImpl(Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
            this.beanType = beanType;
            this.lifecycleStrategy = lifecycleStrategy;
            this.fallbackProducer = fallbackProducer;
        }

        @Override
        public void initialize() {
            if (this.delegateContainedBean == null) {
                this.delegateContainedBean = this.lifecycleStrategy.createBean(this.beanType, this.fallbackProducer, CdiBeanContainerExtendedAccessImpl.this.DUMMY_BEAN_CONTAINER);
            }
            this.delegateContainedBean.initialize();
        }

        @Override
        public B getBeanInstance() {
            if (this.delegateContainedBean == null) {
                this.initialize();
            }
            return this.delegateContainedBean.getBeanInstance();
        }

        @Override
        public void release() {
            this.delegateContainedBean.release();
            this.delegateContainedBean = null;
        }
    }
}

