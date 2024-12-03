/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.velocity.htmlsafe.HtmlSafeClassAnnotator;
import com.atlassian.velocity.htmlsafe.HtmlSafeMethodNameAnnotator;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxingUberspect;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotatorChain;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class HtmlSafeAnnotationBoxingUberspect
extends AnnotationBoxingUberspect {
    private static final MethodAnnotator HTML_METHOD_ANNOTATOR = new MethodAnnotatorChain(Arrays.asList(new HtmlSafeMethodNameAnnotator(), new HtmlSafeClassAnnotator()));

    @Override
    protected Collection<Annotation> getMethodAnnotations(Method method) {
        Collection<Annotation> returnValueAnnotations = super.getMethodAnnotations(method);
        if (returnValueAnnotations.contains(HtmlSafeAnnotationUtils.HTML_SAFE_ANNOTATION)) {
            return returnValueAnnotations;
        }
        return ImmutableSet.copyOf(HTML_METHOD_ANNOTATOR.getAnnotationsForMethod(method));
    }
}

