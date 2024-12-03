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
import com.google.inject.internal.ConstructionContext;
import com.google.inject.internal.CreationListener;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.ProviderInternalFactory;
import com.google.inject.internal.ProvisionListenerStackCallback;
import com.google.inject.spi.Dependency;
import javax.inject.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class BoundProviderFactory<T>
extends ProviderInternalFactory<T>
implements CreationListener {
    private final ProvisionListenerStackCallback<T> provisionCallback;
    private final InjectorImpl injector;
    final Key<? extends Provider<? extends T>> providerKey;
    private InternalFactory<? extends Provider<? extends T>> providerFactory;

    BoundProviderFactory(InjectorImpl injector, Key<? extends Provider<? extends T>> providerKey, Object source, boolean allowProxy, ProvisionListenerStackCallback<T> provisionCallback) {
        super(source, allowProxy);
        this.provisionCallback = (ProvisionListenerStackCallback)Preconditions.checkNotNull(provisionCallback, (Object)"provisionCallback");
        this.injector = injector;
        this.providerKey = providerKey;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
        context.pushState(this.providerKey, this.source);
        try {
            errors = errors.withSource(this.providerKey);
            Provider<? extends T> provider = this.providerFactory.get(errors, context, dependency, true);
            T t = this.circularGet(provider, errors, context, dependency, linked, this.provisionCallback);
            return t;
        }
        finally {
            context.popState();
        }
    }

    @Override
    protected T provision(Provider<? extends T> provider, Errors errors, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws ErrorsException {
        try {
            return super.provision(provider, errors, dependency, constructionContext);
        }
        catch (RuntimeException userException) {
            throw errors.errorInProvider(userException).toException();
        }
    }

    public String toString() {
        return this.providerKey.toString();
    }
}

