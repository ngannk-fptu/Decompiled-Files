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

public class BigIntegerMaxAggregation<Key, Value>
implements AggType<Key, Value, Key, BigInteger, BigInteger, BigInteger, BigInteger> {
    @Override
    public Collator<Map.Entry<Key, BigInteger>, BigInteger> getCollator() {
        return new Collator<Map.Entry<Key, BigInteger>, BigInteger>(){

            @Override
            public BigInteger collate(Iterable<Map.Entry<Key, BigInteger>> values) {
                BigInteger max = null;
                for (Map.Entry entry : values) {
                    BigInteger value = entry.getValue();
                    max = max == null ? value : value.max(max);
                }
                return max;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigInteger> getMapper(Supplier<Key, Value, BigInteger> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigInteger>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigInteger, BigInteger> getCombinerFactory() {
        return new BigIntegerMaxCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, BigInteger, BigInteger> getReducerFactory() {
        return new BigIntegerMaxReducerFactory();
    }

    private static final class BigIntegerMaxReducer
    extends Reducer<BigInteger, BigInteger> {
        private BigInteger max;

        private BigIntegerMaxReducer() {
        }

        @Override
        public void reduce(BigInteger value) {
            this.max = this.max == null ? value : value.max(this.max);
        }

        @Override
        public BigInteger finalizeReduce() {
            return this.max;
        }
    }

    private static final class BigIntegerMaxCombiner
    extends Combiner<BigInteger, BigInteger> {
        private BigInteger max;

        private BigIntegerMaxCombiner() {
        }

        @Override
        public void combine(BigInteger value) {
            this.max = this.max == null ? value : value.max(this.max);
        }

        @Override
        public BigInteger finalizeChunk() {
            return this.max;
        }

        @Override
        public void reset() {
            this.max = null;
        }
    }

    @BinaryInterface
    static final class BigIntegerMaxReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, BigInteger, BigInteger> {
        BigIntegerMaxReducerFactory() {
        }

        @Override
        public Reducer<BigInteger, BigInteger> newReducer(Key key) {
            return new BigIntegerMaxReducer();
        }

        @Override
        public int getId() {
            return 15;
        }
    }

    @BinaryInterface
    static final class BigIntegerMaxCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigInteger, BigInteger> {
        BigIntegerMaxCombinerFactory() {
        }

        @Override
        public Combiner<BigInteger, BigInteger> newCombiner(Key key) {
            return new BigIntegerMaxCombiner();
        }

        @Override
        public int getId() {
            return 14;
        }
    }
}

