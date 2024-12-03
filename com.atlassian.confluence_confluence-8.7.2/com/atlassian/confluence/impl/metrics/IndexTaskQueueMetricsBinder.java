/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  io.micrometer.core.instrument.Gauge
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.binder.MeterBinder
 */
package com.atlassian.confluence.impl.metrics;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.metrics.CoreMetrics;
import com.atlassian.confluence.search.IndexTaskQueue;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Objects;

@Internal
final class IndexTaskQueueMetricsBinder
implements MeterBinder {
    private final IndexTaskQueue<?> indexTaskQueue;
    private final String queueName;

    public IndexTaskQueueMetricsBinder(IndexTaskQueue<?> indexTaskQueue, String queueName) {
        this.indexTaskQueue = Objects.requireNonNull(indexTaskQueue);
        this.queueName = Objects.requireNonNull(queueName);
    }

    public void bindTo(MeterRegistry registry) {
        CoreMetrics.INDEX_TASK_QUEUE_SIZE.resolve(name -> Gauge.builder((String)name, this.indexTaskQueue::getSize)).tags(new String[]{"queueName", this.queueName}).register(registry);
    }
}

