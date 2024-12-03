/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.synchrony.api.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@EventName(value="confluence.synchrony.restarted")
public class SynchronyRestartedEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 1441101018662022254L;
    private final boolean isSuccessful;

    public SynchronyRestartedEvent(Object src, boolean isSuccessful) {
        super(src);
        this.isSuccessful = isSuccessful;
    }

    public boolean isSuccessful() {
        return this.isSuccessful;
    }
}

