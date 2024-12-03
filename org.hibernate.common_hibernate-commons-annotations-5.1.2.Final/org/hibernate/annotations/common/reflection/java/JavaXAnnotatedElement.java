/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;

abstract class JavaXAnnotatedElement
implements XAnnotatedElement {
    private final JavaReflectionManager factory;
    private final AnnotatedElement annotatedElement;

    public JavaXAnnotatedElement(AnnotatedElement annotatedElement, JavaReflectionManager factory) {
        this.factory = factory;
        this.annotatedElement = annotatedElement;
    }

    protected JavaReflectionManager getFactory() {
        return this.factory;
    }

    private AnnotationReader getAnnotationReader() {
        return this.factory.buildAnnotationReader(this.annotatedElement);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return this.getAnnotationReader().getAnnotation(annotationType);
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
        return this.getAnnotationReader().isAnnotationPresent(annotationType);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.getAnnotationReader().getAnnotations();
    }

    AnnotatedElement toAnnotatedElement() {
        return this.annotatedElement;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JavaXAnnotatedElement)) {
            return false;
        }
        JavaXAnnotatedElement other = (JavaXAnnotatedElement)obj;
        return this.annotatedElement.equals(other.toAnnotatedElement());
    }

    public int hashCode() {
        return this.annotatedElement.hashCode();
    }

    public String toString() {
        return this.annotatedElement.toString();
    }
}

