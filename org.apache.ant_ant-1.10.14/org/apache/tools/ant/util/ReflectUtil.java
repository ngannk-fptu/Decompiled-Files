/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;

public class ReflectUtil {
    private ReflectUtil() {
    }

    public static <T> T newInstance(Class<T> ofClass, Class<?>[] argTypes, Object[] args) {
        try {
            Constructor<T> con = ofClass.getConstructor(argTypes);
            return con.newInstance(args);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
            return null;
        }
    }

    public static <T> T invoke(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName, new Class[0]);
            return (T)method.invoke(obj, new Object[0]);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
            return null;
        }
    }

    public static <T> T invokeStatic(Object obj, String methodName) {
        try {
            Method method = ((Class)obj).getMethod(methodName, new Class[0]);
            return (T)method.invoke(obj, new Object[0]);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
            return null;
        }
    }

    public static <T> T invoke(Object obj, String methodName, Class<?> argType, Object arg) {
        try {
            Method method = obj.getClass().getMethod(methodName, argType);
            return (T)method.invoke(obj, arg);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
            return null;
        }
    }

    public static <T> T invoke(Object obj, String methodName, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2) {
        try {
            Method method = obj.getClass().getMethod(methodName, argType1, argType2);
            return (T)method.invoke(obj, arg1, arg2);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
            return null;
        }
    }

    public static <T> T getField(Object obj, String fieldName) throws BuildException {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T)field.get(obj);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
            return null;
        }
    }

    public static void throwBuildException(Exception t) throws BuildException {
        throw ReflectUtil.toBuildException(t);
    }

    public static BuildException toBuildException(Exception t) {
        if (t instanceof InvocationTargetException) {
            Throwable t2 = ((InvocationTargetException)t).getTargetException();
            if (t2 instanceof BuildException) {
                return (BuildException)t2;
            }
            return new BuildException(t2);
        }
        return new BuildException(t);
    }

    public static boolean respondsTo(Object o, String methodName) throws BuildException {
        try {
            return Stream.of(o.getClass().getMethods()).map(Method::getName).anyMatch(Predicate.isEqual(methodName));
        }
        catch (Exception t) {
            throw ReflectUtil.toBuildException(t);
        }
    }
}

