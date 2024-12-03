/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.BeanManager
 */
package org.hibernate.jpa.event.spi.jpa;

import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.resource.beans.container.spi.ExtendedBeanManager;

@Deprecated
public interface ExtendedBeanManager
extends org.hibernate.resource.beans.container.spi.ExtendedBeanManager {
    public void registerLifecycleListener(LifecycleListener var1);

    @Override
    default public void registerLifecycleListener(final ExtendedBeanManager.LifecycleListener lifecycleListener) {
        this.registerLifecycleListener(new LifecycleListener(){

            @Override
            public void beanManagerInitialized(BeanManager beanManager) {
                lifecycleListener.beanManagerInitialized(beanManager);
            }

            @Override
            public void beforeBeanManagerDestroyed(BeanManager beanManager) {
                lifecycleListener.beforeBeanManagerDestroyed(beanManager);
            }
        });
    }

    @Deprecated
    public static interface LifecycleListener
    extends ExtendedBeanManager.LifecycleListener {
    }
}

