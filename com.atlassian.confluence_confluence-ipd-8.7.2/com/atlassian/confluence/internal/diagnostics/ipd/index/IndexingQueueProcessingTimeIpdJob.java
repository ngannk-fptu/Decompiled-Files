/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.confluence.event.events.search.EdgeIndexQueueFlushCompleteEvent
 *  com.atlassian.confluence.event.events.search.IndexQueueFlushCompleteEvent
 *  com.atlassian.confluence.internal.diagnostics.ipd.ConfluenceIpdMainRegistryConfiguration
 *  com.atlassian.confluence.internal.search.LuceneIncrementalIndexManager
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJob
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.MetricTag
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.search.EdgeIndexQueueFlushCompleteEvent;
import com.atlassian.confluence.event.events.search.IndexQueueFlushCompleteEvent;
import com.atlassian.confluence.internal.diagnostics.ipd.ConfluenceIpdMainRegistryConfiguration;
import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueType;
import com.atlassian.confluence.internal.search.LuceneIncrementalIndexManager;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.wrapper.IpdValueAndStatsMetricWrapper;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.MetricTag;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class IndexingQueueProcessingTimeIpdJob
implements IpdJob {
    private final EventPublisher eventPublisher;
    private final ConfluenceIpdMainRegistryConfiguration ipdMainRegistryConfiguration;
    private final EnumMap<IndexQueueType, AtomicLong> processingTime = new EnumMap(IndexQueueType.class);
    private final EnumMap<IndexQueueType, IpdValueAndStatsMetricWrapper> metrics = new EnumMap(IndexQueueType.class);

    public IndexingQueueProcessingTimeIpdJob(IpdJobRunner ipdJobRunner, EventPublisher eventPublisher, IpdMainRegistry ipdMainRegistry, ConfluenceIpdMainRegistryConfiguration ipdMainRegistryConfiguration) {
        this.ipdMainRegistryConfiguration = ipdMainRegistryConfiguration;
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.eventPublisher = eventPublisher;
        IpdMetricRegistry registry = ipdMainRegistry.createRegistry("index.queue.batches", new MetricTag.RequiredMetricTag[0]);
        Arrays.stream(IndexQueueType.values()).forEach(indexQueueType -> {
            IpdValueAndStatsMetricWrapper metric = registry.valueAndStatsMetric("processingTimeMillis", new MetricTag.RequiredMetricTag[]{MetricTag.of((String)"queueName", (String)indexQueueType.toString().toLowerCase())});
            this.metrics.put((IndexQueueType)((Object)indexQueueType), metric);
        });
        Arrays.stream(IndexQueueType.values()).forEach(val -> this.processingTime.put((IndexQueueType)((Object)val), new AtomicLong()));
    }

    @PostConstruct
    public void registerForEvents() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregisterForEvents() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onIndexQueueFlushCompleteEvent(IndexQueueFlushCompleteEvent event) {
        if (!this.isIpdEnabled()) {
            return;
        }
        Object source = event.getSource();
        if (source instanceof LuceneIncrementalIndexManager) {
            SearchIndex targetIndex = ((LuceneIncrementalIndexManager)source).getTargetIndex();
            long elapsedMilliseconds = event.getFlushStatistics().getElapsedMilliseconds();
            if (targetIndex == SearchIndex.CHANGE) {
                this.processingTime.get((Object)IndexQueueType.CHANGE).addAndGet(elapsedMilliseconds);
            } else if (targetIndex == SearchIndex.CONTENT) {
                this.processingTime.get((Object)IndexQueueType.MAIN).addAndGet(elapsedMilliseconds);
            }
        }
    }

    @EventListener
    public void onEdgeIndexQueueFlushCompleteEvent(EdgeIndexQueueFlushCompleteEvent event) {
        if (!this.isIpdEnabled()) {
            return;
        }
        this.processingTime.get((Object)IndexQueueType.EDGE).addAndGet(event.getFlushStatistics().getElapsedMilliseconds());
    }

    @EventListener
    public void onDarkFeatureEnabledEvent(SiteDarkFeatureEnabledEvent event) {
        this.resetMetricsWhenIpdDisabled(event);
    }

    @EventListener
    public void onSiteDarkFeatureEnabledEventInCluster(ClusterEventWrapper clusterEvent) {
        Event event = clusterEvent.getEvent();
        if (event instanceof SiteDarkFeatureEnabledEvent) {
            this.resetMetricsWhenIpdDisabled((SiteDarkFeatureEnabledEvent)event);
        }
    }

    private boolean isIpdEnabled() {
        return this.ipdMainRegistryConfiguration.isIpdEnabled();
    }

    private void resetMetricsWhenIpdDisabled(SiteDarkFeatureEnabledEvent event) {
        if (Objects.equals(event.getFeatureKey(), "confluence.in.product.diagnostics.deny")) {
            this.processingTime.values().forEach(time -> time.set(0L));
        }
    }

    public void runJob() {
        this.processingTime.forEach((key, value) -> this.metrics.get(key).update(Long.valueOf(value.longValue())));
        this.processingTime.values().forEach(atomicLong -> atomicLong.set(0L));
    }
}

