/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
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
                 */
                @Override
                public T get() {
                    Object localInstance;
                    if (this.instance == null) {
                        1 var1_1 = this;
                        synchronized (var1_1) {
                            if (this.instance == null) {
                                Object providedOrSentinel;
                                Object provided = creator.get();
                                if (Scopes.isCircularProxy(provided)) {
                                    return provided;
                                }
                                Object object = providedOrSentinel = provided == null ? NULL : provided;
                                if (this.instance != null && this.instance != providedOrSentinel) {
                                    throw new ProvisionException("Provider was reentrant while creating a singleton");
                                }
                                this.instance = providedOrSentinel;
                            }
                        }
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
    private static final BindingScopingVisitor<Boolean> IS_SINGLETON_VISITOR = new BindingScopingVisitor<Boolean>(){

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
    };

    private Scopes() {
    }

    public static boolean isSingleton(Binding<?> binding) {
        while (true) {
            ExposedBinding exposedBinding;
            Injector injector;
            boolean singleton;
            if (singleton = binding.acceptScopingVisitor(IS_SINGLETON_VISITOR).booleanValue()) {
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

    public static boolean isScoped(Binding<?> binding, final Scope scope, final Class<? extends Annotation> scopeAnnotation) {
        while (true) {
            ExposedBinding exposedBinding;
            Injector injector;
            boolean matches;
            if (matches = binding.acceptScopingVisitor(new BindingScopingVisitor<Boolean>(){

                @Override
                public Boolean visitNoScoping() {
                    return false;
                }

                @Override
                public Boolean visitScopeAnnotation(Class<? extends Annotation> visitedAnnotation) {
                    return visitedAnnotation == scopeAnnotation;
                }

                @Override
                public Boolean visitScope(Scope visitedScope) {
                    return visitedScope == scope;
                }

                @Override
                public Boolean visitEagerSingleton() {
                    return false;
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

    public static boolean isCircularProxy(Object object) {
        return object instanceof CircularDependencyProxy;
    }
}

