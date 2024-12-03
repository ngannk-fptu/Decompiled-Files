/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ForwardingInvocationHandler
implements InvocationHandler {
    Object inner;

    public ForwardingInvocationHandler(Object object) {
        this.inner = object;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] objectArray) throws Throwable {
        return method.invoke(this.inner, objectArray);
    }
}

