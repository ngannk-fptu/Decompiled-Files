/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.convert;

import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ParameterValueProvider;

class EntityInstantiatorAdapter
implements EntityInstantiator {
    private final org.springframework.data.mapping.model.EntityInstantiator delegate;

    EntityInstantiatorAdapter(org.springframework.data.mapping.model.EntityInstantiator delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity, ParameterValueProvider<P> provider) {
        return this.delegate.createInstance(entity, provider);
    }
}

