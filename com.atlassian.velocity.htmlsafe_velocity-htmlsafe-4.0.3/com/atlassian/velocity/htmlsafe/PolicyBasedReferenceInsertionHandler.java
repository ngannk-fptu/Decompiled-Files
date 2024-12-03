/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.util.ContextAware
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.velocity.htmlsafe.ReferenceInsertionPolicy;
import com.google.common.base.Preconditions;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.ContextAware;

public final class PolicyBasedReferenceInsertionHandler
implements ReferenceInsertionEventHandler,
ContextAware {
    private Context context;
    private final ReferenceInsertionPolicy insertionPolicy;

    public PolicyBasedReferenceInsertionHandler(ReferenceInsertionPolicy insertionPolicy) {
        this.insertionPolicy = (ReferenceInsertionPolicy)Preconditions.checkNotNull((Object)insertionPolicy, (Object)"insertionPolicy must not be null");
    }

    public Object referenceInsert(String reference, Object value) {
        return this.insertionPolicy.getReferenceInsertionEventHandler(this.context).referenceInsert(reference, value);
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

