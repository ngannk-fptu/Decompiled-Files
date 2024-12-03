/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.thread.QueuedThreadPool
 *  org.eclipse.jetty.util.thread.ThreadPool
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jetty.JettyServerThreadPoolMetrics;
import java.util.concurrent.BlockingQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

public class InstrumentedQueuedThreadPool
extends QueuedThreadPool {
    private final MeterRegistry registry;
    private final Iterable<Tag> tags;

    public InstrumentedQueuedThreadPool(MeterRegistry registry, Iterable<Tag> tags) {
        this.registry = registry;
        this.tags = tags;
    }

    public InstrumentedQueuedThreadPool(MeterRegistry registry, Iterable<Tag> tags, int maxThreads) {
        super(maxThreads);
        this.registry = registry;
        this.tags = tags;
    }

    public InstrumentedQueuedThreadPool(MeterRegistry registry, Iterable<Tag> tags, int maxThreads, int minThreads) {
        super(maxThreads, minThreads);
        this.registry = registry;
        this.tags = tags;
    }

    public InstrumentedQueuedThreadPool(MeterRegistry registry, Iterable<Tag> tags, int maxThreads, int minThreads, int idleTimeout) {
        super(maxThreads, minThreads, idleTimeout);
        this.registry = registry;
        this.tags = tags;
    }

    public InstrumentedQueuedThreadPool(MeterRegistry registry, Iterable<Tag> tags, int maxThreads, int minThreads, int idleTimeout, BlockingQueue<Runnable> queue) {
        super(maxThreads, minThreads, idleTimeout, queue);
        this.registry = registry;
        this.tags = tags;
    }

    protected void doStart() throws Exception {
        super.doStart();
        JettyServerThreadPoolMetrics threadPoolMetrics = new JettyServerThreadPoolMetrics((ThreadPool)this, this.tags);
        threadPoolMetrics.bindTo(this.registry);
    }
}

