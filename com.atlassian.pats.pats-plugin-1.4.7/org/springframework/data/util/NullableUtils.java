/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.annotation.MergedAnnotations
 *  org.springframework.lang.NonNullApi
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.data.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

public abstract class NullableUtils {
    private static final String NON_NULL_CLASS_NAME = "javax.annotation.Nonnull";
    private static final String TYPE_QUALIFIER_CLASS_NAME = "javax.annotation.meta.TypeQualifierDefault";
    private static final Optional<Class<Annotation>> NON_NULL_ANNOTATION_CLASS = NullableUtils.findClass("javax.annotation.Nonnull");
    private static final Set<Class<?>> NULLABLE_ANNOTATIONS = NullableUtils.findClasses(Nullable.class.getName());
    private static final Set<Class<?>> NON_NULLABLE_ANNOTATIONS = NullableUtils.findClasses("reactor.util.lang.NonNullApi", NonNullApi.class.getName());
    private static final Set<String> WHEN_NULLABLE = new HashSet<String>(Arrays.asList("UNKNOWN", "MAYBE", "NEVER"));
    private static final Set<String> WHEN_NON_NULLABLE = new HashSet<String>(Collections.singletonList("ALWAYS"));

    private NullableUtils() {
    }

    public static boolean isNonNull(Method method, ElementType elementType) {
        return NullableUtils.isNonNull(method.getDeclaringClass(), elementType) || NullableUtils.isNonNull((AnnotatedElement)method, elementType);
    }

    public static boolean isNonNull(Class<?> type, ElementType elementType) {
        return NullableUtils.isNonNull(type.getPackage(), elementType) || NullableUtils.isNonNull(type, elementType);
    }

    public static boolean isNonNull(AnnotatedElement element, ElementType elementType) {
        for (Annotation annotation : element.getAnnotations()) {
            boolean isNonNull;
            boolean bl = isNonNull = NON_NULL_ANNOTATION_CLASS.isPresent() ? NullableUtils.isNonNull(annotation, elementType) : NON_NULLABLE_ANNOTATIONS.contains(annotation.annotationType());
            if (!isNonNull) continue;
            return true;
        }
        return false;
    }

    private static boolean isNonNull(Annotation annotation, ElementType elementType) {
        if (!NON_NULL_ANNOTATION_CLASS.isPresent()) {
            return false;
        }
        Class<Annotation> annotationClass = NON_NULL_ANNOTATION_CLASS.get();
        if (annotation.annotationType().equals(annotationClass)) {
            return true;
        }
        if (!MergedAnnotations.from(annotation.annotationType()).isPresent(annotationClass) || !NullableUtils.isNonNull(annotation)) {
            return false;
        }
        return NullableUtils.test(annotation, TYPE_QUALIFIER_CLASS_NAME, "value", o -> Arrays.binarySearch((Object[])o, (Object)elementType) >= 0);
    }

    public static boolean isExplicitNullable(MethodParameter methodParameter) {
        if (methodParameter.getParameterIndex() == -1) {
            return NullableUtils.isExplicitNullable(methodParameter.getMethodAnnotations());
        }
        return NullableUtils.isExplicitNullable(methodParameter.getParameterAnnotations());
    }

    private static boolean isExplicitNullable(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            boolean isNullable;
            boolean bl = isNullable = NON_NULL_ANNOTATION_CLASS.isPresent() ? NullableUtils.isNullable(annotation) : NULLABLE_ANNOTATIONS.contains(annotation.annotationType());
            if (!isNullable) continue;
            return true;
        }
        return false;
    }

    private static boolean isNonNull(Annotation annotation) {
        return NullableUtils.test(annotation, NON_NULL_CLASS_NAME, "when", o -> WHEN_NON_NULLABLE.contains(o.toString()));
    }

    private static boolean isNullable(Annotation annotation) {
        return NullableUtils.test(annotation, NON_NULL_CLASS_NAME, "when", o -> WHEN_NULLABLE.contains(o.toString()));
    }

    private static <T> boolean test(Annotation annotation, String metaAnnotationName, String attribute, Predicate<T> filter) {
        if (annotation.annotationType().getName().equals(metaAnnotationName)) {
            Map attributes = AnnotationUtils.getAnnotationAttributes((Annotation)annotation);
            return !attributes.isEmpty() && filter.test(attributes.get(attribute));
        }
        MultiValueMap attributes = AnnotatedElementUtils.getAllAnnotationAttributes(annotation.annotationType(), (String)metaAnnotationName);
        if (attributes == null || attributes.isEmpty()) {
            return false;
        }
        List elementTypes = (List)attributes.get((Object)attribute);
        for (Object value : elementTypes) {
            if (!filter.test(value)) continue;
            return true;
        }
        return false;
    }

    private static Set<Class<?>> findClasses(String ... classNames) {
        return Arrays.stream(classNames).map(NullableUtils::findClass).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    private static <T> Optional<Class<T>> findClass(String className) {
        try {
            return Optional.of(ClassUtils.forName((String)className, (ClassLoader)NullableUtils.class.getClassLoader()));
        }
        catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}

