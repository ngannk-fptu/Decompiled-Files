/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.client.impl.protocol.task.map.MapAssignAndGetUuidsOperation;
import com.hazelcast.client.impl.protocol.task.map.MapAssignAndGetUuidsOperationFactory;
import com.hazelcast.internal.nearcache.impl.invalidation.BatchNearCacheInvalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.SingleNearCacheInvalidation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.EntryRemovingProcessor;
import com.hazelcast.map.impl.EntryViews;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.LazyMapEntry;
import com.hazelcast.map.impl.LockAwareLazyMapEntry;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapKeySet;
import com.hazelcast.map.impl.MapPartitionLostEventFilter;
import com.hazelcast.map.impl.MapValueCollection;
import com.hazelcast.map.impl.MerkleTreeNodeEntries;
import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.iterator.MapKeysWithCursor;
import com.hazelcast.map.impl.journal.DeserializingEventJournalMapEvent;
import com.hazelcast.map.impl.journal.InternalEventJournalMapEvent;
import com.hazelcast.map.impl.journal.MapEventJournalReadOperation;
import com.hazelcast.map.impl.journal.MapEventJournalReadResultSetImpl;
import com.hazelcast.map.impl.journal.MapEventJournalSubscribeOperation;
import com.hazelcast.map.impl.nearcache.invalidation.UuidFilter;
import com.hazelcast.map.impl.operation.AccumulatorConsumerOperation;
import com.hazelcast.map.impl.operation.AddIndexBackupOperation;
import com.hazelcast.map.impl.operation.AddIndexOperation;
import com.hazelcast.map.impl.operation.AddIndexOperationFactory;
import com.hazelcast.map.impl.operation.AddInterceptorOperation;
import com.hazelcast.map.impl.operation.AwaitMapFlushOperation;
import com.hazelcast.map.impl.operation.ClearBackupOperation;
import com.hazelcast.map.impl.operation.ClearNearCacheOperation;
import com.hazelcast.map.impl.operation.ClearOperation;
import com.hazelcast.map.impl.operation.ClearOperationFactory;
import com.hazelcast.map.impl.operation.ContainsKeyOperation;
import com.hazelcast.map.impl.operation.ContainsValueOperation;
import com.hazelcast.map.impl.operation.ContainsValueOperationFactory;
import com.hazelcast.map.impl.operation.DeleteOperation;
import com.hazelcast.map.impl.operation.EntryBackupOperation;
import com.hazelcast.map.impl.operation.EntryOffloadableSetUnlockOperation;
import com.hazelcast.map.impl.operation.EntryOperation;
import com.hazelcast.map.impl.operation.EvictAllBackupOperation;
import com.hazelcast.map.impl.operation.EvictAllOperation;
import com.hazelcast.map.impl.operation.EvictAllOperationFactory;
import com.hazelcast.map.impl.operation.EvictBackupOperation;
import com.hazelcast.map.impl.operation.EvictBatchBackupOperation;
import com.hazelcast.map.impl.operation.EvictOperation;
import com.hazelcast.map.impl.operation.GetAllOperation;
import com.hazelcast.map.impl.operation.GetEntryViewOperation;
import com.hazelcast.map.impl.operation.GetOperation;
import com.hazelcast.map.impl.operation.IsEmptyOperationFactory;
import com.hazelcast.map.impl.operation.IsKeyLoadFinishedOperation;
import com.hazelcast.map.impl.operation.IsPartitionLoadedOperation;
import com.hazelcast.map.impl.operation.IsPartitionLoadedOperationFactory;
import com.hazelcast.map.impl.operation.KeyLoadStatusOperation;
import com.hazelcast.map.impl.operation.KeyLoadStatusOperationFactory;
import com.hazelcast.map.impl.operation.LegacyMergeOperation;
import com.hazelcast.map.impl.operation.LoadAllOperation;
import com.hazelcast.map.impl.operation.LoadMapOperation;
import com.hazelcast.map.impl.operation.MapFetchEntriesOperation;
import com.hazelcast.map.impl.operation.MapFetchKeysOperation;
import com.hazelcast.map.impl.operation.MapFetchWithQueryOperation;
import com.hazelcast.map.impl.operation.MapFlushBackupOperation;
import com.hazelcast.map.impl.operation.MapFlushOperation;
import com.hazelcast.map.impl.operation.MapFlushOperationFactory;
import com.hazelcast.map.impl.operation.MapGetAllOperationFactory;
import com.hazelcast.map.impl.operation.MapGetInvalidationMetaDataOperation;
import com.hazelcast.map.impl.operation.MapIsEmptyOperation;
import com.hazelcast.map.impl.operation.MapLoadAllOperationFactory;
import com.hazelcast.map.impl.operation.MapNearCacheStateHolder;
import com.hazelcast.map.impl.operation.MapReplicationOperation;
import com.hazelcast.map.impl.operation.MapReplicationStateHolder;
import com.hazelcast.map.impl.operation.MapSizeOperation;
import com.hazelcast.map.impl.operation.MergeOperation;
import com.hazelcast.map.impl.operation.MergeOperationFactory;
import com.hazelcast.map.impl.operation.MultipleEntryBackupOperation;
import com.hazelcast.map.impl.operation.MultipleEntryOperation;
import com.hazelcast.map.impl.operation.MultipleEntryOperationFactory;
import com.hazelcast.map.impl.operation.MultipleEntryWithPredicateBackupOperation;
import com.hazelcast.map.impl.operation.MultipleEntryWithPredicateOperation;
import com.hazelcast.map.impl.operation.NotifyMapFlushOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryBackupOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryOperationFactory;
import com.hazelcast.map.impl.operation.PartitionWideEntryWithPredicateBackupOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryWithPredicateOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryWithPredicateOperationFactory;
import com.hazelcast.map.impl.operation.PostJoinMapOperation;
import com.hazelcast.map.impl.operation.PutAllBackupOperation;
import com.hazelcast.map.impl.operation.PutAllOperation;
import com.hazelcast.map.impl.operation.PutAllPartitionAwareOperationFactory;
import com.hazelcast.map.impl.operation.PutBackupOperation;
import com.hazelcast.map.impl.operation.PutFromLoadAllBackupOperation;
import com.hazelcast.map.impl.operation.PutFromLoadAllOperation;
import com.hazelcast.map.impl.operation.PutIfAbsentOperation;
import com.hazelcast.map.impl.operation.PutOperation;
import com.hazelcast.map.impl.operation.PutTransientOperation;
import com.hazelcast.map.impl.operation.RemoveBackupOperation;
import com.hazelcast.map.impl.operation.RemoveFromLoadAllOperation;
import com.hazelcast.map.impl.operation.RemoveIfSameOperation;
import com.hazelcast.map.impl.operation.RemoveInterceptorOperation;
import com.hazelcast.map.impl.operation.RemoveOperation;
import com.hazelcast.map.impl.operation.ReplaceIfSameOperation;
import com.hazelcast.map.impl.operation.ReplaceOperation;
import com.hazelcast.map.impl.operation.SetOperation;
import com.hazelcast.map.impl.operation.SetTtlBackupOperation;
import com.hazelcast.map.impl.operation.SetTtlOperation;
import com.hazelcast.map.impl.operation.SizeOperationFactory;
import com.hazelcast.map.impl.operation.TriggerLoadIfNeededOperation;
import com.hazelcast.map.impl.operation.TryPutOperation;
import com.hazelcast.map.impl.operation.TryRemoveOperation;
import com.hazelcast.map.impl.operation.WriteBehindStateHolder;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.map.impl.query.QueryOperation;
import com.hazelcast.map.impl.query.QueryPartitionOperation;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.query.ResultSegment;
import com.hazelcast.map.impl.query.Target;
import com.hazelcast.map.impl.querycache.subscriber.operation.DestroyQueryCacheOperation;
import com.hazelcast.map.impl.querycache.subscriber.operation.MadePublishableOperation;
import com.hazelcast.map.impl.querycache.subscriber.operation.MadePublishableOperationFactory;
import com.hazelcast.map.impl.querycache.subscriber.operation.PublisherCreateOperation;
import com.hazelcast.map.impl.querycache.subscriber.operation.ReadAndResetAccumulatorOperation;
import com.hazelcast.map.impl.querycache.subscriber.operation.SetReadCursorOperation;
import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.map.impl.record.RecordReplicationInfo;
import com.hazelcast.map.impl.tx.MapTransactionLogRecord;
import com.hazelcast.map.impl.tx.TxnDeleteOperation;
import com.hazelcast.map.impl.tx.TxnLockAndGetOperation;
import com.hazelcast.map.impl.tx.TxnPrepareBackupOperation;
import com.hazelcast.map.impl.tx.TxnPrepareOperation;
import com.hazelcast.map.impl.tx.TxnRollbackBackupOperation;
import com.hazelcast.map.impl.tx.TxnRollbackOperation;
import com.hazelcast.map.impl.tx.TxnSetOperation;
import com.hazelcast.map.impl.tx.TxnUnlockBackupOperation;
import com.hazelcast.map.impl.tx.TxnUnlockOperation;
import com.hazelcast.map.impl.tx.VersionedValue;
import com.hazelcast.map.merge.HigherHitsMapMergePolicy;
import com.hazelcast.map.merge.LatestUpdateMapMergePolicy;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import com.hazelcast.map.merge.PutIfAbsentMapMergePolicy;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.IndexInfo;
import com.hazelcast.query.impl.MapIndexInfo;
import com.hazelcast.util.ConstructorFunction;

public final class MapDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.map", -10);
    public static final int PUT = 0;
    public static final int GET = 1;
    public static final int REMOVE = 2;
    public static final int PUT_BACKUP = 3;
    public static final int REMOVE_BACKUP = 4;
    public static final int KEY_SET = 5;
    public static final int VALUES = 6;
    public static final int ENTRIES = 7;
    public static final int ENTRY_VIEW = 8;
    public static final int QUERY_RESULT_ROW = 9;
    public static final int QUERY_RESULT = 10;
    public static final int EVICT_BACKUP = 11;
    public static final int CONTAINS_KEY = 12;
    public static final int KEYS_WITH_CURSOR = 13;
    public static final int ENTRIES_WITH_CURSOR = 14;
    public static final int SET = 15;
    public static final int LOAD_MAP = 16;
    public static final int KEY_LOAD_STATUS = 17;
    public static final int LOAD_ALL = 18;
    public static final int ENTRY_BACKUP = 19;
    public static final int ENTRY_OPERATION = 20;
    public static final int PUT_ALL = 21;
    public static final int PUT_ALL_BACKUP = 22;
    public static final int REMOVE_IF_SAME = 23;
    public static final int REPLACE = 24;
    public static final int SIZE = 25;
    public static final int CLEAR_BACKUP = 26;
    public static final int CLEAR_NEAR_CACHE = 27;
    public static final int CLEAR = 28;
    public static final int DELETE = 29;
    public static final int EVICT = 30;
    public static final int EVICT_ALL = 31;
    public static final int EVICT_ALL_BACKUP = 32;
    public static final int GET_ALL = 33;
    public static final int IS_EMPTY = 34;
    public static final int LEGACY_MERGE = 35;
    public static final int NEAR_CACHE_SINGLE_INVALIDATION = 36;
    public static final int NEAR_CACHE_BATCH_INVALIDATION = 37;
    public static final int IS_PARTITION_LOADED = 38;
    public static final int PARTITION_WIDE_ENTRY = 39;
    public static final int PARTITION_WIDE_ENTRY_BACKUP = 40;
    public static final int PARTITION_WIDE_PREDICATE_ENTRY = 41;
    public static final int PARTITION_WIDE_PREDICATE_ENTRY_BACKUP = 42;
    public static final int ADD_INDEX = 43;
    public static final int AWAIT_MAP_FLUSH = 44;
    public static final int CONTAINS_VALUE = 45;
    public static final int GET_ENTRY_VIEW = 46;
    public static final int FETCH_ENTRIES = 47;
    public static final int FETCH_KEYS = 48;
    public static final int FLUSH_BACKUP = 49;
    public static final int FLUSH = 50;
    public static final int MULTIPLE_ENTRY_BACKUP = 51;
    public static final int MULTIPLE_ENTRY = 52;
    public static final int MULTIPLE_ENTRY_PREDICATE_BACKUP = 53;
    public static final int MULTIPLE_ENTRY_PREDICATE = 54;
    public static final int NOTIFY_MAP_FLUSH = 55;
    public static final int PUT_IF_ABSENT = 56;
    public static final int PUT_FROM_LOAD_ALL = 57;
    public static final int PUT_FROM_LOAD_ALL_BACKUP = 58;
    public static final int QUERY_PARTITION = 59;
    public static final int QUERY_OPERATION = 60;
    public static final int PUT_TRANSIENT = 61;
    public static final int REPLACE_IF_SAME = 62;
    public static final int TRY_PUT = 63;
    public static final int TRY_REMOVE = 64;
    public static final int TXN_LOCK_AND_GET = 65;
    public static final int TXN_DELETE = 66;
    public static final int TXN_PREPARE = 67;
    public static final int TXN_PREPARE_BACKUP = 68;
    public static final int TXN_ROLLBACK = 69;
    public static final int TXN_ROLLBACK_BACKUP = 70;
    public static final int TXN_SET = 71;
    public static final int TXN_UNLOCK = 72;
    public static final int TXN_UNLOCK_BACKUP = 73;
    public static final int IS_PARTITION_LOADED_FACTORY = 74;
    public static final int ADD_INDEX_FACTORY = 75;
    public static final int ADD_INTERCEPTOR_FACTORY = 76;
    public static final int CLEAR_FACTORY = 77;
    public static final int CONTAINS_VALUE_FACTORY = 78;
    public static final int EVICT_ALL_FACTORY = 79;
    public static final int IS_EMPTY_FACTORY = 80;
    public static final int KEY_LOAD_STATUS_FACTORY = 81;
    public static final int MAP_FLUSH_FACTORY = 82;
    public static final int MAP_GET_ALL_FACTORY = 83;
    public static final int LOAD_ALL_FACTORY = 84;
    public static final int PARTITION_WIDE_ENTRY_FACTORY = 85;
    public static final int PARTITION_WIDE_PREDICATE_ENTRY_FACTORY = 86;
    public static final int PUT_ALL_PARTITION_AWARE_FACTORY = 87;
    public static final int REMOVE_INTERCEPTOR_FACTORY = 88;
    public static final int SIZE_FACTORY = 89;
    public static final int MULTIPLE_ENTRY_FACTORY = 90;
    public static final int ENTRY_EVENT_FILTER = 91;
    public static final int EVENT_LISTENER_FILTER = 92;
    public static final int PARTITION_LOST_EVENT_FILTER = 93;
    public static final int NEAR_CACHE_CLEAR_INVALIDATION = 94;
    public static final int ADD_INTERCEPTOR = 95;
    public static final int MAP_REPLICATION = 96;
    public static final int POST_JOIN_MAP_OPERATION = 97;
    public static final int INDEX_INFO = 98;
    public static final int MAP_INDEX_INFO = 99;
    public static final int INTERCEPTOR_INFO = 100;
    public static final int REMOVE_INTERCEPTOR = 101;
    public static final int QUERY_EVENT_FILTER = 102;
    public static final int RECORD_INFO = 103;
    public static final int RECORD_REPLICATION_INFO = 104;
    public static final int HIGHER_HITS_MERGE_POLICY = 105;
    public static final int LATEST_UPDATE_MERGE_POLICY = 106;
    public static final int PASS_THROUGH_MERGE_POLICY = 107;
    public static final int PUT_IF_ABSENT_MERGE_POLICY = 108;
    public static final int UUID_FILTER = 109;
    public static final int CLEAR_NEAR_CACHE_INVALIDATION = 110;
    public static final int MAP_TRANSACTION_LOG_RECORD = 111;
    public static final int VERSIONED_VALUE = 112;
    public static final int MAP_REPLICATION_STATE_HOLDER = 113;
    public static final int WRITE_BEHIND_STATE_HOLDER = 114;
    public static final int AGGREGATION_RESULT = 115;
    public static final int QUERY = 116;
    public static final int TARGET = 117;
    public static final int MAP_INVALIDATION_METADATA = 118;
    public static final int MAP_INVALIDATION_METADATA_RESPONSE = 119;
    public static final int MAP_NEAR_CACHE_STATE_HOLDER = 120;
    public static final int MAP_ASSIGN_AND_GET_UUIDS = 121;
    public static final int MAP_ASSIGN_AND_GET_UUIDS_FACTORY = 122;
    public static final int DESTROY_QUERY_CACHE = 123;
    public static final int MADE_PUBLISHABLE = 124;
    public static final int MADE_PUBLISHABLE_FACTORY = 125;
    public static final int PUBLISHER_CREATE = 126;
    public static final int READ_AND_RESET_ACCUMULATOR = 127;
    public static final int SET_READ_CURSOR = 128;
    public static final int ACCUMULATOR_CONSUMER = 129;
    public static final int LAZY_MAP_ENTRY = 131;
    public static final int TRIGGER_LOAD_IF_NEEDED = 132;
    public static final int IS_KEYLOAD_FINISHED = 133;
    public static final int REMOVE_FROM_LOAD_ALL = 134;
    public static final int ENTRY_REMOVING_PROCESSOR = 135;
    public static final int ENTRY_OFFLOADABLE_SET_UNLOCK = 136;
    public static final int LOCK_AWARE_LAZY_MAP_ENTRY = 137;
    public static final int FETCH_WITH_QUERY = 138;
    public static final int RESULT_SEGMENT = 139;
    public static final int EVICT_BATCH_BACKUP = 140;
    public static final int EVENT_JOURNAL_SUBSCRIBE_OPERATION = 141;
    public static final int EVENT_JOURNAL_READ = 142;
    public static final int EVENT_JOURNAL_DESERIALIZING_MAP_EVENT = 143;
    public static final int EVENT_JOURNAL_INTERNAL_MAP_EVENT = 144;
    public static final int EVENT_JOURNAL_READ_RESULT_SET = 145;
    public static final int MERGE_FACTORY = 146;
    public static final int MERGE = 147;
    public static final int SET_TTL = 148;
    public static final int SET_TTL_BACKUP = 149;
    public static final int MERKLE_TREE_NODE_ENTRIES = 150;
    public static final int ADD_INDEX_BACKUP = 151;
    private static final int LEN = 152;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[152];
        constructors[0] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutOperation();
            }
        };
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetOperation();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveOperation();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutBackupOperation();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveBackupOperation();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EvictBackupOperation();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapKeySet();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapValueCollection();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapEntries();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return (IdentifiedDataSerializable)((Object)EntryViews.createSimpleEntryView());
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueryResultRow();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueryResult();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ContainsKeyOperation();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapKeysWithCursor();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapEntriesWithCursor();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetOperation();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LoadMapOperation();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeyLoadStatusOperation();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LoadAllOperation();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EntryBackupOperation();
            }
        };
        constructors[20] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EntryOperation();
            }
        };
        constructors[21] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutAllOperation();
            }
        };
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutAllBackupOperation();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveIfSameOperation();
            }
        };
        constructors[24] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReplaceOperation();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapSizeOperation();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearBackupOperation();
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearNearCacheOperation();
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearOperation();
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DeleteOperation();
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EvictOperation();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EvictAllOperation();
            }
        };
        constructors[32] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EvictAllBackupOperation();
            }
        };
        constructors[33] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetAllOperation();
            }
        };
        constructors[34] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapIsEmptyOperation();
            }
        };
        constructors[35] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LegacyMergeOperation();
            }
        };
        constructors[38] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IsPartitionLoadedOperation();
            }
        };
        constructors[39] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PartitionWideEntryOperation();
            }
        };
        constructors[40] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PartitionWideEntryBackupOperation();
            }
        };
        constructors[41] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PartitionWideEntryWithPredicateOperation();
            }
        };
        constructors[42] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PartitionWideEntryWithPredicateBackupOperation();
            }
        };
        constructors[43] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddIndexOperation();
            }
        };
        constructors[44] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AwaitMapFlushOperation();
            }
        };
        constructors[45] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ContainsValueOperation();
            }
        };
        constructors[46] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetEntryViewOperation();
            }
        };
        constructors[47] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapFetchEntriesOperation();
            }
        };
        constructors[48] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapFetchKeysOperation();
            }
        };
        constructors[49] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapFlushBackupOperation();
            }
        };
        constructors[50] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapFlushOperation();
            }
        };
        constructors[51] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultipleEntryBackupOperation();
            }
        };
        constructors[52] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultipleEntryOperation();
            }
        };
        constructors[53] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultipleEntryWithPredicateBackupOperation();
            }
        };
        constructors[54] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultipleEntryWithPredicateOperation();
            }
        };
        constructors[55] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new NotifyMapFlushOperation();
            }
        };
        constructors[56] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutIfAbsentOperation();
            }
        };
        constructors[57] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutFromLoadAllOperation();
            }
        };
        constructors[58] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutFromLoadAllBackupOperation();
            }
        };
        constructors[59] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueryPartitionOperation();
            }
        };
        constructors[60] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueryOperation();
            }
        };
        constructors[61] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutTransientOperation();
            }
        };
        constructors[62] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReplaceIfSameOperation();
            }
        };
        constructors[63] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TryPutOperation();
            }
        };
        constructors[64] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TryRemoveOperation();
            }
        };
        constructors[65] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnLockAndGetOperation();
            }
        };
        constructors[66] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnDeleteOperation();
            }
        };
        constructors[67] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPrepareOperation();
            }
        };
        constructors[68] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPrepareBackupOperation();
            }
        };
        constructors[69] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRollbackOperation();
            }
        };
        constructors[70] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRollbackBackupOperation();
            }
        };
        constructors[71] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnSetOperation();
            }
        };
        constructors[72] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnUnlockOperation();
            }
        };
        constructors[73] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnUnlockBackupOperation();
            }
        };
        constructors[74] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IsPartitionLoadedOperationFactory();
            }
        };
        constructors[75] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddIndexOperationFactory();
            }
        };
        constructors[77] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearOperationFactory();
            }
        };
        constructors[78] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ContainsValueOperationFactory();
            }
        };
        constructors[79] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EvictAllOperationFactory();
            }
        };
        constructors[80] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IsEmptyOperationFactory();
            }
        };
        constructors[81] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeyLoadStatusOperationFactory();
            }
        };
        constructors[82] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapFlushOperationFactory();
            }
        };
        constructors[83] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapGetAllOperationFactory();
            }
        };
        constructors[84] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapLoadAllOperationFactory();
            }
        };
        constructors[85] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PartitionWideEntryOperationFactory();
            }
        };
        constructors[86] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PartitionWideEntryWithPredicateOperationFactory();
            }
        };
        constructors[87] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutAllPartitionAwareOperationFactory();
            }
        };
        constructors[89] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SizeOperationFactory();
            }
        };
        constructors[90] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultipleEntryOperationFactory();
            }
        };
        constructors[91] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EntryEventFilter();
            }
        };
        constructors[92] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EventListenerFilter();
            }
        };
        constructors[93] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapPartitionLostEventFilter();
            }
        };
        constructors[36] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SingleNearCacheInvalidation();
            }
        };
        constructors[37] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new BatchNearCacheInvalidation();
            }
        };
        constructors[95] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddInterceptorOperation();
            }
        };
        constructors[96] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapReplicationOperation();
            }
        };
        constructors[97] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PostJoinMapOperation();
            }
        };
        constructors[98] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IndexInfo();
            }
        };
        constructors[99] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapIndexInfo();
            }
        };
        constructors[100] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PostJoinMapOperation.InterceptorInfo();
            }
        };
        constructors[101] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveInterceptorOperation();
            }
        };
        constructors[102] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueryEventFilter();
            }
        };
        constructors[103] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RecordInfo();
            }
        };
        constructors[104] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RecordReplicationInfo();
            }
        };
        constructors[105] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new HigherHitsMapMergePolicy();
            }
        };
        constructors[106] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LatestUpdateMapMergePolicy();
            }
        };
        constructors[107] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PassThroughMergePolicy();
            }
        };
        constructors[108] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutIfAbsentMapMergePolicy();
            }
        };
        constructors[109] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new UuidFilter();
            }
        };
        constructors[111] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapTransactionLogRecord();
            }
        };
        constructors[112] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new VersionedValue();
            }
        };
        constructors[113] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapReplicationStateHolder();
            }
        };
        constructors[114] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new WriteBehindStateHolder();
            }
        };
        constructors[115] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AggregationResult();
            }
        };
        constructors[116] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new Query();
            }
        };
        constructors[117] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new Target();
            }
        };
        constructors[118] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapGetInvalidationMetaDataOperation();
            }
        };
        constructors[119] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapGetInvalidationMetaDataOperation.MetaDataResponse();
            }
        };
        constructors[120] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapNearCacheStateHolder();
            }
        };
        constructors[122] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapAssignAndGetUuidsOperationFactory();
            }
        };
        constructors[121] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapAssignAndGetUuidsOperation();
            }
        };
        constructors[123] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DestroyQueryCacheOperation();
            }
        };
        constructors[124] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MadePublishableOperation();
            }
        };
        constructors[125] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MadePublishableOperationFactory();
            }
        };
        constructors[126] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PublisherCreateOperation();
            }
        };
        constructors[127] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ReadAndResetAccumulatorOperation();
            }
        };
        constructors[128] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetReadCursorOperation();
            }
        };
        constructors[129] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AccumulatorConsumerOperation();
            }
        };
        constructors[131] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LazyMapEntry();
            }
        };
        constructors[132] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TriggerLoadIfNeededOperation();
            }
        };
        constructors[133] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IsKeyLoadFinishedOperation();
            }
        };
        constructors[134] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveFromLoadAllOperation();
            }
        };
        constructors[135] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return EntryRemovingProcessor.ENTRY_REMOVING_PROCESSOR;
            }
        };
        constructors[136] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EntryOffloadableSetUnlockOperation();
            }
        };
        constructors[137] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new LockAwareLazyMapEntry();
            }
        };
        constructors[138] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapFetchWithQueryOperation();
            }
        };
        constructors[139] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ResultSegment();
            }
        };
        constructors[140] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EvictBatchBackupOperation();
            }
        };
        constructors[141] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapEventJournalSubscribeOperation();
            }
        };
        constructors[142] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapEventJournalReadOperation();
            }
        };
        constructors[143] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DeserializingEventJournalMapEvent();
            }
        };
        constructors[144] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new InternalEventJournalMapEvent();
            }
        };
        constructors[145] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MapEventJournalReadResultSetImpl();
            }
        };
        constructors[146] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MergeOperationFactory();
            }
        };
        constructors[147] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MergeOperation();
            }
        };
        constructors[148] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetTtlOperation();
            }
        };
        constructors[149] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetTtlBackupOperation();
            }
        };
        constructors[150] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MerkleTreeNodeEntries();
            }
        };
        constructors[151] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddIndexBackupOperation();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

