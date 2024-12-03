/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.velocity.htmlsafe.HtmlEntities;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.velocity.htmlsafe.RawVelocityReference;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedReferenceHandler;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;

public class HtmlAnnotationEscaper
extends AnnotatedReferenceHandler {
    private static final ImmutableSet<String> SAFE_REFERENCE_NAMES = ImmutableSet.of((Object)"xHtmlContent", (Object)"body", (Object)"head");

    @Override
    protected Object annotatedValueInsert(String referenceName, Object value, Collection<Annotation> annotations) {
        if (value == null) {
            return null;
        }
        if (this.shouldEscape(referenceName, value, annotations)) {
            return HtmlEntities.encode(value.toString());
        }
        return value;
    }

    protected boolean shouldEscape(String referenceName, Object value, Collection<Annotation> annotations) {
        String baseReference;
        RawVelocityReference reference = new RawVelocityReference(referenceName);
        if (reference.isScalar() && (HtmlSafeAnnotationUtils.endsWithHtmlIgnoreCase(baseReference = reference.getBaseReferenceName()) || SAFE_REFERENCE_NAMES.contains((Object)baseReference))) {
            return false;
        }
        return !HtmlSafeAnnotationUtils.hasHtmlSafeToStringMethod(value) && !HtmlSafeAnnotationUtils.containsAnnotationOfType(annotations, HtmlSafe.class);
    }
}

