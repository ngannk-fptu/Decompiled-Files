/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.ratelimiting.internal.analytics.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsUserSettingsEvent;

public class AnalyticsUserSettingsDeletedEvent
extends AnalyticsUserSettingsEvent {
    public AnalyticsUserSettingsDeletedEvent(UserRateLimitSettings settings) {
        super(settings);
    }

    @Override
    @EventName
    public String getAnalyticsEventName() {
        return "rate_limit.user.settings.deleted";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalyticsUserSettingsDeletedEvent)) {
            return false;
        }
        AnalyticsUserSettingsDeletedEvent other = (AnalyticsUserSettingsDeletedEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof AnalyticsUserSettingsDeletedEvent;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    public String toString() {
        return "AnalyticsUserSettingsDeletedEvent()";
    }
}

