/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.search.EdgeIndexQueueFlushCompleteEvent
 *  com.atlassian.confluence.event.events.search.IndexQueueFlushCompleteEvent
 *  com.atlassian.confluence.internal.diagnostics.ipd.ConfluenceIpdMainRegistryConfiguration
 *  com.atlassian.confluence.internal.search.LuceneIncrementalIndexManager
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.internal.diagnostics.ipd.index.items;

import com.atlassian.confluence.event.events.search.EdgeIndexQueueFlushCompleteEvent;
import com.atlassian.confluence.event.events.search.IndexQueueFlushCompleteEvent;
import com.atlassian.confluence.internal.diagnostics.ipd.ConfluenceIpdMainRegistryConfiguration;
import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueSizeService;
import com.atlassian.confluence.internal.diagnostics.ipd.index.IndexQueueType;
import com.atlassian.confluence.internal.diagnostics.ipd.index.items.IndexQueueItemsService;
import com.atlassian.confluence.internal.diagnostics.ipd.index.items.ItemOperationType;
import com.atlassian.confluence.internal.search.LuceneIncrementalIndexManager;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class DefaultIndexQueueItemsService
implements IndexQueueItemsService {
    private final IndexQueueSizeService indexQueueSizeService;
    private final EventPublisher eventPublisher;
    private final ConfluenceIpdMainRegistryConfiguration ipdMainRegistryConfiguration;
    private final Map<IndexQueueType, QueueItems> queueItemsMap = new EnumMap<IndexQueueType, QueueItems>(IndexQueueType.class);
    private final Map<IndexQueueType, Map<ItemOperationType, Long>> previousItems = new EnumMap<IndexQueueType, Map<ItemOperationType, Long>>(IndexQueueType.class);

    public DefaultIndexQueueItemsService(IndexQueueSizeService indexQueueSizeService, EventPublisher eventPublisher, ConfluenceIpdMainRegistryConfiguration ipdMainRegistryConfiguration) {
        this.indexQueueSizeService = indexQueueSizeService;
        this.eventPublisher = eventPublisher;
        this.ipdMainRegistryConfiguration = ipdMainRegistryConfiguration;
        this.initializeQueueItems();
    }

    private void initializeQueueItems() {
        for (IndexQueueType indexQueueType : IndexQueueType.values()) {
            this.queueItemsMap.put(indexQueueType, new QueueItems());
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
    public void onIndexQueueFlushCompleteEvent(IndexQueueFlushCompleteEvent event) {
        if (!this.ipdMainRegistryConfiguration.isIpdEnabled()) {
            return;
        }
        Object source = event.getSource();
        if (source instanceof LuceneIncrementalIndexManager) {
            SearchIndex targetIndex = ((LuceneIncrementalIndexManager)source).getTargetIndex();
            long queueSize = event.getFlushStatistics().getQueueSize();
            IndexQueueType indexQueueType = this.getIndexQueueType(targetIndex.name());
            QueueItems queueItems = this.queueItemsMap.get((Object)indexQueueType);
            queueItems.addProcessedItems(queueSize);
            queueItems.updateAddedItems(this.indexQueueSizeService.getIndexQueueSizeMetric().getQueueSize(indexQueueType));
        }
    }

    @EventListener
    public void onEdgeIndexQueueFlushCompleteEvent(EdgeIndexQueueFlushCompleteEvent event) {
        if (!this.ipdMainRegistryConfiguration.isIpdEnabled()) {
            return;
        }
        long queueSize = event.getFlushStatistics().getQueueSize();
        QueueItems queueItems = this.queueItemsMap.get((Object)IndexQueueType.EDGE);
        queueItems.addProcessedItems(queueSize);
        queueItems.updateAddedItems(this.indexQueueSizeService.getIndexQueueSizeMetric().getQueueSize(IndexQueueType.EDGE));
    }

    @Override
    public long getQueueItemsAdded(IndexQueueType indexQueueType) {
        return this.queueItemsMap.get((Object)indexQueueType).getAddedItems();
    }

    @Override
    public long getQueueItemsProcessed(IndexQueueType indexQueueType) {
        return this.queueItemsMap.get((Object)indexQueueType).getProcessedItems();
    }

    IndexQueueType getIndexQueueType(String indexName) {
        try {
            if (indexName.equalsIgnoreCase("content")) {
                return IndexQueueType.MAIN;
            }
            return IndexQueueType.valueOf(indexName.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid IndexQueueType: " + indexName);
        }
    }

    void resetMetrics() {
        this.previousItems.clear();
        this.queueItemsMap.values().forEach(value -> {
            value.addedItems = 0L;
            value.processedItems = 0L;
        });
    }

    long calculateItemsCountPerInterval(long totalItemsCount, IndexQueueType indexQueueType, ItemOperationType itemOperationType) {
        long itemsCountPerInterval = totalItemsCount - this.getPreviousItemsCount(indexQueueType, itemOperationType);
        this.setPreviousItemsCount(totalItemsCount, indexQueueType, itemOperationType);
        return itemsCountPerInterval;
    }

    private long getPreviousItemsCount(IndexQueueType indexQueueType, ItemOperationType itemOperationType) {
        Map<ItemOperationType, Long> previousItemsCountByQueueType = this.previousItems.get((Object)indexQueueType);
        Long previousItemsCount = previousItemsCountByQueueType != null ? previousItemsCountByQueueType.get((Object)itemOperationType) : null;
        return Objects.requireNonNullElse(previousItemsCount, 0L);
    }

    private void setPreviousItemsCount(long currentItemsCount, IndexQueueType indexQueueType, ItemOperationType operationType) {
        Map previousItemsByQueueType = this.previousItems.computeIfAbsent(indexQueueType, k -> new HashMap());
        previousItemsByQueueType.put(operationType, currentItemsCount);
    }

    private static class QueueItems {
        private long addedItems;
        private long processedItems;

        private QueueItems() {
        }

        private void addProcessedItems(long count) {
            this.processedItems += count;
        }

        private void updateAddedItems(long count) {
            this.addedItems = count + this.processedItems;
        }

        private long getAddedItems() {
            return this.addedItems;
        }

        private long getProcessedItems() {
            return this.processedItems;
        }
    }
}

