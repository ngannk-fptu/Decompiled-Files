/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import com.hazelcast.scheduledexecutor.impl.TaskLifecycleListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskStatisticsImpl
implements ScheduledTaskStatistics,
TaskLifecycleListener,
Versioned {
    private static final TimeUnit MEASUREMENT_UNIT = TimeUnit.NANOSECONDS;
    private long runs;
    private long lastRunDuration;
    private long lastIdleDuration;
    private long totalRunDuration;
    private long totalIdleDuration;
    private transient long createdAt;
    private transient long firstRunStart;
    private transient long lastRunStart;
    private transient long lastRunEnd;

    public ScheduledTaskStatisticsImpl() {
    }

    public ScheduledTaskStatisticsImpl(ScheduledTaskStatisticsImpl copy) {
        this(copy.createdAt, copy.getTotalRuns(), copy.firstRunStart, copy.lastRunStart, copy.lastRunEnd, copy.getLastIdleTime(MEASUREMENT_UNIT), copy.getTotalRunTime(MEASUREMENT_UNIT), copy.getTotalIdleTime(MEASUREMENT_UNIT), copy.getLastRunDuration(MEASUREMENT_UNIT));
    }

    public ScheduledTaskStatisticsImpl(long runs, long lastIdleTimeNanos, long totalRunTimeNanos, long totalIdleTimeNanos, long lastRunDuration) {
        this.runs = runs;
        this.lastIdleDuration = lastIdleTimeNanos;
        this.totalRunDuration = totalRunTimeNanos;
        this.totalIdleDuration = totalIdleTimeNanos;
        this.lastRunDuration = lastRunDuration;
    }

    ScheduledTaskStatisticsImpl(long createdAt, long runs, long firstRunStartNanos, long lastRunStartNanos, long lastRunEndNanos, long lastIdleTimeNanos, long totalRunTimeNanos, long totalIdleTimeNanos, long lastRunDurationNanos) {
        this.createdAt = createdAt;
        this.runs = runs;
        this.firstRunStart = firstRunStartNanos;
        this.lastRunStart = lastRunStartNanos;
        this.lastRunEnd = lastRunEndNanos;
        this.lastRunDuration = lastRunDurationNanos;
        this.lastIdleDuration = lastIdleTimeNanos;
        this.totalRunDuration = totalRunTimeNanos;
        this.totalIdleDuration = totalIdleTimeNanos;
    }

    @Override
    public long getTotalRuns() {
        return this.runs;
    }

    @Override
    public long getLastRunDuration(TimeUnit unit) {
        return unit.convert(this.lastRunDuration, MEASUREMENT_UNIT);
    }

    @Override
    public long getLastIdleTime(TimeUnit unit) {
        return unit.convert(this.lastIdleDuration, MEASUREMENT_UNIT);
    }

    @Override
    public long getTotalIdleTime(TimeUnit unit) {
        return unit.convert(this.totalIdleDuration, MEASUREMENT_UNIT);
    }

    @Override
    public long getTotalRunTime(TimeUnit unit) {
        return unit.convert(this.totalRunDuration, MEASUREMENT_UNIT);
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 16;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.runs);
        out.writeLong(this.lastIdleDuration);
        out.writeLong(this.totalIdleDuration);
        out.writeLong(this.totalRunDuration);
        out.writeLong(this.lastRunDuration);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.runs = in.readLong();
        this.lastIdleDuration = in.readLong();
        this.totalIdleDuration = in.readLong();
        this.totalRunDuration = in.readLong();
        this.lastRunDuration = in.readLong();
    }

    @Override
    public void onInit() {
        this.createdAt = System.nanoTime();
    }

    @Override
    public void onBeforeRun() {
        long now;
        this.lastRunStart = now = System.nanoTime();
        this.lastIdleDuration = now - (this.lastRunEnd != 0L ? this.lastRunEnd : this.createdAt);
        this.totalIdleDuration += this.lastIdleDuration;
        if (this.firstRunStart == 0L) {
            this.firstRunStart = this.lastRunStart;
        }
    }

    @Override
    public void onAfterRun() {
        long now;
        this.lastRunEnd = now = System.nanoTime();
        this.lastRunDuration = this.lastRunEnd - this.lastRunStart;
        ++this.runs;
        this.totalRunDuration += this.lastRunDuration;
    }

    public ScheduledTaskStatisticsImpl snapshot() {
        return new ScheduledTaskStatisticsImpl(this);
    }

    public String toString() {
        return "ScheduledTaskStatisticsImpl{runs=" + this.runs + ", lastIdleDuration=" + this.lastIdleDuration + ", totalRunDuration=" + this.totalRunDuration + ", totalIdleDuration=" + this.totalIdleDuration + ", lastRunDuration=" + this.lastRunDuration + '}';
    }
}

