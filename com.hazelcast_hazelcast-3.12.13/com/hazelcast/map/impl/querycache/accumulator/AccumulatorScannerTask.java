/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.map.impl.operation.AccumulatorConsumerOperation;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AccumulatorScannerTask
implements Runnable {
    private static final int MAX_PROCESSABLE_ACCUMULATOR_COUNT = 10;
    private final ScannerConsumer consumer;
    private final QueryCacheContext context;

    public AccumulatorScannerTask(QueryCacheContext context) {
        this.context = context;
        this.consumer = new ScannerConsumer();
    }

    @Override
    public void run() {
        this.scanAccumulators();
    }

    void scanAccumulators() {
        PublisherContext publisherContext = this.context.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        Map<String, PublisherRegistry> publisherRegistryMap = mapPublisherRegistry.getAll();
        Set<Map.Entry<String, PublisherRegistry>> publishers = publisherRegistryMap.entrySet();
        for (Map.Entry<String, PublisherRegistry> entry : publishers) {
            PublisherRegistry publisherRegistry = entry.getValue();
            Map<String, PartitionAccumulatorRegistry> accumulatorRegistryMap = publisherRegistry.getAll();
            Set<Map.Entry<String, PartitionAccumulatorRegistry>> accumulators = accumulatorRegistryMap.entrySet();
            for (Map.Entry<String, PartitionAccumulatorRegistry> accumulatorRegistryEntry : accumulators) {
                PartitionAccumulatorRegistry accumulatorRegistry = accumulatorRegistryEntry.getValue();
                Map<Integer, Accumulator> accumulatorMap = accumulatorRegistry.getAll();
                for (Map.Entry<Integer, Accumulator> accumulatorEntry : accumulatorMap.entrySet()) {
                    Integer partitionId = accumulatorEntry.getKey();
                    Accumulator accumulator = accumulatorEntry.getValue();
                    int size = accumulator.size();
                    if (size <= 0) continue;
                    this.consumer.consume(accumulator, partitionId);
                }
            }
        }
        this.sendConsumerOperation();
        this.consumer.reset();
    }

    private void sendConsumerOperation() {
        Map<Integer, Queue<Accumulator>> partitionAccumulators = this.consumer.getPartitionAccumulators();
        if (partitionAccumulators == null || partitionAccumulators.isEmpty()) {
            return;
        }
        Set<Map.Entry<Integer, Queue<Accumulator>>> entries = partitionAccumulators.entrySet();
        for (Map.Entry<Integer, Queue<Accumulator>> entry : entries) {
            Integer partitionId = entry.getKey();
            Queue<Accumulator> accumulators = entry.getValue();
            if (accumulators.isEmpty()) continue;
            Operation operation = this.createConsumerOperation(partitionId, accumulators);
            this.context.getInvokerWrapper().executeOperation(operation);
        }
    }

    private Operation createConsumerOperation(int partitionId, Queue<Accumulator> accumulators) {
        PublisherContext publisherContext = this.context.getPublisherContext();
        NodeEngineImpl nodeEngine = (NodeEngineImpl)publisherContext.getNodeEngine();
        AccumulatorConsumerOperation operation = new AccumulatorConsumerOperation(accumulators, 10);
        operation.setNodeEngine(nodeEngine).setCallerUuid(nodeEngine.getLocalMember().getUuid()).setPartitionId(partitionId).setValidateTarget(false).setService(nodeEngine.getService("hz:impl:mapService"));
        return operation;
    }

    private static class ScannerConsumer {
        private Map<Integer, Queue<Accumulator>> partitionAccumulators;

        ScannerConsumer() {
        }

        void consume(Accumulator accumulator, int partitionId) {
            Queue<Accumulator> accumulators;
            if (this.partitionAccumulators == null) {
                this.partitionAccumulators = new HashMap<Integer, Queue<Accumulator>>();
            }
            if ((accumulators = this.partitionAccumulators.get(partitionId)) == null) {
                accumulators = new ArrayDeque<Accumulator>();
                this.partitionAccumulators.put(partitionId, accumulators);
            }
            accumulators.add(accumulator);
        }

        Map<Integer, Queue<Accumulator>> getPartitionAccumulators() {
            return this.partitionAccumulators;
        }

        void reset() {
            if (this.partitionAccumulators != null) {
                this.partitionAccumulators.clear();
            }
        }
    }
}

