/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public final class MethodAnnotatorChain
implements MethodAnnotator {
    private final MethodAnnotator[] ANNOTATOR_CHAIN;

    public MethodAnnotatorChain(List<MethodAnnotator> annotators) {
        Preconditions.checkNotNull(annotators, (Object)"annotators must not be null");
        for (MethodAnnotator annotator : this.ANNOTATOR_CHAIN = annotators.toArray(new MethodAnnotator[annotators.size()])) {
            Preconditions.checkNotNull((Object)annotator, (Object)"null annotator provided in the list");
        }
    }

    @Override
    public Collection<Annotation> getAnnotationsForMethod(Method method) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (MethodAnnotator annotator : this.ANNOTATOR_CHAIN) {
            builder.addAll(annotator.getAnnotationsForMethod(method));
        }
        return builder.build();
    }
}

