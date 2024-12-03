/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.annotation.MergedAnnotation
 *  org.springframework.core.annotation.MergedAnnotations
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

abstract class SpringDataAnnotationUtils {
    private SpringDataAnnotationUtils() {
    }

    public static void assertPageableUniqueness(MethodParameter parameter) {
        Method method = parameter.getMethod();
        if (method == null) {
            throw new IllegalArgumentException(String.format("Method parameter %s is not backed by a method.", parameter));
        }
        if (SpringDataAnnotationUtils.containsMoreThanOnePageableParameter(method)) {
            Annotation[][] annotations = method.getParameterAnnotations();
            SpringDataAnnotationUtils.assertQualifiersFor(method.getParameterTypes(), annotations);
        }
    }

    private static boolean containsMoreThanOnePageableParameter(Method method) {
        boolean pageableFound = false;
        for (Class<?> type : method.getParameterTypes()) {
            if (pageableFound && type.equals(Pageable.class)) {
                return true;
            }
            if (!type.equals(Pageable.class)) continue;
            pageableFound = true;
        }
        return false;
    }

    public static <T> T getSpecificPropertyOrDefaultFromValue(Annotation annotation, String property) {
        Object propertyValue;
        Object result;
        Object propertyDefaultValue = AnnotationUtils.getDefaultValue((Annotation)annotation, (String)property);
        Object object = result = ObjectUtils.nullSafeEquals((Object)propertyDefaultValue, (Object)(propertyValue = AnnotationUtils.getValue((Annotation)annotation, (String)property))) ? AnnotationUtils.getValue((Annotation)annotation) : propertyValue;
        if (result == null) {
            throw new IllegalStateException("Exepected to be able to look up an annotation property value but failed!");
        }
        return (T)result;
    }

    @Nullable
    public static String getQualifier(@Nullable MethodParameter parameter) {
        if (parameter == null) {
            return null;
        }
        MergedAnnotations annotations = MergedAnnotations.from((AnnotatedElement)parameter.getParameter());
        MergedAnnotation qualifier = annotations.get(Qualifier.class);
        return qualifier.isPresent() ? qualifier.getString("value") : null;
    }

    public static void assertQualifiersFor(Class<?>[] parameterTypes, Annotation[][] annotations) {
        HashSet<String> values = new HashSet<String>();
        for (int i = 0; i < annotations.length; ++i) {
            if (!Pageable.class.equals(parameterTypes[i])) continue;
            Qualifier qualifier = SpringDataAnnotationUtils.findAnnotation(annotations[i]);
            if (null == qualifier) {
                throw new IllegalStateException("Ambiguous Pageable arguments in handler method. If you use multiple parameters of type Pageable you need to qualify them with @Qualifier");
            }
            if (values.contains(qualifier.value())) {
                throw new IllegalStateException("Values of the user Qualifiers must be unique!");
            }
            values.add(qualifier.value());
        }
    }

    @Nullable
    private static Qualifier findAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof Qualifier)) continue;
            return (Qualifier)annotation;
        }
        return null;
    }
}

