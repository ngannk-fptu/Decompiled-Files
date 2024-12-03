/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.internal.util.AmbigiousInstantiationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class InstantiationUtils {
    private InstantiationUtils() {
    }

    public static <T> T newInstanceOrNull(Class<? extends T> clazz, Object ... params) {
        Constructor<T> constructor = InstantiationUtils.selectMatchingConstructor(clazz, params);
        if (constructor == null) {
            return null;
        }
        try {
            return constructor.newInstance(params);
        }
        catch (IllegalAccessException e) {
            return null;
        }
        catch (InstantiationException e) {
            return null;
        }
        catch (InvocationTargetException e) {
            return null;
        }
    }

    private static <T> Constructor<T> selectMatchingConstructor(Class<? extends T> clazz, Object[] params) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> selectedConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (!InstantiationUtils.isParamsMatching(constructor, params)) continue;
            if (selectedConstructor == null) {
                selectedConstructor = constructor;
                continue;
            }
            throw new AmbigiousInstantiationException("Class " + clazz + " has multiple constructors matching given parameters: " + Arrays.toString(params));
        }
        return selectedConstructor;
    }

    private static boolean isParamsMatching(Constructor<?> constructor, Object[] params) {
        Class<?>[] constructorParamTypes = constructor.getParameterTypes();
        if (constructorParamTypes.length != params.length) {
            return false;
        }
        for (int i = 0; i < constructorParamTypes.length; ++i) {
            Class<?> paramType;
            Class<?> constructorParamType = constructorParamTypes[i];
            Object param = params[i];
            if (constructorParamType.isPrimitive()) {
                if (param == null) {
                    return false;
                }
                constructorParamType = InstantiationUtils.toBoxedType(constructorParamType);
            }
            if (param == null || constructorParamType.isAssignableFrom(paramType = param.getClass())) continue;
            return false;
        }
        return true;
    }

    private static Class<?> toBoxedType(Class<?> type) {
        assert (type.isPrimitive());
        assert (type != Void.TYPE);
        if (type == Boolean.TYPE) {
            return Boolean.class;
        }
        if (type == Byte.TYPE) {
            return Byte.class;
        }
        if (type == Character.TYPE) {
            return Character.class;
        }
        if (type == Double.TYPE) {
            return Double.class;
        }
        if (type == Float.TYPE) {
            return Float.class;
        }
        if (type == Integer.TYPE) {
            return Integer.class;
        }
        if (type == Long.TYPE) {
            return Long.class;
        }
        if (type == Short.TYPE) {
            return Short.class;
        }
        throw new IllegalArgumentException("Unknown primitive type " + type.getName());
    }
}

