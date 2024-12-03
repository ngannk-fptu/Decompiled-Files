/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InternalFactoryToProviderAdapter<T>
implements InternalFactory<T> {
    private final Provider<? extends T> provider;
    private final Object source;

    public InternalFactoryToProviderAdapter(Provider<? extends T> provider, Object source) {
        this.provider = (Provider)Preconditions.checkNotNull(provider, (Object)"provider");
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
    }

    @Override
    public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
        try {
            return errors.checkForNull(this.provider.get(), this.source, dependency);
        }
        catch (RuntimeException userException) {
            throw errors.withSource(this.source).errorInProvider(userException).toException();
        }
    }

    public String toString() {
        return this.provider.toString();
    }
}

