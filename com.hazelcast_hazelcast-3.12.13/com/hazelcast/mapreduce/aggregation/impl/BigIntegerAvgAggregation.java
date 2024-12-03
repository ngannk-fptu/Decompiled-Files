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
import java.math.BigInteger;
import java.util.Map;

public class BigIntegerAvgAggregation<Key, Value>
implements AggType<Key, Value, Key, BigInteger, AvgTuple<Long, BigInteger>, AvgTuple<Long, BigInteger>, BigInteger> {
    @Override
    public Collator<Map.Entry<Key, AvgTuple<Long, BigInteger>>, BigInteger> getCollator() {
        return new Collator<Map.Entry<Key, AvgTuple<Long, BigInteger>>, BigInteger>(){

            @Override
            public BigInteger collate(Iterable<Map.Entry<Key, AvgTuple<Long, BigInteger>>> values) {
                long count = 0L;
                BigInteger amount = BigInteger.ZERO;
                for (Map.Entry entry : values) {
                    AvgTuple<Long, BigInteger> tuple = entry.getValue();
                    count += tuple.getFirst().longValue();
                    amount = amount.add(tuple.getSecond());
                }
                return amount.divide(BigInteger.valueOf(count));
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigInteger> getMapper(Supplier<Key, Value, BigInteger> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigInteger>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigInteger, AvgTuple<Long, BigInteger>> getCombinerFactory() {
        return new BigIntegerAvgCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, AvgTuple<Long, BigInteger>, AvgTuple<Long, BigInteger>> getReducerFactory() {
        return new BigIntegerAvgReducerFactory();
    }

    private static final class BigIntegerAvgReducer
    extends Reducer<AvgTuple<Long, BigInteger>, AvgTuple<Long, BigInteger>> {
        private long count;
        private BigInteger amount = BigInteger.ZERO;

        private BigIntegerAvgReducer() {
        }

        @Override
        public void reduce(AvgTuple<Long, BigInteger> value) {
            this.count += value.getFirst().longValue();
            this.amount = this.amount.add(value.getSecond());
        }

        @Override
        public AvgTuple<Long, BigInteger> finalizeReduce() {
            return new AvgTuple<Long, BigInteger>(this.count, this.amount);
        }
    }

    private static final class BigIntegerAvgCombiner
    extends Combiner<BigInteger, AvgTuple<Long, BigInteger>> {
        private long count;
        private BigInteger amount = BigInteger.ZERO;

        private BigIntegerAvgCombiner() {
        }

        @Override
        public void combine(BigInteger value) {
            ++this.count;
            this.amount = this.amount.add(value);
        }

        @Override
        public AvgTuple<Long, BigInteger> finalizeChunk() {
            return new AvgTuple<Long, BigInteger>(this.count, this.amount);
        }

        @Override
        public void reset() {
            this.count = 0L;
            this.amount = BigInteger.ZERO;
        }
    }

    @BinaryInterface
    static final class BigIntegerAvgReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, AvgTuple<Long, BigInteger>, AvgTuple<Long, BigInteger>> {
        BigIntegerAvgReducerFactory() {
        }

        @Override
        public Reducer<AvgTuple<Long, BigInteger>, AvgTuple<Long, BigInteger>> newReducer(Key key) {
            return new BigIntegerAvgReducer();
        }

        @Override
        public int getId() {
            return 13;
        }
    }

    @BinaryInterface
    static final class BigIntegerAvgCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigInteger, AvgTuple<Long, BigInteger>> {
        BigIntegerAvgCombinerFactory() {
        }

        @Override
        public Combiner<BigInteger, AvgTuple<Long, BigInteger>> newCombiner(Key key) {
            return new BigIntegerAvgCombiner();
        }

        @Override
        public int getId() {
            return 12;
        }
    }
}

