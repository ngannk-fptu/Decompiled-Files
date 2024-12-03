/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.internal.CreationListener;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.spi.Dependency;
import javax.inject.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class BoundProviderFactory<T>
implements InternalFactory<T>,
CreationListener {
    private final InjectorImpl injector;
    final Key<? extends Provider<? extends T>> providerKey;
    final Object source;
    private InternalFactory<? extends Provider<? extends T>> providerFactory;

    BoundProviderFactory(InjectorImpl injector, Key<? extends Provider<? extends T>> providerKey, Object source) {
        this.injector = injector;
        this.providerKey = providerKey;
        this.source = source;
    }

    @Override
    public void notify(Errors errors) {
        try {
            this.providerFactory = this.injector.getInternalFactory(this.providerKey, errors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
        }
    }

    @Override
    public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
        errors = errors.withSource(this.providerKey);
        Provider<T> provider = this.providerFactory.get(errors, context, dependency, true);
        try {
            return errors.checkForNull(provider.get(), this.source, dependency);
        }
        catch (RuntimeException userException) {
            throw errors.errorInProvider(userException).toException();
        }
    }

    public String toString() {
        return this.providerKey.toString();
    }
}

