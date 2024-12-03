/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionEvent;
import com.hazelcast.collection.impl.collection.CollectionEventFilter;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.TxCollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionAddAllBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionAddAllOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionAddBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionAddOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionClearBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionClearOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionCompareAndRemoveOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionContainsOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionGetAllOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionIsEmptyOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionMergeBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionMergeOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionRemoveBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionRemoveOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionSizeOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.collection.impl.list.operations.ListAddAllOperation;
import com.hazelcast.collection.impl.list.operations.ListAddOperation;
import com.hazelcast.collection.impl.list.operations.ListGetOperation;
import com.hazelcast.collection.impl.list.operations.ListIndexOfOperation;
import com.hazelcast.collection.impl.list.operations.ListRemoveOperation;
import com.hazelcast.collection.impl.list.operations.ListReplicationOperation;
import com.hazelcast.collection.impl.list.operations.ListSetBackupOperation;
import com.hazelcast.collection.impl.list.operations.ListSetOperation;
import com.hazelcast.collection.impl.list.operations.ListSubOperation;
import com.hazelcast.collection.impl.set.SetContainer;
import com.hazelcast.collection.impl.set.operations.SetReplicationOperation;
import com.hazelcast.collection.impl.txncollection.CollectionTransactionLogRecord;
import com.hazelcast.collection.impl.txncollection.operations.CollectionCommitBackupOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionCommitOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionPrepareBackupOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionPrepareOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionReserveAddOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionReserveRemoveOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionRollbackBackupOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionRollbackOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTransactionRollbackOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnAddBackupOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnAddOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnRemoveBackupOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnRemoveOperation;
import com.hazelcast.collection.impl.txnqueue.QueueTransactionLogRecord;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public class CollectionDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.collection", -20);
    public static final int COLLECTION_ADD = 1;
    public static final int COLLECTION_ADD_BACKUP = 2;
    public static final int LIST_ADD = 3;
    public static final int LIST_GET = 4;
    public static final int COLLECTION_REMOVE = 5;
    public static final int COLLECTION_REMOVE_BACKUP = 6;
    public static final int COLLECTION_SIZE = 7;
    public static final int COLLECTION_CLEAR = 8;
    public static final int COLLECTION_CLEAR_BACKUP = 9;
    public static final int LIST_SET = 10;
    public static final int LIST_SET_BACKUP = 11;
    public static final int LIST_REMOVE = 12;
    public static final int LIST_INDEX_OF = 13;
    public static final int COLLECTION_CONTAINS = 14;
    public static final int COLLECTION_ADD_ALL = 15;
    public static final int COLLECTION_ADD_ALL_BACKUP = 16;
    public static final int LIST_ADD_ALL = 17;
    public static final int LIST_SUB = 18;
    public static final int COLLECTION_COMPARE_AND_REMOVE = 19;
    public static final int COLLECTION_GET_ALL = 20;
    public static final int COLLECTION_EVENT_FILTER = 21;
    public static final int COLLECTION_EVENT = 22;
    public static final int COLLECTION_ITEM = 23;
    public static final int COLLECTION_RESERVE_ADD = 24;
    public static final int COLLECTION_RESERVE_REMOVE = 25;
    public static final int COLLECTION_TXN_ADD = 26;
    public static final int COLLECTION_TXN_ADD_BACKUP = 27;
    public static final int COLLECTION_TXN_REMOVE = 28;
    public static final int COLLECTION_TXN_REMOVE_BACKUP = 29;
    public static final int COLLECTION_PREPARE = 30;
    public static final int COLLECTION_PREPARE_BACKUP = 31;
    public static final int COLLECTION_ROLLBACK = 32;
    public static final int COLLECTION_ROLLBACK_BACKUP = 33;
    public static final int TX_COLLECTION_ITEM = 34;
    public static final int TX_ROLLBACK = 35;
    public static final int LIST_REPLICATION = 36;
    public static final int SET_REPLICATION = 37;
    public static final int COLLECTION_IS_EMPTY = 38;
    public static final int TXN_COMMIT = 39;
    public static final int TXN_COMMIT_BACKUP = 40;
    public static final int SET_CONTAINER = 41;
    public static final int LIST_CONTAINER = 42;
    public static final int COLLECTION_TRANSACTION_LOG_RECORD = 43;
    public static final int QUEUE_TRANSACTION_LOG_RECORD = 44;
    public static final int COLLECTION_MERGE = 45;
    public static final int COLLECTION_MERGE_BACKUP = 46;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[47];
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionAddOperation();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionAddBackupOperation();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListAddOperation();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListGetOperation();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionRemoveOperation();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionRemoveBackupOperation();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionSizeOperation();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionClearOperation();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionClearBackupOperation();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListSetOperation();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListSetBackupOperation();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListRemoveOperation();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListIndexOfOperation();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionContainsOperation();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionAddAllOperation();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionAddAllBackupOperation();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListAddAllOperation();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListSubOperation();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionCompareAndRemoveOperation();
            }
        };
        constructors[20] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionGetAllOperation();
            }
        };
        constructors[21] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionEventFilter();
            }
        };
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionEvent();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionItem();
            }
        };
        constructors[24] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionReserveAddOperation();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionReserveRemoveOperation();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionTxnAddOperation();
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionTxnAddBackupOperation();
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionTxnRemoveOperation();
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionTxnRemoveBackupOperation();
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionPrepareOperation();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionPrepareBackupOperation();
            }
        };
        constructors[32] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionRollbackOperation();
            }
        };
        constructors[33] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionRollbackBackupOperation();
            }
        };
        constructors[34] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxCollectionItem();
            }
        };
        constructors[35] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionTransactionRollbackOperation();
            }
        };
        constructors[36] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListReplicationOperation();
            }
        };
        constructors[37] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetReplicationOperation();
            }
        };
        constructors[38] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionIsEmptyOperation();
            }
        };
        constructors[39] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionCommitOperation();
            }
        };
        constructors[40] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionCommitBackupOperation();
            }
        };
        constructors[41] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SetContainer();
            }
        };
        constructors[42] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ListContainer();
            }
        };
        constructors[43] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionTransactionLogRecord();
            }
        };
        constructors[44] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueTransactionLogRecord();
            }
        };
        constructors[45] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionMergeOperation();
            }
        };
        constructors[46] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CollectionMergeBackupOperation();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

