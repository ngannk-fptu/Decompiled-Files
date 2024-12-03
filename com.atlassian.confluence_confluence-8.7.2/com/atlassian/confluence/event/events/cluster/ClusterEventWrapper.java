/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.event.events.cluster;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.event.Event;
import java.io.Serializable;

public class ClusterEventWrapper
extends ConfluenceEvent
implements Serializable {
    protected Event event;

    public ClusterEventWrapper(Object src, Event event) {
        super(src);
        this.event = event;
    }

    public Event getEvent() {
        return this.event;
    }
}

