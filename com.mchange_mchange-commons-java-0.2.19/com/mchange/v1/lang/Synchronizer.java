/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;

public final class Synchronizer {
    public static Object createSynchronizedWrapper(final Object object) {
        InvocationHandler invocationHandler = new InvocationHandler(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Object invoke(Object object3, Method method, Object[] objectArray) throws Throwable {
                Object object2 = object3;
                synchronized (object2) {
                    return method.invoke(object, objectArray);
                }
            }
        };
        Class<?> clazz = object.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), Synchronizer.recurseFindInterfaces(clazz), invocationHandler);
    }

    private static Class[] recurseFindInterfaces(Class clazz) {
        Class[] classArray;
        HashSet<Class> hashSet = new HashSet<Class>();
        while (clazz != null) {
            classArray = clazz.getInterfaces();
            int n = classArray.length;
            for (int i = 0; i < n; ++i) {
                hashSet.add(classArray[i]);
            }
            clazz = clazz.getSuperclass();
        }
        classArray = new Class[hashSet.size()];
        hashSet.toArray(classArray);
        return classArray;
    }

    private Synchronizer() {
    }
}

