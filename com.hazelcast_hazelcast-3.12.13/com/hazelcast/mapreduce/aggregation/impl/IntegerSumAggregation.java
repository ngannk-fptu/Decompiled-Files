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

public class IntegerSumAggregation<Key, Value>
implements AggType<Key, Value, Key, Integer, Integer, Integer, Integer> {
    @Override
    public Collator<Map.Entry<Key, Integer>, Integer> getCollator() {
        return new Collator<Map.Entry<Key, Integer>, Integer>(){

            @Override
            public Integer collate(Iterable<Map.Entry<Key, Integer>> values) {
                int sum = 0;
                for (Map.Entry entry : values) {
                    sum += entry.getValue().intValue();
                }
                return sum;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Integer> getMapper(Supplier<Key, Value, Integer> supplier) {
        return new SupplierConsumingMapper<Key, Value, Integer>(supplier);
    }

    @Override
    public CombinerFactory<Key, Integer, Integer> getCombinerFactory() {
        return new IntegerSumCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Integer, Integer> getReducerFactory() {
        return new IntegerSumReducerFactory();
    }

    private static final class IntegerSumReducer
    extends Reducer<Integer, Integer> {
        private int sum;

        private IntegerSumReducer() {
        }

        @Override
        public void reduce(Integer value) {
            this.sum += value.intValue();
        }

        @Override
        public Integer finalizeReduce() {
            return this.sum;
        }
    }

    private static final class IntegerSumCombiner
    extends Combiner<Integer, Integer> {
        private int chunkSum;

        private IntegerSumCombiner() {
        }

        @Override
        public void combine(Integer value) {
            this.chunkSum += value.intValue();
        }

        @Override
        public Integer finalizeChunk() {
            int value = this.chunkSum;
            this.chunkSum = 0;
            return value;
        }
    }

    @BinaryInterface
    static final class IntegerSumReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Integer, Integer> {
        IntegerSumReducerFactory() {
        }

        @Override
        public Reducer<Integer, Integer> newReducer(Key key) {
            return new IntegerSumReducer();
        }

        @Override
        public int getId() {
            return 43;
        }
    }

    @BinaryInterface
    static final class IntegerSumCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Integer, Integer> {
        IntegerSumCombinerFactory() {
        }

        @Override
        public Combiner<Integer, Integer> newCombiner(Key key) {
            return new IntegerSumCombiner();
        }

        @Override
        public int getId() {
            return 42;
        }
    }
}

