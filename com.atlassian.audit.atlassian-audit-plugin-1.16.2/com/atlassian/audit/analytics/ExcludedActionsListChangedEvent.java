/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.summary.deny.list.changed")
public class ExcludedActionsListChangedEvent
extends BaseAnalyticEvent {
    private final int oldCount;
    private final int newCount;

    public ExcludedActionsListChangedEvent(int oldCount, int newCount, String pluginVersion) {
        super(pluginVersion);
        this.oldCount = oldCount;
        this.newCount = newCount;
    }

    public int getOldCount() {
        return this.oldCount;
    }

    public int getNewCount() {
        return this.newCount;
    }
}

