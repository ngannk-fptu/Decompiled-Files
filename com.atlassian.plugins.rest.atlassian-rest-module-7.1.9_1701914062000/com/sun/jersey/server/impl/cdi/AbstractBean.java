/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.context.Dependent
 *  javax.enterprise.context.spi.CreationalContext
 *  javax.enterprise.inject.spi.Bean
 *  javax.enterprise.inject.spi.InjectionPoint
 */
package com.sun.jersey.server.impl.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public abstract class AbstractBean<T>
implements Bean<T> {
    private Class<?> klass;
    private Set<Annotation> qualifiers;
    private Set<Type> types;

    public AbstractBean(Class<?> klass, Annotation qualifier) {
        this(klass, klass, qualifier);
    }

    public AbstractBean(Class<?> klass, Set<Annotation> qualifiers) {
        this(klass, klass, qualifiers);
    }

    public AbstractBean(Class<?> klass, Type type, Annotation qualifier) {
        this.klass = klass;
        this.qualifiers = new HashSet<Annotation>();
        this.qualifiers.add(qualifier);
        this.types = new HashSet<Type>();
        this.types.add(type);
    }

    public AbstractBean(Class<?> klass, Type type, Set<Annotation> qualifiers) {
        this.klass = klass;
        this.qualifiers = qualifiers;
        this.types = new HashSet<Type>();
        this.types.add(type);
    }

    public Class<?> getBeanClass() {
        return this.klass;
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.EMPTY_SET;
    }

    public String getName() {
        return null;
    }

    public Set<Annotation> getQualifiers() {
        return this.qualifiers;
    }

    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.EMPTY_SET;
    }

    public Set<Type> getTypes() {
        return this.types;
    }

    public boolean isAlternative() {
        return false;
    }

    public boolean isNullable() {
        return false;
    }

    public abstract T create(CreationalContext<T> var1);

    public void destroy(T instance, CreationalContext<T> creationalContext) {
    }
}

