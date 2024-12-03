/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.transaction.impl.operations.PurgeTxBackupLogOperation;

public final class PurgeAllowedDuringPassiveStateTxBackupLogOperation
extends PurgeTxBackupLogOperation
implements AllowedDuringPassiveState {
    public PurgeAllowedDuringPassiveStateTxBackupLogOperation() {
    }

    public PurgeAllowedDuringPassiveStateTxBackupLogOperation(String txnId) {
        super(txnId);
    }

    @Override
    public int getId() {
        return 6;
    }
}

