/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.Errors;
import com.google.inject.internal.Initializables;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.InternalFactoryToProviderAdapter;
import com.google.inject.internal.ProviderToInternalFactoryAdapter;
import com.google.inject.internal.util.$Objects;
import com.google.inject.spi.BindingScopingVisitor;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Scoping {
    public static final Scoping UNSCOPED = new Scoping(){

        @Override
        public <V> V acceptVisitor(BindingScopingVisitor<V> visitor) {
            return visitor.visitNoScoping();
        }

        @Override
        public Scope getScopeInstance() {
            return Scopes.NO_SCOPE;
        }

        public String toString() {
            return ((Object)Scopes.NO_SCOPE).toString();
        }

        @Override
        public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
        }
    };
    public static final Scoping SINGLETON_ANNOTATION = new Scoping(){

        @Override
        public <V> V acceptVisitor(BindingScopingVisitor<V> visitor) {
            return visitor.visitScopeAnnotation(Singleton.class);
        }

        @Override
        public Class<? extends Annotation> getScopeAnnotation() {
            return Singleton.class;
        }

        public String toString() {
            return Singleton.class.getName();
        }

        @Override
        public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
            scopedBindingBuilder.in(Singleton.class);
        }
    };
    public static final Scoping SINGLETON_INSTANCE = new Scoping(){

        @Override
        public <V> V acceptVisitor(BindingScopingVisitor<V> visitor) {
            return visitor.visitScope(Scopes.SINGLETON);
        }

        @Override
        public Scope getScopeInstance() {
            return Scopes.SINGLETON;
        }

        public String toString() {
            return ((Object)Scopes.SINGLETON).toString();
        }

        @Override
        public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
            scopedBindingBuilder.in(Scopes.SINGLETON);
        }
    };
    public static final Scoping EAGER_SINGLETON = new Scoping(){

        @Override
        public <V> V acceptVisitor(BindingScopingVisitor<V> visitor) {
            return visitor.visitEagerSingleton();
        }

        @Override
        public Scope getScopeInstance() {
            return Scopes.SINGLETON;
        }

        public String toString() {
            return "eager singleton";
        }

        @Override
        public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
            scopedBindingBuilder.asEagerSingleton();
        }
    };

    public static Scoping forAnnotation(final Class<? extends Annotation> scopingAnnotation) {
        if (scopingAnnotation == Singleton.class || scopingAnnotation == javax.inject.Singleton.class) {
            return SINGLETON_ANNOTATION;
        }
        return new Scoping(){

            @Override
            public <V> V acceptVisitor(BindingScopingVisitor<V> visitor) {
                return visitor.visitScopeAnnotation(scopingAnnotation);
            }

            @Override
            public Class<? extends Annotation> getScopeAnnotation() {
                return scopingAnnotation;
            }

            public String toString() {
                return scopingAnnotation.getName();
            }

            @Override
            public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
                scopedBindingBuilder.in(scopingAnnotation);
            }
        };
    }

    public static Scoping forInstance(final Scope scope) {
        if (scope == Scopes.SINGLETON) {
            return SINGLETON_INSTANCE;
        }
        return new Scoping(){

            @Override
            public <V> V acceptVisitor(BindingScopingVisitor<V> visitor) {
                return visitor.visitScope(scope);
            }

            @Override
            public Scope getScopeInstance() {
                return scope;
            }

            public String toString() {
                return ((Object)scope).toString();
            }

            @Override
            public void applyTo(ScopedBindingBuilder scopedBindingBuilder) {
                scopedBindingBuilder.in(scope);
            }
        };
    }

    public boolean isExplicitlyScoped() {
        return this != UNSCOPED;
    }

    public boolean isNoScope() {
        return this.getScopeInstance() == Scopes.NO_SCOPE;
    }

    public boolean isEagerSingleton(Stage stage) {
        if (this == EAGER_SINGLETON) {
            return true;
        }
        if (stage == Stage.PRODUCTION) {
            return this == SINGLETON_ANNOTATION || this == SINGLETON_INSTANCE;
        }
        return false;
    }

    public Scope getScopeInstance() {
        return null;
    }

    public Class<? extends Annotation> getScopeAnnotation() {
        return null;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Scoping) {
            Scoping o = (Scoping)obj;
            return $Objects.equal(this.getScopeAnnotation(), o.getScopeAnnotation()) && $Objects.equal(this.getScopeInstance(), o.getScopeInstance());
        }
        return false;
    }

    public int hashCode() {
        return $Objects.hashCode(this.getScopeAnnotation(), this.getScopeInstance());
    }

    public abstract <V> V acceptVisitor(BindingScopingVisitor<V> var1);

    public abstract void applyTo(ScopedBindingBuilder var1);

    private Scoping() {
    }

    static <T> InternalFactory<? extends T> scope(Key<T> key, InjectorImpl injector, InternalFactory<? extends T> creator, Object source, Scoping scoping) {
        if (scoping.isNoScope()) {
            return creator;
        }
        Scope scope = scoping.getScopeInstance();
        Provider<? extends T> scoped = scope.scope(key, new ProviderToInternalFactoryAdapter<T>(injector, creator));
        return new InternalFactoryToProviderAdapter<T>(Initializables.of(scoped), source);
    }

    static Scoping makeInjectable(Scoping scoping, InjectorImpl injector, Errors errors) {
        Class<? extends Annotation> scopeAnnotation = scoping.getScopeAnnotation();
        if (scopeAnnotation == null) {
            return scoping;
        }
        Scope scope = injector.state.getScope(scopeAnnotation);
        if (scope != null) {
            return Scoping.forInstance(scope);
        }
        errors.scopeNotFound(scopeAnnotation);
        return UNSCOPED;
    }
}

