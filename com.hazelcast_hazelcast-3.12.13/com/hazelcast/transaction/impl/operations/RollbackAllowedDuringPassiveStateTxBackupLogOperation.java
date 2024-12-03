/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.transaction.impl.operations.RollbackTxBackupLogOperation;

public class RollbackAllowedDuringPassiveStateTxBackupLogOperation
extends RollbackTxBackupLogOperation
implements AllowedDuringPassiveState {
    public RollbackAllowedDuringPassiveStateTxBackupLogOperation() {
    }

    public RollbackAllowedDuringPassiveStateTxBackupLogOperation(String txnId) {
        super(txnId);
    }

    @Override
    public int getId() {
        return 8;
    }
}

