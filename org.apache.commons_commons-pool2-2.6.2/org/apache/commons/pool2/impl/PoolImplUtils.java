/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.apache.commons.pool2.PooledObjectFactory;

class PoolImplUtils {
    PoolImplUtils() {
    }

    static Class<?> getFactoryType(Class<? extends PooledObjectFactory> factoryClass) {
        Class<PooledObjectFactory> type = PooledObjectFactory.class;
        Object genericType = PoolImplUtils.getGenericType(type, factoryClass);
        if (genericType instanceof Integer) {
            Type bound0;
            Type[] bounds;
            ParameterizedType pi = PoolImplUtils.getParameterizedType(type, factoryClass);
            if (pi != null && (bounds = ((TypeVariable)pi.getActualTypeArguments()[(Integer)genericType]).getBounds()) != null && bounds.length > 0 && (bound0 = bounds[0]) instanceof Class) {
                return (Class)bound0;
            }
            return Object.class;
        }
        return (Class)genericType;
    }

    private static <T> Object getGenericType(Class<T> type, Class<? extends T> clazz) {
        if (type == null || clazz == null) {
            return null;
        }
        ParameterizedType pi = PoolImplUtils.getParameterizedType(type, clazz);
        if (pi != null) {
            return PoolImplUtils.getTypeParameter(clazz, pi.getActualTypeArguments()[0]);
        }
        Class<? extends T> superClass = clazz.getSuperclass();
        Object result = PoolImplUtils.getGenericType(type, superClass);
        if (result instanceof Class) {
            return result;
        }
        if (result instanceof Integer) {
            ParameterizedType superClassType = (ParameterizedType)clazz.getGenericSuperclass();
            return PoolImplUtils.getTypeParameter(clazz, superClassType.getActualTypeArguments()[(Integer)result]);
        }
        return null;
    }

    private static <T> ParameterizedType getParameterizedType(Class<T> type, Class<? extends T> clazz) {
        for (Type iface : clazz.getGenericInterfaces()) {
            ParameterizedType pi;
            if (!(iface instanceof ParameterizedType) || !((pi = (ParameterizedType)iface).getRawType() instanceof Class) || !type.isAssignableFrom((Class)pi.getRawType())) continue;
            return pi;
        }
        return null;
    }

    private static Object getTypeParameter(Class<?> clazz, Type argType) {
        if (argType instanceof Class) {
            return argType;
        }
        TypeVariable<Class<?>>[] tvs = clazz.getTypeParameters();
        for (int i = 0; i < tvs.length; ++i) {
            if (!tvs[i].equals(argType)) continue;
            return i;
        }
        return null;
    }
}

