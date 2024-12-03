/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.Callable;

class TimedCallable<V>
implements Callable<V> {
    private final MeterRegistry registry;
    private final Timer executionTimer;
    private final Timer idleTimer;
    private final Callable<V> callable;
    private final Timer.Sample idleSample;

    TimedCallable(MeterRegistry registry, Timer executionTimer, Timer idleTimer, Callable<V> callable) {
        this.registry = registry;
        this.executionTimer = executionTimer;
        this.idleTimer = idleTimer;
        this.callable = callable;
        this.idleSample = Timer.start(registry);
    }

    @Override
    public V call() throws Exception {
        this.idleSample.stop(this.idleTimer);
        Timer.Sample executionSample = Timer.start(this.registry);
        try {
            V v = this.callable.call();
            return v;
        }
        finally {
            executionSample.stop(this.executionTimer);
        }
    }
}

