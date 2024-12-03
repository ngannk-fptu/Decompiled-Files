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

public class DoubleAvgAggregation<Key, Value>
implements AggType<Key, Value, Key, Double, AvgTuple<Long, Double>, AvgTuple<Long, Double>, Double> {
    @Override
    public Collator<Map.Entry<Key, AvgTuple<Long, Double>>, Double> getCollator() {
        return new Collator<Map.Entry<Key, AvgTuple<Long, Double>>, Double>(){

            @Override
            public Double collate(Iterable<Map.Entry<Key, AvgTuple<Long, Double>>> values) {
                long count = 0L;
                double amount = 0.0;
                for (Map.Entry entry : values) {
                    AvgTuple<Long, Double> tuple = entry.getValue();
                    count += tuple.getFirst().longValue();
                    amount += tuple.getSecond().doubleValue();
                }
                return amount / (double)count;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Double> getMapper(Supplier<Key, Value, Double> supplier) {
        return new SupplierConsumingMapper<Key, Value, Double>(supplier);
    }

    @Override
    public CombinerFactory<Key, Double, AvgTuple<Long, Double>> getCombinerFactory() {
        return new DoubleAvgCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, AvgTuple<Long, Double>, AvgTuple<Long, Double>> getReducerFactory() {
        return new DoubleAvgReducerFactory();
    }

    private static final class DoubleAvgReducer
    extends Reducer<AvgTuple<Long, Double>, AvgTuple<Long, Double>> {
        private long count;
        private double amount;

        private DoubleAvgReducer() {
        }

        @Override
        public void reduce(AvgTuple<Long, Double> value) {
            this.count += value.getFirst().longValue();
            this.amount += value.getSecond().doubleValue();
        }

        @Override
        public AvgTuple<Long, Double> finalizeReduce() {
            return new AvgTuple<Long, Double>(this.count, this.amount);
        }
    }

    private static final class DoubleAvgCombiner
    extends Combiner<Double, AvgTuple<Long, Double>> {
        private long count;
        private double amount;

        private DoubleAvgCombiner() {
        }

        @Override
        public void combine(Double value) {
            ++this.count;
            this.amount += value.doubleValue();
        }

        @Override
        public AvgTuple<Long, Double> finalizeChunk() {
            long count = this.count;
            double amount = this.amount;
            this.count = 0L;
            this.amount = 0.0;
            return new AvgTuple<Long, Double>(count, amount);
        }
    }

    @BinaryInterface
    static final class DoubleAvgReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, AvgTuple<Long, Double>, AvgTuple<Long, Double>> {
        DoubleAvgReducerFactory() {
        }

        @Override
        public Reducer<AvgTuple<Long, Double>, AvgTuple<Long, Double>> newReducer(Key key) {
            return new DoubleAvgReducer();
        }

        @Override
        public int getId() {
            return 29;
        }
    }

    @BinaryInterface
    static final class DoubleAvgCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Double, AvgTuple<Long, Double>> {
        DoubleAvgCombinerFactory() {
        }

        @Override
        public Combiner<Double, AvgTuple<Long, Double>> newCombiner(Key key) {
            return new DoubleAvgCombiner();
        }

        @Override
        public int getId() {
            return 28;
        }
    }
}

