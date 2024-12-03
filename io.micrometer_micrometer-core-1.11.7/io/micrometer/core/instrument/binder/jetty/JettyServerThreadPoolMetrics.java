/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.thread.QueuedThreadPool
 *  org.eclipse.jetty.util.thread.ThreadPool
 *  org.eclipse.jetty.util.thread.ThreadPool$SizedThreadPool
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

public class JettyServerThreadPoolMetrics
implements MeterBinder {
    private final ThreadPool threadPool;
    private final Iterable<Tag> tags;

    public JettyServerThreadPoolMetrics(ThreadPool threadPool, Iterable<Tag> tags) {
        this.threadPool = threadPool;
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        if (this.threadPool instanceof ThreadPool.SizedThreadPool) {
            ThreadPool.SizedThreadPool sizedThreadPool = (ThreadPool.SizedThreadPool)this.threadPool;
            Gauge.builder("jetty.threads.config.min", sizedThreadPool, ThreadPool.SizedThreadPool::getMinThreads).description("The minimum number of threads in the pool").tags(this.tags).register(registry);
            Gauge.builder("jetty.threads.config.max", sizedThreadPool, ThreadPool.SizedThreadPool::getMaxThreads).description("The maximum number of threads in the pool").tags(this.tags).register(registry);
            if (this.threadPool instanceof QueuedThreadPool) {
                QueuedThreadPool queuedThreadPool = (QueuedThreadPool)this.threadPool;
                Gauge.builder("jetty.threads.busy", queuedThreadPool, QueuedThreadPool::getBusyThreads).description("The number of busy threads in the pool").tags(this.tags).register(registry);
                Gauge.builder("jetty.threads.jobs", queuedThreadPool, QueuedThreadPool::getQueueSize).description("Number of jobs queued waiting for a thread").tags(this.tags).register(registry);
            }
        }
        Gauge.builder("jetty.threads.current", this.threadPool, ThreadPool::getThreads).description("The total number of threads in the pool").tags(this.tags).register(registry);
        Gauge.builder("jetty.threads.idle", this.threadPool, ThreadPool::getIdleThreads).description("The number of idle threads in the pool").tags(this.tags).register(registry);
    }
}

