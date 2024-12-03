/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InstanceBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BindingImpl<T>
implements Binding<T> {
    private final InjectorImpl injector;
    private final Key<T> key;
    private final Object source;
    private final Scoping scoping;
    private final InternalFactory<? extends T> internalFactory;
    private volatile Provider<T> provider;

    public BindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping) {
        this.injector = injector;
        this.key = key;
        this.source = source;
        this.internalFactory = internalFactory;
        this.scoping = scoping;
    }

    protected BindingImpl(Object source, Key<T> key, Scoping scoping) {
        this.internalFactory = null;
        this.injector = null;
        this.source = source;
        this.key = key;
        this.scoping = scoping;
    }

    @Override
    public Key<T> getKey() {
        return this.key;
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public Provider<T> getProvider() {
        if (this.provider == null) {
            if (this.injector == null) {
                throw new UnsupportedOperationException("getProvider() not supported for module bindings");
            }
            this.provider = this.injector.getProvider(this.key);
        }
        return this.provider;
    }

    public InternalFactory<? extends T> getInternalFactory() {
        return this.internalFactory;
    }

    public Scoping getScoping() {
        return this.scoping;
    }

    public boolean isConstant() {
        return this instanceof InstanceBinding;
    }

    public <V> V acceptVisitor(ElementVisitor<V> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <V> V acceptScopingVisitor(BindingScopingVisitor<V> visitor) {
        return this.scoping.acceptVisitor(visitor);
    }

    protected BindingImpl<T> withScoping(Scoping scoping) {
        throw new AssertionError();
    }

    protected BindingImpl<T> withKey(Key<T> key) {
        throw new AssertionError();
    }

    public String toString() {
        return new $ToStringBuilder(Binding.class).add("key", this.key).add("scope", this.scoping).add("source", this.source).toString();
    }

    public InjectorImpl getInjector() {
        return this.injector;
    }
}

