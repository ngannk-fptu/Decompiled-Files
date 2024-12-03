/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.events.DashboardRecentlyUpdatedViewEvent;

@EventName(value="confluence.dashboard.popularTabView")
public class DashboardPopularTabViewEvent
extends DashboardRecentlyUpdatedViewEvent {
    private static final long serialVersionUID = 558012042607675420L;

    public DashboardPopularTabViewEvent(Object src) {
        super(src, "popular");
    }

    public DashboardPopularTabViewEvent(Object src, String tabName, long durationMillis) {
        super(src, tabName, durationMillis);
    }
}

