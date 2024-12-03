/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;

public interface TransactionLogRecord
extends IdentifiedDataSerializable {
    public Object getKey();

    public Operation newPrepareOperation();

    public Operation newCommitOperation();

    public void onCommitSuccess();

    public void onCommitFailure();

    public Operation newRollbackOperation();
}

