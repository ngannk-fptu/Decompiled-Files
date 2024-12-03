/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.healthcheck.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.troubleshooting.healthcheck.util.SupportHealthCheckUtils;

public class HealthcheckScheduledFinishedEvent {
    private final String completeKey;
    private final boolean healthy;
    private final String failureReason;
    private final int severity;
    private final String pluginVersion;

    public HealthcheckScheduledFinishedEvent(String completeKey, boolean healthy, String failureReason, int severity, String pluginVersion) {
        this.completeKey = completeKey;
        this.healthy = healthy;
        this.failureReason = failureReason;
        this.severity = severity;
        this.pluginVersion = pluginVersion;
    }

    @EventName
    public String eventName() {
        String healthCheckName = SupportHealthCheckUtils.getCompactKey(this.completeKey).replace("HealthCheck", "").replace("Check", "");
        return "healthchecks.check." + healthCheckName + (this.healthy ? ".pass" : ".fail") + ".done.scheduled";
    }

    public boolean isHealthy() {
        return this.healthy;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public int getSeverity() {
        return this.severity;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }
}

