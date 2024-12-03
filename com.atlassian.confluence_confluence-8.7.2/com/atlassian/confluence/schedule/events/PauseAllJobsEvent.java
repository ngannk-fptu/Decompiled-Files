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
public class PauseAllJobsEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -7636899174419496541L;

    public PauseAllJobsEvent(Object src) {
        super(src);
    }
}

