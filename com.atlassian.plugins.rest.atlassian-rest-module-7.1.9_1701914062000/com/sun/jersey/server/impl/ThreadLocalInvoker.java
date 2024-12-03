/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThreadLocalInvoker<T>
implements InvocationHandler {
    private ThreadLocal<T> threadLocalInstance = new ThreadLocal();
    private ThreadLocal<T> immutableThreadLocalInstance;

    public void set(T threadLocalInstance) {
        this.threadLocalInstance.set(threadLocalInstance);
    }

    public T get() {
        return this.threadLocalInstance.get();
    }

    public ThreadLocal<T> getThreadLocal() {
        return this.threadLocalInstance;
    }

    public ThreadLocal<T> getImmutableThreadLocal() {
        if (this.immutableThreadLocalInstance == null) {
            this.immutableThreadLocalInstance = new ThreadLocal<T>(){

                @Override
                public T get() {
                    return ThreadLocalInvoker.this.get();
                }

                @Override
                public void remove() {
                    throw new IllegalStateException();
                }

                @Override
                public void set(T t) {
                    throw new IllegalStateException();
                }
            };
        }
        return this.immutableThreadLocalInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.threadLocalInstance.get() == null) {
            throw new IllegalStateException("No thread local value in scope for proxy of " + proxy.getClass());
        }
        try {
            return method.invoke(this.threadLocalInstance.get(), args);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}

