/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.internal;

import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.internal.AsynchronousEventResolver;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

public final class AnnotationAsynchronousEventResolver
implements AsynchronousEventResolver {
    private final Class annotationClass;

    AnnotationAsynchronousEventResolver() {
        this(AsynchronousPreferred.class);
    }

    AnnotationAsynchronousEventResolver(Class annotationClass) {
        this.annotationClass = (Class)Preconditions.checkNotNull((Object)annotationClass);
    }

    @Override
    public boolean isAsynchronousEvent(@Nonnull Object event) {
        return Preconditions.checkNotNull((Object)event).getClass().getAnnotation(this.annotationClass) != null;
    }
}

