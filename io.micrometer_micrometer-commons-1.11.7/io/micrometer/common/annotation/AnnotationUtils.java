/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common.annotation;

import io.micrometer.common.annotation.AnnotatedParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class AnnotationUtils {
    private AnnotationUtils() {
    }

    static List<AnnotatedParameter> findAnnotatedParameters(Class<? extends Annotation> annotationClazz, Method method, Object[] args) {
        Annotation[][] parameters = method.getParameterAnnotations();
        ArrayList<AnnotatedParameter> result = new ArrayList<AnnotatedParameter>();
        int i = 0;
        Annotation[][] annotationArray = parameters;
        int n = annotationArray.length;
        for (int j = 0; j < n; ++j) {
            Annotation[] parameter;
            for (Annotation parameter2 : parameter = annotationArray[j]) {
                if (!annotationClazz.isAssignableFrom(parameter2.annotationType())) continue;
                result.add(new AnnotatedParameter(i, parameter2, args[i]));
            }
            ++i;
        }
        return result;
    }
}

