/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableSet
 */
package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.RehashableKeys;
import com.google.inject.internal.Scoping;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ProviderInstanceBindingImpl<T>
extends BindingImpl<T>
implements ProviderInstanceBinding<T> {
    final Provider<? extends T> providerInstance;
    final ImmutableSet<InjectionPoint> injectionPoints;

    public ProviderInstanceBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping, Provider<? extends T> providerInstance, Set<InjectionPoint> injectionPoints) {
        super(injector, key, source, internalFactory, scoping);
        this.providerInstance = providerInstance;
        this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
    }

    public ProviderInstanceBindingImpl(Object source, Key<T> key, Scoping scoping, Set<InjectionPoint> injectionPoints, Provider<? extends T> providerInstance) {
        super(source, key, scoping);
        this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
        this.providerInstance = providerInstance;
    }

    @Override
    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        if (this.providerInstance instanceof ProviderWithExtensionVisitor) {
            return ((ProviderWithExtensionVisitor)this.providerInstance).acceptExtensionVisitor(visitor, this);
        }
        return visitor.visit(this);
    }

    @Override
    public Provider<? extends T> getProviderInstance() {
        return this.providerInstance;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return this.injectionPoints;
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return this.providerInstance instanceof HasDependencies ? ImmutableSet.copyOf(((HasDependencies)((Object)this.providerInstance)).getDependencies()) : Dependency.forInjectionPoints(this.injectionPoints);
    }

    @Override
    public BindingImpl<T> withScoping(Scoping scoping) {
        return new ProviderInstanceBindingImpl<T>(this.getSource(), this.getKey(), scoping, (Set<InjectionPoint>)this.injectionPoints, this.providerInstance);
    }

    @Override
    public BindingImpl<T> withKey(Key<T> key) {
        return new ProviderInstanceBindingImpl<T>(this.getSource(), key, this.getScoping(), (Set<InjectionPoint>)this.injectionPoints, this.providerInstance);
    }

    @Override
    public BindingImpl<T> withRehashedKeys() {
        if (RehashableKeys.Keys.needsRehashing(this.getKey())) {
            return this.withKey(RehashableKeys.Keys.rehash(this.getKey()));
        }
        return this;
    }

    @Override
    public void applyTo(Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).toProvider(this.getProviderInstance()));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ProviderInstanceBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", (Object)this.getScoping()).add("provider", this.providerInstance).toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ProviderInstanceBindingImpl) {
            ProviderInstanceBindingImpl o = (ProviderInstanceBindingImpl)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && Objects.equal(this.providerInstance, o.providerInstance);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getKey(), this.getScoping()});
    }
}

