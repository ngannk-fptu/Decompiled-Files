/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.MethodProxy;

public interface MethodInterceptor
extends Callback {
    public Object intercept(Object var1, Method var2, Object[] var3, MethodProxy var4) throws Throwable;
}

