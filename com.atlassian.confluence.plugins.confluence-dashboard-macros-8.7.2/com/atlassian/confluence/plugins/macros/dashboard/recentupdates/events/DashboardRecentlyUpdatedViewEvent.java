/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.types.Viewed
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;

@EventName(value="confluence.dashboard.tabView")
public class DashboardRecentlyUpdatedViewEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = -2450162588649863081L;
    private String tabName;
    private long durationMillis = -1L;

    public DashboardRecentlyUpdatedViewEvent(Object src, String tabName) {
        super(src);
        this.tabName = tabName;
    }

    public DashboardRecentlyUpdatedViewEvent(Object src, String tabName, long durationMillis) {
        super(src);
        this.tabName = tabName;
        this.durationMillis = durationMillis;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    public String getTabName() {
        return this.tabName;
    }
}

