/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.introspection.HtmlSafeAnnotationBoxingUberspect
 *  com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.aop.support.AopUtils
 */
package com.atlassian.confluence.velocity.introspection;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.confluence.velocity.introspection.ReturnValueAnnotator;
import com.atlassian.velocity.htmlsafe.introspection.HtmlSafeAnnotationBoxingUberspect;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import org.springframework.aop.support.AopUtils;

public class ConfluenceAnnotationBoxingUberspect
extends HtmlSafeAnnotationBoxingUberspect {
    private static final MethodAnnotator RETURN_VALUE_ANNOTATOR = new ReturnValueAnnotator();

    protected Collection<Annotation> getMethodAnnotations(Method method) {
        Collection returnValueAnnotations = super.getMethodAnnotations(method);
        if (returnValueAnnotations.contains(HtmlSafeAnnotationUtils.HTML_SAFE_ANNOTATION) || returnValueAnnotations.contains(HtmlSafeAnnotationUtils.ATLASSIAN_HTML_SAFE_ANNOTATION)) {
            return returnValueAnnotations;
        }
        return ImmutableSet.copyOf((Collection)RETURN_VALUE_ANNOTATOR.getAnnotationsForMethod(method));
    }

    protected Class getClassForTargetObject(Object targetObject) {
        if (AopUtils.isAopProxy((Object)targetObject)) {
            return AopUtils.getTargetClass((Object)targetObject);
        }
        return super.getClassForTargetObject(targetObject);
    }
}

