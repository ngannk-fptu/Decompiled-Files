/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionalTaskContext;
import javax.transaction.xa.XAResource;

public interface TransactionContext
extends TransactionalTaskContext {
    public void beginTransaction();

    public void commitTransaction() throws TransactionException;

    public void rollbackTransaction();

    public String getTxnId();

    @Deprecated
    public XAResource getXaResource();
}

