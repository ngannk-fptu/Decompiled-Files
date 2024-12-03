/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.inject.Provider
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.ConstructionContext;
import com.google.inject.internal.DelayedInitialize;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.ProviderInternalFactory;
import com.google.inject.internal.ProvisionListenerStackCallback;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ProvidedByInternalFactory<T>
extends ProviderInternalFactory<T>
implements DelayedInitialize {
    private final Class<?> rawType;
    private final Class<? extends Provider<?>> providerType;
    private final Key<? extends Provider<T>> providerKey;
    private BindingImpl<? extends Provider<T>> providerBinding;
    private ProvisionListenerStackCallback<T> provisionCallback;

    ProvidedByInternalFactory(Class<?> rawType, Class<? extends Provider<?>> providerType, Key<? extends Provider<T>> providerKey, boolean allowProxy) {
        super(providerKey, allowProxy);
        this.rawType = rawType;
        this.providerType = providerType;
        this.providerKey = providerKey;
    }

    void setProvisionListenerCallback(ProvisionListenerStackCallback<T> listener) {
        this.provisionCallback = listener;
    }

    @Override
    public void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
        this.providerBinding = injector.getBindingOrThrow(this.providerKey, errors, InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T get(Errors errors, InternalContext context, Dependency dependency, boolean linked) throws ErrorsException {
        Preconditions.checkState((this.providerBinding != null ? 1 : 0) != 0, (Object)"not initialized");
        context.pushState(this.providerKey, this.providerBinding.getSource());
        try {
            errors = errors.withSource(this.providerKey);
            Provider<T> provider = this.providerBinding.getInternalFactory().get(errors, context, dependency, true);
            T t = this.circularGet(provider, errors, context, dependency, linked, this.provisionCallback);
            return t;
        }
        finally {
            context.popState();
        }
    }

    @Override
    protected T provision(javax.inject.Provider<? extends T> provider, Errors errors, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws ErrorsException {
        try {
            T o = super.provision(provider, errors, dependency, constructionContext);
            if (o != null && !this.rawType.isInstance(o)) {
                throw errors.subtypeNotProvided(this.providerType, this.rawType).toException();
            }
            T t = o;
            return t;
        }
        catch (RuntimeException e) {
            throw errors.errorInProvider(e).toException();
        }
    }
}

