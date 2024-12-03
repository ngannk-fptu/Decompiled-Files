/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Provider;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializable;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InternalFactoryToProviderAdapter<T>
implements InternalFactory<T> {
    private final Initializable<Provider<? extends T>> initializable;
    private final Object source;

    public InternalFactoryToProviderAdapter(Initializable<Provider<? extends T>> initializable, Object source) {
        this.initializable = $Preconditions.checkNotNull(initializable, "provider");
        this.source = $Preconditions.checkNotNull(source, "source");
    }

    @Override
    public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
        try {
            return errors.checkForNull(this.initializable.get(errors).get(), this.source, dependency);
        }
        catch (RuntimeException userException) {
            throw errors.withSource(this.source).errorInProvider(userException).toException();
        }
    }

    public String toString() {
        return this.initializable.toString();
    }
}

