/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.internal;

import org.hibernate.resource.beans.container.internal.ContainerManagedLifecycleStrategy;
import org.hibernate.resource.beans.container.internal.JpaCompliantLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;

public class Helper {
    public static final Helper INSTANCE = new Helper();

    private Helper() {
    }

    public String determineBeanCacheKey(Class beanType) {
        return beanType.getName();
    }

    public String determineBeanCacheKey(String name, Class beanType) {
        return beanType.getName() + ':' + name;
    }

    public BeanLifecycleStrategy getLifecycleStrategy(boolean shouldRegistryManageLifecycle) {
        if (shouldRegistryManageLifecycle) {
            return JpaCompliantLifecycleStrategy.INSTANCE;
        }
        return ContainerManagedLifecycleStrategy.INSTANCE;
    }
}

