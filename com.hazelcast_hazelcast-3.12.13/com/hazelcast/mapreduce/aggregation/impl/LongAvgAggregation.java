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
import java.util.Map;

public class LongAvgAggregation<Key, Value>
implements AggType<Key, Value, Key, Long, AvgTuple<Long, Long>, AvgTuple<Long, Long>, Long> {
    @Override
    public Collator<Map.Entry<Key, AvgTuple<Long, Long>>, Long> getCollator() {
        return new Collator<Map.Entry<Key, AvgTuple<Long, Long>>, Long>(){

            @Override
            public Long collate(Iterable<Map.Entry<Key, AvgTuple<Long, Long>>> values) {
                long count = 0L;
                long amount = 0L;
                for (Map.Entry entry : values) {
                    AvgTuple<Long, Long> tuple = entry.getValue();
                    count += tuple.getFirst().longValue();
                    amount += tuple.getSecond().longValue();
                }
                return (long)((double)amount / (double)count);
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Long> getMapper(Supplier<Key, Value, Long> supplier) {
        return new SupplierConsumingMapper<Key, Value, Long>(supplier);
    }

    @Override
    public CombinerFactory<Key, Long, AvgTuple<Long, Long>> getCombinerFactory() {
        return new LongAvgCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, AvgTuple<Long, Long>, AvgTuple<Long, Long>> getReducerFactory() {
        return new LongAvgReducerFactory();
    }

    private static final class LongAvgReducer
    extends Reducer<AvgTuple<Long, Long>, AvgTuple<Long, Long>> {
        private long count;
        private long amount;

        private LongAvgReducer() {
        }

        @Override
        public void reduce(AvgTuple<Long, Long> value) {
            this.count += value.getFirst().longValue();
            this.amount += value.getSecond().longValue();
        }

        @Override
        public AvgTuple<Long, Long> finalizeReduce() {
            return new AvgTuple<Long, Long>(this.count, this.amount);
        }
    }

    private static final class LongAvgCombiner
    extends Combiner<Long, AvgTuple<Long, Long>> {
        private long count;
        private long amount;

        private LongAvgCombiner() {
        }

        @Override
        public void combine(Long value) {
            ++this.count;
            this.amount += value.longValue();
        }

        @Override
        public AvgTuple<Long, Long> finalizeChunk() {
            long count = this.count;
            long amount = this.amount;
            this.count = 0L;
            this.amount = 0L;
            return new AvgTuple<Long, Long>(count, amount);
        }
    }

    @BinaryInterface
    static final class LongAvgReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, AvgTuple<Long, Long>, AvgTuple<Long, Long>> {
        LongAvgReducerFactory() {
        }

        @Override
        public Reducer<AvgTuple<Long, Long>, AvgTuple<Long, Long>> newReducer(Key key) {
            return new LongAvgReducer();
        }

        @Override
        public int getId() {
            return 45;
        }
    }

    @BinaryInterface
    static final class LongAvgCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Long, AvgTuple<Long, Long>> {
        LongAvgCombinerFactory() {
        }

        @Override
        public Combiner<Long, AvgTuple<Long, Long>> newCombiner(Key key) {
            return new LongAvgCombiner();
        }

        @Override
        public int getId() {
            return 44;
        }
    }
}

