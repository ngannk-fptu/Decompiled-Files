/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

public class ClusterPanicEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -6285918041051795686L;
    private String description;

    public ClusterPanicEvent(Object src, String description) {
        super(src);
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}

