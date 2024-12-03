/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.ConstructionProxy;
import com.google.inject.internal.ConstructorInjector;
import com.google.inject.internal.DefaultConstructionProxyFactory;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.util.$Classes;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ConstructorBindingImpl<T>
extends BindingImpl<T>
implements ConstructorBinding<T> {
    private final Factory<T> factory;
    private final InjectionPoint constructorInjectionPoint;

    private ConstructorBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> scopedFactory, Scoping scoping, Factory<T> factory, InjectionPoint constructorInjectionPoint) {
        super(injector, key, source, scopedFactory, scoping);
        this.factory = factory;
        this.constructorInjectionPoint = constructorInjectionPoint;
    }

    public ConstructorBindingImpl(Key<T> key, Object source, Scoping scoping, InjectionPoint constructorInjectionPoint, Set<InjectionPoint> injectionPoints) {
        super(source, key, scoping);
        this.factory = new Factory(false, key);
        ConstructionProxy constructionProxy = new DefaultConstructionProxyFactory(constructorInjectionPoint).create();
        this.constructorInjectionPoint = constructorInjectionPoint;
        ((Factory)this.factory).constructorInjector = new ConstructorInjector(injectionPoints, constructionProxy, null, null);
    }

    static <T> ConstructorBindingImpl<T> create(InjectorImpl injector, Key<T> key, InjectionPoint constructorInjector, Object source, Scoping scoping, Errors errors, boolean failIfNotLinked) throws ErrorsException {
        Class<?> annotatedType;
        Class<? extends Annotation> scopeAnnotation;
        Class<Object> rawType;
        int numErrors = errors.size();
        Class<Object> clazz = rawType = constructorInjector == null ? key.getTypeLiteral().getRawType() : constructorInjector.getDeclaringType().getRawType();
        if (Modifier.isAbstract(rawType.getModifiers())) {
            errors.missingImplementation(key);
        }
        if ($Classes.isInnerClass(rawType)) {
            errors.cannotInjectInnerClass(rawType);
        }
        errors.throwIfNewErrors(numErrors);
        if (constructorInjector == null) {
            try {
                constructorInjector = InjectionPoint.forConstructorOf(key.getTypeLiteral());
            }
            catch (ConfigurationException e) {
                throw errors.merge(e.getErrorMessages()).toException();
            }
        }
        if (!scoping.isExplicitlyScoped() && (scopeAnnotation = Annotations.findScopeAnnotation(errors, annotatedType = constructorInjector.getMember().getDeclaringClass())) != null) {
            scoping = Scoping.makeInjectable(Scoping.forAnnotation(scopeAnnotation), injector, errors.withSource(rawType));
        }
        errors.throwIfNewErrors(numErrors);
        Factory factoryFactory = new Factory(failIfNotLinked, key);
        InternalFactory<T> scopedFactory = Scoping.scope(key, injector, factoryFactory, source, scoping);
        return new ConstructorBindingImpl<T>(injector, key, source, scopedFactory, scoping, factoryFactory, constructorInjector);
    }

    public void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
        ((Factory)this.factory).allowCircularProxy = !injector.options.disableCircularProxies;
        ((Factory)this.factory).constructorInjector = injector.constructors.get(this.constructorInjectionPoint, errors);
    }

    boolean isInitialized() {
        return ((Factory)this.factory).constructorInjector != null;
    }

    InjectionPoint getInternalConstructor() {
        if (((Factory)this.factory).constructorInjector != null) {
            return ((Factory)this.factory).constructorInjector.getConstructionProxy().getInjectionPoint();
        }
        return this.constructorInjectionPoint;
    }

    Set<Dependency<?>> getInternalDependencies() {
        $ImmutableSet.Builder<InjectionPoint> builder = $ImmutableSet.builder();
        if (((Factory)this.factory).constructorInjector == null) {
            builder.add(this.constructorInjectionPoint);
            try {
                builder.addAll(InjectionPoint.forInstanceMethodsAndFields(this.constructorInjectionPoint.getDeclaringType()));
            }
            catch (ConfigurationException configurationException) {}
        } else {
            builder.add(this.getConstructor()).addAll(this.getInjectableMembers());
        }
        return Dependency.forInjectionPoints(builder.build());
    }

    @Override
    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        $Preconditions.checkState(((Factory)this.factory).constructorInjector != null, "not initialized");
        return visitor.visit(this);
    }

    @Override
    public InjectionPoint getConstructor() {
        $Preconditions.checkState(((Factory)this.factory).constructorInjector != null, "Binding is not ready");
        return ((Factory)this.factory).constructorInjector.getConstructionProxy().getInjectionPoint();
    }

    @Override
    public Set<InjectionPoint> getInjectableMembers() {
        $Preconditions.checkState(((Factory)this.factory).constructorInjector != null, "Binding is not ready");
        return ((Factory)this.factory).constructorInjector.getInjectableMembers();
    }

    @Override
    public Map<Method, List<MethodInterceptor>> getMethodInterceptors() {
        $Preconditions.checkState(((Factory)this.factory).constructorInjector != null, "Binding is not ready");
        return ((Factory)this.factory).constructorInjector.getConstructionProxy().getMethodInterceptors();
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return Dependency.forInjectionPoints(new $ImmutableSet.Builder<InjectionPoint>().add(this.getConstructor()).addAll(this.getInjectableMembers()).build());
    }

    @Override
    protected BindingImpl<T> withScoping(Scoping scoping) {
        return new ConstructorBindingImpl(null, this.getKey(), this.getSource(), this.factory, scoping, this.factory, this.constructorInjectionPoint);
    }

    @Override
    protected BindingImpl<T> withKey(Key<T> key) {
        return new ConstructorBindingImpl<T>(null, key, this.getSource(), this.factory, this.getScoping(), this.factory, this.constructorInjectionPoint);
    }

    @Override
    public void applyTo(Binder binder) {
        InjectionPoint constructor = this.getConstructor();
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).toConstructor((Constructor)this.getConstructor().getMember(), constructor.getDeclaringType()));
    }

    @Override
    public String toString() {
        return new $ToStringBuilder(ConstructorBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", this.getScoping()).toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ConstructorBindingImpl) {
            ConstructorBindingImpl o = (ConstructorBindingImpl)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.constructorInjectionPoint, o.constructorInjectionPoint);
        }
        return false;
    }

    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping(), this.constructorInjectionPoint);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory<T>
    implements InternalFactory<T> {
        private final boolean failIfNotLinked;
        private final Key<?> key;
        private boolean allowCircularProxy;
        private ConstructorInjector<T> constructorInjector;

        Factory(boolean failIfNotLinked, Key<?> key) {
            this.failIfNotLinked = failIfNotLinked;
            this.key = key;
        }

        @Override
        public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
            $Preconditions.checkState(this.constructorInjector != null, "Constructor not ready");
            if (this.failIfNotLinked && !linked) {
                throw errors.jitDisabled(this.key).toException();
            }
            return (T)this.constructorInjector.construct(errors, context, dependency.getKey().getTypeLiteral().getRawType(), this.allowCircularProxy);
        }
    }
}

