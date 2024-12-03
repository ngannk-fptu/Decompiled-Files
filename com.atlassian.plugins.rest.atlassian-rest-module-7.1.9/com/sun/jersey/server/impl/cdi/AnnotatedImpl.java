/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.spi.Annotated
 */
package com.sun.jersey.server.impl.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import javax.enterprise.inject.spi.Annotated;

public class AnnotatedImpl
implements Annotated {
    private Type baseType;
    private Set<Type> typeClosure;
    private Set<Annotation> annotations;

    public AnnotatedImpl(Type baseType, Set<Type> typeClosure, Set<Annotation> annotations) {
        this.baseType = baseType;
        this.typeClosure = Collections.unmodifiableSet(typeClosure);
        this.annotations = Collections.unmodifiableSet(annotations);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation a : this.annotations) {
            if (!annotationType.isInstance(a)) continue;
            return (T)((Annotation)annotationType.cast(a));
        }
        return null;
    }

    public Set<Annotation> getAnnotations() {
        return this.annotations;
    }

    public Type getBaseType() {
        return this.baseType;
    }

    public Set<Type> getTypeClosure() {
        return this.typeClosure;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.getAnnotation(annotationType) != null;
    }
}

