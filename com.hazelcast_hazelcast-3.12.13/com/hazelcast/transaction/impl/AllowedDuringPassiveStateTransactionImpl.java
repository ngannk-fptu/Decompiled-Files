/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.TransactionImpl;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.transaction.impl.operations.CreateAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.CreateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.PurgeAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.PurgeTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.ReplicateAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.ReplicateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.RollbackAllowedDuringPassiveStateTxBackupLogOperation;
import com.hazelcast.transaction.impl.operations.RollbackTxBackupLogOperation;
import java.util.List;

public class AllowedDuringPassiveStateTransactionImpl
extends TransactionImpl {
    public AllowedDuringPassiveStateTransactionImpl(TransactionManagerServiceImpl transactionManagerService, NodeEngine nodeEngine, TransactionOptions options, String txOwnerUuid) {
        super(transactionManagerService, nodeEngine, options, txOwnerUuid);
    }

    AllowedDuringPassiveStateTransactionImpl(TransactionManagerServiceImpl transactionManagerService, NodeEngine nodeEngine, String txnId, List<TransactionLogRecord> transactionLog, long timeoutMillis, long startTime, String txOwnerUuid) {
        super(transactionManagerService, nodeEngine, txnId, transactionLog, timeoutMillis, startTime, txOwnerUuid);
    }

    @Override
    protected CreateTxBackupLogOperation createCreateTxBackupLogOperation() {
        return new CreateAllowedDuringPassiveStateTxBackupLogOperation(this.getOwnerUuid(), this.getTxnId());
    }

    @Override
    protected ReplicateTxBackupLogOperation createReplicateTxBackupLogOperation() {
        return new ReplicateAllowedDuringPassiveStateTxBackupLogOperation(this.getTransactionLog().getRecords(), this.getOwnerUuid(), this.getTxnId(), this.getTimeoutMillis(), this.getStartTime());
    }

    @Override
    protected RollbackTxBackupLogOperation createRollbackTxBackupLogOperation() {
        return new RollbackAllowedDuringPassiveStateTxBackupLogOperation(this.getTxnId());
    }

    @Override
    protected PurgeTxBackupLogOperation createPurgeTxBackupLogOperation() {
        return new PurgeAllowedDuringPassiveStateTxBackupLogOperation(this.getTxnId());
    }
}

