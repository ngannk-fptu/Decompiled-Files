/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

public class MonitoringAnalytics {
    private MonitoringAnalytics() {
    }

    @AsynchronousPreferred
    @EventName(value="confluence.monitoring.ipd.monitoring.toggled")
    public static class IpdMonitoringToggledAnalyticsEvent
    extends AbstractMonitoringAnalytics {
        public IpdMonitoringToggledAnalyticsEvent(boolean isToggleEnabled) {
            super(isToggleEnabled);
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.monitoring.app.monitoring.toggled")
    public static class AppMonitoringToggledAnalyticsEvent
    extends AbstractMonitoringAnalytics {
        public AppMonitoringToggledAnalyticsEvent(boolean isToggleEnabled) {
            super(isToggleEnabled);
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.monitoring.jmx.toggled")
    public static class JmxToggledAnalyticsEvent
    extends AbstractMonitoringAnalytics {
        public JmxToggledAnalyticsEvent(boolean isToggleEnabled) {
            super(isToggleEnabled);
        }
    }

    private static abstract class AbstractMonitoringAnalytics {
        private final boolean isToggleEnabled;

        public AbstractMonitoringAnalytics(boolean isToggleEnabled) {
            this.isToggleEnabled = isToggleEnabled;
        }

        public boolean isToggleEnabled() {
            return this.isToggleEnabled;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AbstractMonitoringAnalytics)) {
                return false;
            }
            AbstractMonitoringAnalytics that = (AbstractMonitoringAnalytics)o;
            return this.isToggleEnabled == that.isToggleEnabled;
        }

        public int hashCode() {
            return this.isToggleEnabled ? 1 : 0;
        }
    }
}

