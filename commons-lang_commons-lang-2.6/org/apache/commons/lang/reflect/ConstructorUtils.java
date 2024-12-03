/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.MemberUtils;

public class ConstructorUtils {
    public static Object invokeConstructor(Class cls, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return ConstructorUtils.invokeConstructor(cls, new Object[]{arg});
    }

    public static Object invokeConstructor(Class cls, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == args) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return ConstructorUtils.invokeConstructor(cls, args, parameterTypes);
    }

    public static Object invokeConstructor(Class cls, Object[] args, Class[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor ctor;
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        if (null == (ctor = ConstructorUtils.getMatchingAccessibleConstructor(cls, parameterTypes))) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
        }
        return ctor.newInstance(args);
    }

    public static Object invokeExactConstructor(Class cls, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return ConstructorUtils.invokeExactConstructor(cls, new Object[]{arg});
    }

    public static Object invokeExactConstructor(Class cls, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == args) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        int arguments = args.length;
        Class[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return ConstructorUtils.invokeExactConstructor(cls, args, parameterTypes);
    }

    public static Object invokeExactConstructor(Class cls, Object[] args, Class[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor ctor;
        if (args == null) {
            args = ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (null == (ctor = ConstructorUtils.getAccessibleConstructor(cls, parameterTypes))) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
        }
        return ctor.newInstance(args);
    }

    public static Constructor getAccessibleConstructor(Class cls, Class parameterType) {
        return ConstructorUtils.getAccessibleConstructor(cls, new Class[]{parameterType});
    }

    public static Constructor getAccessibleConstructor(Class cls, Class[] parameterTypes) {
        try {
            return ConstructorUtils.getAccessibleConstructor(cls.getConstructor(parameterTypes));
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Constructor getAccessibleConstructor(Constructor ctor) {
        return MemberUtils.isAccessible(ctor) && Modifier.isPublic(ctor.getDeclaringClass().getModifiers()) ? ctor : null;
    }

    public static Constructor getMatchingAccessibleConstructor(Class cls, Class[] parameterTypes) {
        try {
            Constructor ctor = cls.getConstructor(parameterTypes);
            MemberUtils.setAccessibleWorkaround(ctor);
            return ctor;
        }
        catch (NoSuchMethodException e) {
            Constructor result = null;
            Constructor<?>[] ctors = cls.getConstructors();
            for (int i = 0; i < ctors.length; ++i) {
                Constructor ctor;
                if (!ClassUtils.isAssignable(parameterTypes, ctors[i].getParameterTypes(), true) || (ctor = ConstructorUtils.getAccessibleConstructor(ctors[i])) == null) continue;
                MemberUtils.setAccessibleWorkaround(ctor);
                if (result != null && MemberUtils.compareParameterTypes(ctor.getParameterTypes(), result.getParameterTypes(), parameterTypes) >= 0) continue;
                result = ctor;
            }
            return result;
        }
    }
}

