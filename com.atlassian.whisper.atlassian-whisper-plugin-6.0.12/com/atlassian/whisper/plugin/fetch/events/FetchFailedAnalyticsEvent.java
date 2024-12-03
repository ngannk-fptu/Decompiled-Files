/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.base.Objects
 */
package com.atlassian.whisper.plugin.fetch.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.google.common.base.Objects;

@EventName(value="whisper.fetch.failed")
public class FetchFailedAnalyticsEvent {
    private final String type;

    FetchFailedAnalyticsEvent(String type) {
        this.type = type;
    }

    public static FetchFailedAnalyticsEvent fetch() {
        return new FetchFailedAnalyticsEvent("fetch");
    }

    public static FetchFailedAnalyticsEvent parse() {
        return new FetchFailedAnalyticsEvent("parse");
    }

    public static FetchFailedAnalyticsEvent general() {
        return new FetchFailedAnalyticsEvent("general");
    }

    public static FetchFailedAnalyticsEvent expired() {
        return new FetchFailedAnalyticsEvent("expired");
    }

    public static FetchFailedAnalyticsEvent signaturesVerification() {
        return new FetchFailedAnalyticsEvent("signaturesVerification");
    }

    public String getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FetchFailedAnalyticsEvent that = (FetchFailedAnalyticsEvent)o;
        return Objects.equal((Object)this.type, (Object)that.type);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.type});
    }
}

