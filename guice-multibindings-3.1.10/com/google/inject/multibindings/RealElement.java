/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.inject.Binder
 *  com.google.inject.Key
 *  com.google.inject.Provider
 *  com.google.inject.Scope
 *  com.google.inject.Scopes
 *  com.google.inject.TypeLiteral
 *  com.google.inject.binder.LinkedBindingBuilder
 *  com.google.inject.binder.ScopedBindingBuilder
 *  com.google.inject.spi.InjectionPoint
 *  javax.inject.Provider
 */
package com.google.inject.multibindings;

import com.google.common.base.Objects;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.Element;
import com.google.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class RealElement
implements Element {
    private static final AtomicInteger nextUniqueId = new AtomicInteger(1);
    private final int uniqueId;
    private final String setName;
    private final Element.Type type;
    private final Object mapKey;
    private TargetType targetType = TargetType.UNTARGETTED;
    private Object target = null;
    private Object scope = Scopes.NO_SCOPE;
    private static final Object EAGER_SINGLETON = new Object();

    static <T> BindingBuilder<T> addBinding(Binder binder, Element.Type type, TypeLiteral<T> elementType, String setName) {
        RealElement annotation = new RealElement(setName, type, null);
        LinkedBindingBuilder delegate = binder.skipSources(new Class[]{RealElement.class}).bind(Key.get(elementType, (Annotation)annotation));
        return new BindingBuilder(annotation, delegate);
    }

    static <T> BindingBuilder<T> addMapBinding(Binder binder, Object mapKey, TypeLiteral<T> elementType, String setName) {
        RealElement annotation = new RealElement(setName, Element.Type.MAPBINDER, mapKey);
        LinkedBindingBuilder delegate = binder.skipSources(new Class[]{RealElement.class}).bind(Key.get(elementType, (Annotation)annotation));
        return new BindingBuilder(annotation, delegate);
    }

    private RealElement(String setName, Element.Type type, Object mapKey) {
        this.uniqueId = nextUniqueId.incrementAndGet();
        this.setName = setName;
        this.type = type;
        this.mapKey = mapKey;
    }

    @Override
    public String setName() {
        return this.setName;
    }

    @Override
    public int uniqueId() {
        return this.uniqueId;
    }

    @Override
    public Element.Type type() {
        return this.type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Element.class;
    }

    @Override
    public String toString() {
        return String.format("@%s(setName=%s, uniqueId=%d, type=%s)", new Object[]{this.annotationType().getName(), this.setName, this.uniqueId, this.type});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        RealElement other = (RealElement)obj;
        return this.setName.equals(other.setName) && this.type.equals((Object)other.type) && Objects.equal((Object)this.mapKey, (Object)other.mapKey) && this.scope.equals(other.scope) && this.targetType.equals((Object)other.targetType) && Objects.equal((Object)this.target, (Object)other.target);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.setName, this.type, this.mapKey, this.scope, this.targetType, this.target});
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class BindingBuilder<T>
    implements LinkedBindingBuilder<T> {
        private final RealElement annotation;
        private final LinkedBindingBuilder<T> delegate;

        BindingBuilder(RealElement annotation, LinkedBindingBuilder<T> delegate) {
            this.annotation = annotation;
            this.delegate = delegate;
        }

        RealElement getAnnotation() {
            return this.annotation;
        }

        public void in(Class<? extends Annotation> scopeAnnotation) {
            this.delegate.in(scopeAnnotation);
            this.annotation.scope = scopeAnnotation;
        }

        public void in(Scope scope) {
            this.delegate.in(scope);
            this.annotation.scope = scope;
        }

        public void asEagerSingleton() {
            this.delegate.asEagerSingleton();
            this.annotation.scope = EAGER_SINGLETON;
        }

        public ScopedBindingBuilder to(Class<? extends T> implementation) {
            return this.to(Key.get(implementation));
        }

        public ScopedBindingBuilder to(TypeLiteral<? extends T> implementation) {
            return this.to(Key.get(implementation));
        }

        public ScopedBindingBuilder to(Key<? extends T> targetKey) {
            this.delegate.to(targetKey);
            this.annotation.targetType = TargetType.LINKED_KEY;
            this.annotation.target = targetKey;
            return this;
        }

        public void toInstance(T instance) {
            this.delegate.toInstance(instance);
            this.annotation.scope = EAGER_SINGLETON;
            this.annotation.targetType = TargetType.INSTANCE;
            this.annotation.target = instance;
        }

        public ScopedBindingBuilder toProvider(Provider<? extends T> provider) {
            this.delegate.toProvider(provider);
            this.annotation.targetType = TargetType.PROVIDER_INSTANCE;
            this.annotation.target = provider;
            return this;
        }

        public ScopedBindingBuilder toProvider(Class<? extends javax.inject.Provider<? extends T>> providerType) {
            return this.toProvider(Key.get(providerType));
        }

        public ScopedBindingBuilder toProvider(TypeLiteral<? extends javax.inject.Provider<? extends T>> providerType) {
            return this.toProvider(Key.get(providerType));
        }

        public ScopedBindingBuilder toProvider(Key<? extends javax.inject.Provider<? extends T>> providerKey) {
            this.delegate.toProvider(providerKey);
            this.annotation.targetType = TargetType.PROVIDER_KEY;
            this.annotation.target = providerKey;
            return this;
        }

        public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor) {
            return this.toConstructor(constructor, TypeLiteral.get(constructor.getDeclaringClass()));
        }

        public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor, TypeLiteral<? extends S> type) {
            this.delegate.toConstructor(constructor, type);
            this.annotation.targetType = TargetType.CONSTRUCTOR;
            this.annotation.target = InjectionPoint.forConstructor(constructor, type);
            return this;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum TargetType {
        INSTANCE,
        PROVIDER_INSTANCE,
        PROVIDER_KEY,
        LINKED_KEY,
        UNTARGETTED,
        CONSTRUCTOR;

    }
}

