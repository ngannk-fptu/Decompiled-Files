/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.SingleThreadEventExecutor
 */
package io.micrometer.core.instrument.binder.netty4;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.netty4.NettyMeters;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;

public class NettyEventExecutorMetrics
implements MeterBinder {
    private final Iterable<EventExecutor> eventExecutors;

    public NettyEventExecutorMetrics(Iterable<EventExecutor> eventExecutors) {
        this.eventExecutors = eventExecutors;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.eventExecutors.forEach(eventExecutor -> {
            if (eventExecutor instanceof SingleThreadEventExecutor) {
                SingleThreadEventExecutor singleThreadEventExecutor = (SingleThreadEventExecutor)eventExecutor;
                Gauge.builder(NettyMeters.EVENT_EXECUTOR_TASKS_PENDING.getName(), () -> ((SingleThreadEventExecutor)singleThreadEventExecutor).pendingTasks()).tag(NettyMeters.EventExecutorTasksPendingKeyNames.NAME.asString(), singleThreadEventExecutor.threadProperties().name()).register(registry);
            }
        });
    }
}

