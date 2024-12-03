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

public class DoubleSumAggregation<Key, Value>
implements AggType<Key, Value, Key, Double, Double, Double, Double> {
    @Override
    public Collator<Map.Entry<Key, Double>, Double> getCollator() {
        return new Collator<Map.Entry<Key, Double>, Double>(){

            @Override
            public Double collate(Iterable<Map.Entry<Key, Double>> values) {
                double sum = 0.0;
                for (Map.Entry entry : values) {
                    sum += entry.getValue().doubleValue();
                }
                return sum;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Double> getMapper(Supplier<Key, Value, Double> supplier) {
        return new SupplierConsumingMapper<Key, Value, Double>(supplier);
    }

    @Override
    public CombinerFactory<Key, Double, Double> getCombinerFactory() {
        return new DoubleSumCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Double, Double> getReducerFactory() {
        return new DoubleSumReducerFactory();
    }

    private static final class DoubleSumReducer
    extends Reducer<Double, Double> {
        private double sum;

        private DoubleSumReducer() {
        }

        @Override
        public void reduce(Double value) {
            this.sum += value.doubleValue();
        }

        @Override
        public Double finalizeReduce() {
            return this.sum;
        }
    }

    private static final class DoubleSumCombiner
    extends Combiner<Double, Double> {
        private double chunkSum;

        private DoubleSumCombiner() {
        }

        @Override
        public void combine(Double value) {
            this.chunkSum += value.doubleValue();
        }

        @Override
        public Double finalizeChunk() {
            double value = this.chunkSum;
            this.chunkSum = 0.0;
            return value;
        }
    }

    @BinaryInterface
    static final class DoubleSumReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Double, Double> {
        DoubleSumReducerFactory() {
        }

        @Override
        public Reducer<Double, Double> newReducer(Key key) {
            return new DoubleSumReducer();
        }

        @Override
        public int getId() {
            return 35;
        }
    }

    @BinaryInterface
    static final class DoubleSumCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Double, Double> {
        DoubleSumCombinerFactory() {
        }

        @Override
        public Combiner<Double, Double> newCombiner(Key key) {
            return new DoubleSumCombiner();
        }

        @Override
        public int getId() {
            return 34;
        }
    }
}

