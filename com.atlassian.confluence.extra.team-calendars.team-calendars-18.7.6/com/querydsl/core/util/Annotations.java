/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class Annotations
implements AnnotatedElement {
    private final Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();

    public Annotations(AnnotatedElement ... elements) {
        for (AnnotatedElement element : elements) {
            this.addAnnotations(element);
        }
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return (T)this.annotations.get(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.annotations.values().toArray(new Annotation[this.annotations.values().size()]);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return this.annotations.containsKey(annotationClass);
    }

    public void addAnnotation(@Nullable Annotation annotation) {
        if (annotation != null) {
            this.annotations.put(annotation.annotationType(), annotation);
        }
    }

    public void addAnnotations(AnnotatedElement element) {
        for (Annotation annotation : element.getAnnotations()) {
            this.annotations.put(annotation.annotationType(), annotation);
        }
    }
}

