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

public class LongMinAggregation<Key, Value>
implements AggType<Key, Value, Key, Long, Long, Long, Long> {
    @Override
    public Collator<Map.Entry<Key, Long>, Long> getCollator() {
        return new Collator<Map.Entry<Key, Long>, Long>(){

            @Override
            public Long collate(Iterable<Map.Entry<Key, Long>> values) {
                long min = Long.MAX_VALUE;
                for (Map.Entry entry : values) {
                    long value = entry.getValue();
                    if (value >= min) continue;
                    min = value;
                }
                return min;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Long> getMapper(Supplier<Key, Value, Long> supplier) {
        return new SupplierConsumingMapper<Key, Value, Long>(supplier);
    }

    @Override
    public CombinerFactory<Key, Long, Long> getCombinerFactory() {
        return new LongMinCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Long, Long> getReducerFactory() {
        return new LongMinReducerFactory();
    }

    private static final class LongMinReducer
    extends Reducer<Long, Long> {
        private long min = Long.MAX_VALUE;

        private LongMinReducer() {
        }

        @Override
        public void reduce(Long value) {
            if (value < this.min) {
                this.min = value;
            }
        }

        @Override
        public Long finalizeReduce() {
            return this.min;
        }
    }

    private static final class LongMinCombiner
    extends Combiner<Long, Long> {
        private long chunkMin = Long.MAX_VALUE;

        private LongMinCombiner() {
        }

        @Override
        public void combine(Long value) {
            if (value < this.chunkMin) {
                this.chunkMin = value;
            }
        }

        @Override
        public Long finalizeChunk() {
            long value = this.chunkMin;
            this.chunkMin = Long.MAX_VALUE;
            return value;
        }
    }

    @BinaryInterface
    static final class LongMinReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Long, Long> {
        LongMinReducerFactory() {
        }

        @Override
        public Reducer<Long, Long> newReducer(Key key) {
            return new LongMinReducer();
        }

        @Override
        public int getId() {
            return 49;
        }
    }

    @BinaryInterface
    static final class LongMinCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Long, Long> {
        LongMinCombinerFactory() {
        }

        @Override
        public Combiner<Long, Long> newCombiner(Key key) {
            return new LongMinCombiner();
        }

        @Override
        public int getId() {
            return 48;
        }
    }
}

