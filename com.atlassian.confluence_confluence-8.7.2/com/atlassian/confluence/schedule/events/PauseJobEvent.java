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
public class PauseJobEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 3472115535623117769L;
    private final ScheduledJobKey scheduledJobKey;

    public PauseJobEvent(Object src, ScheduledJobKey scheduledJobKey) {
        super(src);
        this.scheduledJobKey = scheduledJobKey;
    }

    public ScheduledJobKey getScheduledJobKey() {
        return this.scheduledJobKey;
    }
}

