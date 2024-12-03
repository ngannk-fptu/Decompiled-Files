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

public abstract class PerRequestTypeInjectableProvider<A extends Annotation, T>
implements InjectableProvider<A, Type> {
    private final Type t;

    public PerRequestTypeInjectableProvider(Type t) {
        this.t = t;
    }

    @Override
    public final ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public final Injectable getInjectable(ComponentContext ic, A a, Type c) {
        if (c.equals(this.t)) {
            return this.getInjectable(ic, a);
        }
        return null;
    }

    public abstract Injectable<T> getInjectable(ComponentContext var1, A var2);
}

