/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.spi;

public interface ManagedBean<T> {
    public Class<T> getBeanClass();

    public T getBeanInstance();
}

