/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.diagnostics.internal.platform.monitor.gc.GCRead;
import java.time.Duration;

public class GCDetailsCalculator {
    private final double percentageOfTimeInGarbageCollection;
    private final Duration timeSpentOnCollection;
    private final long collectionsCount;
    private final Duration timeSinceLastPoll;

    public GCDetailsCalculator(GCRead previousRead, GCRead currentRead) {
        this.timeSpentOnCollection = this.calculateTimeSpentOnGC(previousRead, currentRead);
        this.collectionsCount = this.calculateCollectionsCount(previousRead, currentRead);
        this.timeSinceLastPoll = this.calculateTimeSinceLastPoll(previousRead, currentRead);
        this.percentageOfTimeInGarbageCollection = this.calculatePercentageOfTimeInGarbageCollection();
    }

    public Duration getTimeSpentOnCollection() {
        return this.timeSpentOnCollection;
    }

    public long getCollectionCount() {
        return this.collectionsCount;
    }

    public Duration getTimeSinceLastPoll() {
        return this.timeSinceLastPoll;
    }

    public double getPercentageOfTimeInGarbageCollection() {
        return this.percentageOfTimeInGarbageCollection;
    }

    private Duration calculateTimeSpentOnGC(GCRead previousRead, GCRead currentRead) {
        return currentRead.getCollectionTime().minus(previousRead.getCollectionTime());
    }

    private long calculateCollectionsCount(GCRead previousRead, GCRead currentRead) {
        return currentRead.getCollectionCount() - previousRead.getCollectionCount();
    }

    private Duration calculateTimeSinceLastPoll(GCRead previousRead, GCRead currentRead) {
        return Duration.between(previousRead.getTimestamp(), currentRead.getTimestamp());
    }

    private double calculatePercentageOfTimeInGarbageCollection() {
        return (double)this.timeSpentOnCollection.toMillis() * 100.0 / (double)this.timeSinceLastPoll.toMillis();
    }
}

