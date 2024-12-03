/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InternalContext;
import com.google.inject.spi.Dependency;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class SingleParameterInjector<T> {
    private static final Object[] NO_ARGUMENTS = new Object[0];
    private final Dependency<T> dependency;
    private final BindingImpl<? extends T> binding;

    SingleParameterInjector(Dependency<T> dependency, BindingImpl<? extends T> binding) {
        this.dependency = dependency;
        this.binding = binding;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private T inject(Errors errors, InternalContext context) throws ErrorsException {
        Dependency<?> previous = context.pushDependency(this.dependency, this.binding.getSource());
        try {
            T t = this.binding.getInternalFactory().get(errors.withSource(this.dependency), context, this.dependency, false);
            return t;
        }
        finally {
            context.popStateAndSetDependency(previous);
        }
    }

    static Object[] getAll(Errors errors, InternalContext context, SingleParameterInjector<?>[] parameterInjectors) throws ErrorsException {
        if (parameterInjectors == null) {
            return NO_ARGUMENTS;
        }
        int numErrorsBefore = errors.size();
        int size = parameterInjectors.length;
        Object[] parameters = new Object[size];
        for (int i = 0; i < size; ++i) {
            SingleParameterInjector<?> parameterInjector = parameterInjectors[i];
            try {
                parameters[i] = super.inject(errors, context);
                continue;
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
        errors.throwIfNewErrors(numErrorsBefore);
        return parameters;
    }
}

