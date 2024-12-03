/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.internal.TimedRunnable;
import java.util.concurrent.Executor;

public class TimedExecutor
implements Executor {
    private final MeterRegistry registry;
    private final Executor delegate;
    private final Timer executionTimer;
    private final Timer idleTimer;

    public TimedExecutor(MeterRegistry registry, Executor delegate, String executorName, String metricPrefix, Iterable<Tag> tags) {
        this.registry = registry;
        this.delegate = delegate;
        Tags finalTags = Tags.concat(tags, "name", executorName);
        this.executionTimer = registry.timer(metricPrefix + "executor.execution", finalTags);
        this.idleTimer = registry.timer(metricPrefix + "executor.idle", finalTags);
    }

    @Override
    public void execute(Runnable command) {
        this.delegate.execute(new TimedRunnable(this.registry, this.executionTimer, this.idleTimer, command));
    }
}

