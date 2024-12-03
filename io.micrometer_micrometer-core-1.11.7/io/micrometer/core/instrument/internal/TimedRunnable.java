/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

class TimedRunnable
implements Runnable {
    private final MeterRegistry registry;
    private final Timer executionTimer;
    private final Timer idleTimer;
    private final Runnable command;
    private final Timer.Sample idleSample;

    TimedRunnable(MeterRegistry registry, Timer executionTimer, Timer idleTimer, Runnable command) {
        this.registry = registry;
        this.executionTimer = executionTimer;
        this.idleTimer = idleTimer;
        this.command = command;
        this.idleSample = Timer.start(registry);
    }

    @Override
    public void run() {
        this.idleSample.stop(this.idleTimer);
        Timer.Sample executionSample = Timer.start(this.registry);
        try {
            this.command.run();
        }
        finally {
            executionSample.stop(this.executionTimer);
        }
    }
}

