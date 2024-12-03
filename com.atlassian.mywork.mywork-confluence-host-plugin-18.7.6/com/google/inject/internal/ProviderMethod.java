/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.Exposed;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.internal.Exceptions;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$StackTraceElements;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ProviderWithDependencies;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ProviderMethod<T>
implements ProviderWithDependencies<T> {
    private final Key<T> key;
    private final Class<? extends Annotation> scopeAnnotation;
    private final Object instance;
    private final Method method;
    private final $ImmutableSet<Dependency<?>> dependencies;
    private final List<Provider<?>> parameterProviders;
    private final boolean exposed;

    ProviderMethod(Key<T> key, Method method, Object instance, $ImmutableSet<Dependency<?>> dependencies, List<Provider<?>> parameterProviders, Class<? extends Annotation> scopeAnnotation) {
        this.key = key;
        this.scopeAnnotation = scopeAnnotation;
        this.instance = instance;
        this.dependencies = dependencies;
        this.method = method;
        this.parameterProviders = parameterProviders;
        this.exposed = method.isAnnotationPresent(Exposed.class);
        method.setAccessible(true);
    }

    public Key<T> getKey() {
        return this.key;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object getInstance() {
        return this.instance;
    }

    public void configure(Binder binder) {
        binder = binder.withSource(this.method);
        if (this.scopeAnnotation != null) {
            binder.bind(this.key).toProvider(this).in(this.scopeAnnotation);
        } else {
            binder.bind(this.key).toProvider(this);
        }
        if (this.exposed) {
            ((PrivateBinder)binder).expose(this.key);
        }
    }

    @Override
    public T get() {
        Object[] parameters = new Object[this.parameterProviders.size()];
        for (int i = 0; i < parameters.length; ++i) {
            parameters[i] = this.parameterProviders.get(i).get();
        }
        try {
            Object result = this.method.invoke(this.instance, parameters);
            return (T)result;
        }
        catch (IllegalAccessException e) {
            throw new AssertionError((Object)e);
        }
        catch (InvocationTargetException e) {
            throw Exceptions.throwCleanly(e);
        }
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return this.dependencies;
    }

    public String toString() {
        return "@Provides " + $StackTraceElements.forMember(this.method).toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ProviderMethod) {
            ProviderMethod o = (ProviderMethod)obj;
            return this.method.equals(o.method) && this.instance.equals(o.instance);
        }
        return false;
    }

    public int hashCode() {
        return $Objects.hashCode(this.method);
    }
}

