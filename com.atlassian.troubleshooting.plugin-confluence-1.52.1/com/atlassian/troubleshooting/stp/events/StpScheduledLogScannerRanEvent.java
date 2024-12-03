/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.stp.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="stp.log.analyzer.periodic.scan.saved")
public class StpScheduledLogScannerRanEvent {
    private final String time;
    private final String frequency;
    private final boolean enabled;

    public StpScheduledLogScannerRanEvent(boolean enabled, String time, String frequency) {
        this.enabled = enabled;
        this.time = time;
        this.frequency = frequency;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public String getTime() {
        return this.time;
    }

    public String getFrequency() {
        return this.frequency;
    }
}

