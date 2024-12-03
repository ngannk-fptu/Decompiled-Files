/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.context.spi.CreationalContext
 *  javax.inject.Provider
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.AbstractBean;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.enterprise.context.spi.CreationalContext;
import javax.inject.Provider;

public class ProviderBasedBean<T>
extends AbstractBean<T> {
    private Provider<T> provider;

    public ProviderBasedBean(Class<?> klass, Provider<T> provider, Annotation qualifier) {
        super(klass, qualifier);
        this.provider = provider;
    }

    public ProviderBasedBean(Class<?> klass, Type type, Provider<T> provider, Annotation qualifier) {
        super(klass, type, qualifier);
        this.provider = provider;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        return (T)this.provider.get();
    }
}

