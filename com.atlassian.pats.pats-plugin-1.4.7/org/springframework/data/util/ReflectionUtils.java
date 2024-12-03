/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$FieldFilter
 */
package org.springframework.data.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public final class ReflectionUtils {
    private ReflectionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> T createInstanceIfPresent(String classname, T defaultInstance) {
        try {
            Class type = ClassUtils.forName((String)classname, (ClassLoader)ClassUtils.getDefaultClassLoader());
            return (T)BeanUtils.instantiateClass((Class)type);
        }
        catch (Exception e) {
            return defaultInstance;
        }
    }

    public static boolean isVoid(Class<?> type) {
        if (type == Void.class || Void.TYPE == type) {
            return true;
        }
        return type.getName().equals("kotlin.Unit");
    }

    @Nullable
    public static Field findField(Class<?> type, final ReflectionUtils.FieldFilter filter) {
        return ReflectionUtils.findField(type, new DescribedFieldFilter(){

            public boolean matches(Field field) {
                return filter.matches(field);
            }

            @Override
            public String getDescription() {
                return String.format("FieldFilter %s", filter.toString());
            }
        }, false);
    }

    @Nullable
    public static Field findField(Class<?> type, DescribedFieldFilter filter) {
        return ReflectionUtils.findField(type, filter, true);
    }

    @Nullable
    public static Field findField(Class<?> type, DescribedFieldFilter filter, boolean enforceUniqueness) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull((Object)filter, (String)"Filter must not be null!");
        Field foundField = null;
        for (Class<?> targetClass = type; targetClass != Object.class; targetClass = targetClass.getSuperclass()) {
            for (Field field : targetClass.getDeclaredFields()) {
                if (!filter.matches(field)) continue;
                if (!enforceUniqueness) {
                    return field;
                }
                if (foundField != null && enforceUniqueness) {
                    throw new IllegalStateException(filter.getDescription());
                }
                foundField = field;
            }
        }
        return foundField;
    }

    public static Field findRequiredField(Class<?> type, String name) {
        Field result = org.springframework.util.ReflectionUtils.findField(type, (String)name);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Unable to find field %s on %s!", name, type));
        }
        return result;
    }

    public static void setField(Field field, Object target, @Nullable Object value) {
        org.springframework.util.ReflectionUtils.makeAccessible((Field)field);
        org.springframework.util.ReflectionUtils.setField((Field)field, (Object)target, (Object)value);
    }

    public static Optional<Constructor<?>> findConstructor(Class<?> type, Object ... constructorArguments) {
        Assert.notNull(type, (String)"Target type must not be null!");
        Assert.notNull((Object)constructorArguments, (String)"Constructor arguments must not be null!");
        return Arrays.stream(type.getDeclaredConstructors()).filter(constructor -> ReflectionUtils.argumentsMatch(constructor.getParameterTypes(), constructorArguments)).findFirst();
    }

    public static Method findRequiredMethod(Class<?> type, String name, Class<?> ... parameterTypes) {
        Assert.notNull(type, (String)"Class must not be null");
        Assert.notNull((Object)name, (String)"Method name must not be null");
        Method result = null;
        for (Class<?> searchType = type; searchType != null; searchType = searchType.getSuperclass()) {
            Method[] methods;
            for (Method method : methods = searchType.isInterface() ? searchType.getMethods() : org.springframework.util.ReflectionUtils.getDeclaredMethods(searchType)) {
                if (!name.equals(method.getName()) || !ReflectionUtils.hasSameParams(method, parameterTypes) || result != null && !result.isSynthetic() && !result.isBridge()) continue;
                result = method;
            }
        }
        if (result == null) {
            String parameterTypeNames = Arrays.stream(parameterTypes).map(Object::toString).collect(Collectors.joining(", "));
            throw new IllegalArgumentException(String.format("Unable to find method %s(%s)on %s!", name, parameterTypeNames, type));
        }
        return result;
    }

    private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
        return paramTypes.length == method.getParameterCount() && Arrays.equals(paramTypes, method.getParameterTypes());
    }

    public static Stream<Class<?>> returnTypeAndParameters(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        Stream<Class<?>> returnType = Stream.of(method.getReturnType());
        Stream<Class<?>> parameterTypes = Arrays.stream(method.getParameterTypes());
        return Stream.concat(returnType, parameterTypes);
    }

    public static Optional<Method> getMethod(Class<?> type, String name, ResolvableType ... parameterTypes) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.hasText((String)name, (String)"Name must not be null or empty!");
        Assert.notNull((Object)parameterTypes, (String)"Parameter types must not be null!");
        List<Class> collect = Arrays.stream(parameterTypes).map(ResolvableType::getRawClass).collect(Collectors.toList());
        Method method = org.springframework.util.ReflectionUtils.findMethod(type, (String)name, (Class[])collect.toArray(new Class[collect.size()]));
        return Optional.ofNullable(method).filter(it -> IntStream.range(0, it.getParameterCount()).allMatch(index -> ResolvableType.forMethodParameter((Method)it, (int)index).equals((Object)parameterTypes[index])));
    }

    private static boolean argumentsMatch(Class<?>[] parameterTypes, Object[] arguments) {
        if (parameterTypes.length != arguments.length) {
            return false;
        }
        int index = 0;
        for (Class<?> argumentType : parameterTypes) {
            Object argument = arguments[index];
            if (argumentType.isPrimitive() && argument == null) {
                return false;
            }
            if (argument != null && !ClassUtils.isAssignableValue(argumentType, (Object)argument)) {
                return false;
            }
            ++index;
        }
        return true;
    }

    @Deprecated
    public static boolean isKotlinClass(Class<?> type) {
        return KotlinDetector.isKotlinType(type);
    }

    @Deprecated
    public static boolean isSupportedKotlinClass(Class<?> type) {
        return KotlinReflectionUtils.isSupportedKotlinClass(type);
    }

    public static boolean isNullable(MethodParameter parameter) {
        if (ReflectionUtils.isVoid(parameter.getParameterType())) {
            return true;
        }
        if (ReflectionUtils.isSupportedKotlinClass(parameter.getDeclaringClass())) {
            return KotlinReflectionUtils.isNullable(parameter);
        }
        return !parameter.getParameterType().isPrimitive();
    }

    public static Object getPrimitiveDefault(Class<?> type) {
        if (type == Byte.TYPE || type == Byte.class) {
            return (byte)0;
        }
        if (type == Short.TYPE || type == Short.class) {
            return (short)0;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            return 0;
        }
        if (type == Long.TYPE || type == Long.class) {
            return 0L;
        }
        if (type == Float.TYPE || type == Float.class) {
            return Float.valueOf(0.0f);
        }
        if (type == Double.TYPE || type == Double.class) {
            return 0.0;
        }
        if (type == Character.TYPE || type == Character.class) {
            return Character.valueOf('\u0000');
        }
        if (type == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException(String.format("Primitive type %s not supported!", type));
    }

    @Nullable
    public static Class<?> loadIfPresent(String name, ClassLoader classLoader) {
        try {
            return ClassUtils.forName((String)name, (ClassLoader)classLoader);
        }
        catch (Exception o_O) {
            return null;
        }
    }

    public static class AnnotationFieldFilter
    implements DescribedFieldFilter {
        private final Class<? extends Annotation> annotationType;

        public AnnotationFieldFilter(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean matches(Field field) {
            return AnnotationUtils.getAnnotation((AnnotatedElement)field, this.annotationType) != null;
        }

        @Override
        public String getDescription() {
            return String.format("Annotation filter for %s", this.annotationType.getName());
        }
    }

    public static interface DescribedFieldFilter
    extends ReflectionUtils.FieldFilter {
        public String getDescription();
    }
}

