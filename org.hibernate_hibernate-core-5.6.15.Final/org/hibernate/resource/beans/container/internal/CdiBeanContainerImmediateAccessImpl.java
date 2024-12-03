/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.BeanManager
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.beans.container.internal;

import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.resource.beans.container.spi.AbstractCdiBeanContainer;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.jboss.logging.Logger;

public class CdiBeanContainerImmediateAccessImpl
extends AbstractCdiBeanContainer {
    private static final Logger log = Logger.getLogger(CdiBeanContainerImmediateAccessImpl.class);
    private final BeanManager beanManager;

    private CdiBeanContainerImmediateAccessImpl(BeanManager beanManager) {
        log.debugf("Standard access requested to CDI BeanManager : " + beanManager, new Object[0]);
        this.beanManager = beanManager;
    }

    @Override
    public BeanManager getUsableBeanManager() {
        return this.beanManager;
    }

    @Override
    protected <B> ContainedBeanImplementor<B> createBean(Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
        ContainedBeanImplementor<B> bean = lifecycleStrategy.createBean(beanType, fallbackProducer, this);
        bean.initialize();
        return bean;
    }

    @Override
    protected <B> ContainedBeanImplementor<B> createBean(String name, Class<B> beanType, BeanLifecycleStrategy lifecycleStrategy, BeanInstanceProducer fallbackProducer) {
        ContainedBeanImplementor<B> bean = lifecycleStrategy.createBean(name, beanType, fallbackProducer, this);
        bean.initialize();
        return bean;
    }
}

