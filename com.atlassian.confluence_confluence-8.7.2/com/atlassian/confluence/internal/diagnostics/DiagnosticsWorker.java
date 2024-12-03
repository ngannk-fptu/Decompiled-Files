/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.internal.diagnostics.DiagnosticsInfo;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

class DiagnosticsWorker<T>
implements Runnable {
    private final Map<T, DiagnosticsInfo> activities;
    private final BiConsumer<T, DiagnosticsInfo> alerter;
    private final Duration timeLimit;

    DiagnosticsWorker(Map<T, DiagnosticsInfo> activities, BiConsumer<T, DiagnosticsInfo> alerter, Duration timeLimit) {
        this.activities = activities;
        this.alerter = alerter;
        this.timeLimit = timeLimit;
    }

    @Override
    public void run() {
        while (true) {
            long now = System.nanoTime();
            long minSleepNanos = this.timeLimit.toNanos();
            for (Map.Entry<T, DiagnosticsInfo> entry : this.activities.entrySet()) {
                T activity = entry.getKey();
                DiagnosticsInfo info = entry.getValue();
                if (info.shouldAlert()) {
                    this.alerter.accept(activity, info);
                    this.activities.put(activity, info.next());
                    continue;
                }
                long sleepNanosForCurrentActivity = info.getTimeLimit().toNanos() - (now - info.getStartTimeNanos());
                minSleepNanos = Math.min(minSleepNanos, sleepNanosForCurrentActivity);
            }
            try {
                TimeUnit.NANOSECONDS.sleep(minSleepNanos);
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }
}

