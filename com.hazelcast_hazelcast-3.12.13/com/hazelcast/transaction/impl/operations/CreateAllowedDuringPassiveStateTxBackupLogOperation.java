/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.transaction.impl.operations.CreateTxBackupLogOperation;

public final class CreateAllowedDuringPassiveStateTxBackupLogOperation
extends CreateTxBackupLogOperation
implements AllowedDuringPassiveState {
    public CreateAllowedDuringPassiveStateTxBackupLogOperation() {
    }

    public CreateAllowedDuringPassiveStateTxBackupLogOperation(String callerUuid, String txnId) {
        super(callerUuid, txnId);
    }

    @Override
    public void run() throws Exception {
        TransactionManagerServiceImpl txManagerService = (TransactionManagerServiceImpl)this.getService();
        txManagerService.createAllowedDuringPassiveStateBackupLog(this.getCallerUuid(), this.getTxnId());
    }

    @Override
    public int getId() {
        return 5;
    }
}

