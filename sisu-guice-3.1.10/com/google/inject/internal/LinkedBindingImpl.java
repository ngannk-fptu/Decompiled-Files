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
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.RehashableKeys;
import com.google.inject.internal.Scoping;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.LinkedKeyBinding;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class LinkedBindingImpl<T>
extends BindingImpl<T>
implements LinkedKeyBinding<T>,
HasDependencies {
    final Key<? extends T> targetKey;

    public LinkedBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping, Key<? extends T> targetKey) {
        super(injector, key, source, internalFactory, scoping);
        this.targetKey = targetKey;
    }

    public LinkedBindingImpl(Object source, Key<T> key, Scoping scoping, Key<? extends T> targetKey) {
        super(source, key, scoping);
        this.targetKey = targetKey;
    }

    @Override
    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Key<? extends T> getLinkedKey() {
        return this.targetKey;
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return ImmutableSet.of(Dependency.get(this.targetKey));
    }

    @Override
    public BindingImpl<T> withScoping(Scoping scoping) {
        return new LinkedBindingImpl<T>(this.getSource(), this.getKey(), scoping, this.targetKey);
    }

    @Override
    public BindingImpl<T> withKey(Key<T> key) {
        return new LinkedBindingImpl<T>(this.getSource(), key, this.getScoping(), this.targetKey);
    }

    @Override
    public BindingImpl<T> withRehashedKeys() {
        boolean keyNeedsRehashing = RehashableKeys.Keys.needsRehashing(this.getKey());
        boolean targetKeyNeedsRehashing = RehashableKeys.Keys.needsRehashing(this.targetKey);
        if (keyNeedsRehashing || targetKeyNeedsRehashing) {
            return new LinkedBindingImpl<T>(this.getSource(), keyNeedsRehashing ? RehashableKeys.Keys.rehash(this.getKey()) : this.getKey(), this.getScoping(), targetKeyNeedsRehashing ? RehashableKeys.Keys.rehash(this.targetKey) : this.targetKey);
        }
        return this;
    }

    @Override
    public void applyTo(Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).to(this.getLinkedKey()));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(LinkedKeyBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", (Object)this.getScoping()).add("target", this.targetKey).toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof LinkedBindingImpl) {
            LinkedBindingImpl o = (LinkedBindingImpl)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && Objects.equal(this.targetKey, o.targetKey);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getKey(), this.getScoping(), this.targetKey});
    }
}

