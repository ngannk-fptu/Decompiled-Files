/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.binder.jersey.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public interface AnnotationFinder {
    public static final AnnotationFinder DEFAULT = new AnnotationFinder(){};

    default public <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        Annotation[] anns;
        for (Annotation ann : anns = annotatedElement.getDeclaredAnnotations()) {
            if (ann.annotationType() != annotationType) continue;
            return (A)ann;
        }
        return null;
    }
}

