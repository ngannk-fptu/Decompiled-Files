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
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.Scoping;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.PrivateElements;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ExposedBindingImpl<T>
extends BindingImpl<T>
implements ExposedBinding<T> {
    private final PrivateElements privateElements;

    public ExposedBindingImpl(InjectorImpl injector, Object source, Key<T> key, InternalFactory<T> factory, PrivateElements privateElements) {
        super(injector, key, source, factory, Scoping.UNSCOPED);
        this.privateElements = privateElements;
    }

    @Override
    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Set<Dependency<?>> getDependencies() {
        return ImmutableSet.of(Dependency.get(Key.get(Injector.class)));
    }

    @Override
    public PrivateElements getPrivateElements() {
        return this.privateElements;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ExposedBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("privateElements", (Object)this.privateElements).toString();
    }

    @Override
    public void applyTo(Binder binder) {
        throw new UnsupportedOperationException("This element represents a synthetic binding.");
    }
}

