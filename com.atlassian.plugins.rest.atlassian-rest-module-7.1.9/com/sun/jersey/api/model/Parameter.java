/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.spi.container.ParamQualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public class Parameter
implements AnnotatedElement {
    private final Annotation[] annotations;
    private final Annotation annotation;
    private final Source source;
    private final String sourceName;
    private final boolean encoded;
    private final String defaultValue;
    private final Type type;
    private final Class<?> clazz;

    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz) {
        this(as, a, source, sourceName, type, clazz, false, null);
    }

    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz, boolean encoded) {
        this(as, a, source, sourceName, type, clazz, encoded, null);
    }

    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz, String defaultValue) {
        this(as, a, source, sourceName, type, clazz, false, defaultValue);
    }

    public Parameter(Annotation[] as, Annotation a, Source source, String sourceName, Type type, Class<?> clazz, boolean encoded, String defaultValue) {
        this.annotations = as;
        this.annotation = a;
        this.source = source;
        this.sourceName = sourceName;
        this.type = type;
        this.clazz = clazz;
        this.encoded = encoded;
        this.defaultValue = defaultValue;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public Source getSource() {
        return this.source;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public boolean isEncoded() {
        return this.encoded;
    }

    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public Class<?> getParameterClass() {
        return this.clazz;
    }

    public Type getParameterType() {
        return this.type;
    }

    public boolean isQualified() {
        for (Annotation a : this.getAnnotations()) {
            if (!a.annotationType().isAnnotationPresent(ParamQualifier.class)) continue;
            return true;
        }
        return false;
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

    public static enum Source {
        ENTITY,
        QUERY,
        MATRIX,
        PATH,
        COOKIE,
        HEADER,
        CONTEXT,
        FORM,
        UNKNOWN;

    }
}

