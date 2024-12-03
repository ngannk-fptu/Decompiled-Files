/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.xa.operations.CollectRemoteTransactionsOperation;
import com.hazelcast.util.function.Supplier;

public class CollectRemoteTransactionsOperationSupplier
implements Supplier<Operation> {
    @Override
    public Operation get() {
        return new CollectRemoteTransactionsOperation();
    }
}

