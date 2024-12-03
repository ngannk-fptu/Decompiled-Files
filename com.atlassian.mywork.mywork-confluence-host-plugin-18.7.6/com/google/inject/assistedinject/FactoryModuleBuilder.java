/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.assistedinject;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.BindingCollector;
import com.google.inject.assistedinject.FactoryProvider2;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FactoryModuleBuilder {
    private final BindingCollector bindings = new BindingCollector();

    public <T> FactoryModuleBuilder implement(Class<T> source, Class<? extends T> target) {
        return this.implement(source, TypeLiteral.get(target));
    }

    public <T> FactoryModuleBuilder implement(Class<T> source, TypeLiteral<? extends T> target) {
        return this.implement(TypeLiteral.get(source), target);
    }

    public <T> FactoryModuleBuilder implement(TypeLiteral<T> source, Class<? extends T> target) {
        return this.implement(source, TypeLiteral.get(target));
    }

    public <T> FactoryModuleBuilder implement(TypeLiteral<T> source, TypeLiteral<? extends T> target) {
        return this.implement(Key.get(source), target);
    }

    public <T> FactoryModuleBuilder implement(Class<T> source, Annotation annotation, Class<? extends T> target) {
        return this.implement(source, annotation, TypeLiteral.get(target));
    }

    public <T> FactoryModuleBuilder implement(Class<T> source, Annotation annotation, TypeLiteral<? extends T> target) {
        return this.implement(TypeLiteral.get(source), annotation, target);
    }

    public <T> FactoryModuleBuilder implement(TypeLiteral<T> source, Annotation annotation, Class<? extends T> target) {
        return this.implement(source, annotation, TypeLiteral.get(target));
    }

    public <T> FactoryModuleBuilder implement(TypeLiteral<T> source, Annotation annotation, TypeLiteral<? extends T> target) {
        return this.implement(Key.get(source, annotation), target);
    }

    public <T> FactoryModuleBuilder implement(Class<T> source, Class<? extends Annotation> annotationType, Class<? extends T> target) {
        return this.implement(source, annotationType, TypeLiteral.get(target));
    }

    public <T> FactoryModuleBuilder implement(Class<T> source, Class<? extends Annotation> annotationType, TypeLiteral<? extends T> target) {
        return this.implement(TypeLiteral.get(source), annotationType, target);
    }

    public <T> FactoryModuleBuilder implement(TypeLiteral<T> source, Class<? extends Annotation> annotationType, Class<? extends T> target) {
        return this.implement(source, annotationType, TypeLiteral.get(target));
    }

    public <T> FactoryModuleBuilder implement(TypeLiteral<T> source, Class<? extends Annotation> annotationType, TypeLiteral<? extends T> target) {
        return this.implement(Key.get(source, annotationType), target);
    }

    public <T> FactoryModuleBuilder implement(Key<T> source, Class<? extends T> target) {
        return this.implement(source, TypeLiteral.get(target));
    }

    public <T> FactoryModuleBuilder implement(Key<T> source, TypeLiteral<? extends T> target) {
        this.bindings.addBinding(source, target);
        return this;
    }

    public <F> Module build(Class<F> factoryInterface) {
        return this.build(TypeLiteral.get(factoryInterface));
    }

    public <F> Module build(TypeLiteral<F> factoryInterface) {
        return this.build(Key.get(factoryInterface));
    }

    public <F> Module build(final Key<F> factoryInterface) {
        return new AbstractModule(){

            protected void configure() {
                FactoryProvider2 provider = new FactoryProvider2(factoryInterface, FactoryModuleBuilder.this.bindings);
                this.bind(factoryInterface).toProvider(provider);
            }
        };
    }
}

