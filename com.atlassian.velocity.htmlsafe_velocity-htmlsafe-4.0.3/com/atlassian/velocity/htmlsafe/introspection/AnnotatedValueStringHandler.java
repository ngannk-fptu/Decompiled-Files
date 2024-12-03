/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.atlassian.velocity.htmlsafe.introspection.ToStringDelegatingAnnotationBoxedElement;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

final class AnnotatedValueStringHandler
implements ReferenceInsertionEventHandler {
    AnnotatedValueStringHandler() {
    }

    public Object referenceInsert(String reference, Object value) {
        if (value instanceof AnnotationBoxedElement) {
            return new ToStringDelegatingAnnotationBoxedElement((AnnotationBoxedElement)value);
        }
        return value;
    }
}

