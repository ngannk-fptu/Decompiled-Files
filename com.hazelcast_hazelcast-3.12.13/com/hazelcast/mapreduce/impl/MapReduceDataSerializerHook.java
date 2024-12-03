/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.mapreduce.impl.CombinerResultList;
import com.hazelcast.mapreduce.impl.HashMapAdapter;
import com.hazelcast.mapreduce.impl.ListKeyValueSource;
import com.hazelcast.mapreduce.impl.MapKeyValueSource;
import com.hazelcast.mapreduce.impl.MultiMapKeyValueSource;
import com.hazelcast.mapreduce.impl.SetKeyValueSource;
import com.hazelcast.mapreduce.impl.notification.IntermediateChunkNotification;
import com.hazelcast.mapreduce.impl.notification.LastChunkNotification;
import com.hazelcast.mapreduce.impl.notification.ReducingFinishedNotification;
import com.hazelcast.mapreduce.impl.operation.CancelJobSupervisorOperation;
import com.hazelcast.mapreduce.impl.operation.FireNotificationOperation;
import com.hazelcast.mapreduce.impl.operation.GetResultOperation;
import com.hazelcast.mapreduce.impl.operation.KeyValueJobOperation;
import com.hazelcast.mapreduce.impl.operation.KeysAssignmentOperation;
import com.hazelcast.mapreduce.impl.operation.KeysAssignmentResult;
import com.hazelcast.mapreduce.impl.operation.NotifyRemoteExceptionOperation;
import com.hazelcast.mapreduce.impl.operation.PostPonePartitionProcessing;
import com.hazelcast.mapreduce.impl.operation.ProcessStatsUpdateOperation;
import com.hazelcast.mapreduce.impl.operation.RequestMemberIdAssignment;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionMapping;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionProcessed;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionReducing;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.operation.StartProcessingJobOperation;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public class MapReduceDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.map_reduce", -23);
    public static final int KEY_VALUE_SOURCE_MAP = 0;
    public static final int KEY_VALUE_SOURCE_MULTIMAP = 1;
    public static final int REDUCER_CHUNK_MESSAGE = 2;
    public static final int REDUCER_LAST_CHUNK_MESSAGE = 3;
    public static final int TRACKED_JOB_OPERATION = 4;
    public static final int REQUEST_PARTITION_MAPPING = 5;
    public static final int REQUEST_PARTITION_REDUCING = 6;
    public static final int REQUEST_PARTITION_PROCESSED = 7;
    public static final int GET_RESULT_OPERATION = 8;
    public static final int START_PROCESSING_OPERATION = 9;
    public static final int REQUEST_PARTITION_RESULT = 10;
    public static final int REDUCING_FINISHED_MESSAGE = 11;
    public static final int FIRE_NOTIFICATION_OPERATION = 12;
    public static final int REQUEST_MEMBERID_ASSIGNMENT = 13;
    public static final int PROCESS_STATS_UPDATE_OPERATION = 14;
    public static final int NOTIFY_REMOTE_EXCEPTION_OPERATION = 15;
    public static final int CANCEL_JOB_SUPERVISOR_OPERATION = 16;
    public static final int POSTPONE_PARTITION_PROCESSING_OPERATION = 17;
    public static final int KEY_VALUE_SOURCE_LIST = 18;
    public static final int KEY_VALUE_SOURCE_SET = 19;
    public static final int KEYS_ASSIGNMENT_RESULT = 20;
    public static final int KEYS_ASSIGNMENT_OPERATION = 21;
    public static final int HASH_MAP_ADAPTER = 22;
    public static final int COMBINER_RESULT_LIST = 23;
    private static final int LEN = 24;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[24];
        constructors[0] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapKeyValueSource();
            }
        };
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapKeyValueSource();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IntermediateChunkNotification();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LastChunkNotification();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeyValueJobOperation();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RequestPartitionMapping();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RequestPartitionReducing();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RequestPartitionProcessed();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetResultOperation();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new StartProcessingJobOperation();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RequestPartitionResult();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReducingFinishedNotification();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new FireNotificationOperation();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RequestMemberIdAssignment();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ProcessStatsUpdateOperation();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new NotifyRemoteExceptionOperation();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CancelJobSupervisorOperation();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListKeyValueSource();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetKeyValueSource();
            }
        };
        constructors[20] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeysAssignmentResult();
            }
        };
        constructors[21] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeysAssignmentOperation();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PostPonePartitionProcessing();
            }
        };
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new HashMapAdapter();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CombinerResultList();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

