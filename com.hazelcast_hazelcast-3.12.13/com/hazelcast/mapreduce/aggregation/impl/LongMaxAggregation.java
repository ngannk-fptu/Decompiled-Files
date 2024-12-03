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

public class LongMaxAggregation<Key, Value>
implements AggType<Key, Value, Key, Long, Long, Long, Long> {
    @Override
    public Collator<Map.Entry<Key, Long>, Long> getCollator() {
        return new Collator<Map.Entry<Key, Long>, Long>(){

            @Override
            public Long collate(Iterable<Map.Entry<Key, Long>> values) {
                long max = Long.MIN_VALUE;
                for (Map.Entry entry : values) {
                    long value = entry.getValue();
                    if (value <= max) continue;
                    max = value;
                }
                return max;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Long> getMapper(Supplier<Key, Value, Long> supplier) {
        return new SupplierConsumingMapper<Key, Value, Long>(supplier);
    }

    @Override
    public CombinerFactory<Key, Long, Long> getCombinerFactory() {
        return new LongMaxCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Long, Long> getReducerFactory() {
        return new LongMaxReducerFactory();
    }

    private static final class LongMaxReducer
    extends Reducer<Long, Long> {
        private long max = Long.MIN_VALUE;

        private LongMaxReducer() {
        }

        @Override
        public void reduce(Long value) {
            if (value > this.max) {
                this.max = value;
            }
        }

        @Override
        public Long finalizeReduce() {
            return this.max;
        }
    }

    private static final class LongMaxCombiner
    extends Combiner<Long, Long> {
        private long chunkMax = Long.MIN_VALUE;

        private LongMaxCombiner() {
        }

        @Override
        public void combine(Long value) {
            if (value > this.chunkMax) {
                this.chunkMax = value;
            }
        }

        @Override
        public Long finalizeChunk() {
            long value = this.chunkMax;
            this.chunkMax = Long.MIN_VALUE;
            return value;
        }
    }

    @BinaryInterface
    static final class LongMaxReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Long, Long> {
        LongMaxReducerFactory() {
        }

        @Override
        public Reducer<Long, Long> newReducer(Key key) {
            return new LongMaxReducer();
        }

        @Override
        public int getId() {
            return 47;
        }
    }

    @BinaryInterface
    static final class LongMaxCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Long, Long> {
        LongMaxCombinerFactory() {
        }

        @Override
        public Combiner<Long, Long> newCombiner(Key key) {
            return new LongMaxCombiner();
        }

        @Override
        public int getId() {
            return 46;
        }
    }
}

