/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.api.model;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class HealthCheckUserSettings {
    @JsonProperty
    private final SupportHealthStatus.Severity severityThresholdForNotifications;
    @JsonProperty
    private final boolean watching;
    @JsonProperty
    private final boolean canWatch;

    @JsonCreator
    private HealthCheckUserSettings() {
        this(HealthCheckUserSettings.builder());
    }

    private HealthCheckUserSettings(Builder builder) {
        this.severityThresholdForNotifications = builder.severityThreshold;
        this.watching = builder.watching;
        this.canWatch = builder.canWatch;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(HealthCheckUserSettings original) {
        return HealthCheckUserSettings.builder().severityThreshold(original.severityThresholdForNotifications);
    }

    public SupportHealthStatus.Severity getSeverityThresholdForNotifications() {
        return this.severityThresholdForNotifications;
    }

    public boolean isWatching() {
        return this.watching;
    }

    public boolean isCanWatch() {
        return this.canWatch;
    }

    public static class Builder {
        private SupportHealthStatus.Severity severityThreshold = SupportHealthStatus.Severity.UNDEFINED;
        private boolean watching;
        private boolean canWatch;

        private Builder() {
        }

        public Builder severityThreshold(SupportHealthStatus.Severity severityThreshold) {
            this.severityThreshold = severityThreshold;
            return this;
        }

        public Builder watching(boolean watching) {
            this.watching = watching;
            return this;
        }

        public Builder canWatch(boolean mailConfigured) {
            this.canWatch = mailConfigured;
            return this;
        }

        public HealthCheckUserSettings build() {
            return new HealthCheckUserSettings(this);
        }
    }
}

