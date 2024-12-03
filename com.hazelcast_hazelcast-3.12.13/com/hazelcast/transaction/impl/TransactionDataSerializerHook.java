/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.transaction.impl.operations.BroadcastTxRollbackOperation;
import com.hazelcast.transaction.impl.operations.CreateAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.CreateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.PurgeAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.PurgeTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.ReplicateAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.ReplicateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.RollbackAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.RollbackTxBackupLogOperation;
import com.hazelcast.transaction.impl.xa.XATransactionDTO;
import com.hazelcast.transaction.impl.xa.operations.ClearRemoteTransactionBackupOperation;
import com.hazelcast.transaction.impl.xa.operations.ClearRemoteTransactionOperation;
import com.hazelcast.transaction.impl.xa.operations.CollectRemoteTransactionsOperation;
import com.hazelcast.transaction.impl.xa.operations.FinalizeRemoteTransactionBackupOperation;
import com.hazelcast.transaction.impl.xa.operations.FinalizeRemoteTransactionOperation;
import com.hazelcast.transaction.impl.xa.operations.PutRemoteTransactionBackupOperation;
import com.hazelcast.transaction.impl.xa.operations.PutRemoteTransactionOperation;
import com.hazelcast.transaction.impl.xa.operations.XaReplicationOperation;

public final class TransactionDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId("hazelcast.serialization.ds.transaction", -19);
    public static final int CREATE_TX_BACKUP_LOG = 0;
    public static final int BROADCAST_TX_ROLLBACK = 1;
    public static final int PURGE_TX_BACKUP_LOG = 2;
    public static final int REPLICATE_TX_BACKUP_LOG = 3;
    public static final int ROLLBACK_TX_BACKUP_LOG = 4;
    public static final int CREATE_ALLOWED_DURING_PASSIVE_STATE_TX_BACKUP_LOG = 5;
    public static final int PURGE_ALLOWED_DURING_PASSIVE_STATE_TX_BACKUP_LOG = 6;
    public static final int REPLICATE_ALLOWED_DURING_PASSIVE_STATE_TX_BACKUP_LOG = 7;
    public static final int ROLLBACK_ALLOWED_DURING_PASSIVE_STATE_TX_BACKUP_LOG = 8;
    public static final int CLEAR_REMOTE_TX_BACKUP = 9;
    public static final int CLEAR_REMOTE_TX = 10;
    public static final int COLLECT_REMOTE_TX = 11;
    public static final int COLLECT_REMOTE_TX_FACTORY = 12;
    public static final int FINALIZE_REMOTE_TX_BACKUP = 13;
    public static final int FINALIZE_REMOTE_TX = 14;
    public static final int PUT_REMOTE_TX_BACKUP = 15;
    public static final int PUT_REMOTE_TX = 16;
    public static final int XA_REPLICATION = 17;
    public static final int XA_TRANSACTION_DTO = 18;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        return new DataSerializableFactory(){

            @Override
            public IdentifiedDataSerializable create(int typeId) {
                switch (typeId) {
                    case 0: {
                        return new CreateTxBackupLogOperation();
                    }
                    case 1: {
                        return new BroadcastTxRollbackOperation();
                    }
                    case 2: {
                        return new PurgeTxBackupLogOperation();
                    }
                    case 3: {
                        return new ReplicateTxBackupLogOperation();
                    }
                    case 4: {
                        return new RollbackTxBackupLogOperation();
                    }
                    case 5: {
                        return new CreateAllowedDuringPassiveStateTxBackupLogOperation();
                    }
                    case 6: {
                        return new PurgeAllowedDuringPassiveStateTxBackupLogOperation();
                    }
                    case 7: {
                        return new ReplicateAllowedDuringPassiveStateTxBackupLogOperation();
                    }
                    case 8: {
                        return new RollbackAllowedDuringPassiveStateTxBackupLogOperation();
                    }
                    case 9: {
                        return new ClearRemoteTransactionBackupOperation();
                    }
                    case 10: {
                        return new ClearRemoteTransactionOperation();
                    }
                    case 11: {
                        return new CollectRemoteTransactionsOperation();
                    }
                    case 13: {
                        return new FinalizeRemoteTransactionBackupOperation();
                    }
                    case 14: {
                        return new FinalizeRemoteTransactionOperation();
                    }
                    case 15: {
                        return new PutRemoteTransactionBackupOperation();
                    }
                    case 16: {
                        return new PutRemoteTransactionOperation();
                    }
                    case 17: {
                        return new XaReplicationOperation();
                    }
                    case 18: {
                        return new XATransactionDTO();
                    }
                }
                return null;
            }
        };
    }
}

