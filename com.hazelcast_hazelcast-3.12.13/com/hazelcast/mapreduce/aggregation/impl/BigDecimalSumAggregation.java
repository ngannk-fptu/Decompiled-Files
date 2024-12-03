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
import java.math.BigDecimal;
import java.util.Map;

public class BigDecimalSumAggregation<Key, Value>
implements AggType<Key, Value, Key, BigDecimal, BigDecimal, BigDecimal, BigDecimal> {
    @Override
    public Collator<Map.Entry<Key, BigDecimal>, BigDecimal> getCollator() {
        return new Collator<Map.Entry<Key, BigDecimal>, BigDecimal>(){

            @Override
            public BigDecimal collate(Iterable<Map.Entry<Key, BigDecimal>> values) {
                BigDecimal sum = BigDecimal.ZERO;
                for (Map.Entry entry : values) {
                    sum = sum.add(entry.getValue());
                }
                return sum;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigDecimal> getMapper(Supplier<Key, Value, BigDecimal> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigDecimal>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigDecimal, BigDecimal> getCombinerFactory() {
        return new BigDecimalSumCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, BigDecimal, BigDecimal> getReducerFactory() {
        return new BigDecimalSumReducerFactory();
    }

    private static final class BigDecimalSumReducer
    extends Reducer<BigDecimal, BigDecimal> {
        private BigDecimal sum = BigDecimal.ZERO;

        private BigDecimalSumReducer() {
        }

        @Override
        public void reduce(BigDecimal value) {
            this.sum = this.sum.add(value);
        }

        @Override
        public BigDecimal finalizeReduce() {
            return this.sum;
        }
    }

    private static final class BigDecimalSumCombiner
    extends Combiner<BigDecimal, BigDecimal> {
        private BigDecimal sum = BigDecimal.ZERO;

        private BigDecimalSumCombiner() {
        }

        @Override
        public void combine(BigDecimal value) {
            this.sum = this.sum.add(value);
        }

        @Override
        public BigDecimal finalizeChunk() {
            return this.sum;
        }

        @Override
        public void reset() {
            this.sum = BigDecimal.ZERO;
        }
    }

    @BinaryInterface
    static final class BigDecimalSumReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, BigDecimal, BigDecimal> {
        BigDecimalSumReducerFactory() {
        }

        @Override
        public Reducer<BigDecimal, BigDecimal> newReducer(Key key) {
            return new BigDecimalSumReducer();
        }

        @Override
        public int getId() {
            return 11;
        }
    }

    @BinaryInterface
    static final class BigDecimalSumCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigDecimal, BigDecimal> {
        BigDecimalSumCombinerFactory() {
        }

        @Override
        public Combiner<BigDecimal, BigDecimal> newCombiner(Key key) {
            return new BigDecimalSumCombiner();
        }

        @Override
        public int getId() {
            return 10;
        }
    }
}

