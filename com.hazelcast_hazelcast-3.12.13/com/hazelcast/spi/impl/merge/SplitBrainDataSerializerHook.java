/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.merge.AtomicLongMergingValueImpl;
import com.hazelcast.spi.impl.merge.AtomicReferenceMergingValueImpl;
import com.hazelcast.spi.impl.merge.CacheMergingEntryImpl;
import com.hazelcast.spi.impl.merge.CardinalityEstimatorMergingEntry;
import com.hazelcast.spi.impl.merge.CollectionMergingValueImpl;
import com.hazelcast.spi.impl.merge.MapMergingEntryImpl;
import com.hazelcast.spi.impl.merge.MultiMapMergingEntryImpl;
import com.hazelcast.spi.impl.merge.QueueMergingValueImpl;
import com.hazelcast.spi.impl.merge.ReplicatedMapMergingEntryImpl;
import com.hazelcast.spi.impl.merge.RingbufferMergingValueImpl;
import com.hazelcast.spi.impl.merge.ScheduledExecutorMergingEntryImpl;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.spi.merge.ExpirationTimeMergePolicy;
import com.hazelcast.spi.merge.HigherHitsMergePolicy;
import com.hazelcast.spi.merge.HyperLogLogMergePolicy;
import com.hazelcast.spi.merge.LatestAccessMergePolicy;
import com.hazelcast.spi.merge.LatestUpdateMergePolicy;
import com.hazelcast.spi.merge.PassThroughMergePolicy;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import com.hazelcast.util.ConstructorFunction;

public final class SplitBrainDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.split_brain", -47);
    public static final int COLLECTION_MERGING_VALUE = 0;
    public static final int QUEUE_MERGING_VALUE = 1;
    public static final int ATOMIC_LONG_MERGING_VALUE = 2;
    public static final int ATOMIC_REFERENCE_MERGING_VALUE = 3;
    public static final int MAP_MERGING_ENTRY = 4;
    public static final int CACHE_MERGING_ENTRY = 5;
    public static final int MULTI_MAP_MERGING_ENTRY = 6;
    public static final int REPLICATED_MAP_MERGING_ENTRY = 7;
    public static final int RINGBUFFER_MERGING_ENTRY = 8;
    public static final int CARDINALITY_ESTIMATOR_MERGING_ENTRY = 9;
    public static final int SCHEDULED_EXECUTOR_MERGING_ENTRY = 10;
    public static final int DISCARD = 11;
    public static final int EXPIRATION_TIME = 12;
    public static final int HIGHER_HITS = 13;
    public static final int HYPER_LOG_LOG = 14;
    public static final int LATEST_ACCESS = 15;
    public static final int LATEST_UPDATE = 16;
    public static final int PASS_THROUGH = 17;
    public static final int PUT_IF_ABSENT = 18;
    private static final int LEN = 19;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[]{new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionMergingValueImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueMergingValueImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AtomicLongMergingValueImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AtomicReferenceMergingValueImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapMergingEntryImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheMergingEntryImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapMergingEntryImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReplicatedMapMergingEntryImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RingbufferMergingValueImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CardinalityEstimatorMergingEntry();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ScheduledExecutorMergingEntryImpl();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DiscardMergePolicy();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ExpirationTimeMergePolicy();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new HigherHitsMergePolicy();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new HyperLogLogMergePolicy();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LatestAccessMergePolicy();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LatestUpdateMergePolicy();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PassThroughMergePolicy();
            }
        }, new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutIfAbsentMergePolicy();
            }
        }};
        return new ArrayDataSerializableFactory(constructors);
    }
}

