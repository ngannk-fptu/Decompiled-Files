/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.inject.Provider
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.internal.ConstructionContext;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.ProvisionListenerStackCallback;
import com.google.inject.spi.Dependency;
import javax.inject.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class ProviderInternalFactory<T>
implements InternalFactory<T> {
    private final boolean allowProxy;
    protected final Object source;

    ProviderInternalFactory(Object source, boolean allowProxy) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
        this.allowProxy = allowProxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected T circularGet(final Provider<? extends T> provider, final Errors errors, InternalContext context, final Dependency<?> dependency, boolean linked, ProvisionListenerStackCallback<T> provisionCallback) throws ErrorsException {
        Class<?> expectedType = dependency.getKey().getTypeLiteral().getRawType();
        final ConstructionContext constructionContext = context.getConstructionContext(this);
        if (constructionContext.isConstructing()) {
            if (!this.allowProxy) {
                throw errors.circularProxiesDisabled(expectedType).toException();
            }
            Object proxyType = constructionContext.createProxy(errors, expectedType);
            return (T)proxyType;
        }
        constructionContext.startConstruction();
        try {
            if (!provisionCallback.hasListeners()) {
                T t = this.provision(provider, errors, dependency, constructionContext);
                return t;
            }
            T t = provisionCallback.provision(errors, context, new ProvisionListenerStackCallback.ProvisionCallback<T>(){

                @Override
                public T call() throws ErrorsException {
                    return ProviderInternalFactory.this.provision(provider, errors, dependency, constructionContext);
                }
            });
            return t;
        }
        finally {
            constructionContext.removeCurrentReference();
            constructionContext.finishConstruction();
        }
    }

    protected T provision(Provider<? extends T> provider, Errors errors, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws ErrorsException {
        Object t = errors.checkForNull(provider.get(), this.source, dependency);
        constructionContext.setProxyDelegates(t);
        return (T)t;
    }
}

