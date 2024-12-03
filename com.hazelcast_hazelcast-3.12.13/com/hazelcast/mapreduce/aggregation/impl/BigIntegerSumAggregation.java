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
import java.math.BigInteger;
import java.util.Map;

public class BigIntegerSumAggregation<Key, Value>
implements AggType<Key, Value, Key, BigInteger, BigInteger, BigInteger, BigInteger> {
    @Override
    public Collator<Map.Entry<Key, BigInteger>, BigInteger> getCollator() {
        return new Collator<Map.Entry<Key, BigInteger>, BigInteger>(){

            @Override
            public BigInteger collate(Iterable<Map.Entry<Key, BigInteger>> values) {
                BigInteger sum = BigInteger.ZERO;
                for (Map.Entry entry : values) {
                    sum = sum.add(entry.getValue());
                }
                return sum;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigInteger> getMapper(Supplier<Key, Value, BigInteger> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigInteger>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigInteger, BigInteger> getCombinerFactory() {
        return new BigIntegerSumCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, BigInteger, BigInteger> getReducerFactory() {
        return new BigIntegerSumReducerFactory();
    }

    private static final class BigIntegerSumReducer
    extends Reducer<BigInteger, BigInteger> {
        private BigInteger sum = BigInteger.ZERO;

        private BigIntegerSumReducer() {
        }

        @Override
        public void reduce(BigInteger value) {
            this.sum = this.sum.add(value);
        }

        @Override
        public BigInteger finalizeReduce() {
            return this.sum;
        }
    }

    private static final class BigIntegerSumCombiner
    extends Combiner<BigInteger, BigInteger> {
        private BigInteger sum = BigInteger.ZERO;

        private BigIntegerSumCombiner() {
        }

        @Override
        public void combine(BigInteger value) {
            this.sum = this.sum.add(value);
        }

        @Override
        public BigInteger finalizeChunk() {
            return this.sum;
        }

        @Override
        public void reset() {
            this.sum = BigInteger.ZERO;
        }
    }

    @BinaryInterface
    static final class BigIntegerSumReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, BigInteger, BigInteger> {
        BigIntegerSumReducerFactory() {
        }

        @Override
        public Reducer<BigInteger, BigInteger> newReducer(Key key) {
            return new BigIntegerSumReducer();
        }

        @Override
        public int getId() {
            return 19;
        }
    }

    @BinaryInterface
    static final class BigIntegerSumCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigInteger, BigInteger> {
        BigIntegerSumCombinerFactory() {
        }

        @Override
        public Combiner<BigInteger, BigInteger> newCombiner(Key key) {
            return new BigIntegerSumCombiner();
        }

        @Override
        public int getId() {
            return 18;
        }
    }
}

