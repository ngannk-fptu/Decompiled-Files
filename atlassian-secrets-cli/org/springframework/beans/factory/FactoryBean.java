/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

public interface FactoryBean<T> {
    public static final String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";

    @Nullable
    public T getObject() throws Exception;

    @Nullable
    public Class<?> getObjectType();

    default public boolean isSingleton() {
        return true;
    }
}

