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
import java.util.Objects;

@AsynchronousPreferred
@EventName(value="confluence.monitoring.statistic.event")
@Internal
public class MonitoringStatsAnalyticEvent {
    private final boolean jmxEnabled;
    private final boolean appMonitoringEnabled;
    private final boolean ipdMonitoringEnabled;

    public MonitoringStatsAnalyticEvent(boolean jmxEnabled, boolean appMonitoringEnabled, boolean ipdMonitoringEnabled) {
        this.jmxEnabled = jmxEnabled;
        this.appMonitoringEnabled = appMonitoringEnabled;
        this.ipdMonitoringEnabled = ipdMonitoringEnabled;
    }

    public boolean isJmxEnabled() {
        return this.jmxEnabled;
    }

    public boolean isAppMonitoringEnabled() {
        return this.appMonitoringEnabled;
    }

    public boolean isIpdMonitoringEnabled() {
        return this.ipdMonitoringEnabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MonitoringStatsAnalyticEvent that = (MonitoringStatsAnalyticEvent)o;
        return this.jmxEnabled == that.jmxEnabled && this.appMonitoringEnabled == that.appMonitoringEnabled && this.ipdMonitoringEnabled == that.ipdMonitoringEnabled;
    }

    public int hashCode() {
        return Objects.hash(this.jmxEnabled, this.appMonitoringEnabled, this.ipdMonitoringEnabled);
    }
}

