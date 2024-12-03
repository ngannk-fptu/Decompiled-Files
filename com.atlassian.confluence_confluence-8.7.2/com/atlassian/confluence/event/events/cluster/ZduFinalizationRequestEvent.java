/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class ZduFinalizationRequestEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 8502194367574305845L;

    public ZduFinalizationRequestEvent(Object source) {
        super(source);
    }
}

