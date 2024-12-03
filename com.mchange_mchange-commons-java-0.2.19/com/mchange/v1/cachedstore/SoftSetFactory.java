/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.ManualCleanupSoftSet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

public final class SoftSetFactory {
    public static Set createSynchronousCleanupSoftSet() {
        final ManualCleanupSoftSet manualCleanupSoftSet = new ManualCleanupSoftSet();
        InvocationHandler invocationHandler = new InvocationHandler(){

            @Override
            public Object invoke(Object object, Method method, Object[] objectArray) throws Throwable {
                manualCleanupSoftSet.vacuum();
                return method.invoke((Object)manualCleanupSoftSet, objectArray);
            }
        };
        return (Set)Proxy.newProxyInstance(SoftSetFactory.class.getClassLoader(), new Class[]{Set.class}, invocationHandler);
    }

    private SoftSetFactory() {
    }
}

