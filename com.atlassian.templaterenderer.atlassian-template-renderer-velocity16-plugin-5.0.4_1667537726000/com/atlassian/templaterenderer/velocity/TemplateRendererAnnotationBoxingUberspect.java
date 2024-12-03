/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafeAnnotationUtils
 *  com.atlassian.velocity.htmlsafe.HtmlSafeClassAnnotator
 *  com.atlassian.velocity.htmlsafe.HtmlSafeMethodNameAnnotator
 *  com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxingUberspect
 *  com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator
 *  com.atlassian.velocity.htmlsafe.introspection.MethodAnnotatorChain
 */
package com.atlassian.templaterenderer.velocity;

import com.atlassian.templaterenderer.velocity.TemplateRendererHtmlSafeAnnotationUtils;
import com.atlassian.templaterenderer.velocity.TemplateRendererReturnValueAnnotator;
import com.atlassian.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.velocity.htmlsafe.HtmlSafeClassAnnotator;
import com.atlassian.velocity.htmlsafe.HtmlSafeMethodNameAnnotator;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxingUberspect;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotatorChain;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class TemplateRendererAnnotationBoxingUberspect
extends AnnotationBoxingUberspect {
    private static final MethodAnnotator HTML_METHOD_ANNOTATOR = new MethodAnnotatorChain(Arrays.asList(new HtmlSafeMethodNameAnnotator(), new HtmlSafeClassAnnotator()));
    private static final MethodAnnotator RETURN_VALUE_ANNOTATOR = new TemplateRendererReturnValueAnnotator();

    protected Collection<Annotation> getMethodAnnotations(Method method) {
        Collection<Annotation> returnValueAnnotations = Collections.unmodifiableCollection(RETURN_VALUE_ANNOTATOR.getAnnotationsForMethod(method));
        if (returnValueAnnotations.contains(TemplateRendererHtmlSafeAnnotationUtils.HTML_SAFE_ANNOTATION) || returnValueAnnotations.contains(HtmlSafeAnnotationUtils.HTML_SAFE_ANNOTATION)) {
            return returnValueAnnotations;
        }
        LinkedList<Annotation> htmlAnnotations = new LinkedList<Annotation>(returnValueAnnotations);
        htmlAnnotations.addAll(HTML_METHOD_ANNOTATOR.getAnnotationsForMethod(method));
        return htmlAnnotations;
    }
}

