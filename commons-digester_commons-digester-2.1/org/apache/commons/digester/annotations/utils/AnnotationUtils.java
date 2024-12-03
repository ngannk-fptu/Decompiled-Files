/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.MethodUtils
 */
package org.apache.commons.digester.annotations.utils;

import java.lang.annotation.Annotation;
import org.apache.commons.beanutils.MethodUtils;

public class AnnotationUtils {
    private static final String VALUE = "value";
    private static final String PATTERN = "pattern";

    private AnnotationUtils() {
    }

    public static Object getAnnotationValue(Annotation annotation) {
        return AnnotationUtils.invokeAnnotationMethod(annotation, VALUE);
    }

    public static String getAnnotationPattern(Annotation annotation) {
        Object ret = AnnotationUtils.invokeAnnotationMethod(annotation, PATTERN);
        if (ret != null) {
            return (String)ret;
        }
        return null;
    }

    public static Annotation[] getAnnotationsArrayValue(Annotation annotation) {
        Object value = AnnotationUtils.getAnnotationValue(annotation);
        if (value != null && value.getClass().isArray() && Annotation.class.isAssignableFrom(value.getClass().getComponentType())) {
            return (Annotation[])value;
        }
        return null;
    }

    private static Object invokeAnnotationMethod(Annotation annotation, String method) {
        try {
            return MethodUtils.invokeExactMethod((Object)annotation, (String)method, null);
        }
        catch (Throwable t) {
            return null;
        }
    }
}

