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
public class ClusterDisableJobEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 7741085649760790713L;
    private final ScheduledJobKey scheduledJobKey;

    public ClusterDisableJobEvent(Object src, ScheduledJobKey scheduledJobKey) {
        super(src);
        this.scheduledJobKey = Objects.requireNonNull(scheduledJobKey);
    }

    public ScheduledJobKey getScheduledJobKey() {
        return this.scheduledJobKey;
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
        ClusterDisableJobEvent event = (ClusterDisableJobEvent)o;
        return !(this.scheduledJobKey != null ? !this.scheduledJobKey.equals(event.getScheduledJobKey()) : event.getScheduledJobKey() != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.scheduledJobKey != null ? this.scheduledJobKey.hashCode() : 0);
        return result;
    }
}

