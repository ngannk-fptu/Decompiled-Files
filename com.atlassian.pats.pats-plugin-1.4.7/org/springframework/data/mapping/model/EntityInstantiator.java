/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ParameterValueProvider;

public interface EntityInstantiator {
    public <T, E extends PersistentEntity<? extends T, P>, P extends PersistentProperty<P>> T createInstance(E var1, ParameterValueProvider<P> var2);
}

