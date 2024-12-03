/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.annotations.ReturnValueAnnotation
 *  com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator
 */
package com.atlassian.confluence.velocity.introspection;

import com.atlassian.velocity.htmlsafe.annotations.ReturnValueAnnotation;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

final class ReturnValueAnnotator
implements MethodAnnotator {
    ReturnValueAnnotator() {
    }

    public Collection<Annotation> getAnnotationsForMethod(Method method) {
        HashSet<Annotation> returnValueAnnotations = new HashSet<Annotation>();
        for (Annotation annotation : method.getAnnotations()) {
            if (!annotation.annotationType().isAnnotationPresent(com.atlassian.confluence.velocity.annotations.ReturnValueAnnotation.class) && !annotation.annotationType().isAnnotationPresent(ReturnValueAnnotation.class)) continue;
            returnValueAnnotations.add(annotation);
        }
        return Collections.unmodifiableCollection(returnValueAnnotations);
    }
}

