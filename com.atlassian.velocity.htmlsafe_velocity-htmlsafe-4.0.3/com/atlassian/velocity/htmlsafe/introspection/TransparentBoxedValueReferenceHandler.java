/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.BoxingUtils;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public final class TransparentBoxedValueReferenceHandler
implements ReferenceInsertionEventHandler {
    public Object referenceInsert(String name, Object value) {
        return BoxingUtils.unboxObject(value);
    }
}

