/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Provider
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.AbstractBindingBuilder;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.ConstructorBindingImpl;
import com.google.inject.internal.InstanceBindingImpl;
import com.google.inject.internal.LinkedBindingImpl;
import com.google.inject.internal.LinkedProviderBindingImpl;
import com.google.inject.internal.ProviderInstanceBindingImpl;
import com.google.inject.internal.RehashableKeys;
import com.google.inject.internal.Scoping;
import com.google.inject.spi.Element;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Message;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BindingBuilder<T>
extends AbstractBindingBuilder<T>
implements AnnotatedBindingBuilder<T>,
RehashableKeys {
    public BindingBuilder(Binder binder, List<Element> elements, Object source, Key<T> key) {
        super(binder, elements, source, key);
    }

    @Override
    public BindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
        this.annotatedWithInternal(annotationType);
        return this;
    }

    @Override
    public BindingBuilder<T> annotatedWith(Annotation annotation) {
        this.annotatedWithInternal(annotation);
        return this;
    }

    @Override
    public BindingBuilder<T> to(Class<? extends T> implementation) {
        return this.to((Key)Key.get(implementation));
    }

    @Override
    public BindingBuilder<T> to(TypeLiteral<? extends T> implementation) {
        return this.to((Key)Key.get(implementation));
    }

    @Override
    public BindingBuilder<T> to(Key<? extends T> linkedKey) {
        Preconditions.checkNotNull(linkedKey, (Object)"linkedKey");
        this.checkNotTargetted();
        BindingImpl base = this.getBinding();
        this.setBinding(new LinkedBindingImpl<T>(base.getSource(), base.getKey(), base.getScoping(), linkedKey));
        return this;
    }

    @Override
    public void toInstance(T instance) {
        Object injectionPoints;
        this.checkNotTargetted();
        if (instance != null) {
            try {
                injectionPoints = InjectionPoint.forInstanceMethodsAndFields(instance.getClass());
            }
            catch (ConfigurationException e) {
                this.copyErrorsToBinder(e);
                injectionPoints = (Set)e.getPartialValue();
            }
        } else {
            this.binder.addError("Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.", new Object[0]);
            injectionPoints = ImmutableSet.of();
        }
        BindingImpl base = this.getBinding();
        this.setBinding(new InstanceBindingImpl(base.getSource(), base.getKey(), Scoping.EAGER_SINGLETON, (Set<InjectionPoint>)injectionPoints, instance));
    }

    @Override
    public BindingBuilder<T> toProvider(Provider<? extends T> provider) {
        Set injectionPoints;
        Preconditions.checkNotNull(provider, (Object)"provider");
        this.checkNotTargetted();
        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(provider.getClass());
        }
        catch (ConfigurationException e) {
            this.copyErrorsToBinder(e);
            injectionPoints = (Set)e.getPartialValue();
        }
        BindingImpl base = this.getBinding();
        this.setBinding(new ProviderInstanceBindingImpl<T>(base.getSource(), base.getKey(), base.getScoping(), injectionPoints, provider));
        return this;
    }

    @Override
    public BindingBuilder<T> toProvider(Class<? extends javax.inject.Provider<? extends T>> providerType) {
        return this.toProvider((Key)Key.get(providerType));
    }

    @Override
    public BindingBuilder<T> toProvider(TypeLiteral<? extends javax.inject.Provider<? extends T>> providerType) {
        return this.toProvider((Key)Key.get(providerType));
    }

    @Override
    public BindingBuilder<T> toProvider(Key<? extends javax.inject.Provider<? extends T>> providerKey) {
        Preconditions.checkNotNull(providerKey, (Object)"providerKey");
        this.checkNotTargetted();
        BindingImpl base = this.getBinding();
        this.setBinding(new LinkedProviderBindingImpl(base.getSource(), base.getKey(), base.getScoping(), providerKey));
        return this;
    }

    @Override
    public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor) {
        return this.toConstructor(constructor, TypeLiteral.get(constructor.getDeclaringClass()));
    }

    @Override
    public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor, TypeLiteral<? extends S> type) {
        Set injectionPoints;
        Preconditions.checkNotNull(constructor, (Object)"constructor");
        Preconditions.checkNotNull(type, (Object)"type");
        this.checkNotTargetted();
        BindingImpl base = this.getBinding();
        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(type);
        }
        catch (ConfigurationException e) {
            this.copyErrorsToBinder(e);
            injectionPoints = (Set)e.getPartialValue();
        }
        try {
            InjectionPoint constructorPoint = InjectionPoint.forConstructor(constructor, type);
            this.setBinding(new ConstructorBindingImpl(base.getKey(), base.getSource(), base.getScoping(), constructorPoint, injectionPoints));
        }
        catch (ConfigurationException e) {
            this.copyErrorsToBinder(e);
        }
        return this;
    }

    @Override
    public void rehashKeys() {
        this.setBinding(this.getBinding().withRehashedKeys());
    }

    public String toString() {
        return "BindingBuilder<" + this.getBinding().getKey().getTypeLiteral() + ">";
    }

    private void copyErrorsToBinder(ConfigurationException e) {
        for (Message message : e.getErrorMessages()) {
            this.binder.addError(message);
        }
    }
}

