/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.KotlinDetector
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import java.util.function.Function;
import org.springframework.core.KotlinDetector;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class InstantiationAwarePropertyAccessor<T>
implements PersistentPropertyAccessor<T> {
    private static final String NO_SETTER_OR_CONSTRUCTOR = "Cannot set property %s because no setter, wither or copy constructor exists for %s!";
    private static final String NO_CONSTRUCTOR_PARAMETER = "Cannot set property %s because no setter, no wither and it's not part of the persistence constructor %s!";
    private final Function<T, PersistentPropertyAccessor<T>> delegateFunction;
    private final EntityInstantiators instantiators;
    private T bean;

    @Deprecated
    public InstantiationAwarePropertyAccessor(PersistentPropertyAccessor<T> delegate, EntityInstantiators instantiators) {
        Assert.notNull(delegate, (String)"Delegate PersistentPropertyAccessor must not be null!");
        Assert.notNull((Object)instantiators, (String)"EntityInstantiators must not be null!");
        this.instantiators = instantiators;
        this.delegateFunction = t -> delegate;
        this.bean = delegate.getBean();
    }

    public InstantiationAwarePropertyAccessor(T bean, Function<T, PersistentPropertyAccessor<T>> accessorFunction, EntityInstantiators instantiators) {
        Assert.notNull(bean, (String)"Bean must not be null!");
        Assert.notNull(accessorFunction, (String)"PersistentPropertyAccessor function must not be null!");
        Assert.notNull((Object)instantiators, (String)"EntityInstantiators must not be null!");
        this.delegateFunction = accessorFunction;
        this.instantiators = instantiators;
        this.bean = bean;
    }

    @Override
    public void setProperty(final PersistentProperty<?> property, final @Nullable Object value) {
        final PersistentEntity<?, ?> owner = property.getOwner();
        final PersistentPropertyAccessor<T> delegate = this.delegateFunction.apply(this.bean);
        if (!property.isImmutable() || property.getWither() != null || KotlinDetector.isKotlinType(owner.getType())) {
            delegate.setProperty(property, value);
            this.bean = delegate.getBean();
            return;
        }
        PreferredConstructor<?, ?> constructor = owner.getPersistenceConstructor();
        if (constructor == null) {
            throw new IllegalStateException(String.format(NO_SETTER_OR_CONSTRUCTOR, property.getName(), owner.getType()));
        }
        if (!constructor.isConstructorParameter(property)) {
            throw new IllegalStateException(String.format(NO_CONSTRUCTOR_PARAMETER, property.getName(), constructor.getConstructor()));
        }
        constructor.getParameters().forEach(it -> {
            if (it.getName() == null) {
                throw new IllegalStateException(String.format("Cannot detect parameter names of copy constructor of %s!", owner.getType()));
            }
        });
        EntityInstantiator instantiator = this.instantiators.getInstantiatorFor(owner);
        this.bean = instantiator.createInstance(owner, new ParameterValueProvider(){

            @Nullable
            public Object getParameterValue(PreferredConstructor.Parameter parameter) {
                return property.getName().equals(parameter.getName()) ? value : delegate.getProperty((PersistentProperty<?>)owner.getRequiredPersistentProperty(parameter.getName()));
            }
        });
    }

    @Override
    @Nullable
    public Object getProperty(PersistentProperty<?> property) {
        return this.delegateFunction.apply(this.bean).getProperty(property);
    }

    @Override
    public T getBean() {
        return this.bean;
    }
}

