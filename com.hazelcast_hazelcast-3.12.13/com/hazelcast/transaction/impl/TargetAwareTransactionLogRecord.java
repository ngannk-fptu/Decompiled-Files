/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.transaction.impl.TransactionLogRecord;

public interface TargetAwareTransactionLogRecord
extends TransactionLogRecord {
    public Address getTarget();
}

