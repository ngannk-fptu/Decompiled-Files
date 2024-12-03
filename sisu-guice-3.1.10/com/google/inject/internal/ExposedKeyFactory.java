/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.CreationListener;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.PrivateElements;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ExposedKeyFactory<T>
implements InternalFactory<T>,
CreationListener {
    private final Key<T> key;
    private final PrivateElements privateElements;
    private BindingImpl<T> delegate;

    ExposedKeyFactory(Key<T> key, PrivateElements privateElements) {
        this.key = key;
        this.privateElements = privateElements;
    }

    @Override
    public void notify(Errors errors) {
        InjectorImpl privateInjector = (InjectorImpl)this.privateElements.getInjector();
        BindingImpl<T> explicitBinding = privateInjector.state.getExplicitBinding(this.key);
        if (explicitBinding.getInternalFactory() == this) {
            errors.withSource(explicitBinding.getSource()).exposedButNotBound(this.key);
            return;
        }
        this.delegate = explicitBinding;
    }

    @Override
    public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
        return this.delegate.getInternalFactory().get(errors, context, dependency, linked);
    }
}

