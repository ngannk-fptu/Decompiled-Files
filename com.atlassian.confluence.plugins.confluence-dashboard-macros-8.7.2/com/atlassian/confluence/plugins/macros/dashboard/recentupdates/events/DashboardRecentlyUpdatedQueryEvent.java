/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

@EventName(value="confluence.dashboard.recentlyUpdatedQuery")
public class DashboardRecentlyUpdatedQueryEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 9085348776999647387L;
    private final long durationMillis;
    private final int totalHits;

    public DashboardRecentlyUpdatedQueryEvent(Object src, long durationMillis, int totalHits) {
        super(src);
        this.durationMillis = durationMillis;
        this.totalHits = totalHits;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    public long getTotalHits() {
        return this.totalHits;
    }
}

