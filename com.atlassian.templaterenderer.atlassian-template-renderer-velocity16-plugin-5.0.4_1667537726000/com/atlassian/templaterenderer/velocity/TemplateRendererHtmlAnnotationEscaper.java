/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.annotations.HtmlSafe
 *  com.atlassian.velocity.htmlsafe.HtmlAnnotationEscaper
 */
package com.atlassian.templaterenderer.velocity;

import com.atlassian.templaterenderer.annotations.HtmlSafe;
import com.atlassian.templaterenderer.velocity.TemplateRendererHtmlSafeAnnotationUtils;
import com.atlassian.velocity.htmlsafe.HtmlAnnotationEscaper;
import java.lang.annotation.Annotation;
import java.util.Collection;

public class TemplateRendererHtmlAnnotationEscaper
extends HtmlAnnotationEscaper {
    protected boolean shouldEscape(String referenceName, Object value, Collection<Annotation> annotations) {
        return !TemplateRendererHtmlSafeAnnotationUtils.hasHtmlSafeToStringMethod(value) && !TemplateRendererHtmlSafeAnnotationUtils.containsAnnotationOfType(annotations, HtmlSafe.class) && super.shouldEscape(referenceName, value, annotations);
    }
}

