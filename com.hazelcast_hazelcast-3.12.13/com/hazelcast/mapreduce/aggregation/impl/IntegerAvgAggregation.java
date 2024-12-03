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
import com.hazelcast.mapreduce.aggregation.impl.AvgTuple;
import com.hazelcast.mapreduce.aggregation.impl.SupplierConsumingMapper;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.util.Map;

public class IntegerAvgAggregation<Key, Value>
implements AggType<Key, Value, Key, Integer, AvgTuple<Integer, Integer>, AvgTuple<Integer, Integer>, Integer> {
    @Override
    public Collator<Map.Entry<Key, AvgTuple<Integer, Integer>>, Integer> getCollator() {
        return new Collator<Map.Entry<Key, AvgTuple<Integer, Integer>>, Integer>(){

            @Override
            public Integer collate(Iterable<Map.Entry<Key, AvgTuple<Integer, Integer>>> values) {
                int count = 0;
                int amount = 0;
                for (Map.Entry entry : values) {
                    AvgTuple<Integer, Integer> tuple = entry.getValue();
                    count += tuple.getFirst().intValue();
                    amount += tuple.getSecond().intValue();
                }
                return (int)((double)amount / (double)count);
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Integer> getMapper(Supplier<Key, Value, Integer> supplier) {
        return new SupplierConsumingMapper<Key, Value, Integer>(supplier);
    }

    @Override
    public CombinerFactory<Key, Integer, AvgTuple<Integer, Integer>> getCombinerFactory() {
        return new IntegerAvgCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, AvgTuple<Integer, Integer>, AvgTuple<Integer, Integer>> getReducerFactory() {
        return new IntegerAvgReducerFactory();
    }

    private static final class IntegerAvgReducer
    extends Reducer<AvgTuple<Integer, Integer>, AvgTuple<Integer, Integer>> {
        private int count;
        private int amount;

        private IntegerAvgReducer() {
        }

        @Override
        public void reduce(AvgTuple<Integer, Integer> value) {
            this.count += value.getFirst().intValue();
            this.amount += value.getSecond().intValue();
        }

        @Override
        public AvgTuple<Integer, Integer> finalizeReduce() {
            return new AvgTuple<Integer, Integer>(this.count, this.amount);
        }
    }

    private static final class IntegerAvgCombiner
    extends Combiner<Integer, AvgTuple<Integer, Integer>> {
        private int count;
        private int amount;

        private IntegerAvgCombiner() {
        }

        @Override
        public void combine(Integer value) {
            ++this.count;
            this.amount += value.intValue();
        }

        @Override
        public AvgTuple<Integer, Integer> finalizeChunk() {
            int count = this.count;
            int amount = this.amount;
            this.count = 0;
            this.amount = 0;
            return new AvgTuple<Integer, Integer>(count, amount);
        }
    }

    @BinaryInterface
    static final class IntegerAvgReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, AvgTuple<Integer, Integer>, AvgTuple<Integer, Integer>> {
        IntegerAvgReducerFactory() {
        }

        @Override
        public Reducer<AvgTuple<Integer, Integer>, AvgTuple<Integer, Integer>> newReducer(Key key) {
            return new IntegerAvgReducer();
        }

        @Override
        public int getId() {
            return 37;
        }
    }

    @BinaryInterface
    static final class IntegerAvgCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Integer, AvgTuple<Integer, Integer>> {
        IntegerAvgCombinerFactory() {
        }

        @Override
        public Combiner<Integer, AvgTuple<Integer, Integer>> newCombiner(Key key) {
            return new IntegerAvgCombiner();
        }

        @Override
        public int getId() {
            return 36;
        }
    }
}

