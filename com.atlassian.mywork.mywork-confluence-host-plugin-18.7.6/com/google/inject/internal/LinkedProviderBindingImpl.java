/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.ProviderKeyBinding;
import java.util.Set;
import javax.inject.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class LinkedProviderBindingImpl<T>
extends BindingImpl<T>
implements ProviderKeyBinding<T>,
HasDependencies {
    final Key<? extends Provider<? extends T>> providerKey;

    public LinkedProviderBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping, Key<? extends Provider<? extends T>> providerKey) {
        super(injector, key, source, internalFactory, scoping);
        this.providerKey = providerKey;
    }

    LinkedProviderBindingImpl(Object source, Key<T> key, Scoping scoping, Key<? extends Provider<? extends T>> providerKey) {
        super(source, key, scoping);
        this.providerKey = providerKey;
    }

    @Override
    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Key<? extends Provider<? extends T>> getProviderKey() {
        return this.providerKey;
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return $ImmutableSet.of(Dependency.get(this.providerKey));
    }

    @Override
    public BindingImpl<T> withScoping(Scoping scoping) {
        return new LinkedProviderBindingImpl(this.getSource(), this.getKey(), scoping, this.providerKey);
    }

    @Override
    public BindingImpl<T> withKey(Key<T> key) {
        return new LinkedProviderBindingImpl<T>(this.getSource(), key, this.getScoping(), this.providerKey);
    }

    @Override
    public void applyTo(Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).toProvider(this.getProviderKey()));
    }

    @Override
    public String toString() {
        return new $ToStringBuilder(ProviderKeyBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", this.getScoping()).add("provider", this.providerKey).toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LinkedProviderBindingImpl) {
            LinkedProviderBindingImpl o = (LinkedProviderBindingImpl)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.providerKey, o.providerKey);
        }
        return false;
    }

    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping(), this.providerKey);
    }
}

