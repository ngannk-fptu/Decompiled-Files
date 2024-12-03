/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlAnnotationEscaper
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.velocity.htmlsafe;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.confluence.velocity.htmlsafe.RawVelocityReference;
import com.atlassian.velocity.htmlsafe.HtmlAnnotationEscaper;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

@Deprecated
public class ConfluenceHtmlAnnotationEscaper
extends HtmlAnnotationEscaper {
    private static final Set<String> SAFE_REFERENCE_NAMES = ImmutableSet.of((Object)"xHtmlContent", (Object)"body", (Object)"head");

    protected boolean shouldEscape(String referenceName, Object value, Collection<Annotation> annotations) {
        String baseReference;
        RawVelocityReference reference = new RawVelocityReference(referenceName);
        if (reference.isScalar() && ((baseReference = reference.getBaseReferenceName()).toLowerCase().endsWith("html") || SAFE_REFERENCE_NAMES.contains(baseReference))) {
            return false;
        }
        return !HtmlSafeAnnotationUtils.hasHtmlSafeToStringMethod(value) && !HtmlSafeAnnotationUtils.containsAnnotationOfType(annotations, HtmlSafe.class) && !HtmlSafeAnnotationUtils.containsAnnotationOfType(annotations, com.atlassian.velocity.htmlsafe.HtmlSafe.class);
    }
}

