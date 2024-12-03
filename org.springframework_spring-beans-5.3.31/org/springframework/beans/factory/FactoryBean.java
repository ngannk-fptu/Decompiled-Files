/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

