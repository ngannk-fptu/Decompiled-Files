/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Severity
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GCDetailsCalculator;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class HighGCTimeDetails {
    private final Severity severity;
    private final String garbageCollectorName;
    private final Instant timestamp;
    private final Duration timeWindow;
    private final Duration garbageCollectionTime;
    private final long garbageCollectionCount;

    public HighGCTimeDetails(Severity severity, String garbageCollectorName, Instant timestamp, Duration timeWindow, Duration garbageCollectionTime, long garbageCollectionCount) {
        this.severity = severity;
        this.garbageCollectorName = garbageCollectorName;
        this.timestamp = timestamp;
        this.timeWindow = timeWindow;
        this.garbageCollectionTime = garbageCollectionTime;
        this.garbageCollectionCount = garbageCollectionCount;
    }

    public static HighGCTimeAlertBuilder builder() {
        return new HighGCTimeAlertBuilder();
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public String getGarbageCollectorName() {
        return this.garbageCollectorName;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public Duration getTimeWindow() {
        return this.timeWindow;
    }

    public Duration getGarbageCollectionTime() {
        return this.garbageCollectionTime;
    }

    public long getGarbageCollectionCount() {
        return this.garbageCollectionCount;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HighGCTimeDetails that = (HighGCTimeDetails)o;
        return this.garbageCollectionCount == that.garbageCollectionCount && this.severity == that.severity && this.garbageCollectorName.equals(that.garbageCollectorName) && this.timestamp.equals(that.timestamp) && this.timeWindow.equals(that.timeWindow) && this.garbageCollectionTime.equals(that.garbageCollectionTime);
    }

    public int hashCode() {
        return Objects.hash(this.severity, this.garbageCollectorName, this.timestamp, this.timeWindow, this.garbageCollectionTime, this.garbageCollectionCount);
    }

    public String toString() {
        return "HighGCTimeDetails{severity=" + this.severity + ", garbageCollectorName='" + this.garbageCollectorName + '\'' + ", timestamp=" + this.timestamp + ", timeWindow=" + this.timeWindow + ", garbageCollectionTime=" + this.garbageCollectionTime + ", garbageCollectionCount=" + this.garbageCollectionCount + '}';
    }

    public static final class HighGCTimeAlertBuilder {
        private Severity severity;
        private String garbageCollectorName;
        private Instant timestamp;
        private Duration timeWindow;
        private Duration garbageCollectionTime;
        private Long garbageCollectionCount;

        private HighGCTimeAlertBuilder() {
        }

        public HighGCTimeAlertBuilder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public HighGCTimeAlertBuilder garbageCollectorName(String garbageCollectorName) {
            this.garbageCollectorName = garbageCollectorName;
            return this;
        }

        public HighGCTimeAlertBuilder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public HighGCTimeAlertBuilder timeWindow(Duration timeWindow) {
            this.timeWindow = timeWindow;
            return this;
        }

        public HighGCTimeAlertBuilder garbageCollectionTime(Duration garbageCollectionTime) {
            this.garbageCollectionTime = garbageCollectionTime;
            return this;
        }

        public HighGCTimeAlertBuilder garbageCollectionCount(long garbageCollectionCount) {
            this.garbageCollectionCount = garbageCollectionCount;
            return this;
        }

        public HighGCTimeAlertBuilder addAlertInfo(GCDetailsCalculator gcDetailsCalculator) {
            this.garbageCollectionTime = gcDetailsCalculator.getTimeSpentOnCollection();
            this.timeWindow = gcDetailsCalculator.getTimeSinceLastPoll();
            this.garbageCollectionCount = gcDetailsCalculator.getCollectionCount();
            return this;
        }

        public HighGCTimeDetails build() {
            return new HighGCTimeDetails(Objects.requireNonNull(this.severity), Objects.requireNonNull(this.garbageCollectorName), Objects.requireNonNull(this.timestamp), Objects.requireNonNull(this.timeWindow), Objects.requireNonNull(this.garbageCollectionTime), Objects.requireNonNull(this.garbageCollectionCount));
        }
    }
}

