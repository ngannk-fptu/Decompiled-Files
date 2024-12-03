/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.PropertyValueProvider;
import org.springframework.lang.Nullable;

public class PersistentEntityParameterValueProvider<P extends PersistentProperty<P>>
implements ParameterValueProvider<P> {
    private final PersistentEntity<?, P> entity;
    private final PropertyValueProvider<P> provider;
    @Nullable
    private final Object parent;

    public PersistentEntityParameterValueProvider(PersistentEntity<?, P> entity, PropertyValueProvider<P> provider, Object parent) {
        this.entity = entity;
        this.provider = provider;
        this.parent = parent;
    }

    @Override
    @Nullable
    public <T> T getParameterValue(PreferredConstructor.Parameter<T, P> parameter) {
        PreferredConstructor<T, P> constructor = this.entity.getPersistenceConstructor();
        if (constructor != null && constructor.isEnclosingClassParameter(parameter)) {
            return (T)this.parent;
        }
        String name = parameter.getName();
        if (name == null) {
            throw new MappingException(String.format("Parameter %s does not have a name!", parameter));
        }
        P property = this.entity.getPersistentProperty(name);
        if (property == null) {
            throw new MappingException(String.format("No property %s found on entity %s to bind constructor parameter to!", name, this.entity.getType()));
        }
        return this.provider.getPropertyValue(property);
    }
}

