/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

public class ClusterReindexRequiredEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 423882848717042058L;

    public ClusterReindexRequiredEvent(String reason) {
        super(reason);
    }
}

