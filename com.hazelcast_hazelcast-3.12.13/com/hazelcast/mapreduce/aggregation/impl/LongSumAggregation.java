/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.AbstractAggregationCombinerFactory;
import com.hazelcast.mapreduce.aggregation.impl.AbstractAggregationReducerFactory;
import com.hazelcast.mapreduce.aggregation.impl.AggType;
import com.hazelcast.mapreduce.aggregation.impl.SupplierConsumingMapper;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.util.Map;

public class LongSumAggregation<Key, Value>
implements AggType<Key, Value, Key, Long, Long, Long, Long> {
    @Override
    public Collator<Map.Entry<Key, Long>, Long> getCollator() {
        return new Collator<Map.Entry<Key, Long>, Long>(){

            @Override
            public Long collate(Iterable<Map.Entry<Key, Long>> values) {
                long sum = 0L;
                for (Map.Entry entry : values) {
                    sum += entry.getValue().longValue();
                }
                return sum;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Long> getMapper(Supplier<Key, Value, Long> supplier) {
        return new SupplierConsumingMapper<Key, Value, Long>(supplier);
    }

    @Override
    public CombinerFactory<Key, Long, Long> getCombinerFactory() {
        return new LongSumCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Long, Long> getReducerFactory() {
        return new LongSumReducerFactory();
    }

    private static final class LongSumReducer
    extends Reducer<Long, Long> {
        private long sum;

        private LongSumReducer() {
        }

        @Override
        public void reduce(Long value) {
            this.sum += value.longValue();
        }

        @Override
        public Long finalizeReduce() {
            return this.sum;
        }
    }

    private static final class LongSumCombiner
    extends Combiner<Long, Long> {
        private long chunkSum;

        private LongSumCombiner() {
        }

        @Override
        public void combine(Long value) {
            this.chunkSum += value.longValue();
        }

        @Override
        public Long finalizeChunk() {
            long value = this.chunkSum;
            this.chunkSum = 0L;
            return value;
        }
    }

    @BinaryInterface
    static final class LongSumReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Long, Long> {
        LongSumReducerFactory() {
        }

        @Override
        public Reducer<Long, Long> newReducer(Key key) {
            return new LongSumReducer();
        }

        @Override
        public int getId() {
            return 51;
        }
    }

    @BinaryInterface
    static final class LongSumCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Long, Long> {
        LongSumCombinerFactory() {
        }

        @Override
        public Combiner<Long, Long> newCombiner(Key key) {
            return new LongSumCombiner();
        }

        @Override
        public int getId() {
            return 50;
        }
    }
}

