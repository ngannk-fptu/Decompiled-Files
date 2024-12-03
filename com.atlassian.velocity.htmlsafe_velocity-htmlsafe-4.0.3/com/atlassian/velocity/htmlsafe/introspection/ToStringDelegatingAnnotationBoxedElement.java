/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.util.Collection;

public final class ToStringDelegatingAnnotationBoxedElement<E>
implements AnnotationBoxedElement<E> {
    private final AnnotationBoxedElement<E> delegate;

    public ToStringDelegatingAnnotationBoxedElement(AnnotationBoxedElement<E> delegate) {
        this.delegate = (AnnotationBoxedElement)Preconditions.checkNotNull(delegate, (Object)"delegate must not be null");
    }

    @Override
    public E unbox() {
        return (E)this.delegate.unbox();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.delegate.isAnnotationPresent(annotationType);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return this.delegate.getAnnotation(annotationType);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.delegate.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.delegate.getDeclaredAnnotations();
    }

    @Override
    public Collection<Annotation> getAnnotationCollection() {
        return this.delegate.getAnnotationCollection();
    }

    @Override
    public <T extends Annotation> boolean hasAnnotation(Class<T> clazz) {
        return this.delegate.hasAnnotation(clazz);
    }

    @Override
    public Object box(Object value) {
        Object boxedValue = this.delegate.box(value);
        if (boxedValue instanceof AnnotationBoxedElement) {
            return new ToStringDelegatingAnnotationBoxedElement<E>((AnnotationBoxedElement)boxedValue);
        }
        return boxedValue;
    }

    public String toString() {
        return String.valueOf(this.delegate.unbox());
    }
}

