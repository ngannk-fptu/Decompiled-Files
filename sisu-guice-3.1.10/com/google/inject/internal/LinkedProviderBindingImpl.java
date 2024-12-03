/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Provider
 */
package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.DelayedInitialize;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.RehashableKeys;
import com.google.inject.internal.Scoping;
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
HasDependencies,
DelayedInitialize {
    final Key<? extends Provider<? extends T>> providerKey;
    final DelayedInitialize delayedInitializer;

    private LinkedProviderBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping, Key<? extends Provider<? extends T>> providerKey, DelayedInitialize delayedInitializer) {
        super(injector, key, source, internalFactory, scoping);
        this.providerKey = providerKey;
        this.delayedInitializer = delayedInitializer;
    }

    public LinkedProviderBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping, Key<? extends Provider<? extends T>> providerKey) {
        this(injector, key, source, internalFactory, scoping, providerKey, null);
    }

    LinkedProviderBindingImpl(Object source, Key<T> key, Scoping scoping, Key<? extends Provider<? extends T>> providerKey) {
        super(source, key, scoping);
        this.providerKey = providerKey;
        this.delayedInitializer = null;
    }

    static <T> LinkedProviderBindingImpl<T> createWithInitializer(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping, Key<? extends Provider<? extends T>> providerKey, DelayedInitialize delayedInitializer) {
        return new LinkedProviderBindingImpl<T>(injector, key, source, internalFactory, scoping, providerKey, delayedInitializer);
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
    public void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
        if (this.delayedInitializer != null) {
            this.delayedInitializer.initialize(injector, errors);
        }
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return ImmutableSet.of(Dependency.get(this.providerKey));
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
    public BindingImpl<T> withRehashedKeys() {
        boolean keyNeedsRehashing = RehashableKeys.Keys.needsRehashing(this.getKey());
        boolean providerKeyNeedsRehashing = RehashableKeys.Keys.needsRehashing(this.providerKey);
        if (keyNeedsRehashing || providerKeyNeedsRehashing) {
            return new LinkedProviderBindingImpl(this.getSource(), keyNeedsRehashing ? RehashableKeys.Keys.rehash(this.getKey()) : this.getKey(), this.getScoping(), providerKeyNeedsRehashing ? RehashableKeys.Keys.rehash(this.providerKey) : this.providerKey);
        }
        return this;
    }

    @Override
    public void applyTo(Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).toProvider(this.getProviderKey()));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ProviderKeyBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", (Object)this.getScoping()).add("provider", this.providerKey).toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LinkedProviderBindingImpl) {
            LinkedProviderBindingImpl o = (LinkedProviderBindingImpl)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && Objects.equal(this.providerKey, o.providerKey);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getKey(), this.getScoping(), this.providerKey});
    }
}

