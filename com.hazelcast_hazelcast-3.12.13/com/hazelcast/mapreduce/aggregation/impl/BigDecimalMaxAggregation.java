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

public class BigDecimalMaxAggregation<Key, Value>
implements AggType<Key, Value, Key, BigDecimal, BigDecimal, BigDecimal, BigDecimal> {
    @Override
    public Collator<Map.Entry<Key, BigDecimal>, BigDecimal> getCollator() {
        return new Collator<Map.Entry<Key, BigDecimal>, BigDecimal>(){

            @Override
            public BigDecimal collate(Iterable<Map.Entry<Key, BigDecimal>> values) {
                BigDecimal max = null;
                for (Map.Entry entry : values) {
                    BigDecimal value = entry.getValue();
                    max = max == null ? value : value.max(max);
                }
                return max;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigDecimal> getMapper(Supplier<Key, Value, BigDecimal> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigDecimal>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigDecimal, BigDecimal> getCombinerFactory() {
        return new BigDecimalMaxCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, BigDecimal, BigDecimal> getReducerFactory() {
        return new BigDecimalMaxReducerFactory();
    }

    private static final class BigDecimalMaxReducer
    extends Reducer<BigDecimal, BigDecimal> {
        private BigDecimal max;

        private BigDecimalMaxReducer() {
        }

        @Override
        public void reduce(BigDecimal value) {
            this.max = this.max == null ? value : value.max(this.max);
        }

        @Override
        public BigDecimal finalizeReduce() {
            return this.max;
        }
    }

    private static final class BigDecimalMaxCombiner
    extends Combiner<BigDecimal, BigDecimal> {
        private BigDecimal max;

        private BigDecimalMaxCombiner() {
        }

        @Override
        public void combine(BigDecimal value) {
            this.max = this.max == null ? value : value.max(this.max);
        }

        @Override
        public BigDecimal finalizeChunk() {
            return this.max;
        }

        @Override
        public void reset() {
            this.max = null;
        }
    }

    @BinaryInterface
    static final class BigDecimalMaxReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, BigDecimal, BigDecimal> {
        BigDecimalMaxReducerFactory() {
        }

        @Override
        public Reducer<BigDecimal, BigDecimal> newReducer(Key key) {
            return new BigDecimalMaxReducer();
        }

        @Override
        public int getId() {
            return 7;
        }
    }

    @BinaryInterface
    static final class BigDecimalMaxCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigDecimal, BigDecimal> {
        BigDecimalMaxCombinerFactory() {
        }

        @Override
        public Combiner<BigDecimal, BigDecimal> newCombiner(Key key) {
            return new BigDecimalMaxCombiner();
        }

        @Override
        public int getId() {
            return 6;
        }
    }
}

