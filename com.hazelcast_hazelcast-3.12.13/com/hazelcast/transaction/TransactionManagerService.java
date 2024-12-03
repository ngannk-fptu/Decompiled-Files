/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;

public interface TransactionManagerService {
    public <T> T executeTransaction(TransactionOptions var1, TransactionalTask<T> var2) throws TransactionException;

    public TransactionContext newTransactionContext(TransactionOptions var1);

    public TransactionContext newClientTransactionContext(TransactionOptions var1, String var2);
}

