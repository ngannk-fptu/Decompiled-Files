/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.reflect;

import com.google.common.base.Preconditions;
import com.google.common.reflect.ElementTypesAreNonnullByDefault;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@ElementTypesAreNonnullByDefault
public final class Reflection {
    public static String getPackageName(Class<?> clazz) {
        return Reflection.getPackageName(clazz.getName());
    }

    public static String getPackageName(String classFullName) {
        int lastDot = classFullName.lastIndexOf(46);
        return lastDot < 0 ? "" : classFullName.substring(0, lastDot);
    }

    public static void initialize(Class<?> ... classes) {
        for (Class<?> clazz : classes) {
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            }
            catch (ClassNotFoundException e) {
                throw new AssertionError((Object)e);
            }
        }
    }

    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        Preconditions.checkNotNull(handler);
        Preconditions.checkArgument(interfaceType.isInterface(), "%s is not an interface", interfaceType);
        Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
        return interfaceType.cast(object);
    }

    private Reflection() {
    }
}

