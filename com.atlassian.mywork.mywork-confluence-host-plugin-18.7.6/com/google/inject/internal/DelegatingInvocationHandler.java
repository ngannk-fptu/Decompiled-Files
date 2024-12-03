/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class DelegatingInvocationHandler<T>
implements InvocationHandler {
    private T delegate;

    DelegatingInvocationHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.delegate == null) {
            throw new IllegalStateException("This is a proxy used to support circular references involving constructors. The object we're proxying is not constructed yet. Please wait until after injection has completed to use this object.");
        }
        try {
            return method.invoke(this.delegate, args);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public T getDelegate() {
        return this.delegate;
    }

    void setDelegate(T delegate) {
        this.delegate = delegate;
    }
}

