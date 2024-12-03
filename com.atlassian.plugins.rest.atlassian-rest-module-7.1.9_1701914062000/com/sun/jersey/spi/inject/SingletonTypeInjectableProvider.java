/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.inject;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public abstract class SingletonTypeInjectableProvider<A extends Annotation, T>
implements InjectableProvider<A, Type>,
Injectable<T> {
    private final Type t;
    private final T instance;

    public SingletonTypeInjectableProvider(Type t, T instance) {
        this.t = t;
        this.instance = instance;
    }

    @Override
    public final ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public final Injectable<T> getInjectable(ComponentContext ic, A a, Type c) {
        if (c.equals(this.t)) {
            return this;
        }
        return null;
    }

    @Override
    public final T getValue() {
        return this.instance;
    }
}

