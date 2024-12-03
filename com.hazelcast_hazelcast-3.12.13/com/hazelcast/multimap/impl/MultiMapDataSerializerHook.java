/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.multimap.impl.MultiMapEventFilter;
import com.hazelcast.multimap.impl.MultiMapMergeContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.operations.ClearBackupOperation;
import com.hazelcast.multimap.impl.operations.ClearOperation;
import com.hazelcast.multimap.impl.operations.ContainsEntryOperation;
import com.hazelcast.multimap.impl.operations.CountOperation;
import com.hazelcast.multimap.impl.operations.DeleteBackupOperation;
import com.hazelcast.multimap.impl.operations.DeleteOperation;
import com.hazelcast.multimap.impl.operations.EntrySetOperation;
import com.hazelcast.multimap.impl.operations.EntrySetResponse;
import com.hazelcast.multimap.impl.operations.GetAllOperation;
import com.hazelcast.multimap.impl.operations.KeySetOperation;
import com.hazelcast.multimap.impl.operations.MergeBackupOperation;
import com.hazelcast.multimap.impl.operations.MergeOperation;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.multimap.impl.operations.MultiMapReplicationOperation;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.multimap.impl.operations.PutBackupOperation;
import com.hazelcast.multimap.impl.operations.PutOperation;
import com.hazelcast.multimap.impl.operations.RemoveAllBackupOperation;
import com.hazelcast.multimap.impl.operations.RemoveAllOperation;
import com.hazelcast.multimap.impl.operations.RemoveBackupOperation;
import com.hazelcast.multimap.impl.operations.RemoveOperation;
import com.hazelcast.multimap.impl.operations.SizeOperation;
import com.hazelcast.multimap.impl.operations.ValuesOperation;
import com.hazelcast.multimap.impl.txn.MultiMapTransactionLogRecord;
import com.hazelcast.multimap.impl.txn.TxnCommitBackupOperation;
import com.hazelcast.multimap.impl.txn.TxnCommitOperation;
import com.hazelcast.multimap.impl.txn.TxnGenerateRecordIdOperation;
import com.hazelcast.multimap.impl.txn.TxnLockAndGetOperation;
import com.hazelcast.multimap.impl.txn.TxnPrepareBackupOperation;
import com.hazelcast.multimap.impl.txn.TxnPrepareOperation;
import com.hazelcast.multimap.impl.txn.TxnPutBackupOperation;
import com.hazelcast.multimap.impl.txn.TxnPutOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveAllBackupOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveAllOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveBackupOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveOperation;
import com.hazelcast.multimap.impl.txn.TxnRollbackBackupOperation;
import com.hazelcast.multimap.impl.txn.TxnRollbackOperation;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public class MultiMapDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.multimap", -12);
    public static final int ADD_ALL_BACKUP = 0;
    public static final int ADD_ALL = 1;
    public static final int CLEAR_BACKUP = 2;
    public static final int CLEAR = 3;
    public static final int COMPARE_AND_REMOVE_BACKUP = 4;
    public static final int COMPARE_AND_REMOVE = 5;
    public static final int CONTAINS_ALL = 6;
    public static final int CONTAINS_ENTRY = 7;
    public static final int CONTAINS = 8;
    public static final int COUNT = 9;
    public static final int ENTRY_SET = 10;
    public static final int GET_ALL = 11;
    public static final int GET = 12;
    public static final int INDEX_OF = 13;
    public static final int KEY_SET = 14;
    public static final int PUT_BACKUP = 15;
    public static final int PUT = 16;
    public static final int REMOVE_ALL_BACKUP = 17;
    public static final int REMOVE_ALL = 18;
    public static final int REMOVE_BACKUP = 19;
    public static final int REMOVE = 20;
    public static final int REMOVE_INDEX_BACKUP = 21;
    public static final int REMOVE_INDEX = 22;
    public static final int SET_BACKUP = 23;
    public static final int SET = 24;
    public static final int SIZE = 25;
    public static final int VALUES = 26;
    public static final int TXN_COMMIT_BACKUP = 27;
    public static final int TXN_COMMIT = 28;
    public static final int TXN_GENERATE_RECORD_ID = 29;
    public static final int TXN_LOCK_AND_GET = 30;
    public static final int TXN_PREPARE_BACKUP = 31;
    public static final int TXN_PREPARE = 32;
    public static final int TXN_PUT = 33;
    public static final int TXN_PUT_BACKUP = 34;
    public static final int TXN_REMOVE = 35;
    public static final int TXN_REMOVE_BACKUP = 36;
    public static final int TXN_REMOVE_ALL = 37;
    public static final int TXN_REMOVE_ALL_BACKUP = 38;
    public static final int TXN_ROLLBACK = 39;
    public static final int TXN_ROLLBACK_BACKUP = 40;
    public static final int MULTIMAP_OP_FACTORY = 41;
    public static final int MULTIMAP_TRANSACTION_LOG_RECORD = 42;
    public static final int MULTIMAP_EVENT_FILTER = 43;
    public static final int MULTIMAP_RECORD = 44;
    public static final int MULTIMAP_REPLICATION_OPERATION = 45;
    public static final int MULTIMAP_RESPONSE = 46;
    public static final int ENTRY_SET_RESPONSE = 47;
    public static final int MERGE_CONTAINER = 48;
    public static final int MERGE_OPERATION = 49;
    public static final int MERGE_BACKUP_OPERATION = 50;
    public static final int DELETE = 51;
    public static final int DELETE_BACKUP = 52;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[53];
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearBackupOperation();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearOperation();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ContainsEntryOperation();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CountOperation();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EntrySetOperation();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new GetAllOperation();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new KeySetOperation();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutBackupOperation();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PutOperation();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveAllBackupOperation();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveAllOperation();
            }
        };
        constructors[19] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveBackupOperation();
            }
        };
        constructors[20] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemoveOperation();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SizeOperation();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ValuesOperation();
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnCommitBackupOperation();
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnCommitOperation();
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnGenerateRecordIdOperation();
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnLockAndGetOperation();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPrepareBackupOperation();
            }
        };
        constructors[32] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPrepareOperation();
            }
        };
        constructors[33] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPutOperation();
            }
        };
        constructors[34] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPutBackupOperation();
            }
        };
        constructors[35] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRemoveOperation();
            }
        };
        constructors[36] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRemoveBackupOperation();
            }
        };
        constructors[37] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRemoveAllOperation();
            }
        };
        constructors[38] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRemoveAllBackupOperation();
            }
        };
        constructors[40] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRollbackBackupOperation();
            }
        };
        constructors[39] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRollbackOperation();
            }
        };
        constructors[41] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapOperationFactory();
            }
        };
        constructors[42] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapTransactionLogRecord();
            }
        };
        constructors[43] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapEventFilter();
            }
        };
        constructors[44] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapRecord();
            }
        };
        constructors[45] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapReplicationOperation();
            }
        };
        constructors[46] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapResponse();
            }
        };
        constructors[47] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new EntrySetResponse();
            }
        };
        constructors[48] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MultiMapMergeContainer();
            }
        };
        constructors[49] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MergeOperation();
            }
        };
        constructors[50] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new MergeBackupOperation();
            }
        };
        constructors[51] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DeleteOperation();
            }
        };
        constructors[52] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DeleteBackupOperation();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

