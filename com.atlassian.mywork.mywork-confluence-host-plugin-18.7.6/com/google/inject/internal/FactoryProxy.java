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
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class FactoryProxy<T>
implements InternalFactory<T>,
CreationListener {
    private final InjectorImpl injector;
    private final Key<T> key;
    private final Key<? extends T> targetKey;
    private final Object source;
    private InternalFactory<? extends T> targetFactory;

    FactoryProxy(InjectorImpl injector, Key<T> key, Key<? extends T> targetKey, Object source) {
        this.injector = injector;
        this.key = key;
        this.targetKey = targetKey;
        this.source = source;
    }

    @Override
    public void notify(Errors errors) {
        try {
            this.targetFactory = this.injector.getInternalFactory(this.targetKey, errors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
        }
    }

    @Override
    public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
        return this.targetFactory.get(errors.withSource(this.targetKey), context, dependency, true);
    }

    public String toString() {
        return new $ToStringBuilder(FactoryProxy.class).add("key", this.key).add("provider", this.targetFactory).toString();
    }
}

