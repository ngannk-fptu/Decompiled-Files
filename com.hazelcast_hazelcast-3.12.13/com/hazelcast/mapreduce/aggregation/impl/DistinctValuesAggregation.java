/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.mapreduce.aggregation.impl;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.mapreduce.aggregation.impl.AbstractAggregationCombinerFactory;
import com.hazelcast.mapreduce.aggregation.impl.AbstractAggregationReducerFactory;
import com.hazelcast.mapreduce.aggregation.impl.AggType;
import com.hazelcast.mapreduce.aggregation.impl.AggregationsDataSerializerHook;
import com.hazelcast.mapreduce.aggregation.impl.SetAdapter;
import com.hazelcast.mapreduce.aggregation.impl.SimpleEntry;
import com.hazelcast.mapreduce.impl.task.DefaultContext;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.getters.Extractors;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DistinctValuesAggregation<Key, Value, DistinctType>
implements AggType<Key, Value, Integer, DistinctType, Set<DistinctType>, Set<DistinctType>, Set<DistinctType>> {
    private static final int DEFAULT_DISTRIBUTION_FACTOR = 20;

    @Override
    public Collator<Map.Entry<Integer, Set<DistinctType>>, Set<DistinctType>> getCollator() {
        return new Collator<Map.Entry<Integer, Set<DistinctType>>, Set<DistinctType>>(){

            @Override
            public Set<DistinctType> collate(Iterable<Map.Entry<Integer, Set<DistinctType>>> values) {
                HashSet distinctValues = new HashSet();
                for (Map.Entry value : values) {
                    distinctValues.addAll(value.getValue());
                }
                return distinctValues;
            }
        };
    }

    @Override
    public Mapper<Key, Value, Integer, DistinctType> getMapper(Supplier<Key, Value, DistinctType> supplier) {
        return new DistinctValueMapper<Key, Value, DistinctType>(supplier);
    }

    @Override
    public CombinerFactory<Integer, DistinctType, Set<DistinctType>> getCombinerFactory() {
        return new DistinctValuesCombinerFactory();
    }

    @Override
    public ReducerFactory<Integer, Set<DistinctType>, Set<DistinctType>> getReducerFactory() {
        return new DistinctValuesReducerFactory();
    }

    @BinaryInterface
    @SuppressFBWarnings(value={"SE_NO_SERIALVERSIONID"})
    static class DistinctValueMapper<Key, Value, DistinctType>
    implements Mapper<Key, Value, Integer, DistinctType>,
    IdentifiedDataSerializable {
        private static final int[] DISTRIBUTION_KEYS;
        private transient SimpleEntry<Key, Value> entry = new SimpleEntry();
        private transient int keyPosition;
        private Supplier<Key, Value, DistinctType> supplier;

        DistinctValueMapper() {
        }

        DistinctValueMapper(Supplier<Key, Value, DistinctType> supplier) {
            this.supplier = supplier;
        }

        @Override
        public void map(Key key, Value value, Context<Integer, DistinctType> context) {
            int mappingKey = this.key();
            this.entry.setKey(key);
            this.entry.setValue(value);
            this.entry.setSerializationService(((DefaultContext)context).getSerializationService());
            this.entry.setExtractors(Extractors.newBuilder(((DefaultContext)context).getSerializationService()).build());
            DistinctType valueOut = this.supplier.apply(this.entry);
            if (valueOut != null) {
                context.emit(mappingKey, valueOut);
            }
        }

        @Override
        public int getFactoryId() {
            return AggregationsDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 2;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(this.supplier);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.supplier = (Supplier)in.readObject();
        }

        private int key() {
            if (this.keyPosition >= DISTRIBUTION_KEYS.length) {
                this.keyPosition = 0;
            }
            return this.keyPosition++;
        }

        static {
            Random random = new Random();
            DISTRIBUTION_KEYS = new int[20];
            for (int i = 0; i < DISTRIBUTION_KEYS.length; ++i) {
                DistinctValueMapper.DISTRIBUTION_KEYS[i] = random.nextInt();
            }
        }
    }

    private static class DistinctValuesReducer<DistinctType>
    extends Reducer<Set<DistinctType>, Set<DistinctType>> {
        private final Set<DistinctType> distinctValues = new SetAdapter<DistinctType>();

        private DistinctValuesReducer() {
        }

        @Override
        public void reduce(Set<DistinctType> value) {
            this.distinctValues.addAll(value);
        }

        @Override
        public Set<DistinctType> finalizeReduce() {
            return this.distinctValues;
        }
    }

    @BinaryInterface
    static class DistinctValuesReducerFactory<DistinctType>
    extends AbstractAggregationReducerFactory<Integer, Set<DistinctType>, Set<DistinctType>> {
        DistinctValuesReducerFactory() {
        }

        @Override
        public Reducer<Set<DistinctType>, Set<DistinctType>> newReducer(Integer key) {
            return new DistinctValuesReducer();
        }

        @Override
        public int getId() {
            return 27;
        }
    }

    @BinaryInterface
    private static class DistinctValuesCombiner<DistinctType>
    extends Combiner<DistinctType, Set<DistinctType>> {
        private final Set<DistinctType> distinctValues = new HashSet<DistinctType>();

        private DistinctValuesCombiner() {
        }

        @Override
        public void combine(DistinctType value) {
            this.distinctValues.add(value);
        }

        @Override
        public Set<DistinctType> finalizeChunk() {
            SetAdapter<DistinctType> distinctValues = new SetAdapter<DistinctType>();
            distinctValues.addAll(this.distinctValues);
            this.distinctValues.clear();
            return distinctValues;
        }
    }

    @BinaryInterface
    static class DistinctValuesCombinerFactory<DistinctType>
    extends AbstractAggregationCombinerFactory<Integer, DistinctType, Set<DistinctType>> {
        DistinctValuesCombinerFactory() {
        }

        @Override
        public Combiner<DistinctType, Set<DistinctType>> newCombiner(Integer key) {
            return new DistinctValuesCombiner();
        }

        @Override
        public int getId() {
            return 26;
        }
    }
}

