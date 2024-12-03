/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.spi;

import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.service.Service;

public interface ManagedBeanRegistry
extends Service {
    public <T> ManagedBean<T> getBean(Class<T> var1);

    public <T> ManagedBean<T> getBean(String var1, Class<T> var2);

    public BeanContainer getBeanContainer();
}

