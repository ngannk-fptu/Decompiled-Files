/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.Callback;

public interface InvocationHandler
extends Callback {
    public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable;
}

