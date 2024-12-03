/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  javax.inject.Provider
 */
package com.google.inject.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderWithDependencies;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Providers {
    private Providers() {
    }

    public static <T> Provider<T> of(final T instance) {
        return new Provider<T>(){

            @Override
            public T get() {
                return instance;
            }

            public String toString() {
                return "of(" + instance + ")";
            }
        };
    }

    public static <T> Provider<T> guicify(javax.inject.Provider<T> provider) {
        if (provider instanceof Provider) {
            return (Provider)provider;
        }
        final javax.inject.Provider delegate = (javax.inject.Provider)Preconditions.checkNotNull(provider, (Object)"provider");
        Set<InjectionPoint> injectionPoints = InjectionPoint.forInstanceMethodsAndFields(provider.getClass());
        if (injectionPoints.isEmpty()) {
            return new Provider<T>(){

                @Override
                public T get() {
                    return delegate.get();
                }

                public String toString() {
                    return "guicified(" + delegate + ")";
                }
            };
        }
        HashSet mutableDeps = Sets.newHashSet();
        for (InjectionPoint ip : injectionPoints) {
            mutableDeps.addAll(ip.getDependencies());
        }
        ImmutableSet dependencies = ImmutableSet.copyOf((Collection)mutableDeps);
        return new ProviderWithDependencies<T>((Set)dependencies){
            final /* synthetic */ Set val$dependencies;
            {
                this.val$dependencies = set;
            }

            @Inject
            void initialize(Injector injector) {
                injector.injectMembers(delegate);
            }

            @Override
            public Set<Dependency<?>> getDependencies() {
                return this.val$dependencies;
            }

            @Override
            public T get() {
                return delegate.get();
            }

            public String toString() {
                return "guicified(" + delegate + ")";
            }
        };
    }
}

