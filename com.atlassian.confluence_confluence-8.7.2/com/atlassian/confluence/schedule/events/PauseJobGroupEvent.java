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

@Internal
public class PauseJobGroupEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 7465621301206163226L;
    private final String jobGroupName;

    public PauseJobGroupEvent(Object src, String quartzJobGroupNameOrAtlassianSchedulerJobRunnerKey) {
        super(src);
        this.jobGroupName = quartzJobGroupNameOrAtlassianSchedulerJobRunnerKey;
    }

    public String getJobGroupName() {
        return this.jobGroupName;
    }
}

