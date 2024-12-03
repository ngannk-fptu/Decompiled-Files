/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric
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
package com.atlassian.confluence.internal.diagnostics.ipd.index.items;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueType;
import com.atlassian.confluence.internal.diagnostics.ipd.index.items.DefaultIndexQueueItemsService;
import com.atlassian.confluence.internal.diagnostics.ipd.index.items.IndexQueueItemType;
import com.atlassian.confluence.internal.diagnostics.ipd.index.items.ItemOperationType;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJob;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.MetricTag;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class IndexQueueItemsIpdJob
implements IpdJob {
    private final Map<IndexQueueType, IpdCustomMetric<IndexQueueItemType>> addedItemsIndexQueueMetrics = new EnumMap<IndexQueueType, IpdCustomMetric<IndexQueueItemType>>(IndexQueueType.class);
    private final Map<IndexQueueType, IpdCustomMetric<IndexQueueItemType>> processedItemsIndexQueueMetrics = new EnumMap<IndexQueueType, IpdCustomMetric<IndexQueueItemType>>(IndexQueueType.class);
    private final DefaultIndexQueueItemsService indexQueueItemsService;
    private final EventPublisher eventPublisher;

    public IndexQueueItemsIpdJob(IpdJobRunner ipdJobRunner, DefaultIndexQueueItemsService indexQueueItemsService, IpdMainRegistry ipdMainRegistry, EventPublisher eventPublisher) {
        Objects.requireNonNull(ipdJobRunner).register((IpdJob)this);
        this.indexQueueItemsService = Objects.requireNonNull(indexQueueItemsService);
        IpdMetricRegistry registry = Objects.requireNonNull(ipdMainRegistry).createRegistry("index.queue.items", new MetricTag.RequiredMetricTag[0]);
        this.eventPublisher = eventPublisher;
        this.initializeMetrics(registry);
    }

    private void initializeMetrics(IpdMetricRegistry registry) {
        for (IndexQueueType indexQueueType : IndexQueueType.values()) {
            MetricTag.RequiredMetricTag metricTag = MetricTag.of((String)"queueName", (String)indexQueueType.name().toLowerCase());
            this.addedItemsIndexQueueMetrics.put(indexQueueType, (IpdCustomMetric<IndexQueueItemType>)registry.customMetric("added", IndexQueueItemType.class, new MetricTag.RequiredMetricTag[]{metricTag}));
            this.processedItemsIndexQueueMetrics.put(indexQueueType, (IpdCustomMetric<IndexQueueItemType>)registry.customMetric("processed", IndexQueueItemType.class, new MetricTag.RequiredMetricTag[]{metricTag}));
        }
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onDarkFeatureEnabledEvent(SiteDarkFeatureEnabledEvent event) {
        this.handleDarkFeatureEnabledEvent(event);
    }

    @EventListener
    public void onClusteredDarkFeatureEnabledEvent(ClusterEventWrapper clusterEvent) {
        Event event = clusterEvent.getEvent();
        if (event instanceof SiteDarkFeatureEnabledEvent) {
            this.handleDarkFeatureEnabledEvent((SiteDarkFeatureEnabledEvent)event);
        }
    }

    private void handleDarkFeatureEnabledEvent(SiteDarkFeatureEnabledEvent event) {
        if (Objects.equals(event.getFeatureKey(), "confluence.in.product.diagnostics.deny")) {
            this.indexQueueItemsService.resetMetrics();
            this.resetJmxMetrics();
        }
    }

    private void resetJmxMetrics() {
        for (IndexQueueType indexQueueType : IndexQueueType.values()) {
            this.addedItemsIndexQueueMetrics.get((Object)indexQueueType).update(bean -> {
                bean.setValue(0L);
                bean.setTotal(0L);
            });
            this.processedItemsIndexQueueMetrics.get((Object)indexQueueType).update(bean -> {
                bean.setValue(0L);
                bean.setTotal(0L);
            });
        }
    }

    public void runJob() {
        for (IndexQueueType indexQueueType : IndexQueueType.values()) {
            this.updateIndexQueueMetrics(indexQueueType);
        }
    }

    private void updateIndexQueueMetrics(IndexQueueType indexQueueType) {
        long totalAddedItemsCount = this.indexQueueItemsService.getQueueItemsAdded(indexQueueType);
        long addedItemsPerInterval = this.indexQueueItemsService.calculateItemsCountPerInterval(totalAddedItemsCount, indexQueueType, ItemOperationType.ADDED);
        long totalProcessedItemsCount = this.indexQueueItemsService.getQueueItemsProcessed(indexQueueType);
        long processedItemsPerInterval = this.indexQueueItemsService.calculateItemsCountPerInterval(totalProcessedItemsCount, indexQueueType, ItemOperationType.PROCESSED);
        this.addedItemsIndexQueueMetrics.get((Object)indexQueueType).update(bean -> {
            bean.setValue(addedItemsPerInterval);
            bean.setTotal(totalAddedItemsCount);
        });
        this.processedItemsIndexQueueMetrics.get((Object)indexQueueType).update(bean -> {
            bean.setValue(processedItemsPerInterval);
            bean.setTotal(totalProcessedItemsCount);
        });
    }
}

