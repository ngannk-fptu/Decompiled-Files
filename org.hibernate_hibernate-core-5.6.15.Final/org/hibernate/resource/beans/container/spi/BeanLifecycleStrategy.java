/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.spi;

import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;

public interface BeanLifecycleStrategy {
    public <B> ContainedBeanImplementor<B> createBean(Class<B> var1, BeanInstanceProducer var2, BeanContainer var3);

    public <B> ContainedBeanImplementor<B> createBean(String var1, Class<B> var2, BeanInstanceProducer var3, BeanContainer var4);
}

