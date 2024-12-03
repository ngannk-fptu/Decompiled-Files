/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodInvocation<T> {
    private final T delegate;
    private final Object proxy;
    private final Method method;
    private final Object[] args;

    public MethodInvocation(T target, Object proxy, Method method, Object ... args) {
        this.delegate = target;
        this.proxy = proxy;
        this.method = method;
        this.args = args;
    }

    public int getArgCount() {
        return this.args == null ? 0 : this.args.length;
    }

    public Object getArgAt(int argIndex) {
        return this.args[argIndex];
    }

    public <R> R getArgAt(int argIndex, Class<R> argType) {
        return argType.cast(this.getArgAt(argIndex));
    }

    public String getMethodName() {
        return this.method.getName();
    }

    public Object proceed() throws Throwable {
        try {
            return this.method.invoke(this.delegate, this.args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}

