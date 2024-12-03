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

@EventName(value="whisper.fetch.success")
public class FetchSuccessAnalyticsEvent {
    private final boolean wasModified;
    private final int numberMessages;
    private final int numberMappings;
    private final long fetchTime;
    private final long storageTime;

    public FetchSuccessAnalyticsEvent(boolean wasModified, int numberMessages, int numberMappings, long fetchTime, long storageTime) {
        this.wasModified = wasModified;
        this.numberMessages = numberMessages;
        this.numberMappings = numberMappings;
        this.fetchTime = fetchTime;
        this.storageTime = storageTime;
    }

    public boolean getWasModified() {
        return this.wasModified;
    }

    public int getNumberMessages() {
        return this.numberMessages;
    }

    public int getNumberMappings() {
        return this.numberMappings;
    }

    public long getFetchTime() {
        return this.fetchTime;
    }

    public long getStorageTime() {
        return this.storageTime;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FetchSuccessAnalyticsEvent that = (FetchSuccessAnalyticsEvent)o;
        return this.wasModified == that.wasModified && this.numberMessages == that.numberMessages && this.numberMappings == that.numberMappings && this.fetchTime == that.fetchTime && this.storageTime == that.storageTime;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.wasModified, this.numberMessages, this.numberMappings, this.fetchTime, this.storageTime});
    }
}

