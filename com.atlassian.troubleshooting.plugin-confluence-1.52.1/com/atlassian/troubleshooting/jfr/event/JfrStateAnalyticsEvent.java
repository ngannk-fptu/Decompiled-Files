/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.jfr.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="atst.jfr.state")
public class JfrStateAnalyticsEvent {
    private final boolean jvmCompatibility;
    private final boolean recordingRunning;
    private final boolean featureFlagEnabled;

    public JfrStateAnalyticsEvent(boolean jvmCompatibility, boolean recordingRunning, boolean featureFlagEnabled) {
        this.jvmCompatibility = jvmCompatibility;
        this.recordingRunning = recordingRunning;
        this.featureFlagEnabled = featureFlagEnabled;
    }

    public boolean isJvmCompatibility() {
        return this.jvmCompatibility;
    }

    public boolean isRecordingRunning() {
        return this.recordingRunning;
    }

    public boolean isFeatureFlagEnabled() {
        return this.featureFlagEnabled;
    }
}

