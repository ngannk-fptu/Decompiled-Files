/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;
import com.atlassian.velocity.htmlsafe.introspection.BoxingStrategy;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

public interface AnnotationBoxedElement<E>
extends BoxedValue<E>,
AnnotatedElement,
BoxingStrategy {
    public Collection<Annotation> getAnnotationCollection();

    public <T extends Annotation> boolean hasAnnotation(Class<T> var1);
}

