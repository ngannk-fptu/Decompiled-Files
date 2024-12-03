/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.user.remove.done")
@Internal
public class UserRemoveDoneAnalyticsEvent {
    private boolean unsynced;

    public UserRemoveDoneAnalyticsEvent(boolean unsynced) {
        this.unsynced = unsynced;
    }

    public boolean isUnsynced() {
        return this.unsynced;
    }
}

