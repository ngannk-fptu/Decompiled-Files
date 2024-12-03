/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.util;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$Sets;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderWithDependencies;
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
        final javax.inject.Provider<T> delegate = $Preconditions.checkNotNull(provider, "provider");
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
        HashSet<Dependency<?>> mutableDeps = $Sets.newHashSet();
        for (InjectionPoint ip : injectionPoints) {
            mutableDeps.addAll(ip.getDependencies());
        }
        final $ImmutableSet dependencies = $ImmutableSet.copyOf(mutableDeps);
        return new ProviderWithDependencies<T>(){

            @Inject
            void initialize(Injector injector) {
                injector.injectMembers(delegate);
            }

            @Override
            public Set<Dependency<?>> getDependencies() {
                return dependencies;
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

