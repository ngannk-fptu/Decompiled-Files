/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.apache.velocity.context.Context
 */
package com.atlassian.velocity.htmlsafe;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;

public interface ReferenceInsertionPolicy {
    public ReferenceInsertionEventHandler getReferenceInsertionEventHandler(Context var1);
}

