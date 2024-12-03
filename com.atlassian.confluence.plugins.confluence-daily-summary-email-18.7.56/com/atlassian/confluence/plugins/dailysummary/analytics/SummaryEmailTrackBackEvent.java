/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.dailysummary.analytics;

public class SummaryEmailTrackBackEvent {
    private final String schedule;

    public SummaryEmailTrackBackEvent(String schedule) {
        this.schedule = schedule;
    }

    public String getSchedule() {
        return this.schedule;
    }
}

