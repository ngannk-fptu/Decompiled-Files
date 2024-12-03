/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorHandler;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.subscriber.DefaultQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.EventPublisherHelper;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.nio.serialization.Data;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;

class SubscriberAccumulatorHandler
implements AccumulatorHandler<QueryCacheEventData> {
    private static final Queue<Integer> POLL_PERMIT = new ConcurrentLinkedQueue<Integer>();
    private final int partitionCount;
    private final boolean includeValue;
    private final InternalQueryCache queryCache;
    private final InternalSerializationService serializationService;
    private final AtomicReferenceArray<Queue<Integer>> clearAllRemovedCountHolders;
    private final AtomicReferenceArray<Queue<Integer>> evictAllRemovedCountHolders;

    public SubscriberAccumulatorHandler(boolean includeValue, InternalQueryCache queryCache, InternalSerializationService serializationService) {
        this.includeValue = includeValue;
        this.queryCache = queryCache;
        this.serializationService = serializationService;
        this.partitionCount = ((DefaultQueryCache)queryCache).context.getPartitionCount();
        this.clearAllRemovedCountHolders = SubscriberAccumulatorHandler.initRemovedCountHolders(this.partitionCount);
        this.evictAllRemovedCountHolders = SubscriberAccumulatorHandler.initRemovedCountHolders(this.partitionCount);
    }

    @Override
    public void reset() {
        this.queryCache.clear();
        for (int i = 0; i < this.partitionCount; ++i) {
            this.clearAllRemovedCountHolders.set(i, new ConcurrentLinkedQueue());
            this.evictAllRemovedCountHolders.set(i, new ConcurrentLinkedQueue());
        }
    }

    private static AtomicReferenceArray<Queue<Integer>> initRemovedCountHolders(int partitionCount) {
        AtomicReferenceArray<Queue<Integer>> removedCountHolders = new AtomicReferenceArray<Queue<Integer>>(partitionCount + 1);
        for (int i = 0; i < partitionCount; ++i) {
            removedCountHolders.set(i, new ConcurrentLinkedQueue());
        }
        removedCountHolders.set(partitionCount, POLL_PERMIT);
        return removedCountHolders;
    }

    @Override
    public void handle(QueryCacheEventData eventData, boolean ignored) {
        eventData.setSerializationService(this.serializationService);
        Data keyData = eventData.getDataKey();
        Data valueData = this.includeValue ? eventData.getDataNewValue() : null;
        int eventType = eventData.getEventType();
        EntryEventType entryEventType = EntryEventType.getByType(eventType);
        if (entryEventType == null) {
            SubscriberAccumulatorHandler.throwException(String.format("No matching EntryEventType found for event type id `%d`", eventType));
        }
        switch (entryEventType) {
            case ADDED: 
            case UPDATED: 
            case MERGED: 
            case LOADED: {
                this.queryCache.set(keyData, valueData, entryEventType);
                break;
            }
            case REMOVED: 
            case EVICTED: {
                this.queryCache.delete(keyData, entryEventType);
                break;
            }
            case CLEAR_ALL: {
                this.handleMapWideEvent(eventData, entryEventType, this.clearAllRemovedCountHolders);
                break;
            }
            case EVICT_ALL: {
                this.handleMapWideEvent(eventData, entryEventType, this.evictAllRemovedCountHolders);
                break;
            }
            default: {
                SubscriberAccumulatorHandler.throwException(String.format("Unexpected EntryEventType was found: `%s`", new Object[]{entryEventType}));
            }
        }
    }

    private void handleMapWideEvent(QueryCacheEventData eventData, EntryEventType eventType, AtomicReferenceArray<Queue<Integer>> removedCountHolders) {
        int partitionId = eventData.getPartitionId();
        int removedCount = this.queryCache.removeEntriesOf(partitionId);
        this.tryPublishMapWideEvent(eventType, partitionId, removedCount, removedCountHolders);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryPublishMapWideEvent(EntryEventType eventType, int partitionId, int removedEntryCount, AtomicReferenceArray<Queue<Integer>> removedCountHolders) {
        if (!EventPublisherHelper.hasListener(this.queryCache)) {
            return;
        }
        removedCountHolders.get(partitionId).offer(removedEntryCount);
        if (this.noMissingMapWideEvent(removedCountHolders) && removedCountHolders.compareAndSet(this.partitionCount, POLL_PERMIT, null)) {
            try {
                if (this.noMissingMapWideEvent(removedCountHolders)) {
                    int totalRemovedCount = this.pollRemovedCountHolders(removedCountHolders);
                    EventPublisherHelper.publishCacheWideEvent(this.queryCache, totalRemovedCount, eventType);
                }
            }
            finally {
                removedCountHolders.set(this.partitionCount, POLL_PERMIT);
            }
        }
    }

    private boolean noMissingMapWideEvent(AtomicReferenceArray<Queue<Integer>> removedCountHolders) {
        for (int i = 0; i < this.partitionCount; ++i) {
            if (!removedCountHolders.get(i).isEmpty()) continue;
            return false;
        }
        return true;
    }

    private int pollRemovedCountHolders(AtomicReferenceArray<Queue<Integer>> removedCountHolders) {
        int count = 0;
        for (int i = 0; i < this.partitionCount; ++i) {
            Queue<Integer> removalCounts = removedCountHolders.get(i);
            count += removalCounts.poll().intValue();
        }
        return count;
    }

    private static void throwException(String msg) {
        throw new IllegalArgumentException(msg);
    }
}

