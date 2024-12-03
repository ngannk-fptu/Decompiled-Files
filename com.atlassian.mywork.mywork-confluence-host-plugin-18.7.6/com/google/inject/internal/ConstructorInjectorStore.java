/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.ConstructorInjector;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.FailableCache;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.MembersInjectorImpl;
import com.google.inject.internal.MethodAspect;
import com.google.inject.internal.ProxyFactory;
import com.google.inject.internal.SingleParameterInjector;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Iterables;
import com.google.inject.spi.InjectionPoint;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ConstructorInjectorStore {
    private final InjectorImpl injector;
    private final FailableCache<InjectionPoint, ConstructorInjector<?>> cache = new FailableCache<InjectionPoint, ConstructorInjector<?>>(){

        @Override
        protected ConstructorInjector<?> create(InjectionPoint constructorInjector, Errors errors) throws ErrorsException {
            return ConstructorInjectorStore.this.createConstructor(constructorInjector, errors);
        }
    };

    ConstructorInjectorStore(InjectorImpl injector) {
        this.injector = injector;
    }

    public ConstructorInjector<?> get(InjectionPoint constructorInjector, Errors errors) throws ErrorsException {
        return this.cache.get(constructorInjector, errors);
    }

    boolean remove(InjectionPoint ip) {
        return this.cache.remove(ip);
    }

    private <T> ConstructorInjector<T> createConstructor(InjectionPoint injectionPoint, Errors errors) throws ErrorsException {
        int numErrorsBefore = errors.size();
        SingleParameterInjector<?>[] constructorParameterInjectors = this.injector.getParametersInjectors(injectionPoint.getDependencies(), errors);
        MembersInjectorImpl<?> membersInjector = this.injector.membersInjectorStore.get(injectionPoint.getDeclaringType(), errors);
        $ImmutableList<MethodAspect> injectorAspects = this.injector.state.getMethodAspects();
        $ImmutableList<MethodAspect> methodAspects = membersInjector.getAddedAspects().isEmpty() ? injectorAspects : $ImmutableList.copyOf($Iterables.concat(injectorAspects, membersInjector.getAddedAspects()));
        ProxyFactory factory = new ProxyFactory(injectionPoint, methodAspects);
        errors.throwIfNewErrors(numErrorsBefore);
        return new ConstructorInjector(membersInjector.getInjectionPoints(), factory.create(), constructorParameterInjectors, membersInjector);
    }
}

