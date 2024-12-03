/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MemberUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class ConstructorUtils {
    public static <T> T invokeConstructor(Class<T> cls, Object ... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        return ConstructorUtils.invokeConstructor(cls, args, ClassUtils.toClass(args));
    }

    public static <T> T invokeConstructor(Class<T> cls, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        Constructor<T> ctor = ConstructorUtils.getMatchingAccessibleConstructor(cls, parameterTypes = ArrayUtils.nullToEmpty(parameterTypes));
        if (ctor == null) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
        }
        if (ctor.isVarArgs()) {
            Class<?>[] methodParameterTypes = ctor.getParameterTypes();
            args = MethodUtils.getVarArgs(args, methodParameterTypes);
        }
        return ctor.newInstance(args);
    }

    public static <T> T invokeExactConstructor(Class<T> cls, Object ... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        return ConstructorUtils.invokeExactConstructor(cls, args, ClassUtils.toClass(args));
    }

    public static <T> T invokeExactConstructor(Class<T> cls, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        args = ArrayUtils.nullToEmpty(args);
        Constructor<T> ctor = ConstructorUtils.getAccessibleConstructor(cls, parameterTypes = ArrayUtils.nullToEmpty(parameterTypes));
        if (ctor == null) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
        }
        return ctor.newInstance(args);
    }

    public static <T> Constructor<T> getAccessibleConstructor(Class<T> cls, Class<?> ... parameterTypes) {
        Objects.requireNonNull(cls, "cls");
        try {
            return ConstructorUtils.getAccessibleConstructor(cls.getConstructor(parameterTypes));
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> Constructor<T> getAccessibleConstructor(Constructor<T> ctor) {
        Objects.requireNonNull(ctor, "ctor");
        return MemberUtils.isAccessible(ctor) && ConstructorUtils.isAccessible(ctor.getDeclaringClass()) ? ctor : null;
    }

    public static <T> Constructor<T> getMatchingAccessibleConstructor(Class<T> cls, Class<?> ... parameterTypes) {
        Objects.requireNonNull(cls, "cls");
        try {
            return MemberUtils.setAccessibleWorkaround(cls.getConstructor(parameterTypes));
        }
        catch (NoSuchMethodException noSuchMethodException) {
            Constructor<?>[] ctors;
            Constructor<?> result = null;
            for (Constructor<?> ctor : ctors = cls.getConstructors()) {
                Constructor<?> constructor;
                if (!MemberUtils.isMatchingConstructor(ctor, parameterTypes) || (ctor = ConstructorUtils.getAccessibleConstructor(ctor)) == null) continue;
                MemberUtils.setAccessibleWorkaround(ctor);
                if (result != null && MemberUtils.compareConstructorFit(ctor, result, parameterTypes) >= 0) continue;
                result = constructor = ctor;
            }
            return result;
        }
    }

    private static boolean isAccessible(Class<?> type) {
        for (Class<?> cls = type; cls != null; cls = cls.getEnclosingClass()) {
            if (ClassUtils.isPublic(cls)) continue;
            return false;
        }
        return true;
    }
}

