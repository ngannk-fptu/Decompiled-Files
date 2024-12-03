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

public class ComparableMinAggregation<Key, Value>
implements AggType<Key, Value, Key, Comparable, Comparable, Comparable, Comparable> {
    @Override
    public Collator<Map.Entry<Key, Comparable>, Comparable> getCollator() {
        return new Collator<Map.Entry<Key, Comparable>, Comparable>(){

            @Override
            public Comparable collate(Iterable<Map.Entry<Key, Comparable>> values) {
                Comparable min = null;
                for (Map.Entry entry : values) {
                    Comparable value = entry.getValue();
                    if (min != null && value.compareTo(min) >= 0) continue;
                    min = value;
                }
                return min;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Comparable> getMapper(Supplier<Key, Value, Comparable> supplier) {
        return new SupplierConsumingMapper<Key, Value, Comparable>(supplier);
    }

    @Override
    public CombinerFactory<Key, Comparable, Comparable> getCombinerFactory() {
        return new ComparableMinCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Comparable, Comparable> getReducerFactory() {
        return new ComparableMinReducerFactory();
    }

    private static final class ComparableMinReducer
    extends Reducer<Comparable, Comparable> {
        private Comparable min;

        private ComparableMinReducer() {
        }

        @Override
        public void reduce(Comparable value) {
            if (this.min == null || value.compareTo(this.min) < 0) {
                this.min = value;
            }
        }

        @Override
        public Comparable finalizeReduce() {
            return this.min;
        }
    }

    private static final class ComparableMinCombiner
    extends Combiner<Comparable, Comparable> {
        private Comparable min;

        private ComparableMinCombiner() {
        }

        @Override
        public void combine(Comparable value) {
            if (this.min == null || value.compareTo(this.min) < 0) {
                this.min = value;
            }
        }

        @Override
        public Comparable finalizeChunk() {
            Comparable value = this.min;
            this.min = null;
            return value;
        }
    }

    @BinaryInterface
    static final class ComparableMinReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Comparable, Comparable> {
        ComparableMinReducerFactory() {
        }

        @Override
        public Reducer<Comparable, Comparable> newReducer(Key key) {
            return new ComparableMinReducer();
        }

        @Override
        public int getId() {
            return 23;
        }
    }

    @BinaryInterface
    static final class ComparableMinCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Comparable, Comparable> {
        ComparableMinCombinerFactory() {
        }

        @Override
        public Combiner<Comparable, Comparable> newCombiner(Key key) {
            return new ComparableMinCombiner();
        }

        @Override
        public int getId() {
            return 22;
        }
    }
}

