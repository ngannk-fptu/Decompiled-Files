/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueEvent;
import com.hazelcast.collection.impl.queue.QueueEventFilter;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.operations.AddAllBackupOperation;
import com.hazelcast.collection.impl.queue.operations.AddAllOperation;
import com.hazelcast.collection.impl.queue.operations.CheckAndEvictOperation;
import com.hazelcast.collection.impl.queue.operations.ClearBackupOperation;
import com.hazelcast.collection.impl.queue.operations.ClearOperation;
import com.hazelcast.collection.impl.queue.operations.CompareAndRemoveBackupOperation;
import com.hazelcast.collection.impl.queue.operations.CompareAndRemoveOperation;
import com.hazelcast.collection.impl.queue.operations.ContainsOperation;
import com.hazelcast.collection.impl.queue.operations.DrainBackupOperation;
import com.hazelcast.collection.impl.queue.operations.DrainOperation;
import com.hazelcast.collection.impl.queue.operations.IsEmptyOperation;
import com.hazelcast.collection.impl.queue.operations.IteratorOperation;
import com.hazelcast.collection.impl.queue.operations.OfferBackupOperation;
import com.hazelcast.collection.impl.queue.operations.OfferOperation;
import com.hazelcast.collection.impl.queue.operations.PeekOperation;
import com.hazelcast.collection.impl.queue.operations.PollBackupOperation;
import com.hazelcast.collection.impl.queue.operations.PollOperation;
import com.hazelcast.collection.impl.queue.operations.QueueMergeBackupOperation;
import com.hazelcast.collection.impl.queue.operations.QueueMergeOperation;
import com.hazelcast.collection.impl.queue.operations.QueueReplicationOperation;
import com.hazelcast.collection.impl.queue.operations.RemainingCapacityOperation;
import com.hazelcast.collection.impl.queue.operations.RemoveBackupOperation;
import com.hazelcast.collection.impl.queue.operations.RemoveOperation;
import com.hazelcast.collection.impl.queue.operations.SizeOperation;
import com.hazelcast.collection.impl.txnqueue.TxQueueItem;
import com.hazelcast.collection.impl.txnqueue.operations.QueueTransactionRollbackOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnCommitBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnCommitOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnOfferBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnOfferOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPeekOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPollBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPollOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPrepareBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPrepareOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReserveOfferBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReserveOfferOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReservePollBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReservePollOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnRollbackBackupOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnRollbackOperation;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.ArrayDataSerializableFactory;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.ConstructorFunction;

public final class QueueDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.queue", -11);
    public static final int OFFER = 0;
    public static final int POLL = 1;
    public static final int PEEK = 2;
    public static final int OFFER_BACKUP = 3;
    public static final int POLL_BACKUP = 4;
    public static final int ADD_ALL_BACKUP = 5;
    public static final int ADD_ALL = 6;
    public static final int CLEAR_BACKUP = 7;
    public static final int CLEAR = 8;
    public static final int COMPARE_AND_REMOVE_BACKUP = 9;
    public static final int COMPARE_AND_REMOVE = 10;
    public static final int CONTAINS = 11;
    public static final int DRAIN_BACKUP = 12;
    public static final int DRAIN = 13;
    public static final int ITERATOR = 14;
    public static final int QUEUE_EVENT = 15;
    public static final int QUEUE_EVENT_FILTER = 16;
    public static final int QUEUE_ITEM = 17;
    public static final int QUEUE_REPLICATION = 18;
    public static final int REMOVE_BACKUP = 19;
    public static final int REMOVE = 20;
    public static final int SIZE = 22;
    public static final int TXN_OFFER_BACKUP = 23;
    public static final int TXN_OFFER = 24;
    public static final int TXN_POLL_BACKUP = 25;
    public static final int TXN_POLL = 26;
    public static final int TXN_PREPARE_BACKUP = 27;
    public static final int TXN_PREPARE = 28;
    public static final int TXN_RESERVE_OFFER = 29;
    public static final int TXN_RESERVE_OFFER_BACKUP = 30;
    public static final int TXN_RESERVE_POLL = 31;
    public static final int TXN_RESERVE_POLL_BACKUP = 32;
    public static final int TXN_ROLLBACK_BACKUP = 33;
    public static final int TXN_ROLLBACK = 34;
    public static final int CHECK_EVICT = 35;
    public static final int TRANSACTION_ROLLBACK = 36;
    public static final int TX_QUEUE_ITEM = 37;
    public static final int QUEUE_CONTAINER = 38;
    public static final int TXN_PEEK = 39;
    public static final int IS_EMPTY = 40;
    public static final int REMAINING_CAPACITY = 41;
    public static final int TXN_COMMIT = 42;
    public static final int TXN_COMMIT_BACKUP = 43;
    public static final int MERGE = 44;
    public static final int MERGE_BACKUP = 45;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[46];
        constructors[0] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new OfferOperation();
            }
        };
        constructors[3] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new OfferBackupOperation();
            }
        };
        constructors[1] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PollOperation();
            }
        };
        constructors[4] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PollBackupOperation();
            }
        };
        constructors[2] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new PeekOperation();
            }
        };
        constructors[5] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddAllBackupOperation();
            }
        };
        constructors[6] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new AddAllOperation();
            }
        };
        constructors[7] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearBackupOperation();
            }
        };
        constructors[8] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ClearOperation();
            }
        };
        constructors[9] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CompareAndRemoveBackupOperation();
            }
        };
        constructors[10] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CompareAndRemoveOperation();
            }
        };
        constructors[11] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new ContainsOperation();
            }
        };
        constructors[12] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DrainBackupOperation();
            }
        };
        constructors[13] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new DrainOperation();
            }
        };
        constructors[14] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IteratorOperation();
            }
        };
        constructors[15] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueEvent();
            }
        };
        constructors[16] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueEventFilter();
            }
        };
        constructors[17] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueItem();
            }
        };
        constructors[18] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueReplicationOperation();
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
        constructors[22] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SizeOperation();
            }
        };
        constructors[23] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnOfferBackupOperation();
            }
        };
        constructors[24] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnOfferOperation();
            }
        };
        constructors[25] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPollBackupOperation();
            }
        };
        constructors[26] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPollOperation();
            }
        };
        constructors[27] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPrepareBackupOperation();
            }
        };
        constructors[28] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPrepareOperation();
            }
        };
        constructors[29] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnReserveOfferOperation();
            }
        };
        constructors[30] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnReserveOfferBackupOperation();
            }
        };
        constructors[31] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnReservePollOperation();
            }
        };
        constructors[32] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnReservePollBackupOperation();
            }
        };
        constructors[33] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRollbackBackupOperation();
            }
        };
        constructors[34] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnRollbackOperation();
            }
        };
        constructors[35] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new CheckAndEvictOperation();
            }
        };
        constructors[38] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueContainer(null);
            }
        };
        constructors[36] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueTransactionRollbackOperation();
            }
        };
        constructors[37] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxQueueItem();
            }
        };
        constructors[39] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnPeekOperation();
            }
        };
        constructors[40] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new IsEmptyOperation();
            }
        };
        constructors[41] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new RemainingCapacityOperation();
            }
        };
        constructors[42] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnCommitOperation();
            }
        };
        constructors[43] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new TxnCommitBackupOperation();
            }
        };
        constructors[44] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueMergeOperation();
            }
        };
        constructors[45] = new ConstructorFunction<Integer, IdentifiedDataSerializable>(){

            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new QueueMergeBackupOperation();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }
}

