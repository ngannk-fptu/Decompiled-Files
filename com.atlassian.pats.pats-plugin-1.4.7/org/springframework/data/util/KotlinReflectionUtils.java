/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.jvm.JvmClassMappingKt
 *  kotlin.reflect.KCallable
 *  kotlin.reflect.KClass
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.KMutableProperty
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.KProperty
 *  kotlin.reflect.KType
 *  kotlin.reflect.jvm.KTypesJvm
 *  kotlin.reflect.jvm.ReflectJvmMapping
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KCallable;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.KMutableProperty;
import kotlin.reflect.KParameter;
import kotlin.reflect.KProperty;
import kotlin.reflect.KType;
import kotlin.reflect.jvm.KTypesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

public final class KotlinReflectionUtils {
    private KotlinReflectionUtils() {
    }

    public static boolean isSupportedKotlinClass(Class<?> type) {
        if (!KotlinDetector.isKotlinType(type)) {
            return false;
        }
        return Arrays.stream(type.getDeclaredAnnotations()).filter(annotation -> annotation.annotationType().getName().equals("kotlin.Metadata")).map(annotation -> AnnotationUtils.getValue((Annotation)annotation, (String)"k")).anyMatch(it -> Integer.valueOf(KotlinClassHeaderKind.CLASS.id).equals(it));
    }

    public static boolean isDataClass(Class<?> type) {
        if (!KotlinDetector.isKotlinType(type)) {
            return false;
        }
        KClass kotlinClass = JvmClassMappingKt.getKotlinClass(type);
        return kotlinClass.isData();
    }

    @Nullable
    public static KFunction<?> findKotlinFunction(Method method) {
        KFunction kotlinFunction = ReflectJvmMapping.getKotlinFunction((Method)method);
        return kotlinFunction == null ? (KFunction)KotlinReflectionUtils.findKFunction(method).orElse(null) : kotlinFunction;
    }

    public static boolean isSuspend(Method method) {
        KFunction<?> invokedFunction = KotlinDetector.isKotlinType(method.getDeclaringClass()) ? KotlinReflectionUtils.findKotlinFunction(method) : null;
        return invokedFunction != null && invokedFunction.isSuspend();
    }

    public static Class<?> getReturnType(Method method) {
        KFunction<?> kotlinFunction = KotlinReflectionUtils.findKotlinFunction(method);
        if (kotlinFunction == null) {
            throw new IllegalArgumentException(String.format("Cannot resolve %s to a KFunction!", method));
        }
        return JvmClassMappingKt.getJavaClass((KClass)KTypesJvm.getJvmErasure((KType)kotlinFunction.getReturnType()));
    }

    static boolean isNullable(MethodParameter parameter) {
        Method method = parameter.getMethod();
        if (method == null) {
            throw new IllegalStateException(String.format("Cannot obtain method from parameter %s!", parameter));
        }
        KFunction<?> kotlinFunction = KotlinReflectionUtils.findKotlinFunction(method);
        if (kotlinFunction == null) {
            throw new IllegalArgumentException(String.format("Cannot resolve %s to a Kotlin function!", parameter));
        }
        if (kotlinFunction.isSuspend() && KotlinReflectionUtils.isLast(parameter)) {
            return false;
        }
        if (kotlinFunction.getParameters().size() > parameter.getParameterIndex() + 1) {
            KType type = parameter.getParameterIndex() == -1 ? kotlinFunction.getReturnType() : ((KParameter)kotlinFunction.getParameters().get(parameter.getParameterIndex() + 1)).getType();
            return type.isMarkedNullable();
        }
        return true;
    }

    private static boolean isLast(MethodParameter parameter) {
        Method method = parameter.getMethod();
        return method != null && parameter.getParameterIndex() == method.getParameterCount() - 1;
    }

    private static Optional<? extends KFunction<?>> findKFunction(Method method) {
        KClass kotlinClass = JvmClassMappingKt.getKotlinClass(method.getDeclaringClass());
        return kotlinClass.getMembers().stream().flatMap(KotlinReflectionUtils::toKFunctionStream).filter(it -> KotlinReflectionUtils.isSame(it, method)).findFirst();
    }

    private static Stream<? extends KFunction<?>> toKFunctionStream(KCallable<?> it) {
        if (it instanceof KMutableProperty) {
            KMutableProperty property = (KMutableProperty)it;
            return Stream.of(property.getGetter(), property.getSetter());
        }
        if (it instanceof KProperty) {
            KProperty property = (KProperty)it;
            return Stream.of(property.getGetter());
        }
        if (it instanceof KFunction) {
            return Stream.of((KFunction)it);
        }
        return Stream.empty();
    }

    private static boolean isSame(KFunction<?> function, Method method) {
        Method javaMethod = ReflectJvmMapping.getJavaMethod(function);
        return javaMethod != null && javaMethod.equals(method);
    }

    private static enum KotlinClassHeaderKind {
        CLASS(1),
        FILE(2),
        SYNTHETIC_CLASS(3),
        MULTI_FILE_CLASS_FACADE(4),
        MULTI_FILE_CLASS_PART(5);

        int id;

        private KotlinClassHeaderKind(int val) {
            this.id = val;
        }
    }
}

