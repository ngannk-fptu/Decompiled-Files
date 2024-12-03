/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.diagnostics;

import java.time.Duration;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

class DiagnosticsInfo {
    private final Thread worker;
    private final long startTime;
    private final String username;
    private final long timeLimit;

    DiagnosticsInfo(Thread worker, @Nullable String username, Duration timeLimit) {
        this(worker, username, System.nanoTime(), timeLimit.toNanos());
    }

    private DiagnosticsInfo(Thread worker, @Nullable String username, long startTimeNanos, long timeLimit) {
        this.worker = worker;
        this.username = username;
        this.startTime = startTimeNanos;
        this.timeLimit = timeLimit;
    }

    public Thread getWorkerThread() {
        return this.worker;
    }

    public long getStartTimeNanos() {
        return this.startTime;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(this.username);
    }

    public boolean shouldAlert() {
        return this.getActualTime().toNanos() > this.timeLimit;
    }

    public DiagnosticsInfo next() {
        return new DiagnosticsInfo(this.worker, this.username, System.nanoTime(), 2L * this.timeLimit);
    }

    public Duration getTimeLimit() {
        return Duration.ofNanos(this.timeLimit);
    }

    public Duration getActualTime() {
        long now = System.nanoTime();
        return Duration.ofNanos(now - this.getStartTimeNanos());
    }
}

