/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.HazelcastExpiryPolicy;
import com.hazelcast.cache.impl.CacheClearResponse;
import com.hazelcast.cache.impl.CacheEntryIterationResult;
import com.hazelcast.cache.impl.CacheEventDataImpl;
import com.hazelcast.cache.impl.CacheEventListenerAdaptor;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.cache.impl.CacheKeyIterationResult;
import com.hazelcast.cache.impl.CachePartitionEventData;
import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.cache.impl.event.CachePartitionLostEventFilter;
import com.hazelcast.cache.impl.journal.CacheEventJournalReadOperation;
import com.hazelcast.cache.impl.journal.CacheEventJournalReadResultSetImpl;
import com.hazelcast.cache.impl.journal.CacheEventJournalSubscribeOperation;
import com.hazelcast.cache.impl.journal.DeserializingEventJournalCacheEvent;
import com.hazelcast.cache.impl.journal.InternalEventJournalCacheEvent;
import com.hazelcast.cache.impl.merge.entry.DefaultCacheEntryView;
import com.hazelcast.cache.impl.operation.AddCacheConfigOperation;
import com.hazelcast.cache.impl.operation.CacheBackupEntryProcessorOperation;
import com.hazelcast.cache.impl.operation.CacheClearBackupOperation;
import com.hazelcast.cache.impl.operation.CacheClearOperation;
import com.hazelcast.cache.impl.operation.CacheClearOperationFactory;
import com.hazelcast.cache.impl.operation.CacheContainsKeyOperation;
import com.hazelcast.cache.impl.operation.CacheCreateConfigOperation;
import com.hazelcast.cache.impl.operation.CacheDestroyOperation;
import com.hazelcast.cache.impl.operation.CacheEntryIteratorOperation;
import com.hazelcast.cache.impl.operation.CacheEntryProcessorOperation;
import com.hazelcast.cache.impl.operation.CacheExpireBatchBackupOperation;
import com.hazelcast.cache.impl.operation.CacheGetAllOperation;
import com.hazelcast.cache.impl.operation.CacheGetAllOperationFactory;
import com.hazelcast.cache.impl.operation.CacheGetAndRemoveOperation;
import com.hazelcast.cache.impl.operation.CacheGetAndReplaceOperation;
import com.hazelcast.cache.impl.operation.CacheGetConfigOperation;
import com.hazelcast.cache.impl.operation.CacheGetInvalidationMetaDataOperation;
import com.hazelcast.cache.impl.operation.CacheGetOperation;
import com.hazelcast.cache.impl.operation.CacheKeyIteratorOperation;
import com.hazelcast.cache.impl.operation.CacheLegacyMergeOperation;
import com.hazelcast.cache.impl.operation.CacheListenerRegistrationOperation;
import com.hazelcast.cache.impl.operation.CacheLoadAllOperation;
import com.hazelcast.cache.impl.operation.CacheLoadAllOperationFactory;
import com.hazelcast.cache.impl.operation.CacheManagementConfigOperation;
import com.hazelcast.cache.impl.operation.CacheMergeOperation;
import com.hazelcast.cache.impl.operation.CacheMergeOperationFactory;
import com.hazelcast.cache.impl.operation.CacheNearCacheStateHolder;
import com.hazelcast.cache.impl.operation.CachePutAllBackupOperation;
import com.hazelcast.cache.impl.operation.CachePutAllOperation;
import com.hazelcast.cache.impl.operation.CachePutBackupOperation;
import com.hazelcast.cache.impl.operation.CachePutIfAbsentOperation;
import com.hazelcast.cache.impl.operation.CachePutOperation;
import com.hazelcast.cache.impl.operation.CacheRemoveAllBackupOperation;
import com.hazelcast.cache.impl.operation.CacheRemoveAllOperation;
import com.hazelcast.cache.impl.operation.CacheRemoveAllOperationFactory;
import com.hazelcast.cache.impl.operation.CacheRemoveBackupOperation;
import com.hazelcast.cache.impl.operation.CacheRemoveOperation;
import com.hazelcast.cache.impl.operation.CacheReplaceOperation;
import com.hazelcast.cache.impl.operation.CacheReplicationOperation;
import com.hazelcast.cache.impl.operation.CacheSetExpiryPolicyBackupOperation;
import com.hazelcast.cache.impl.operation.CacheSetExpiryPolicyOperation;
import com.hazelcast.cache.impl.operation.CacheSizeOperation;
import com.hazelcast.cache.impl.operation.CacheSizeOperationFactory;
import com.hazelcast.cache.impl.operation.OnJoinCacheOperation;
import com.hazelcast.cache.impl.record.CacheDataRecord;
import com.hazelcast.cache.impl.record.CacheObjectRecord;
import com.hazelcast.cache.impl.tenantcontrol.CacheDestroyEventContext;
import com.hazelcast.client.impl.protocol.task.cache.CacheAssignAndGetUuidsOperation;
import com.hazelcast.client.impl.protocol.task.cache.CacheAssignAndGetUuidsOperationFactory;
import com.hazelcast.internal.management.request.GetCacheEntryRequest;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public final class CacheDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.cache", -25);
    public static final short GET = 1;
    public static final short CONTAINS_KEY = 2;
    public static final short PUT = 3;
    public static final short PUT_IF_ABSENT = 4;
    public static final short REMOVE = 5;
    public static final short GET_AND_REMOVE = 6;
    public static final short REPLACE = 7;
    public static final short GET_AND_REPLACE = 8;
    public static final short PUT_BACKUP = 9;
    public static final short PUT_ALL_BACKUP = 10;
    public static final short REMOVE_BACKUP = 11;
    public static final short CLEAR_BACKUP = 12;
    public static final short SIZE = 13;
    public static final short SIZE_FACTORY = 14;
    public static final short CLEAR = 15;
    public static final short CLEAR_FACTORY = 16;
    public static final short GET_ALL = 17;
    public static final short GET_ALL_FACTORY = 18;
    public static final short LOAD_ALL = 19;
    public static final short LOAD_ALL_FACTORY = 20;
    public static final short EXPIRY_POLICY = 21;
    public static final short KEY_ITERATOR = 22;
    public static final short KEY_ITERATION_RESULT = 23;
    public static final short ENTRY_PROCESSOR = 24;
    public static final short CLEAR_RESPONSE = 25;
    public static final short CREATE_CONFIG = 26;
    public static final short GET_CONFIG = 27;
    public static final short MANAGEMENT_CONFIG = 28;
    public static final short LISTENER_REGISTRATION = 29;
    public static final short DESTROY_CACHE = 30;
    public static final short CACHE_EVENT_DATA = 31;
    public static final short CACHE_EVENT_DATA_SET = 32;
    public static final short BACKUP_ENTRY_PROCESSOR = 33;
    public static final short REMOVE_ALL = 34;
    public static final short REMOVE_ALL_BACKUP = 35;
    public static final short REMOVE_ALL_FACTORY = 36;
    public static final short PUT_ALL = 37;
    public static final short LEGACY_MERGE = 38;
    public static final short INVALIDATION_MESSAGE = 39;
    public static final short BATCH_INVALIDATION_MESSAGE = 40;
    public static final short ENTRY_ITERATOR = 41;
    public static final short ENTRY_ITERATION_RESULT = 42;
    public static final short CACHE_PARTITION_LOST_EVENT_FILTER = 43;
    public static final short DEFAULT_CACHE_ENTRY_VIEW = 44;
    public static final short CACHE_REPLICATION = 45;
    public static final short CACHE_POST_JOIN = 46;
    public static final short CACHE_DATA_RECORD = 47;
    public static final short CACHE_OBJECT_RECORD = 48;
    public static final short CACHE_PARTITION_EVENT_DATA = 49;
    public static final short CACHE_INVALIDATION_METADATA = 50;
    public static final short CACHE_INVALIDATION_METADATA_RESPONSE = 51;
    public static final short CACHE_ASSIGN_AND_GET_UUIDS = 52;
    public static final short CACHE_ASSIGN_AND_GET_UUIDS_FACTORY = 53;
    public static final short CACHE_NEAR_CACHE_STATE_HOLDER = 54;
    public static final short CACHE_EVENT_LISTENER_ADAPTOR = 55;
    public static final short EVENT_JOURNAL_SUBSCRIBE_OPERATION = 56;
    public static final short EVENT_JOURNAL_READ_OPERATION = 57;
    public static final short EVENT_JOURNAL_DESERIALIZING_CACHE_EVENT = 58;
    public static final short EVENT_JOURNAL_INTERNAL_CACHE_EVENT = 59;
    public static final short EVENT_JOURNAL_READ_RESULT_SET = 60;
    public static final int PRE_JOIN_CACHE_CONFIG = 61;
    public static final int CACHE_BROWSER_ENTRY_VIEW = 62;
    public static final int GET_CACHE_ENTRY_VIEW_PROCESSOR = 63;
    public static final int MERGE_FACTORY = 64;
    public static final int MERGE = 65;
    public static final int ADD_CACHE_CONFIG_OPERATION = 66;
    public static final int SET_EXPIRY_POLICY = 67;
    public static final int SET_EXPIRY_POLICY_BACKUP = 68;
    public static final int EXPIRE_BATCH_BACKUP = 69;
    public static final int CACHE_DESTROY_EVENT_CONTEXT = 70;
    private static final int LEN = 71;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[71];
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetOperation();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheContainsKeyOperation();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CachePutOperation();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CachePutIfAbsentOperation();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheRemoveOperation();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetAndRemoveOperation();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheReplaceOperation();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetAndReplaceOperation();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CachePutBackupOperation();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CachePutAllBackupOperation();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheRemoveBackupOperation();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheSizeOperation();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheSizeOperationFactory();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheClearOperationFactory();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetAllOperation();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetAllOperationFactory();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheLoadAllOperation();
            }
        };
        constructors[20] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheLoadAllOperationFactory();
            }
        };
        constructors[21] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new HazelcastExpiryPolicy();
            }
        };
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheKeyIteratorOperation();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheKeyIterationResult();
            }
        };
        constructors[24] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEntryProcessorOperation();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheClearResponse();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheCreateConfigOperation();
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetConfigOperation();
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheManagementConfigOperation();
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheListenerRegistrationOperation();
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheDestroyOperation();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEventDataImpl();
            }
        };
        constructors[32] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEventSet();
            }
        };
        constructors[33] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheBackupEntryProcessorOperation();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheClearOperation();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheClearBackupOperation();
            }
        };
        constructors[34] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheRemoveAllOperation();
            }
        };
        constructors[35] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheRemoveAllBackupOperation();
            }
        };
        constructors[36] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheRemoveAllOperationFactory();
            }
        };
        constructors[37] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CachePutAllOperation();
            }
        };
        constructors[38] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheLegacyMergeOperation();
            }
        };
        constructors[41] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEntryIteratorOperation();
            }
        };
        constructors[42] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEntryIterationResult();
            }
        };
        constructors[43] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CachePartitionLostEventFilter();
            }
        };
        constructors[44] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DefaultCacheEntryView();
            }
        };
        constructors[45] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheReplicationOperation();
            }
        };
        constructors[46] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new OnJoinCacheOperation();
            }
        };
        constructors[47] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheDataRecord();
            }
        };
        constructors[48] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheObjectRecord();
            }
        };
        constructors[49] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CachePartitionEventData();
            }
        };
        constructors[50] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetInvalidationMetaDataOperation();
            }
        };
        constructors[51] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheGetInvalidationMetaDataOperation.MetaDataResponse();
            }
        };
        constructors[52] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheAssignAndGetUuidsOperation();
            }
        };
        constructors[53] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheAssignAndGetUuidsOperationFactory();
            }
        };
        constructors[54] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheNearCacheStateHolder();
            }
        };
        constructors[55] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEventListenerAdaptor();
            }
        };
        constructors[56] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEventJournalSubscribeOperation();
            }
        };
        constructors[57] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEventJournalReadOperation();
            }
        };
        constructors[58] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DeserializingEventJournalCacheEvent();
            }
        };
        constructors[59] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new InternalEventJournalCacheEvent();
            }
        };
        constructors[60] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheEventJournalReadResultSetImpl();
            }
        };
        constructors[61] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PreJoinCacheConfig();
            }
        };
        constructors[62] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetCacheEntryRequest.CacheBrowserEntryView();
            }
        };
        constructors[63] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetCacheEntryRequest.GetCacheEntryViewEntryProcessor();
            }
        };
        constructors[64] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheMergeOperationFactory();
            }
        };
        constructors[65] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheMergeOperation();
            }
        };
        constructors[66] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddCacheConfigOperation();
            }
        };
        constructors[67] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheSetExpiryPolicyOperation();
            }
        };
        constructors[68] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheSetExpiryPolicyBackupOperation();
            }
        };
        constructors[69] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheExpireBatchBackupOperation();
            }
        };
        constructors[70] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CacheDestroyEventContext();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

