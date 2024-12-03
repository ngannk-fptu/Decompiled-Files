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
public class ClusterUpdateCronJobScheduleEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -7592770186339219767L;
    private final ScheduledJobKey scheduledJobKey;
    private final String newCronSchedule;

    public ClusterUpdateCronJobScheduleEvent(Object src, ScheduledJobKey scheduledJobKey, String newCronSchedule) {
        super(src);
        this.scheduledJobKey = Objects.requireNonNull(scheduledJobKey);
        this.newCronSchedule = Objects.requireNonNull(newCronSchedule);
    }

    public ScheduledJobKey getScheduledJobKey() {
        return this.scheduledJobKey;
    }

    public String getNewCronSchedule() {
        return this.newCronSchedule;
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
        ClusterUpdateCronJobScheduleEvent event = (ClusterUpdateCronJobScheduleEvent)o;
        if (this.scheduledJobKey != null ? !this.scheduledJobKey.equals(event.getScheduledJobKey()) : event.getScheduledJobKey() != null) {
            return false;
        }
        return !(this.newCronSchedule != null ? !this.newCronSchedule.equals(event.getNewCronSchedule()) : event.getNewCronSchedule() != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.scheduledJobKey != null ? this.scheduledJobKey.hashCode() : 0);
        result = 29 * result + (this.newCronSchedule != null ? this.newCronSchedule.hashCode() : 0);
        return result;
    }
}

