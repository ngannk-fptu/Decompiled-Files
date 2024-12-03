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

public class ComparableMaxAggregation<Key, Value>
implements AggType<Key, Value, Key, Comparable, Comparable, Comparable, Comparable> {
    @Override
    public Collator<Map.Entry<Key, Comparable>, Comparable> getCollator() {
        return new Collator<Map.Entry<Key, Comparable>, Comparable>(){

            @Override
            public Comparable collate(Iterable<Map.Entry<Key, Comparable>> values) {
                Comparable max = null;
                for (Map.Entry entry : values) {
                    Comparable value = entry.getValue();
                    if (max != null && value.compareTo(max) <= 0) continue;
                    max = value;
                }
                return max;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Key, Comparable> getMapper(Supplier<Key, Value, Comparable> supplier) {
        return new SupplierConsumingMapper<Key, Value, Comparable>(supplier);
    }

    @Override
    public CombinerFactory<Key, Comparable, Comparable> getCombinerFactory() {
        return new ComparableMaxCombinerFactory();
    }

    @Override
    public ReducerFactory<Key, Comparable, Comparable> getReducerFactory() {
        return new ComparableMaxReducerFactory();
    }

    private static final class ComparableMaxReducer
    extends Reducer<Comparable, Comparable> {
        private Comparable max;

        private ComparableMaxReducer() {
        }

        @Override
        public void reduce(Comparable value) {
            if (this.max == null || value.compareTo(this.max) > 0) {
                this.max = value;
            }
        }

        @Override
        public Comparable finalizeReduce() {
            return this.max;
        }
    }

    private static final class ComparableMaxCombiner
    extends Combiner<Comparable, Comparable> {
        private Comparable max;

        private ComparableMaxCombiner() {
        }

        @Override
        public void combine(Comparable value) {
            if (this.max == null || value.compareTo(this.max) > 0) {
                this.max = value;
            }
        }

        @Override
        public Comparable finalizeChunk() {
            Comparable value = this.max;
            this.max = null;
            return value;
        }
    }

    @BinaryInterface
    static final class ComparableMaxReducerFactory<Key>
    extends AbstractAggregationReducerFactory<Key, Comparable, Comparable> {
        ComparableMaxReducerFactory() {
        }

        @Override
        public Reducer<Comparable, Comparable> newReducer(Key key) {
            return new ComparableMaxReducer();
        }

        @Override
        public int getId() {
            return 21;
        }
    }

    @BinaryInterface
    static final class ComparableMaxCombinerFactory<Key>
    extends AbstractAggregationCombinerFactory<Key, Comparable, Comparable> {
        ComparableMaxCombinerFactory() {
        }

        @Override
        public Combiner<Comparable, Comparable> newCombiner(Key key) {
            return new ComparableMaxCombiner();
        }

        @Override
        public int getId() {
            return 20;
        }
    }
}

