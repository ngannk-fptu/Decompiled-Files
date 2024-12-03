/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorHandler;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.map.impl.querycache.publisher.EventPublisherAccumulatorProcessor;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherAccumulatorHandler;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.Preconditions;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class AccumulatorConsumerOperation
extends Operation
implements PartitionAwareOperation,
IdentifiedDataSerializable {
    private int maxProcessableAccumulatorCount;
    private Queue<Accumulator> accumulators;

    public AccumulatorConsumerOperation() {
    }

    public AccumulatorConsumerOperation(Queue<Accumulator> accumulators, int maxProcessableAccumulatorCount) {
        Preconditions.checkPositive(maxProcessableAccumulatorCount, "maxProcessableAccumulatorCount");
        this.accumulators = accumulators;
        this.maxProcessableAccumulatorCount = maxProcessableAccumulatorCount;
    }

    @Override
    public void run() throws Exception {
        Accumulator accumulator;
        QueryCacheContext context = this.getQueryCacheContext();
        QueryCacheEventService queryCacheEventService = context.getQueryCacheEventService();
        EventPublisherAccumulatorProcessor processor = new EventPublisherAccumulatorProcessor(queryCacheEventService);
        PublisherAccumulatorHandler handler = new PublisherAccumulatorHandler(context, processor);
        int processed = 0;
        while ((accumulator = this.accumulators.poll()) != null) {
            if (this.isLocal()) {
                this.publishAccumulator(processor, handler, accumulator);
            } else {
                this.removeAccumulator(context, accumulator);
            }
            if (++processed <= this.maxProcessableAccumulatorCount) continue;
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    private void publishAccumulator(EventPublisherAccumulatorProcessor processor, AccumulatorHandler<Sequenced> handler, Accumulator accumulator) {
        AccumulatorInfo info = accumulator.getInfo();
        processor.setInfo(info);
        accumulator.poll(handler, info.getDelaySeconds(), TimeUnit.SECONDS);
    }

    private QueryCacheContext getQueryCacheContext() {
        MapService mapService = (MapService)this.getService();
        return mapService.getMapServiceContext().getQueryCacheContext();
    }

    private boolean isLocal() {
        NodeEngine nodeEngine = this.getNodeEngine();
        IPartitionService partitionService = nodeEngine.getPartitionService();
        IPartition partition = partitionService.getPartition(this.getPartitionId());
        return partition.isLocal();
    }

    private void removeAccumulator(QueryCacheContext context, Accumulator accumulator) {
        PublisherContext publisherContext = context.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        AccumulatorInfo info = accumulator.getInfo();
        String mapName = info.getMapName();
        String cacheName = info.getCacheId();
        PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrNull(mapName);
        if (publisherRegistry == null) {
            return;
        }
        PartitionAccumulatorRegistry partitionAccumulatorRegistry = publisherRegistry.getOrNull(cacheName);
        if (partitionAccumulatorRegistry == null) {
            return;
        }
        partitionAccumulatorRegistry.remove(this.getPartitionId());
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 129;
    }
}

