/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MethodArgument
implements AnnotatedElement {
    private final int index;
    private final Class<?> parameterType;
    private final Annotation[] annotations;

    public MethodArgument(int index, Class<?> parameterType, Annotation[] annotations) {
        this.index = index;
        this.parameterType = parameterType;
        this.annotations = annotations;
    }

    public int getIndex() {
        return this.index;
    }

    public Class<?> getParameterType() {
        return this.parameterType;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation annotation : this.annotations) {
            if (annotationType != annotation.annotationType()) continue;
            return (T)((Annotation)annotationType.cast(annotation));
        }
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.getAnnotationsArrayCopy();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotationsArrayCopy();
    }

    private Annotation[] getAnnotationsArrayCopy() {
        Annotation[] annotations = new Annotation[this.annotations.length];
        System.arraycopy(this.annotations, 0, annotations, 0, annotations.length);
        return annotations;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        for (Annotation annotation : this.annotations) {
            if (annotationType != annotation.annotationType()) continue;
            return true;
        }
        return false;
    }
}

