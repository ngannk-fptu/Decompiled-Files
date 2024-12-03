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

public class DoubleMinAggregation<Key, Value>
implements AggType<Key, Value, Key, Double, Double, Double, Double> {
    @Override
    public Collator<Map.Entry<Key, Double>, Double> getCollator() {
        return new Collator<Map.Entry<Key, Double>, Double>(){

            @Override
            public Double collate(Iterable<Map.Entry<Key, Double>> values) {
                double min = Double.MAX_VALUE;
                for (Map.Entry entry : values) {
                    double value = entry.getValue();
                    if (!(value < min)) continue;
                    min = value;
                }
                return min;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Double> getMapper(Supplier<Key, Value, Double> supplier) {
        return new SupplierConsumingMapper<Key, Value, Double>(supplier);
    }

    @Override
    public CombinerFactory<Key, Double, Double> getCombinerFactory() {
        return new DoubleMinCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Double, Double> getReducerFactory() {
        return new DoubleMinReducerFactory();
    }

    private static final class DoubleMinReducer
    extends Reducer<Double, Double> {
        private double min = Double.MAX_VALUE;

        private DoubleMinReducer() {
        }

        @Override
        public void reduce(Double value) {
            if (value < this.min) {
                this.min = value;
            }
        }

        @Override
        public Double finalizeReduce() {
            return this.min;
        }
    }

    private static final class DoubleMinCombiner
    extends Combiner<Double, Double> {
        private double chunkMin = Double.MAX_VALUE;

        private DoubleMinCombiner() {
        }

        @Override
        public void combine(Double value) {
            if (value < this.chunkMin) {
                this.chunkMin = value;
            }
        }

        @Override
        public Double finalizeChunk() {
            double value = this.chunkMin;
            this.chunkMin = Double.MAX_VALUE;
            return value;
        }
    }

    @BinaryInterface
    static final class DoubleMinReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Double, Double> {
        DoubleMinReducerFactory() {
        }

        @Override
        public Reducer<Double, Double> newReducer(Key key) {
            return new DoubleMinReducer();
        }

        @Override
        public int getId() {
            return 33;
        }
    }

    @BinaryInterface
    static final class DoubleMinCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Double, Double> {
        DoubleMinCombinerFactory() {
        }

        @Override
        public Combiner<Double, Double> newCombiner(Key key) {
            return new DoubleMinCombiner();
        }

        @Override
        public int getId() {
            return 32;
        }
    }
}

