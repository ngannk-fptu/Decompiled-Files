/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation.annotation;

import java.lang.annotation.Annotation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

public abstract class ValidationAnnotationUtils {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    @Nullable
    public static Object[] determineValidationHints(Annotation ann) {
        if (ann instanceof Validated) {
            return ((Validated)ann).value();
        }
        Class<? extends Annotation> annotationType = ann.annotationType();
        if ("javax.validation.Valid".equals(annotationType.getName())) {
            return EMPTY_OBJECT_ARRAY;
        }
        Validated validatedAnn = (Validated)AnnotationUtils.getAnnotation((Annotation)ann, Validated.class);
        if (validatedAnn != null) {
            return validatedAnn.value();
        }
        if (annotationType.getSimpleName().startsWith("Valid")) {
            return ValidationAnnotationUtils.convertValidationHints(AnnotationUtils.getValue((Annotation)ann));
        }
        return null;
    }

    private static Object[] convertValidationHints(@Nullable Object hints) {
        Object[] objectArray;
        if (hints == null) {
            return EMPTY_OBJECT_ARRAY;
        }
        if (hints instanceof Object[]) {
            objectArray = (Object[])hints;
        } else {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = hints;
        }
        return objectArray;
    }
}

