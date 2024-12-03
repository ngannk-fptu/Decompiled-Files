/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.dashboard;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.dashboard.actions.DashboardAction;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;

@EventName(value="confluence.dashboard.view")
public class DashboardViewEvent
extends ConfluenceEvent
implements Viewed {
    private static final long serialVersionUID = -5844442962833326626L;

    public DashboardViewEvent(DashboardAction dashboard) {
        super(dashboard);
    }
}

