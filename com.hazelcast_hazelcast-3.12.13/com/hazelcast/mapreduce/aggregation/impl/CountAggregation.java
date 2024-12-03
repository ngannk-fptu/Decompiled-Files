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

public class CountAggregation<Key, Value>
implements AggType<Key, Value, Key, Object, Long, Long, Long> {
    @Override
    public Collator<Map.Entry<Key, Long>, Long> getCollator() {
        return new Collator<Map.Entry<Key, Long>, Long>(){

            @Override
            public Long collate(Iterable<Map.Entry<Key, Long>> values) {
                long count = 0L;
                for (Map.Entry entry : values) {
                    count += entry.getValue().longValue();
                }
                return count;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Object> getMapper(Supplier<Key, Value, Object> supplier) {
        return new SupplierConsumingMapper<Key, Value, Object>(supplier);
    }

    @Override
    public CombinerFactory<Key, Object, Long> getCombinerFactory() {
        return new CountCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Long, Long> getReducerFactory() {
        return new CountReducerFactory();
    }

    private static final class CountReducer
    extends Reducer<Long, Long> {
        private long count;

        private CountReducer() {
        }

        @Override
        public void reduce(Long value) {
            this.count += value.longValue();
        }

        @Override
        public Long finalizeReduce() {
            return this.count;
        }
    }

    private static final class CountCombiner
    extends Combiner<Object, Long> {
        private long chunkCount;

        private CountCombiner() {
        }

        @Override
        public void combine(Object value) {
            ++this.chunkCount;
        }

        @Override
        public Long finalizeChunk() {
            long value = this.chunkCount;
            this.chunkCount = 0L;
            return value;
        }
    }

    @BinaryInterface
    static final class CountReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Long, Long> {
        CountReducerFactory() {
        }

        @Override
        public Reducer<Long, Long> newReducer(Key key) {
            return new CountReducer();
        }

        @Override
        public int getId() {
            return 25;
        }
    }

    @BinaryInterface
    static final class CountCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Object, Long> {
        CountCombinerFactory() {
        }

        @Override
        public Combiner<Object, Long> newCombiner(Key key) {
            return new CountCombiner();
        }

        @Override
        public int getId() {
            return 24;
        }
    }
}

