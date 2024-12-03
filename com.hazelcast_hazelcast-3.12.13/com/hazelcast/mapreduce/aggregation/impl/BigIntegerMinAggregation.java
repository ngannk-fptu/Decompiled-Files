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

public class BigIntegerMinAggregation<Key, Value>
implements AggType<Key, Value, Key, BigInteger, BigInteger, BigInteger, BigInteger> {
    @Override
    public Collator<Map.Entry<Key, BigInteger>, BigInteger> getCollator() {
        return new Collator<Map.Entry<Key, BigInteger>, BigInteger>(){

            @Override
            public BigInteger collate(Iterable<Map.Entry<Key, BigInteger>> values) {
                BigInteger min = null;
                for (Map.Entry entry : values) {
                    BigInteger value = entry.getValue();
                    min = min == null ? value : value.min(min);
                }
                return min;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, BigInteger> getMapper(Supplier<Key, Value, BigInteger> supplier) {
        return new SupplierConsumingMapper<Key, Value, BigInteger>(supplier);
    }

    @Override
    public CombinerFactory<Key, BigInteger, BigInteger> getCombinerFactory() {
        return new BigIntegerMinCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, BigInteger, BigInteger> getReducerFactory() {
        return new BigIntegerMinReducerFactory();
    }

    private static final class BigIntegerMinReducer
    extends Reducer<BigInteger, BigInteger> {
        private BigInteger min;

        private BigIntegerMinReducer() {
        }

        @Override
        public void reduce(BigInteger value) {
            this.min = this.min == null ? value : value.min(this.min);
        }

        @Override
        public BigInteger finalizeReduce() {
            return this.min;
        }
    }

    private static final class BigIntegerMinCombiner
    extends Combiner<BigInteger, BigInteger> {
        private BigInteger min;

        private BigIntegerMinCombiner() {
        }

        @Override
        public void combine(BigInteger value) {
            this.min = this.min == null ? value : value.min(this.min);
        }

        @Override
        public BigInteger finalizeChunk() {
            return this.min;
        }

        @Override
        public void reset() {
            this.min = null;
        }
    }

    @BinaryInterface
    static final class BigIntegerMinReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, BigInteger, BigInteger> {
        BigIntegerMinReducerFactory() {
        }

        @Override
        public Reducer<BigInteger, BigInteger> newReducer(Key key) {
            return new BigIntegerMinReducer();
        }

        @Override
        public int getId() {
            return 17;
        }
    }

    @BinaryInterface
    static final class BigIntegerMinCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, BigInteger, BigInteger> {
        BigIntegerMinCombinerFactory() {
        }

        @Override
        public Combiner<BigInteger, BigInteger> newCombiner(Key key) {
            return new BigIntegerMinCombiner();
        }

        @Override
        public int getId() {
            return 16;
        }
    }
}

