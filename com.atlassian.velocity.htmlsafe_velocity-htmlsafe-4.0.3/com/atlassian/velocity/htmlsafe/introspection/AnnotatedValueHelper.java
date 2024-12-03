/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;
import java.lang.annotation.Annotation;
import java.util.Collection;

interface AnnotatedValueHelper
extends BoxedValue {
    public Object get();

    public Collection<Annotation> getAnnotations();

    public AnnotationBoxedElement getBoxedValueWithInheritedAnnotations();

    public Class getTargetClass();

    public boolean isBoxedValue();
}

