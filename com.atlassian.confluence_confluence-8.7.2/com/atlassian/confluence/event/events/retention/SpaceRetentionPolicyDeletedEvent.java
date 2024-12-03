/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 */
package com.atlassian.confluence.event.events.retention;

import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyEvent;
import com.atlassian.confluence.spaces.Space;

public class SpaceRetentionPolicyDeletedEvent
extends SpaceRetentionPolicyEvent {
    public SpaceRetentionPolicyDeletedEvent(SpaceRetentionPolicy oldPolicy, Space space) {
        super((RetentionPolicy)oldPolicy, null, space);
    }
}

