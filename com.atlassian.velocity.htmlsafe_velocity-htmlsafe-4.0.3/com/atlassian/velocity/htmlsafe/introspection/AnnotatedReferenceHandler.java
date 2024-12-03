/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public abstract class AnnotatedReferenceHandler
implements ReferenceInsertionEventHandler {
    public Object referenceInsert(String referenceName, Object referenceValue) {
        if (referenceValue instanceof AnnotationBoxedElement) {
            AnnotationBoxedElement returnValue = (AnnotationBoxedElement)referenceValue;
            Object processedValue = this.annotatedValueInsert(referenceName, returnValue.unbox(), returnValue.getAnnotationCollection());
            return returnValue.box(processedValue);
        }
        return this.annotatedValueInsert(referenceName, referenceValue, (Collection<Annotation>)ImmutableSet.of());
    }

    protected abstract Object annotatedValueInsert(String var1, Object var2, Collection<Annotation> var3);
}

