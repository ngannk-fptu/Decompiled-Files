/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.convert;

import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.InternalEntityInstantiatorFactory;
import org.springframework.data.mapping.model.ParameterValueProvider;

@Deprecated
public class KotlinClassGeneratingEntityInstantiator
implements EntityInstantiator {
    private final org.springframework.data.mapping.model.EntityInstantiator delegate = InternalEntityInstantiatorFactory.getKotlinClassGeneratingEntityInstantiator();

    @Override
    public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E entity, ParameterValueProvider<P> provider) {
        return this.delegate.createInstance(entity, provider);
    }
}

