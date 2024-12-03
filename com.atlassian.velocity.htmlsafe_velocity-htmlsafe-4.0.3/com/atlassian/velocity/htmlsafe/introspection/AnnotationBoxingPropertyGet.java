/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.apache.velocity.util.introspection.VelPropertyGet
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.apache.velocity.util.introspection.VelPropertyGet;

final class AnnotationBoxingPropertyGet
implements VelPropertyGet {
    private final VelPropertyGet delegate;
    private final Collection<Annotation> annotations;

    public AnnotationBoxingPropertyGet(VelPropertyGet delegate, Collection<Annotation> annotations) {
        this.delegate = (VelPropertyGet)Preconditions.checkNotNull((Object)delegate, (Object)"delegate must not be null");
        this.annotations = ImmutableSet.copyOf(annotations);
    }

    public Object invoke(Object o) throws Exception {
        Object obj = this.delegate.invoke(o);
        if (obj == null) {
            return null;
        }
        return new AnnotatedValue<Object>(obj, this.annotations);
    }

    public boolean isCacheable() {
        return this.delegate.isCacheable();
    }

    public String getMethodName() {
        return this.delegate.getMethodName();
    }
}

