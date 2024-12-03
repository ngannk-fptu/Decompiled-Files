/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.commons.beanutils.MethodUtils;

public class ConstructorUtils {
    private static final Class<?>[] EMPTY_CLASS_PARAMETERS = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static <T> T invokeConstructor(Class<T> klass, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] args = ConstructorUtils.toArray(arg);
        return ConstructorUtils.invokeConstructor(klass, args);
    }

    public static <T> T invokeConstructor(Class<T> klass, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == args) {
            args = EMPTY_OBJECT_ARRAY;
        }
        int arguments = args.length;
        Class[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return ConstructorUtils.invokeConstructor(klass, args, parameterTypes);
    }

    public static <T> T invokeConstructor(Class<T> klass, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> ctor;
        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        if (null == (ctor = ConstructorUtils.getMatchingAccessibleConstructor(klass, parameterTypes))) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);
    }

    public static <T> T invokeExactConstructor(Class<T> klass, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] args = ConstructorUtils.toArray(arg);
        return ConstructorUtils.invokeExactConstructor(klass, args);
    }

    public static <T> T invokeExactConstructor(Class<T> klass, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == args) {
            args = EMPTY_OBJECT_ARRAY;
        }
        int arguments = args.length;
        Class[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return ConstructorUtils.invokeExactConstructor(klass, args, parameterTypes);
    }

    public static <T> T invokeExactConstructor(Class<T> klass, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> ctor;
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if (null == (ctor = ConstructorUtils.getAccessibleConstructor(klass, parameterTypes))) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);
    }

    public static <T> Constructor<T> getAccessibleConstructor(Class<T> klass, Class<?> parameterType) {
        Class[] parameterTypes = new Class[]{parameterType};
        return ConstructorUtils.getAccessibleConstructor(klass, parameterTypes);
    }

    public static <T> Constructor<T> getAccessibleConstructor(Class<T> klass, Class<?>[] parameterTypes) {
        try {
            return ConstructorUtils.getAccessibleConstructor(klass.getConstructor(parameterTypes));
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> Constructor<T> getAccessibleConstructor(Constructor<T> ctor) {
        if (ctor == null) {
            return null;
        }
        if (!Modifier.isPublic(ctor.getModifiers())) {
            return null;
        }
        Class<T> clazz = ctor.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return ctor;
        }
        return null;
    }

    private static Object[] toArray(Object arg) {
        Object[] args = null;
        if (arg != null) {
            args = new Object[]{arg};
        }
        return args;
    }

    private static <T> Constructor<T> getMatchingAccessibleConstructor(Class<T> clazz, Class<?>[] parameterTypes) {
        try {
            Constructor<T> ctor = clazz.getConstructor(parameterTypes);
            try {
                ctor.setAccessible(true);
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
            return ctor;
        }
        catch (NoSuchMethodException ctor) {
            Constructor<?>[] ctors;
            int paramSize = parameterTypes.length;
            for (Constructor<?> ctor2 : ctors = clazz.getConstructors()) {
                Constructor<?> ctor3;
                Class<?>[] ctorParams = ctor2.getParameterTypes();
                int ctorParamSize = ctorParams.length;
                if (ctorParamSize != paramSize) continue;
                boolean match = true;
                for (int n = 0; n < ctorParamSize; ++n) {
                    if (MethodUtils.isAssignmentCompatible(ctorParams[n], parameterTypes[n])) continue;
                    match = false;
                    break;
                }
                if (!match || (ctor3 = ConstructorUtils.getAccessibleConstructor(ctor2)) == null) continue;
                try {
                    ctor3.setAccessible(true);
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                Constructor<?> typedCtor = ctor3;
                return typedCtor;
            }
            return null;
        }
    }
}

