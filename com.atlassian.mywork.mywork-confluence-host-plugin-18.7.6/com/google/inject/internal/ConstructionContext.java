/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.BytecodeGen;
import com.google.inject.internal.CircularDependencyProxy;
import com.google.inject.internal.DelegatingInvocationHandler;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ConstructionContext<T> {
    T currentReference;
    boolean constructing;
    List<DelegatingInvocationHandler<T>> invocationHandlers;

    ConstructionContext() {
    }

    public T getCurrentReference() {
        return this.currentReference;
    }

    public void removeCurrentReference() {
        this.currentReference = null;
    }

    public void setCurrentReference(T currentReference) {
        this.currentReference = currentReference;
    }

    public boolean isConstructing() {
        return this.constructing;
    }

    public void startConstruction() {
        this.constructing = true;
    }

    public void finishConstruction() {
        this.constructing = false;
        this.invocationHandlers = null;
    }

    public Object createProxy(Errors errors, Class<?> expectedType) throws ErrorsException {
        if (!expectedType.isInterface()) {
            throw errors.cannotSatisfyCircularDependency(expectedType).toException();
        }
        if (this.invocationHandlers == null) {
            this.invocationHandlers = new ArrayList<DelegatingInvocationHandler<T>>();
        }
        DelegatingInvocationHandler invocationHandler = new DelegatingInvocationHandler();
        this.invocationHandlers.add(invocationHandler);
        ClassLoader classLoader = BytecodeGen.getClassLoader(expectedType);
        return expectedType.cast(Proxy.newProxyInstance(classLoader, new Class[]{expectedType, CircularDependencyProxy.class}, invocationHandler));
    }

    public void setProxyDelegates(T delegate) {
        if (this.invocationHandlers != null) {
            for (DelegatingInvocationHandler<T> handler : this.invocationHandlers) {
                handler.setDelegate(delegate);
            }
        }
    }
}

