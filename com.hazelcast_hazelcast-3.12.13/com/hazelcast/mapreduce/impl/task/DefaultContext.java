/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.core.IFunction;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.impl.CombinerResultList;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.task.MapCombineTask;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.util.ConcurrentReferenceHashMap;
import com.hazelcast.util.IConcurrentMap;
import com.hazelcast.util.MapUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class DefaultContext<KeyIn, ValueIn>
implements Context<KeyIn, ValueIn> {
    private static final AtomicIntegerFieldUpdater<DefaultContext> COLLECTED = AtomicIntegerFieldUpdater.newUpdater(DefaultContext.class, "collected");
    private final IConcurrentMap<KeyIn, Combiner<ValueIn, ?>> combiners = new ConcurrentReferenceHashMap(ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.STRONG);
    private final CombinerFactory<KeyIn, ValueIn, ?> combinerFactory;
    private final MapCombineTask mapCombineTask;
    private final IFunction<KeyIn, Combiner<ValueIn, ?>> combinerFunction = new CombinerFunction();
    private volatile int collected;
    private volatile int partitionId;
    private volatile InternalSerializationService serializationService;

    protected DefaultContext(CombinerFactory<KeyIn, ValueIn, ?> combinerFactory, MapCombineTask mapCombineTask) {
        this.mapCombineTask = mapCombineTask;
        this.combinerFactory = combinerFactory != null ? combinerFactory : new CollectingCombinerFactory();
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    @Override
    public void emit(KeyIn key, ValueIn value) {
        Combiner<ValueIn, ?> combiner = this.getOrCreateCombiner(key);
        combiner.combine(value);
        COLLECTED.incrementAndGet(this);
        this.mapCombineTask.onEmit(this, this.partitionId);
    }

    public <Chunk> Map<KeyIn, Chunk> requestChunk() {
        int mapSize = MapReduceUtil.mapSize(this.combiners.size());
        Map chunkMap = MapUtil.createHashMapAdapter(mapSize);
        for (Map.Entry entry : this.combiners.entrySet()) {
            Combiner combiner = (Combiner)entry.getValue();
            Object chunk = combiner.finalizeChunk();
            combiner.reset();
            if (chunk == null) continue;
            chunkMap.put(entry.getKey(), chunk);
        }
        COLLECTED.set(this, 0);
        return chunkMap;
    }

    public int getCollected() {
        return this.collected;
    }

    public void finalizeCombiners() {
        for (Combiner combiner : this.combiners.values()) {
            combiner.finalizeCombine();
        }
    }

    public Combiner<ValueIn, ?> getOrCreateCombiner(KeyIn key) {
        return this.combiners.applyIfAbsent(key, this.combinerFunction);
    }

    public void setSerializationService(InternalSerializationService serializationService) {
        this.serializationService = serializationService;
    }

    public InternalSerializationService getSerializationService() {
        return this.serializationService;
    }

    @SerializableByConvention
    private class CombinerFunction
    implements IFunction<KeyIn, Combiner<ValueIn, ?>> {
        private CombinerFunction() {
        }

        @Override
        public Combiner<ValueIn, ?> apply(KeyIn keyIn) {
            Combiner combiner = DefaultContext.this.combinerFactory.newCombiner(keyIn);
            combiner.beginCombine();
            return combiner;
        }
    }

    @BinaryInterface
    private static class CollectingCombinerFactory<KeyIn, ValueIn>
    implements CombinerFactory<KeyIn, ValueIn, List<ValueIn>> {
        private CollectingCombinerFactory() {
        }

        @Override
        public Combiner<ValueIn, List<ValueIn>> newCombiner(KeyIn key) {
            return new Combiner<ValueIn, List<ValueIn>>(){
                private final List<ValueIn> values = new ArrayList();

                @Override
                public void combine(ValueIn value) {
                    this.values.add(value);
                }

                @Override
                public List<ValueIn> finalizeChunk() {
                    return new CombinerResultList(this.values);
                }

                @Override
                public void reset() {
                    this.values.clear();
                }
            };
        }
    }
}

