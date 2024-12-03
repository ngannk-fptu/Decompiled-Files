/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.AnnotatedElementBuilder;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExposureBuilder<T>
implements AnnotatedElementBuilder {
    private final Binder binder;
    private final Object source;
    private Key<T> key;

    public ExposureBuilder(Binder binder, Object source, Key<T> key) {
        this.binder = binder;
        this.source = source;
        this.key = key;
    }

    protected void checkNotAnnotated() {
        if (this.key.getAnnotationType() != null) {
            this.binder.addError("More than one annotation is specified for this binding.", new Object[0]);
        }
    }

    @Override
    public void annotatedWith(Class<? extends Annotation> annotationType) {
        Preconditions.checkNotNull(annotationType, (Object)"annotationType");
        this.checkNotAnnotated();
        this.key = Key.get(this.key.getTypeLiteral(), annotationType);
    }

    @Override
    public void annotatedWith(Annotation annotation) {
        Preconditions.checkNotNull((Object)annotation, (Object)"annotation");
        this.checkNotAnnotated();
        this.key = Key.get(this.key.getTypeLiteral(), annotation);
    }

    public Key<?> getKey() {
        return this.key;
    }

    public Object getSource() {
        return this.source;
    }

    public String toString() {
        return "AnnotatedElementBuilder";
    }
}

