/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.proxy.$Callback;
import com.google.inject.internal.cglib.proxy.$MethodProxy;
import java.lang.reflect.Method;

public interface $MethodInterceptor
extends $Callback {
    public Object intercept(Object var1, Method var2, Object[] var3, $MethodProxy var4) throws Throwable;
}

