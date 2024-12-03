/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.replicatedmap.impl.operation.CheckReplicaVersionOperation;
import com.hazelcast.replicatedmap.impl.operation.ClearOperation;
import com.hazelcast.replicatedmap.impl.operation.ClearOperationFactory;
import com.hazelcast.replicatedmap.impl.operation.ContainsKeyOperation;
import com.hazelcast.replicatedmap.impl.operation.ContainsValueOperation;
import com.hazelcast.replicatedmap.impl.operation.EntrySetOperation;
import com.hazelcast.replicatedmap.impl.operation.EvictionOperation;
import com.hazelcast.replicatedmap.impl.operation.GetOperation;
import com.hazelcast.replicatedmap.impl.operation.IsEmptyOperation;
import com.hazelcast.replicatedmap.impl.operation.KeySetOperation;
import com.hazelcast.replicatedmap.impl.operation.LegacyMergeOperation;
import com.hazelcast.replicatedmap.impl.operation.MergeOperation;
import com.hazelcast.replicatedmap.impl.operation.MergeOperationFactory;
import com.hazelcast.replicatedmap.impl.operation.PutAllOperation;
import com.hazelcast.replicatedmap.impl.operation.PutAllOperationFactory;
import com.hazelcast.replicatedmap.impl.operation.PutOperation;
import com.hazelcast.replicatedmap.impl.operation.RemoveOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicateUpdateOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicateUpdateToCallerOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicationOperation;
import com.hazelcast.replicatedmap.impl.operation.RequestMapDataOperation;
import com.hazelcast.replicatedmap.impl.operation.SizeOperation;
import com.hazelcast.replicatedmap.impl.operation.SyncReplicatedMapDataOperation;
import com.hazelcast.replicatedmap.impl.operation.ValuesOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.replicatedmap.impl.record.RecordMigrationInfo;
import com.hazelcast.replicatedmap.impl.record.ReplicatedMapEntryView;
import com.hazelcast.replicatedmap.merge.HigherHitsMapMergePolicy;
import com.hazelcast.replicatedmap.merge.LatestUpdateMapMergePolicy;
import com.hazelcast.replicatedmap.merge.PassThroughMergePolicy;
import com.hazelcast.replicatedmap.merge.PutIfAbsentMapMergePolicy;
import com.hazelcast.util.ConstructorFunction;

public class ReplicatedMapDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.replicated_map", -22);
    public static final int CLEAR = 1;
    public static final int ENTRY_VIEW = 2;
    public static final int REPLICATE_UPDATE = 3;
    public static final int REPLICATE_UPDATE_TO_CALLER = 4;
    public static final int PUT_ALL = 5;
    public static final int PUT = 6;
    public static final int REMOVE = 7;
    public static final int SIZE = 8;
    public static final int LEGACY_MERGE = 9;
    public static final int VERSION_RESPONSE_PAIR = 10;
    public static final int GET = 11;
    public static final int CHECK_REPLICA_VERSION = 12;
    public static final int CONTAINS_KEY = 13;
    public static final int CONTAINS_VALUE = 14;
    public static final int ENTRY_SET = 15;
    public static final int EVICTION = 16;
    public static final int IS_EMPTY = 17;
    public static final int KEY_SET = 18;
    public static final int REPLICATION = 19;
    public static final int REQUEST_MAP_DATA = 20;
    public static final int SYNC_REPLICATED_DATA = 21;
    public static final int VALUES = 22;
    public static final int CLEAR_OP_FACTORY = 23;
    public static final int PUT_ALL_OP_FACTORY = 24;
    public static final int RECORD_MIGRATION_INFO = 25;
    public static final int HIGHER_HITS_MERGE_POLICY = 26;
    public static final int LATEST_UPDATE_MERGE_POLICY = 27;
    public static final int PASS_THROUGH_MERGE_POLICY = 28;
    public static final int PUT_IF_ABSENT_MERGE_POLICY = 29;
    public static final int MERGE_FACTORY = 30;
    public static final int MERGE = 31;
    private static final int LEN = 32;
    private static final DataSerializableFactory FACTORY = ReplicatedMapDataSerializerHook.createFactoryInternal();

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return FACTORY;
    }

    private static DataSerializableFactory createFactoryInternal() {
        ConstructorFunction[] constructors = new ConstructorFunction[32];
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearOperation();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReplicatedMapEntryView();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReplicateUpdateOperation();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReplicateUpdateToCallerOperation();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutAllOperation();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutOperation();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveOperation();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SizeOperation();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LegacyMergeOperation();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new VersionResponsePair();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetOperation();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CheckReplicaVersionOperation();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ContainsKeyOperation();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ContainsValueOperation();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EntrySetOperation();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EvictionOperation();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IsEmptyOperation();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeySetOperation();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReplicationOperation();
            }
        };
        constructors[20] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RequestMapDataOperation();
            }
        };
        constructors[21] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SyncReplicatedMapDataOperation();
            }
        };
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ValuesOperation();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearOperationFactory();
            }
        };
        constructors[24] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutAllOperationFactory();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RecordMigrationInfo();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return HigherHitsMapMergePolicy.INSTANCE;
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return LatestUpdateMapMergePolicy.INSTANCE;
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return PassThroughMergePolicy.INSTANCE;
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return PutIfAbsentMapMergePolicy.INSTANCE;
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MergeOperationFactory();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MergeOperation();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

