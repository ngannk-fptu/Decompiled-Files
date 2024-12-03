/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.spi;

import org.hibernate.resource.beans.spi.ManagedBean;

public class ProvidedInstanceManagedBeanImpl<T>
implements ManagedBean<T> {
    private final T instance;

    public ProvidedInstanceManagedBeanImpl(T instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Bean instance cannot be null");
        }
        this.instance = instance;
    }

    @Override
    public Class<T> getBeanClass() {
        return this.instance.getClass();
    }

    @Override
    public T getBeanInstance() {
        return this.instance;
    }
}

