/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.mapping.model.PersistentPropertyAccessorFactory;

public enum BeanWrapperPropertyAccessorFactory implements PersistentPropertyAccessorFactory
{
    INSTANCE;


    @Override
    public <T> PersistentPropertyAccessor<T> getPropertyAccessor(PersistentEntity<?, ?> entity, T bean) {
        return new BeanWrapper<T>(bean);
    }

    @Override
    public boolean isSupported(PersistentEntity<?, ?> entity) {
        return true;
    }
}

