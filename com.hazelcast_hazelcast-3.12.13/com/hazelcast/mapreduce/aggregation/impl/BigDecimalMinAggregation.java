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

public class BigDecimalMinAggregation<Key, Value>
implements AggType<Key, Value, Key, BigDecimal, BigDecimal, BigDecimal, BigDecimal> {
    @Override
    public Collator<Map.Entry<Key, BigDecimal>, BigDecimal> getCollator() {
        return new Collator<Map.Entry<Key, BigDecimal>, BigDecimal>(){

            @Override
            public BigDecimal collate(Iterable<Map.Entry<Key, BigDecimal>> values) {
                BigDecimal min = null;
                for (Map.Entry entry : values) {
                    BigDecimal value = entry.getValue();
                    min = min == null ? value : value.min(min);
                }
                return min;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigDecimal> getMapper(Supplier<Key, Value, BigDecimal> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigDecimal>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigDecimal, BigDecimal> getCombinerFactory() {
        return new BigDecimalMinCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, BigDecimal, BigDecimal> getReducerFactory() {
        return new BigDecimalMinReducerFactory();
    }

    private static final class BigDecimalMinReducer
    extends Reducer<BigDecimal, BigDecimal> {
        private BigDecimal min;

        private BigDecimalMinReducer() {
        }

        @Override
        public void reduce(BigDecimal value) {
            this.min = this.min == null ? value : value.min(this.min);
        }

        @Override
        public BigDecimal finalizeReduce() {
            return this.min;
        }
    }

    private static final class BigDecimalMinCombiner
    extends Combiner<BigDecimal, BigDecimal> {
        private BigDecimal min;

        private BigDecimalMinCombiner() {
        }

        @Override
        public void combine(BigDecimal value) {
            this.min = this.min == null ? value : value.min(this.min);
        }

        @Override
        public BigDecimal finalizeChunk() {
            return this.min;
        }

        @Override
        public void reset() {
            this.min = null;
        }
    }

    @BinaryInterface
    static final class BigDecimalMinReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, BigDecimal, BigDecimal> {
        BigDecimalMinReducerFactory() {
        }

        @Override
        public Reducer<BigDecimal, BigDecimal> newReducer(Key key) {
            return new BigDecimalMinReducer();
        }

        @Override
        public int getId() {
            return 9;
        }
    }

    @BinaryInterface
    static final class BigDecimalMinCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigDecimal, BigDecimal> {
        BigDecimalMinCombinerFactory() {
        }

        @Override
        public Combiner<BigDecimal, BigDecimal> newCombiner(Key key) {
            return new BigDecimalMinCombiner();
        }

        @Override
        public int getId() {
            return 8;
        }
    }
}

