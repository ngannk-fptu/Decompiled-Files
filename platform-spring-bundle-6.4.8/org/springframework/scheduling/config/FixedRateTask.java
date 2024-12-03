/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import org.springframework.scheduling.config.IntervalTask;

public class FixedRateTask
extends IntervalTask {
    public FixedRateTask(Runnable runnable, long interval, long initialDelay) {
        super(runnable, interval, initialDelay);
    }
}

