/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

final class ObjectToObjectConverter
implements ConditionalGenericConverter {
    private static final Map<Class<?>, Executable> conversionExecutableCache = new ConcurrentReferenceHashMap(32);

    ObjectToObjectConverter() {
    }

    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(Object.class, Object.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getType() != targetType.getType() && ObjectToObjectConverter.hasConversionMethodOrConstructor(targetType.getType(), sourceType.getType());
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class<?> sourceClass = sourceType.getType();
        Class<?> targetClass = targetType.getType();
        Executable executable = ObjectToObjectConverter.getValidatedExecutable(targetClass, sourceClass);
        try {
            if (executable instanceof Method) {
                Method method = (Method)executable;
                ReflectionUtils.makeAccessible(method);
                if (!Modifier.isStatic(method.getModifiers())) {
                    return method.invoke(source, new Object[0]);
                }
                return method.invoke(null, source);
            }
            if (executable instanceof Constructor) {
                Constructor ctor = (Constructor)executable;
                ReflectionUtils.makeAccessible(ctor);
                return ctor.newInstance(source);
            }
        }
        catch (InvocationTargetException ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex.getTargetException());
        }
        catch (Throwable ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex);
        }
        throw new IllegalStateException(String.format("No to%3$s() method exists on %1$s, and no static valueOf/of/from(%1$s) method or %3$s(%1$s) constructor exists on %2$s.", sourceClass.getName(), targetClass.getName(), targetClass.getSimpleName()));
    }

    static boolean hasConversionMethodOrConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return ObjectToObjectConverter.getValidatedExecutable(targetClass, sourceClass) != null;
    }

    @Nullable
    private static Executable getValidatedExecutable(Class<?> targetClass, Class<?> sourceClass) {
        Executable executable = conversionExecutableCache.get(targetClass);
        if (ObjectToObjectConverter.isApplicable(executable, sourceClass)) {
            return executable;
        }
        executable = ObjectToObjectConverter.determineToMethod(targetClass, sourceClass);
        if (executable == null && (executable = ObjectToObjectConverter.determineFactoryMethod(targetClass, sourceClass)) == null && (executable = ObjectToObjectConverter.determineFactoryConstructor(targetClass, sourceClass)) == null) {
            return null;
        }
        conversionExecutableCache.put(targetClass, executable);
        return executable;
    }

    private static boolean isApplicable(Executable executable, Class<?> sourceClass) {
        if (executable instanceof Method) {
            Method method = (Method)executable;
            return !Modifier.isStatic(method.getModifiers()) ? ClassUtils.isAssignable(method.getDeclaringClass(), sourceClass) : method.getParameterTypes()[0] == sourceClass;
        }
        if (executable instanceof Constructor) {
            Constructor ctor = (Constructor)executable;
            return ctor.getParameterTypes()[0] == sourceClass;
        }
        return false;
    }

    @Nullable
    private static Method determineToMethod(Class<?> targetClass, Class<?> sourceClass) {
        if (String.class == targetClass || String.class == sourceClass) {
            return null;
        }
        Method method = ClassUtils.getMethodIfAvailable(sourceClass, "to" + targetClass.getSimpleName(), new Class[0]);
        return method != null && !Modifier.isStatic(method.getModifiers()) && ClassUtils.isAssignable(targetClass, method.getReturnType()) ? method : null;
    }

    @Nullable
    private static Method determineFactoryMethod(Class<?> targetClass, Class<?> sourceClass) {
        if (String.class == targetClass) {
            return null;
        }
        Method method = ClassUtils.getStaticMethod(targetClass, "valueOf", sourceClass);
        if (method == null && (method = ClassUtils.getStaticMethod(targetClass, "of", sourceClass)) == null) {
            method = ClassUtils.getStaticMethod(targetClass, "from", sourceClass);
        }
        return method != null && ObjectToObjectConverter.areRelatedTypes(targetClass, method.getReturnType()) ? method : null;
    }

    private static boolean areRelatedTypes(Class<?> type1, Class<?> type2) {
        return ClassUtils.isAssignable(type1, type2) || ClassUtils.isAssignable(type2, type1);
    }

    @Nullable
    private static Constructor<?> determineFactoryConstructor(Class<?> targetClass, Class<?> sourceClass) {
        return ClassUtils.getConstructorIfAvailable(targetClass, sourceClass);
    }
}

