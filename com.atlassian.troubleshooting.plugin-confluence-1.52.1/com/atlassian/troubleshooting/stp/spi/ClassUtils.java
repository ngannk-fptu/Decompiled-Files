/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.troubleshooting.stp.spi.SupportDataAppender;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

final class ClassUtils {
    private ClassUtils() {
        throw new UnsupportedOperationException("Not for instantiation");
    }

    static Class<?> getSupportDataAppenderType(Class<? extends SupportDataAppender> implementingClass) {
        Type[] interfacesWithTypeArguments;
        Type type = ClassUtils.getImplementingType(implementingClass);
        for (Type interfaceType : interfacesWithTypeArguments = ClassUtils.getInterfacesWithTypeArguments(type)) {
            Class<?> foundInterfaceClass = ClassUtils.getClass(interfaceType);
            if (!SupportDataAppender.class.equals(foundInterfaceClass)) continue;
            ParameterizedType parameterizedType = (ParameterizedType)interfaceType;
            Type[] typeParameters = parameterizedType.getActualTypeArguments();
            if (typeParameters.length != 1) {
                throw new IllegalArgumentException("Unable to find the class for the type " + type);
            }
            return ClassUtils.getClass(typeParameters[0]);
        }
        throw new IllegalArgumentException("Unable to find the class for the type " + type);
    }

    private static Type getImplementingType(Class<? extends SupportDataAppender> implementingClass) {
        Type type = implementingClass;
        Class<?> typeClass = ClassUtils.getClass(type);
        while (!ClassUtils.isSupportDataAppender(typeClass)) {
            if (type instanceof Class) {
                type = type.getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType)type;
                Class rawType = (Class)parameterizedType.getRawType();
                type = rawType.getGenericSuperclass();
            }
            if ((typeClass = ClassUtils.getClass(type)) != null) continue;
            throw new IllegalArgumentException("Unable to find the class for the type " + type);
        }
        return type;
    }

    @Nullable
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

    private static Type[] getInterfacesWithTypeArguments(Type type) {
        if (type instanceof Class) {
            return ((Class)type).getGenericInterfaces();
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        Class rawType = (Class)parameterizedType.getRawType();
        return rawType.getGenericInterfaces();
    }

    private static boolean isSupportDataAppender(@Nullable Class<?> type) {
        return type != null && ArrayUtils.contains((Object[])type.getInterfaces(), SupportDataAppender.class);
    }
}

