/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model;

public class SynchronyRowsCount {
    private final long numberOfEvents;
    private final long numberOfSnapshots;

    public SynchronyRowsCount(long numberOfEvents, long numberOfSnapshots) {
        this.numberOfEvents = numberOfEvents;
        this.numberOfSnapshots = numberOfSnapshots;
    }

    public long getNumberOfEvents() {
        return this.numberOfEvents;
    }

    public long getNumberOfSnapshots() {
        return this.numberOfSnapshots;
    }
}

