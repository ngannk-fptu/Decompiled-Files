/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.spi;

import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.hibernate.resource.beans.spi.ManagedBean;

public class FallbackContainedBean<B>
implements ContainedBean<B>,
ManagedBean<B> {
    private final Class<B> beanType;
    private final B beanInstance;

    public FallbackContainedBean(Class<B> beanType, BeanInstanceProducer producer) {
        this.beanType = beanType;
        this.beanInstance = producer.produceBeanInstance(beanType);
    }

    public FallbackContainedBean(String beanName, Class<B> beanType, BeanInstanceProducer producer) {
        this.beanType = beanType;
        this.beanInstance = producer.produceBeanInstance(beanName, beanType);
    }

    @Override
    public Class<B> getBeanClass() {
        return this.beanType;
    }

    @Override
    public B getBeanInstance() {
        return this.beanInstance;
    }
}

