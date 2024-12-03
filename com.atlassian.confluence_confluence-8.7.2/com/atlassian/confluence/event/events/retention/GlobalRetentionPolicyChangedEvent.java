/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 */
package com.atlassian.confluence.event.events.retention;

import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.event.events.retention.RetentionPolicyChangedEvent;

public class GlobalRetentionPolicyChangedEvent
extends RetentionPolicyChangedEvent {
    public GlobalRetentionPolicyChangedEvent(GlobalRetentionPolicy oldPolicy, GlobalRetentionPolicy newPolicy) {
        super((RetentionPolicy)oldPolicy, (RetentionPolicy)newPolicy);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

