/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.aspectj.lang.NoAspectBoundException;

public class Aspects {
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Class[] PEROBJECT_CLASS_ARRAY = new Class[]{Object.class};
    private static final Class[] PERTYPEWITHIN_CLASS_ARRAY = new Class[]{Class.class};
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final String ASPECTOF = "aspectOf";
    private static final String HASASPECT = "hasAspect";

    public static <T> T aspectOf(Class<T> aspectClass) throws NoAspectBoundException {
        try {
            return (T)Aspects.getSingletonOrThreadAspectOf(aspectClass).invoke(null, EMPTY_OBJECT_ARRAY);
        }
        catch (InvocationTargetException e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        }
        catch (Exception e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        }
    }

    public static <T> T aspectOf(Class<T> aspectClass, Object perObject) throws NoAspectBoundException {
        try {
            return (T)Aspects.getPerObjectAspectOf(aspectClass).invoke(null, perObject);
        }
        catch (InvocationTargetException e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        }
        catch (Exception e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        }
    }

    public static <T> T aspectOf(Class<T> aspectClass, Class<?> perTypeWithin) throws NoAspectBoundException {
        try {
            return (T)Aspects.getPerTypeWithinAspectOf(aspectClass).invoke(null, perTypeWithin);
        }
        catch (InvocationTargetException e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        }
        catch (Exception e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        }
    }

    public static boolean hasAspect(Class<?> aspectClass) throws NoAspectBoundException {
        try {
            return (Boolean)Aspects.getSingletonOrThreadHasAspect(aspectClass).invoke(null, EMPTY_OBJECT_ARRAY);
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class<?> aspectClass, Object perObject) throws NoAspectBoundException {
        try {
            return (Boolean)Aspects.getPerObjectHasAspect(aspectClass).invoke(null, perObject);
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class<?> aspectClass, Class<?> perTypeWithin) throws NoAspectBoundException {
        try {
            return (Boolean)Aspects.getPerTypeWithinHasAspect(aspectClass).invoke(null, perTypeWithin);
        }
        catch (Exception e) {
            return false;
        }
    }

    private static Method getSingletonOrThreadAspectOf(Class<?> aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(ASPECTOF, EMPTY_CLASS_ARRAY);
        return Aspects.checkAspectOf(method, aspectClass);
    }

    private static Method getPerObjectAspectOf(Class<?> aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(ASPECTOF, PEROBJECT_CLASS_ARRAY);
        return Aspects.checkAspectOf(method, aspectClass);
    }

    private static Method getPerTypeWithinAspectOf(Class<?> aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(ASPECTOF, PERTYPEWITHIN_CLASS_ARRAY);
        return Aspects.checkAspectOf(method, aspectClass);
    }

    private static Method checkAspectOf(Method method, Class<?> aspectClass) throws NoSuchMethodException {
        method.setAccessible(true);
        if (!(method.isAccessible() && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()))) {
            throw new NoSuchMethodException(aspectClass.getName() + ".aspectOf(..) is not accessible public static");
        }
        return method;
    }

    private static Method getSingletonOrThreadHasAspect(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(HASASPECT, EMPTY_CLASS_ARRAY);
        return Aspects.checkHasAspect(method, aspectClass);
    }

    private static Method getPerObjectHasAspect(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(HASASPECT, PEROBJECT_CLASS_ARRAY);
        return Aspects.checkHasAspect(method, aspectClass);
    }

    private static Method getPerTypeWithinHasAspect(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(HASASPECT, PERTYPEWITHIN_CLASS_ARRAY);
        return Aspects.checkHasAspect(method, aspectClass);
    }

    private static Method checkHasAspect(Method method, Class aspectClass) throws NoSuchMethodException {
        method.setAccessible(true);
        if (!(method.isAccessible() && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()))) {
            throw new NoSuchMethodException(aspectClass.getName() + ".hasAspect(..) is not accessible public static");
        }
        return method;
    }
}

