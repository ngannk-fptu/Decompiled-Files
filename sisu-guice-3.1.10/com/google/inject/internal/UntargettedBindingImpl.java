/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.RehashableKeys;
import com.google.inject.internal.Scoping;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.UntargettedBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class UntargettedBindingImpl<T>
extends BindingImpl<T>
implements UntargettedBinding<T> {
    UntargettedBindingImpl(InjectorImpl injector, Key<T> key, Object source) {
        super(injector, key, source, new InternalFactory<T>(){

            @Override
            public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) {
                throw new AssertionError();
            }
        }, Scoping.UNSCOPED);
    }

    public UntargettedBindingImpl(Object source, Key<T> key, Scoping scoping) {
        super(source, key, scoping);
    }

    @Override
    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit(this);
    }

    @Override
    public BindingImpl<T> withScoping(Scoping scoping) {
        return new UntargettedBindingImpl(this.getSource(), this.getKey(), scoping);
    }

    @Override
    public BindingImpl<T> withKey(Key<T> key) {
        return new UntargettedBindingImpl<T>(this.getSource(), key, this.getScoping());
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
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(UntargettedBinding.class).add("key", this.getKey()).add("source", this.getSource()).toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof UntargettedBindingImpl) {
            UntargettedBindingImpl o = (UntargettedBindingImpl)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getKey(), this.getScoping()});
    }
}

