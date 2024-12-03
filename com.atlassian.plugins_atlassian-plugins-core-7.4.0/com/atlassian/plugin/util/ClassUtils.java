/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassUtils {
    private ClassUtils() {
    }

    public static Set<Class> findAllTypes(Class cls) {
        HashSet<Class> types = new HashSet<Class>();
        ClassUtils.findAllTypes(cls, types);
        return types;
    }

    public static void findAllTypes(Class cls, Set<Class> types) {
        if (cls == null) {
            return;
        }
        if (types.contains(cls)) {
            return;
        }
        types.add(cls);
        ClassUtils.findAllTypes(cls.getSuperclass(), types);
        for (int x = 0; x < cls.getInterfaces().length; ++x) {
            ClassUtils.findAllTypes(cls.getInterfaces()[x], types);
        }
    }

    private static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return ClassUtils.getClass(((ParameterizedType)type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            Class<?> componentClass = ClassUtils.getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            }
            return null;
        }
        return null;
    }

    public static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
        HashMap resolvedTypes = new HashMap();
        Type type = childClass;
        Class<?> typeClass = ClassUtils.getClass(type);
        while (!typeClass.equals(baseClass)) {
            if (type instanceof Class) {
                type = type.getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType)type;
                Class rawType = (Class)parameterizedType.getRawType();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<Class<T>>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; ++i) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }
                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
            if ((typeClass = ClassUtils.getClass(type)) != null) continue;
            throw new IllegalArgumentException("Unable to find the class for the type " + type);
        }
        Type[] actualTypeArguments = type instanceof Class ? type.getTypeParameters() : ((ParameterizedType)type).getActualTypeArguments();
        ArrayList typeArgumentsAsClasses = new ArrayList();
        for (Type baseType : actualTypeArguments) {
            while (resolvedTypes.containsKey(baseType)) {
                baseType = (Type)resolvedTypes.get(baseType);
            }
            typeArgumentsAsClasses.add(ClassUtils.getClass(baseType));
        }
        return typeArgumentsAsClasses;
    }
}

