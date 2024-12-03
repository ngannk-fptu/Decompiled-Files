/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.BeanManager
 */
package org.hibernate.resource.beans.container.spi;

import javax.enterprise.inject.spi.BeanManager;

public interface ExtendedBeanManager {
    public void registerLifecycleListener(LifecycleListener var1);

    public static interface LifecycleListener {
        public void beanManagerInitialized(BeanManager var1);

        public void beforeBeanManagerDestroyed(BeanManager var1);
    }
}

