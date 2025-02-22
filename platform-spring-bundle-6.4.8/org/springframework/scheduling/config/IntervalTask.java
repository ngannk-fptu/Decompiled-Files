/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import org.springframework.scheduling.config.Task;

public class IntervalTask
extends Task {
    private final long interval;
    private final long initialDelay;

    public IntervalTask(Runnable runnable, long interval, long initialDelay) {
        super(runnable);
        this.interval = interval;
        this.initialDelay = initialDelay;
    }

    public IntervalTask(Runnable runnable, long interval) {
        this(runnable, interval, 0L);
    }

    public long getInterval() {
        return this.interval;
    }

    public long getInitialDelay() {
        return this.initialDelay;
    }
}

