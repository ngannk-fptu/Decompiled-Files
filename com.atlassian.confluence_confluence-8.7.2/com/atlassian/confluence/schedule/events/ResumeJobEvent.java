/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.schedule.events;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.schedule.ScheduledJobKey;

@Internal
public class ResumeJobEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -3600694782826208840L;
    private final ScheduledJobKey scheduledJobKey;

    public ResumeJobEvent(Object src, ScheduledJobKey scheduledJobKey) {
        super(src);
        this.scheduledJobKey = scheduledJobKey;
    }

    public ScheduledJobKey getScheduledJobKey() {
        return this.scheduledJobKey;
    }
}

