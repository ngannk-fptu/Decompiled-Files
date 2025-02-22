/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.operations;

import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.transaction.impl.TransactionLogRecord;
import com.hazelcast.transaction.impl.operations.ReplicateTxBackupLogOperation;
import java.util.Collection;

public final class ReplicateAllowedDuringPassiveStateTxBackupLogOperation
extends ReplicateTxBackupLogOperation
implements AllowedDuringPassiveState {
    public ReplicateAllowedDuringPassiveStateTxBackupLogOperation() {
    }

    public ReplicateAllowedDuringPassiveStateTxBackupLogOperation(Collection<TransactionLogRecord> logs, String callerUuid, String txnId, long timeoutMillis, long startTime) {
        super(logs, callerUuid, txnId, timeoutMillis, startTime);
    }

    @Override
    public int getId() {
        return 7;
    }
}

