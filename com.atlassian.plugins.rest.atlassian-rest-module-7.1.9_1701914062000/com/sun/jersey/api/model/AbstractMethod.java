/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractResource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public abstract class AbstractMethod
implements AnnotatedElement {
    private Method method;
    private Annotation[] annotations;
    private AbstractResource resource;

    public AbstractMethod(AbstractResource resource, Method method, Annotation[] annotations) {
        this.method = method;
        this.annotations = annotations;
        this.resource = resource;
    }

    public AbstractResource getResource() {
        return this.resource;
    }

    public Method getMethod() {
        return this.method;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation a : this.annotations) {
            if (annotationType != a.annotationType()) continue;
            return (T)((Annotation)annotationType.cast(a));
        }
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return (Annotation[])this.annotations.clone();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return (Annotation[])this.annotations.clone();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.getAnnotation(annotationType) != null;
    }
}

