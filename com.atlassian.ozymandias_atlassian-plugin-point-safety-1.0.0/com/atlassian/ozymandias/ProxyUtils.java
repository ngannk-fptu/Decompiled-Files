/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.ozymandias;

import com.atlassian.ozymandias.ExceptionSafeInvocationHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ProxyUtils {
    @Nonnull
    public static <T> T safeProxyWithReturnValue(@Nonnull Class<T> classToProxy, @Nonnull T objectToProxy, final @Nullable Object returnValue) {
        return ProxyUtils.safeProxyWithReturnValueSupplier(classToProxy, objectToProxy, new ExceptionSafeInvocationHandler.ReturnValueSupplier(){

            @Override
            @Nullable
            public Object get(@Nonnull Method method, Object ... args) {
                return returnValue;
            }
        });
    }

    @Nonnull
    public static <T> T safeProxyWithReturnValueSupplier(@Nonnull Class<T> classToProxy, @Nonnull T objectToProxy, @Nullable ExceptionSafeInvocationHandler.ReturnValueSupplier returnValueSupplier) {
        return (T)Proxy.newProxyInstance(classToProxy.getClassLoader(), new Class[]{classToProxy}, (InvocationHandler)new ExceptionSafeInvocationHandler(objectToProxy, returnValueSupplier));
    }

    private ProxyUtils() {
    }
}

