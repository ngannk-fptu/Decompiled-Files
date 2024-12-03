/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.stp.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.troubleshooting.stp.events.EventStage;

public class StpLogScannerEvent {
    private final EventStage stage;
    private final String scanId;
    private final long duration;
    private final int resultSize;

    public StpLogScannerEvent(EventStage stage, String scanId) {
        this(stage, scanId, 0L, 0);
    }

    public StpLogScannerEvent(EventStage stage, String scanId, long duration, int resultSize) {
        this.stage = stage;
        this.scanId = scanId;
        this.duration = duration;
        this.resultSize = resultSize;
    }

    public String getScanId() {
        return this.scanId;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getResultSize() {
        return this.resultSize;
    }

    public EventStage getStage() {
        return this.stage;
    }

    @EventName
    public String eventName() {
        return "stp.log.analyzer." + this.getStage().toString().toLowerCase();
    }
}

