/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import com.querydsl.core.util.Annotations;
import com.querydsl.core.util.BeanUtils;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public final class ReflectionUtils {
    private static final AnnotatedElement EMPTY = new Annotations(new AnnotatedElement[0]);

    private ReflectionUtils() {
    }

    public static AnnotatedElement getAnnotatedElement(Class<?> beanClass, String propertyName, Class<?> propertyClass) {
        Field field = ReflectionUtils.getFieldOrNull(beanClass, propertyName);
        Method method = ReflectionUtils.getGetterOrNull(beanClass, propertyName, propertyClass);
        if (field == null || field.getAnnotations().length == 0) {
            return method != null && method.getAnnotations().length > 0 ? method : EMPTY;
        }
        if (method == null || method.getAnnotations().length == 0) {
            return field;
        }
        return new Annotations(field, method);
    }

    @Nullable
    public static Field getFieldOrNull(Class<?> beanClass, String propertyName) {
        while (beanClass != null && !beanClass.equals(Object.class)) {
            try {
                return beanClass.getDeclaredField(propertyName);
            }
            catch (SecurityException securityException) {
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
            beanClass = beanClass.getSuperclass();
        }
        return null;
    }

    @Nullable
    public static Method getGetterOrNull(Class<?> beanClass, String name) {
        Method method = ReflectionUtils.getGetterOrNull(beanClass, name, Object.class);
        if (method != null) {
            return method;
        }
        return ReflectionUtils.getGetterOrNull(beanClass, name, Boolean.class);
    }

    @Nullable
    public static Method getGetterOrNull(Class<?> beanClass, String name, Class<?> type) {
        String methodName = (type.equals(Boolean.class) || type.equals(Boolean.TYPE) ? "is" : "get") + BeanUtils.capitalize(name);
        while (beanClass != null && !beanClass.equals(Object.class)) {
            try {
                return beanClass.getDeclaredMethod(methodName, new Class[0]);
            }
            catch (SecurityException securityException) {
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            beanClass = beanClass.getSuperclass();
        }
        return null;
    }

    public static int getTypeParameterCount(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType)type).getActualTypeArguments().length;
        }
        if (type instanceof TypeVariable) {
            return ReflectionUtils.getTypeParameterCount(((TypeVariable)type).getBounds()[0]);
        }
        return 0;
    }

    public static Class<?> getTypeParameterAsClass(Type type, int index) {
        Type parameter = ReflectionUtils.getTypeParameter(type, index);
        if (parameter != null) {
            return ReflectionUtils.asClass(parameter);
        }
        return null;
    }

    @Nullable
    public static Type getTypeParameter(Type type, int index) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType)type).getActualTypeArguments()[index];
        }
        if (type instanceof TypeVariable) {
            return ReflectionUtils.getTypeParameter(((TypeVariable)type).getBounds()[0], index);
        }
        return null;
    }

    private static Class<?> asClass(Type type) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;
            if (wildcardType.getUpperBounds()[0] instanceof Class) {
                return (Class)wildcardType.getUpperBounds()[0];
            }
            if (wildcardType.getUpperBounds()[0] instanceof ParameterizedType) {
                return (Class)((ParameterizedType)wildcardType.getUpperBounds()[0]).getRawType();
            }
            return Object.class;
        }
        if (type instanceof TypeVariable) {
            return ReflectionUtils.asClass(((TypeVariable)type).getBounds()[0]);
        }
        if (type instanceof ParameterizedType) {
            return (Class)((ParameterizedType)type).getRawType();
        }
        if (type instanceof GenericArrayType) {
            Type component = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance(ReflectionUtils.asClass(component), 0).getClass();
        }
        if (type instanceof Class) {
            return (Class)type;
        }
        throw new IllegalArgumentException(type.getClass().toString());
    }

    public static Set<Class<?>> getSuperClasses(Class<?> cl) {
        HashSet classes = new HashSet();
        for (Class<?> c = cl; c != null; c = c.getSuperclass()) {
            classes.add(c);
        }
        return classes;
    }

    public static Set<Field> getFields(Class<?> cl) {
        HashSet<Field> fields = new HashSet<Field>();
        for (Class<?> c = cl; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static Set<Class<?>> getImplementedInterfaces(Class<?> cl) {
        HashSet interfaces = new HashSet();
        ArrayDeque classes = new ArrayDeque();
        classes.add(cl);
        while (!classes.isEmpty()) {
            Class c = (Class)classes.pop();
            interfaces.addAll(Arrays.asList(c.getInterfaces()));
            if (c.getSuperclass() != null) {
                classes.add(c.getSuperclass());
            }
            classes.addAll(Arrays.asList(c.getInterfaces()));
        }
        return interfaces;
    }
}

