/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.schedule.ScheduledJobKey;
import java.util.Objects;

@Internal
public class ClusterUpdateSimpleJobScheduleEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 9014603671156788784L;
    private final ScheduledJobKey scheduledJobKey;
    private final long newRepeatInterval;

    public ClusterUpdateSimpleJobScheduleEvent(Object src, ScheduledJobKey scheduledJobKey, long newRepeatInterval) {
        super(src);
        this.scheduledJobKey = Objects.requireNonNull(scheduledJobKey);
        this.newRepeatInterval = newRepeatInterval;
    }

    public ScheduledJobKey getScheduledJobKey() {
        return this.scheduledJobKey;
    }

    public long getNewRepeatInterval() {
        return this.newRepeatInterval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ClusterUpdateSimpleJobScheduleEvent event = (ClusterUpdateSimpleJobScheduleEvent)o;
        if (this.scheduledJobKey != null ? !this.scheduledJobKey.equals(event.getScheduledJobKey()) : event.getScheduledJobKey() != null) {
            return false;
        }
        return this.newRepeatInterval == event.getNewRepeatInterval();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.scheduledJobKey != null ? this.scheduledJobKey.hashCode() : 0);
        result = 29 * result + Long.hashCode(this.newRepeatInterval);
        return result;
    }
}

