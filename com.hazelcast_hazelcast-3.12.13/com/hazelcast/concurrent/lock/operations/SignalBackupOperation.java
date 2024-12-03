/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.operations.BaseSignalOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ObjectNamespace;

public class SignalBackupOperation
extends BaseSignalOperation
implements BackupOperation {
    public SignalBackupOperation() {
    }

    public SignalBackupOperation(ObjectNamespace namespace, Data key, long threadId, String conditionId, boolean all) {
        super(namespace, key, threadId, conditionId, all);
    }

    @Override
    public int getId() {
        return 13;
    }
}

