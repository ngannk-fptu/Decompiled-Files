/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.spi;

import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.hibernate.service.spi.Stoppable;

public interface BeanContainer
extends Stoppable {
    public <B> ContainedBean<B> getBean(Class<B> var1, LifecycleOptions var2, BeanInstanceProducer var3);

    public <B> ContainedBean<B> getBean(String var1, Class<B> var2, LifecycleOptions var3, BeanInstanceProducer var4);

    public static interface LifecycleOptions {
        public boolean canUseCachedReferences();

        public boolean useJpaCompliantCreation();
    }
}

