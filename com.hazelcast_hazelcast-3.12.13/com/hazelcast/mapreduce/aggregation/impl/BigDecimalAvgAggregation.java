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
import java.math.BigDecimal;
import java.util.Map;

public class BigDecimalAvgAggregation<Key, Value>
implements AggType<Key, Value, Key, BigDecimal, AvgTuple<Long, BigDecimal>, AvgTuple<Long, BigDecimal>, BigDecimal> {
    @Override
    public Collator<Map.Entry<Key, AvgTuple<Long, BigDecimal>>, BigDecimal> getCollator() {
        return new Collator<Map.Entry<Key, AvgTuple<Long, BigDecimal>>, BigDecimal>(){

            @Override
            public BigDecimal collate(Iterable<Map.Entry<Key, AvgTuple<Long, BigDecimal>>> values) {
                long count = 0L;
                BigDecimal amount = BigDecimal.ZERO;
                for (Map.Entry entry : values) {
                    AvgTuple<Long, BigDecimal> tuple = entry.getValue();
                    count += tuple.getFirst().longValue();
                    amount = amount.add(tuple.getSecond());
                }
                return amount.divide(BigDecimal.valueOf(count));
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigDecimal> getMapper(Supplier<Key, Value, BigDecimal> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigDecimal>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigDecimal, AvgTuple<Long, BigDecimal>> getCombinerFactory() {
        return new BigDecimalAvgCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, AvgTuple<Long, BigDecimal>, AvgTuple<Long, BigDecimal>> getReducerFactory() {
        return new BigDecimalAvgReducerFactory();
    }

    private static final class BigDecimalAvgReducer
    extends Reducer<AvgTuple<Long, BigDecimal>, AvgTuple<Long, BigDecimal>> {
        private long count;
        private BigDecimal amount = BigDecimal.ZERO;

        private BigDecimalAvgReducer() {
        }

        @Override
        public void reduce(AvgTuple<Long, BigDecimal> value) {
            this.count += value.getFirst().longValue();
            this.amount = this.amount.add(value.getSecond());
        }

        @Override
        public AvgTuple<Long, BigDecimal> finalizeReduce() {
            return new AvgTuple<Long, BigDecimal>(this.count, this.amount);
        }
    }

    private static final class BigDecimalAvgCombiner
    extends Combiner<BigDecimal, AvgTuple<Long, BigDecimal>> {
        private long count;
        private BigDecimal amount = BigDecimal.ZERO;

        private BigDecimalAvgCombiner() {
        }

        @Override
        public void combine(BigDecimal value) {
            ++this.count;
            this.amount = this.amount.add(value);
        }

        @Override
        public AvgTuple<Long, BigDecimal> finalizeChunk() {
            return new AvgTuple<Long, BigDecimal>(this.count, this.amount);
        }

        @Override
        public void reset() {
            this.count = 0L;
            this.amount = BigDecimal.ZERO;
        }
    }

    @BinaryInterface
    static final class BigDecimalAvgReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, AvgTuple<Long, BigDecimal>, AvgTuple<Long, BigDecimal>> {
        BigDecimalAvgReducerFactory() {
        }

        @Override
        public Reducer<AvgTuple<Long, BigDecimal>, AvgTuple<Long, BigDecimal>> newReducer(Key key) {
            return new BigDecimalAvgReducer();
        }

        @Override
        public int getId() {
            return 5;
        }
    }

    @BinaryInterface
    static final class BigDecimalAvgCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigDecimal, AvgTuple<Long, BigDecimal>> {
        BigDecimalAvgCombinerFactory() {
        }

        @Override
        public Combiner<BigDecimal, AvgTuple<Long, BigDecimal>> newCombiner(Key key) {
            return new BigDecimalAvgCombiner();
        }

        @Override
        public int getId() {
            return 4;
        }
    }
}

