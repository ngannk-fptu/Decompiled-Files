/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import com.atlassian.core.util.ClassLoaderUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassHelper {
    public static Object instantiateClass(Class clazz, Object[] constructorArgs) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class[] args = new Class[constructorArgs.length];
        for (int i = 0; i < constructorArgs.length; ++i) {
            args[i] = constructorArgs == null ? null : constructorArgs[i].getClass();
        }
        Constructor<Object> ctor = null;
        ctor = clazz.getConstructors().length == 1 ? clazz.getConstructors()[0] : clazz.getConstructor(args);
        return ctor.newInstance(constructorArgs);
    }

    public static Object instantiateClass(String name, Object[] constructorArgs) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class clazz = ClassLoaderUtils.loadClass(name, ClassHelper.class);
        return ClassHelper.instantiateClass(clazz, constructorArgs);
    }
}

