/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public class HtmlSafeMethodNameAnnotator
implements MethodAnnotator {
    @Override
    public Collection<Annotation> getAnnotationsForMethod(Method method) {
        String methodName = method.getName();
        if (HtmlSafeAnnotationUtils.endsWithHtmlIgnoreCase(methodName) || methodName.startsWith("render") || methodName.startsWith("getRender")) {
            return HtmlSafeAnnotationUtils.HTML_SAFE_ANNOTATION_COLLECTION;
        }
        return ImmutableSet.of();
    }
}

