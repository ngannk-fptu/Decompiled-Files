/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTaskContext;

public interface TransactionalTask<T> {
    public T execute(TransactionalTaskContext var1) throws TransactionException;
}

