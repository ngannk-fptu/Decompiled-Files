/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.events.discarded")
public class DiscardEvent
extends BaseAnalyticEvent {
    private final int numberOfEvents;
    private final String source;

    public DiscardEvent(int numberOfEvents, String source, String pluginVersion) {
        super(pluginVersion);
        this.numberOfEvents = numberOfEvents;
        this.source = source;
    }

    public int getNumberOfEvents() {
        return this.numberOfEvents;
    }

    public String getSource() {
        return this.source;
    }
}

