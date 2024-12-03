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

public class IntegerMaxAggregation<Key, Value>
implements AggType<Key, Value, Key, Integer, Integer, Integer, Integer> {
    @Override
    public Collator<Map.Entry<Key, Integer>, Integer> getCollator() {
        return new Collator<Map.Entry<Key, Integer>, Integer>(){

            @Override
            public Integer collate(Iterable<Map.Entry<Key, Integer>> values) {
                int max = Integer.MIN_VALUE;
                for (Map.Entry entry : values) {
                    int value = entry.getValue();
                    if (value <= max) continue;
                    max = value;
                }
                return max;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Integer> getMapper(Supplier<Key, Value, Integer> supplier) {
        return new SupplierConsumingMapper<Key, Value, Integer>(supplier);
    }

    @Override
    public CombinerFactory<Key, Integer, Integer> getCombinerFactory() {
        return new IntegerMaxCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Integer, Integer> getReducerFactory() {
        return new IntegerMaxReducerFactory();
    }

    private static final class IntegerMaxReducer
    extends Reducer<Integer, Integer> {
        private int max = Integer.MIN_VALUE;

        private IntegerMaxReducer() {
        }

        @Override
        public void reduce(Integer value) {
            if (value > this.max) {
                this.max = value;
            }
        }

        @Override
        public Integer finalizeReduce() {
            return this.max;
        }
    }

    private static final class IntegerMaxCombiner
    extends Combiner<Integer, Integer> {
        private int chunkMax = Integer.MIN_VALUE;

        private IntegerMaxCombiner() {
        }

        @Override
        public void combine(Integer value) {
            if (value > this.chunkMax) {
                this.chunkMax = value;
            }
        }

        @Override
        public Integer finalizeChunk() {
            int value = this.chunkMax;
            this.chunkMax = Integer.MIN_VALUE;
            return value;
        }
    }

    @BinaryInterface
    static final class IntegerMaxReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Integer, Integer> {
        IntegerMaxReducerFactory() {
        }

        @Override
        public Reducer<Integer, Integer> newReducer(Key key) {
            return new IntegerMaxReducer();
        }

        @Override
        public int getId() {
            return 39;
        }
    }

    @BinaryInterface
    static final class IntegerMaxCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Integer, Integer> {
        IntegerMaxCombinerFactory() {
        }

        @Override
        public Combiner<Integer, Integer> newCombiner(Key key) {
            return new IntegerMaxCombiner();
        }

        @Override
        public int getId() {
            return 38;
        }
    }
}

