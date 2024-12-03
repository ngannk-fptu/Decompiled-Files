/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.internal.CircularDependencyProxy;
import com.google.inject.internal.InternalInjectorCreator;
import com.google.inject.internal.LinkedBindingImpl;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.ExposedBinding;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Scopes {
    private static final Object NULL = new Object();
    public static final Scope SINGLETON = new Scope(){

        @Override
        public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
            return new Provider<T>(){
                private volatile Object instance;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 * Enabled aggressive block sorting
                 * Enabled unnecessary exception pruning
                 * Enabled aggressive exception aggregation
                 * Converted monitor instructions to comments
                 * Lifted jumps to return sites
                 */
                @Override
                public T get() {
                    Object localInstance;
                    if (this.instance == null) {
                        Class<InternalInjectorCreator> clazz = InternalInjectorCreator.class;
                        // MONITORENTER : com.google.inject.internal.InternalInjectorCreator.class
                        if (this.instance == null) {
                            Object providedOrSentinel;
                            Object provided = creator.get();
                            if (provided instanceof CircularDependencyProxy) {
                                // MONITOREXIT : clazz
                                return provided;
                            }
                            Object object = providedOrSentinel = provided == null ? NULL : provided;
                            if (this.instance != null && this.instance != providedOrSentinel) {
                                throw new ProvisionException("Provider was reentrant while creating a singleton");
                            }
                            this.instance = providedOrSentinel;
                        }
                        // MONITOREXIT : clazz
                    }
                    Object returnedInstance = (localInstance = this.instance) != NULL ? localInstance : null;
                    return returnedInstance;
                }

                public String toString() {
                    return String.format("%s[%s]", creator, SINGLETON);
                }
            };
        }

        @Override
        public String toString() {
            return "Scopes.SINGLETON";
        }
    };
    public static final Scope NO_SCOPE = new Scope(){

        @Override
        public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
            return unscoped;
        }

        @Override
        public String toString() {
            return "Scopes.NO_SCOPE";
        }
    };

    private Scopes() {
    }

    public static boolean isSingleton(Binding<?> binding) {
        while (true) {
            ExposedBinding exposedBinding;
            Injector injector;
            boolean singleton;
            if (singleton = binding.acceptScopingVisitor(new BindingScopingVisitor<Boolean>(){

                @Override
                public Boolean visitNoScoping() {
                    return false;
                }

                @Override
                public Boolean visitScopeAnnotation(Class<? extends Annotation> scopeAnnotation) {
                    return scopeAnnotation == Singleton.class || scopeAnnotation == javax.inject.Singleton.class;
                }

                @Override
                public Boolean visitScope(Scope scope) {
                    return scope == SINGLETON;
                }

                @Override
                public Boolean visitEagerSingleton() {
                    return true;
                }
            }).booleanValue()) {
                return true;
            }
            if (binding instanceof LinkedBindingImpl) {
                LinkedBindingImpl linkedBinding = (LinkedBindingImpl)binding;
                injector = linkedBinding.getInjector();
                if (injector == null) break;
                binding = injector.getBinding(linkedBinding.getLinkedKey());
                continue;
            }
            if (!(binding instanceof ExposedBinding) || (injector = (exposedBinding = (ExposedBinding)binding).getPrivateElements().getInjector()) == null) break;
            binding = injector.getBinding(exposedBinding.getKey());
        }
        return false;
    }
}

