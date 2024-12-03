/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.util.profiling.MetricTag
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index;

import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueSizeMetric;
import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueSizeService;
import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueType;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdValueMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.util.profiling.MetricTag;
import java.util.EnumMap;
import java.util.Objects;

public class IndexQueueSizeIpdJob
implements IpdJob {
    private final EnumMap<IndexQueueType, IpdValueMetric> queueSizeMetrics = new EnumMap(IndexQueueType.class);
    private final IndexQueueSizeService indexQueueSizeService;

    public IndexQueueSizeIpdJob(IpdJobRunner ipdJobRunner, IndexQueueSizeService indexQueueSizeService, IpdMainRegistry ipdMainRegistry) {
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.indexQueueSizeService = Objects.requireNonNull(indexQueueSizeService);
        IpdMetricRegistry registry = Objects.requireNonNull(ipdMainRegistry).createRegistry("index.queue", new MetricTag.RequiredMetricTag[0]);
        for (IndexQueueType indexQueueType : IndexQueueType.values()) {
            this.queueSizeMetrics.put(indexQueueType, registry.valueMetric("size", new MetricTag.RequiredMetricTag[]{MetricTag.of((String)"queueName", (String)indexQueueType.name().toLowerCase())}));
        }
    }

    public void runJob() {
        IndexQueueSizeMetric metric = this.indexQueueSizeService.getIndexQueueSizeMetric();
        for (IndexQueueType indexQueueType : IndexQueueType.values()) {
            this.queueSizeMetrics.get((Object)indexQueueType).update(Long.valueOf(metric.getQueueSize(indexQueueType)));
        }
    }
}

