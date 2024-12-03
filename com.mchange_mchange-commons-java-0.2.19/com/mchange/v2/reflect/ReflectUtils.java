/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class ReflectUtils {
    public static final Class[] PROXY_CTOR_ARGS = new Class[]{InvocationHandler.class};

    public static Constructor findProxyConstructor(ClassLoader classLoader, Class clazz) throws NoSuchMethodException {
        return ReflectUtils.findProxyConstructor(classLoader, new Class[]{clazz});
    }

    public static Constructor findProxyConstructor(ClassLoader classLoader, Class[] classArray) throws NoSuchMethodException {
        Class<?> clazz = Proxy.getProxyClass(classLoader, classArray);
        return clazz.getConstructor(PROXY_CTOR_ARGS);
    }

    public static boolean isPublic(Member member) {
        return (member.getModifiers() & 1) != 0;
    }

    public static boolean isPublic(Class clazz) {
        return (clazz.getModifiers() & 1) != 0;
    }

    public static Class findPublicParent(Class clazz) {
        while ((clazz = clazz.getSuperclass()) != null && !ReflectUtils.isPublic(clazz)) {
        }
        return clazz;
    }

    public static Iterator traverseInterfaces(Class clazz) {
        HashSet<Class> hashSet = new HashSet<Class>();
        if (clazz.isInterface()) {
            hashSet.add(clazz);
        }
        ReflectUtils.addParentInterfaces(hashSet, clazz);
        return hashSet.iterator();
    }

    private static void addParentInterfaces(Set set, Class clazz) {
        Class<?>[] classArray = clazz.getInterfaces();
        int n = classArray.length;
        for (int i = 0; i < n; ++i) {
            set.add(classArray[i]);
            ReflectUtils.addParentInterfaces(set, classArray[i]);
        }
    }

    public static Method findInPublicScope(Method method) {
        if (!ReflectUtils.isPublic(method)) {
            return null;
        }
        Class clazz = method.getDeclaringClass();
        if (ReflectUtils.isPublic(clazz)) {
            return method;
        }
        Class clazz2 = clazz;
        while ((clazz2 = ReflectUtils.findPublicParent(clazz2)) != null) {
            try {
                return clazz2.getMethod(method.getName(), method.getParameterTypes());
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
        }
        Iterator iterator = ReflectUtils.traverseInterfaces(clazz);
        while (iterator.hasNext()) {
            clazz2 = (Class)iterator.next();
            if (!ReflectUtils.isPublic(clazz2)) continue;
            try {
                return clazz2.getMethod(method.getName(), method.getParameterTypes());
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
        }
        return null;
    }

    private ReflectUtils() {
    }
}

