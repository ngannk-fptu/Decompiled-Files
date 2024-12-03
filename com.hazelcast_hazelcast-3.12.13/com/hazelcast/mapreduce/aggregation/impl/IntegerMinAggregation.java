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

public class IntegerMinAggregation<Key, Value>
implements AggType<Key, Value, Key, Integer, Integer, Integer, Integer> {
    @Override
    public Collator<Map.Entry<Key, Integer>, Integer> getCollator() {
        return new Collator<Map.Entry<Key, Integer>, Integer>(){

            @Override
            public Integer collate(Iterable<Map.Entry<Key, Integer>> values) {
                int min = Integer.MAX_VALUE;
                for (Map.Entry entry : values) {
                    int value = entry.getValue();
                    if (value >= min) continue;
                    min = value;
                }
                return min;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Integer> getMapper(Supplier<Key, Value, Integer> supplier) {
        return new SupplierConsumingMapper<Key, Value, Integer>(supplier);
    }

    @Override
    public CombinerFactory<Key, Integer, Integer> getCombinerFactory() {
        return new IntegerMinCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Integer, Integer> getReducerFactory() {
        return new IntegerMinReducerFactory();
    }

    private static final class IntegerMinReducer
    extends Reducer<Integer, Integer> {
        private int min = Integer.MAX_VALUE;

        private IntegerMinReducer() {
        }

        @Override
        public void reduce(Integer value) {
            if (value < this.min) {
                this.min = value;
            }
        }

        @Override
        public Integer finalizeReduce() {
            return this.min;
        }
    }

    private static final class IntegerMinCombiner
    extends Combiner<Integer, Integer> {
        private int chunkMin = Integer.MAX_VALUE;

        private IntegerMinCombiner() {
        }

        @Override
        public void combine(Integer value) {
            if (value < this.chunkMin) {
                this.chunkMin = value;
            }
        }

        @Override
        public Integer finalizeChunk() {
            int value = this.chunkMin;
            this.chunkMin = Integer.MAX_VALUE;
            return value;
        }
    }

    @BinaryInterface
    static final class IntegerMinReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Integer, Integer> {
        IntegerMinReducerFactory() {
        }

        @Override
        public Reducer<Integer, Integer> newReducer(Key key) {
            return new IntegerMinReducer();
        }

        @Override
        public int getId() {
            return 41;
        }
    }

    @BinaryInterface
    static final class IntegerMinCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Integer, Integer> {
        IntegerMinCombinerFactory() {
        }

        @Override
        public Combiner<Integer, Integer> newCombiner(Key key) {
            return new IntegerMinCombiner();
        }

        @Override
        public int getId() {
            return 40;
        }
    }
}

