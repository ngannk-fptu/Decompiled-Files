/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.util;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.InvocationHandler;

class UnsupportedOperationInvocationHandler
implements java.lang.reflect.InvocationHandler,
InvocationHandler {
    public static UnsupportedOperationInvocationHandler INSTANCE = new UnsupportedOperationInvocationHandler();

    UnsupportedOperationInvocationHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new UnsupportedOperationException();
    }
}

