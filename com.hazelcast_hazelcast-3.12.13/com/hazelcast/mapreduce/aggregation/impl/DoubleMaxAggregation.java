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

public class DoubleMaxAggregation<Key, Value>
implements AggType<Key, Value, Key, Double, Double, Double, Double> {
    @Override
    public Collator<Map.Entry<Key, Double>, Double> getCollator() {
        return new Collator<Map.Entry<Key, Double>, Double>(){

            @Override
            public Double collate(Iterable<Map.Entry<Key, Double>> values) {
                double max = -1.7976931348623157E308;
                for (Map.Entry entry : values) {
                    double value = entry.getValue();
                    if (!(value > max)) continue;
                    max = value;
                }
                return max;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Double> getMapper(Supplier<Key, Value, Double> supplier) {
        return new SupplierConsumingMapper<Key, Value, Double>(supplier);
    }

    @Override
    public CombinerFactory<Key, Double, Double> getCombinerFactory() {
        return new DoubleMaxCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Double, Double> getReducerFactory() {
        return new DoubleMaxReducerFactory();
    }

    private static final class DoubleMaxReducer
    extends Reducer<Double, Double> {
        private double max = -1.7976931348623157E308;

        private DoubleMaxReducer() {
        }

        @Override
        public void reduce(Double value) {
            if (value > this.max) {
                this.max = value;
            }
        }

        @Override
        public Double finalizeReduce() {
            return this.max;
        }
    }

    private static final class DoubleMaxCombiner
    extends Combiner<Double, Double> {
        private double chunkMax = -1.7976931348623157E308;

        private DoubleMaxCombiner() {
        }

        @Override
        public void combine(Double value) {
            if (value > this.chunkMax) {
                this.chunkMax = value;
            }
        }

        @Override
        public Double finalizeChunk() {
            double value = this.chunkMax;
            this.chunkMax = -1.7976931348623157E308;
            return value;
        }
    }

    @BinaryInterface
    static final class DoubleMaxReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Double, Double> {
        DoubleMaxReducerFactory() {
        }

        @Override
        public Reducer<Double, Double> newReducer(Key key) {
            return new DoubleMaxReducer();
        }

        @Override
        public int getId() {
            return 31;
        }
    }

    @BinaryInterface
    static final class DoubleMaxCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Double, Double> {
        DoubleMaxCombinerFactory() {
        }

        @Override
        public Combiner<Double, Double> newCombiner(Key key) {
            return new DoubleMaxCombiner();
        }

        @Override
        public int getId() {
            return 30;
        }
    }
}

