/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.inject.Provider
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.google.inject.internal.ConstructionContext;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializable;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.ProviderInternalFactory;
import com.google.inject.internal.ProvisionListenerStackCallback;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InternalFactoryToInitializableAdapter<T>
extends ProviderInternalFactory<T> {
    private final ProvisionListenerStackCallback<T> provisionCallback;
    private final Initializable<Provider<? extends T>> initializable;

    public InternalFactoryToInitializableAdapter(Initializable<Provider<? extends T>> initializable, Object source, boolean allowProxy, ProvisionListenerStackCallback<T> provisionCallback) {
        super(source, allowProxy);
        this.provisionCallback = (ProvisionListenerStackCallback)Preconditions.checkNotNull(provisionCallback, (Object)"provisionCallback");
        this.initializable = (Initializable)Preconditions.checkNotNull(initializable, (Object)"provider");
    }

    @Override
    public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
        return this.circularGet((javax.inject.Provider)this.initializable.get(errors), errors, context, dependency, linked, this.provisionCallback);
    }

    @Override
    protected T provision(javax.inject.Provider<? extends T> provider, Errors errors, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws ErrorsException {
        try {
            return super.provision(provider, errors, dependency, constructionContext);
        }
        catch (RuntimeException userException) {
            throw errors.withSource(this.source).errorInProvider(userException).toException();
        }
    }

    public String toString() {
        return this.initializable.toString();
    }
}

