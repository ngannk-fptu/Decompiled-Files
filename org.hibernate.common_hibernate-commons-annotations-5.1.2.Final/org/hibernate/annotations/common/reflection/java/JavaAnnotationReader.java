/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.hibernate.annotations.common.reflection.AnnotationReader;

final class JavaAnnotationReader
implements AnnotationReader {
    protected final AnnotatedElement element;

    public JavaAnnotationReader(AnnotatedElement el) {
        this.element = el;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return this.element.getAnnotation(annotationType);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        return this.element.isAnnotationPresent(annotationType);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.element.getAnnotations();
    }
}

