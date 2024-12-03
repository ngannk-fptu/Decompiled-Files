/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.ConstructionContext;
import com.google.inject.internal.ConstructionProxy;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.MembersInjectorImpl;
import com.google.inject.internal.SingleParameterInjector;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ConstructorInjector<T> {
    private final $ImmutableSet<InjectionPoint> injectableMembers;
    private final SingleParameterInjector<?>[] parameterInjectors;
    private final ConstructionProxy<T> constructionProxy;
    private final MembersInjectorImpl<T> membersInjector;

    ConstructorInjector(Set<InjectionPoint> injectableMembers, ConstructionProxy<T> constructionProxy, SingleParameterInjector<?>[] parameterInjectors, MembersInjectorImpl<T> membersInjector) {
        this.injectableMembers = $ImmutableSet.copyOf(injectableMembers);
        this.constructionProxy = constructionProxy;
        this.parameterInjectors = parameterInjectors;
        this.membersInjector = membersInjector;
    }

    public $ImmutableSet<InjectionPoint> getInjectableMembers() {
        return this.injectableMembers;
    }

    ConstructionProxy<T> getConstructionProxy() {
        return this.constructionProxy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object construct(Errors errors, InternalContext context, Class<?> expectedType, boolean allowProxy) throws ErrorsException {
        ConstructionContext constructionContext = context.getConstructionContext(this);
        if (constructionContext.isConstructing()) {
            if (!allowProxy) {
                throw errors.circularProxiesDisabled(expectedType).toException();
            }
            return constructionContext.createProxy(errors, expectedType);
        }
        Object t = constructionContext.getCurrentReference();
        if (t != null) {
            return t;
        }
        try {
            Object parameters;
            constructionContext.startConstruction();
            try {
                parameters = SingleParameterInjector.getAll(errors, context, this.parameterInjectors);
                t = this.constructionProxy.newInstance(parameters);
                constructionContext.setProxyDelegates(t);
            }
            finally {
                constructionContext.finishConstruction();
            }
            constructionContext.setCurrentReference(t);
            this.membersInjector.injectMembers(t, errors, context, false);
            this.membersInjector.notifyListeners(t, errors);
            parameters = t;
            return parameters;
        }
        catch (InvocationTargetException userException) {
            Throwable cause = userException.getCause() != null ? userException.getCause() : userException;
            throw errors.withSource(this.constructionProxy.getInjectionPoint()).errorInjectingConstructor(cause).toException();
        }
        finally {
            constructionContext.removeCurrentReference();
        }
    }
}

