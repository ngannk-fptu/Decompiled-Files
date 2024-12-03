/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 */
package com.atlassian.velocity.htmlsafe;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public final class IdentityReferenceInsertionHandler
implements ReferenceInsertionEventHandler {
    public Object referenceInsert(String reference, Object value) {
        return value;
    }
}

