/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventDataBuilder;
import com.hazelcast.map.impl.querycache.publisher.EventPublisherAccumulatorProcessor;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherAccumulatorHandler;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public final class AccumulatorSweeper {
    public static final long END_SEQUENCE = -1L;

    private AccumulatorSweeper() {
    }

    public static void flushAllAccumulators(PublisherContext publisherContext) {
        QueryCacheContext context = publisherContext.getContext();
        EventPublisherAccumulatorProcessor processor = new EventPublisherAccumulatorProcessor(context.getQueryCacheEventService());
        PublisherAccumulatorHandler handler = new PublisherAccumulatorHandler(context, processor);
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        Map<String, PublisherRegistry> allPublisherRegistryMap = mapPublisherRegistry.getAll();
        for (PublisherRegistry publisherRegistry : allPublisherRegistryMap.values()) {
            Map<String, PartitionAccumulatorRegistry> accumulatorRegistryMap = publisherRegistry.getAll();
            for (PartitionAccumulatorRegistry accumulatorRegistry : accumulatorRegistryMap.values()) {
                Map<Integer, Accumulator> accumulatorMap = accumulatorRegistry.getAll();
                for (Map.Entry<Integer, Accumulator> entry : accumulatorMap.entrySet()) {
                    Integer partitionId = entry.getKey();
                    Accumulator accumulator = entry.getValue();
                    processor.setInfo(accumulator.getInfo());
                    accumulator.poll(handler, 0L, TimeUnit.SECONDS);
                    QueryCacheEventData eventData = AccumulatorSweeper.createEndOfSequenceEvent(partitionId);
                    processor.process(eventData);
                }
            }
        }
    }

    public static void flushAccumulator(PublisherContext publisherContext, int partitionId) {
        QueryCacheEventData endOfSequenceEvent = AccumulatorSweeper.createEndOfSequenceEvent(partitionId);
        QueryCacheContext context = publisherContext.getContext();
        EventPublisherAccumulatorProcessor processor = new EventPublisherAccumulatorProcessor(context.getQueryCacheEventService());
        PublisherAccumulatorHandler handler = new PublisherAccumulatorHandler(context, processor);
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        Map<String, PublisherRegistry> allPublisherRegistryMap = mapPublisherRegistry.getAll();
        for (PublisherRegistry publisherRegistry : allPublisherRegistryMap.values()) {
            Map<String, PartitionAccumulatorRegistry> accumulatorRegistryMap = publisherRegistry.getAll();
            for (PartitionAccumulatorRegistry accumulatorRegistry : accumulatorRegistryMap.values()) {
                Map<Integer, Accumulator> accumulatorMap = accumulatorRegistry.getAll();
                Accumulator accumulator = accumulatorMap.get(partitionId);
                if (accumulator == null) continue;
                processor.setInfo(accumulator.getInfo());
                accumulator.poll(handler, 0L, TimeUnit.SECONDS);
                processor.process(endOfSequenceEvent);
            }
        }
    }

    public static void sendEndOfSequenceEvents(PublisherContext publisherContext, int partitionId) {
        QueryCacheEventData endOfSequenceEvent = AccumulatorSweeper.createEndOfSequenceEvent(partitionId);
        QueryCacheContext context = publisherContext.getContext();
        EventPublisherAccumulatorProcessor processor = new EventPublisherAccumulatorProcessor(context.getQueryCacheEventService());
        AccumulatorInfoSupplier infoSupplier = publisherContext.getAccumulatorInfoSupplier();
        ConcurrentMap<String, ConcurrentMap<String, AccumulatorInfo>> all = infoSupplier.getAll();
        for (ConcurrentMap oneMapsAccumulators : all.values()) {
            for (AccumulatorInfo accumulatorInfo : oneMapsAccumulators.values()) {
                if (accumulatorInfo.getDelaySeconds() != 0L) continue;
                processor.setInfo(accumulatorInfo);
                processor.process(endOfSequenceEvent);
            }
        }
    }

    public static void removeAccumulator(PublisherContext publisherContext, int partitionId) {
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        Map<String, PublisherRegistry> allPublisherRegistryMap = mapPublisherRegistry.getAll();
        for (PublisherRegistry publisherRegistry : allPublisherRegistryMap.values()) {
            Map<String, PartitionAccumulatorRegistry> accumulatorRegistryMap = publisherRegistry.getAll();
            for (PartitionAccumulatorRegistry accumulatorRegistry : accumulatorRegistryMap.values()) {
                accumulatorRegistry.remove(partitionId);
            }
        }
    }

    private static QueryCacheEventData createEndOfSequenceEvent(int partitionId) {
        return QueryCacheEventDataBuilder.newQueryCacheEventDataBuilder(false).withSequence(-1L).withPartitionId(partitionId).build();
    }
}

