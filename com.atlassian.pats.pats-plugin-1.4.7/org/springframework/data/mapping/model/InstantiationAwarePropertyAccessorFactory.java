/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.mapping.model.InstantiationAwarePropertyAccessor;
import org.springframework.data.mapping.model.PersistentPropertyAccessorFactory;

public class InstantiationAwarePropertyAccessorFactory
implements PersistentPropertyAccessorFactory {
    private final PersistentPropertyAccessorFactory delegate;
    private final EntityInstantiators instantiators;

    public InstantiationAwarePropertyAccessorFactory(PersistentPropertyAccessorFactory delegate, EntityInstantiators instantiators) {
        this.delegate = delegate;
        this.instantiators = instantiators;
    }

    @Override
    public <T> PersistentPropertyAccessor<T> getPropertyAccessor(PersistentEntity<?, ?> entity, T bean) {
        return new InstantiationAwarePropertyAccessor<Object>(bean, it -> this.delegate.getPropertyAccessor(entity, it), this.instantiators);
    }

    @Override
    public boolean isSupported(PersistentEntity<?, ?> entity) {
        return this.delegate.isSupported(entity);
    }
}

