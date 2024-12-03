/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 */
package com.atlassian.confluence.plugins.synchrony.api.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.plugins.synchrony.api.CollaborativeEditingMode;
import com.atlassian.confluence.plugins.synchrony.api.events.CollaborativeEditingModeChangeEvent;

@EventName(value="confluence.collaborative.editing.mode.change.off")
public class CollaborativeEditingOffEvent
extends CollaborativeEditingModeChangeEvent
implements ClusterEvent {
    private static final long serialVersionUID = 3905280564537281452L;

    public CollaborativeEditingOffEvent(CollaborativeEditingMode previousMode, boolean synchronyUp, long modeDurationInSeconds) {
        super(previousMode, synchronyUp, modeDurationInSeconds);
    }

    @Override
    public CollaborativeEditingMode getNewMode() {
        return CollaborativeEditingMode.DISABLED;
    }
}

