/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.util.Clock;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MigrationStats {
    @Probe
    private final AtomicLong lastRepartitionTime = new AtomicLong();
    @Probe
    private final AtomicLong completedMigrations = new AtomicLong();
    @Probe
    private final AtomicLong totalCompletedMigrations = new AtomicLong();
    @Probe
    private final AtomicLong elapsedMigrationOperationTime = new AtomicLong();
    @Probe
    private final AtomicLong elapsedDestinationCommitTime = new AtomicLong();
    @Probe
    private final AtomicLong elapsedMigrationTime = new AtomicLong();
    @Probe
    private final AtomicLong totalElapsedMigrationOperationTime = new AtomicLong();
    @Probe
    private final AtomicLong totalElapsedDestinationCommitTime = new AtomicLong();
    @Probe
    private final AtomicLong totalElapsedMigrationTime = new AtomicLong();

    void markNewRepartition() {
        this.lastRepartitionTime.set(Clock.currentTimeMillis());
        this.elapsedMigrationOperationTime.set(0L);
        this.elapsedDestinationCommitTime.set(0L);
        this.elapsedMigrationTime.set(0L);
        this.completedMigrations.set(0L);
    }

    void incrementCompletedMigrations() {
        this.completedMigrations.incrementAndGet();
        this.totalCompletedMigrations.incrementAndGet();
    }

    void recordMigrationOperationTime(long time) {
        this.elapsedMigrationOperationTime.addAndGet(time);
        this.totalElapsedMigrationOperationTime.addAndGet(time);
    }

    void recordDestinationCommitTime(long time) {
        this.elapsedDestinationCommitTime.addAndGet(time);
        this.totalElapsedDestinationCommitTime.addAndGet(time);
    }

    void recordMigrationTaskTime(long time) {
        this.elapsedMigrationTime.addAndGet(time);
        this.totalElapsedMigrationTime.addAndGet(time);
    }

    public Date getLastRepartitionTime() {
        return new Date(this.lastRepartitionTime.get());
    }

    public long getCompletedMigrations() {
        return this.completedMigrations.get();
    }

    public long getTotalCompletedMigrations() {
        return this.totalCompletedMigrations.get();
    }

    public long getElapsedMigrationOperationTime() {
        return TimeUnit.NANOSECONDS.toMillis(this.elapsedMigrationOperationTime.get());
    }

    public long getElapsedDestinationCommitTime() {
        return TimeUnit.NANOSECONDS.toMillis(this.elapsedDestinationCommitTime.get());
    }

    public long getElapsedMigrationTime() {
        return TimeUnit.NANOSECONDS.toMillis(this.elapsedMigrationTime.get());
    }

    public long getTotalElapsedMigrationOperationTime() {
        return TimeUnit.NANOSECONDS.toMillis(this.totalElapsedMigrationOperationTime.get());
    }

    public long getTotalElapsedDestinationCommitTime() {
        return TimeUnit.NANOSECONDS.toMillis(this.totalElapsedDestinationCommitTime.get());
    }

    public long getTotalElapsedMigrationTime() {
        return TimeUnit.NANOSECONDS.toMillis(this.totalElapsedMigrationTime.get());
    }

    public String formatToString(boolean detailed) {
        StringBuilder s = new StringBuilder();
        s.append("lastRepartitionTime=").append(this.getLastRepartitionTime()).append(", completedMigrations=").append(this.getCompletedMigrations()).append(", totalCompletedMigrations=").append(this.getTotalCompletedMigrations());
        if (detailed) {
            s.append(", elapsedMigrationOperationTime=").append(this.getElapsedMigrationOperationTime()).append("ms").append(", totalElapsedMigrationOperationTime=").append(this.getTotalElapsedMigrationOperationTime()).append("ms").append(", elapsedDestinationCommitTime=").append(this.getElapsedDestinationCommitTime()).append("ms").append(", totalElapsedDestinationCommitTime=").append(this.getTotalElapsedDestinationCommitTime()).append("ms");
        }
        s.append(", elapsedMigrationTime=").append(this.getElapsedMigrationTime()).append("ms").append(", totalElapsedMigrationTime=").append(this.getTotalElapsedMigrationTime()).append("ms");
        return s.toString();
    }

    public String toString() {
        return "MigrationStats{" + this.formatToString(true) + "}";
    }
}

