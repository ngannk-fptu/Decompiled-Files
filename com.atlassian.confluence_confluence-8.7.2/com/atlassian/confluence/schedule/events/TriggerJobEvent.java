/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.schedule.events;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.schedule.ScheduledJobKey;

@Internal
public class TriggerJobEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -7137569061362176136L;
    private final ScheduledJobKey scheduledJobKey;
    private final boolean pauseBackgroundJobs;

    public TriggerJobEvent(Object src, ScheduledJobKey scheduledJobKey, boolean pauseBackgroundJobs) {
        super(src);
        this.scheduledJobKey = scheduledJobKey;
        this.pauseBackgroundJobs = pauseBackgroundJobs;
    }

    public ScheduledJobKey getScheduledJobKey() {
        return this.scheduledJobKey;
    }

    public boolean isPauseBackgroundJobs() {
        return this.pauseBackgroundJobs;
    }
}

