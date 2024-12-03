/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.apache.velocity.util.introspection.VelMethod
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.apache.velocity.util.introspection.VelMethod;

final class AnnotationBoxingMethod
implements VelMethod {
    private final VelMethod delegate;
    private final Collection<Annotation> returnValAnnotations;

    AnnotationBoxingMethod(VelMethod delegateMethod, Collection<Annotation> annotations) {
        this.delegate = (VelMethod)Preconditions.checkNotNull((Object)delegateMethod, (Object)"degateMethod must not be null");
        this.returnValAnnotations = ImmutableSet.copyOf(annotations);
    }

    public Object invoke(Object o, Object[] params) throws Exception {
        Object obj = this.delegate.invoke(o, params);
        return obj == null ? null : new AnnotatedValue<Object>(obj, this.returnValAnnotations);
    }

    public boolean isCacheable() {
        return this.delegate.isCacheable();
    }

    public String getMethodName() {
        return this.delegate.getMethodName();
    }

    public Class<?> getReturnType() {
        return this.delegate.getReturnType();
    }
}

