/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.internal.ContextualCallable;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ProviderToInternalFactoryAdapter<T>
implements Provider<T> {
    private final InjectorImpl injector;
    private final InternalFactory<? extends T> internalFactory;

    public ProviderToInternalFactoryAdapter(InjectorImpl injector, InternalFactory<? extends T> internalFactory) {
        this.injector = injector;
        this.internalFactory = internalFactory;
    }

    @Override
    public T get() {
        final Errors errors = new Errors();
        try {
            Object t = this.injector.callInContext(new ContextualCallable<T>(){

                @Override
                public T call(InternalContext context) throws ErrorsException {
                    Dependency dependency = context.getDependency();
                    return ProviderToInternalFactoryAdapter.this.internalFactory.get(errors, context, dependency, true);
                }
            });
            errors.throwIfNewErrors(0);
            return t;
        }
        catch (ErrorsException e) {
            throw new ProvisionException(errors.merge(e.getErrors()).getMessages());
        }
    }

    public String toString() {
        return this.internalFactory.toString();
    }
}

