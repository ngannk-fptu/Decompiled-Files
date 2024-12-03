/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class AnnotationProvider {
    @Deprecated
    public <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }
}

