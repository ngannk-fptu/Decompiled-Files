/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.TransactionLogRecord;

public interface Transaction {
    public void begin() throws IllegalStateException;

    public void prepare() throws TransactionException;

    public void commit() throws TransactionException, IllegalStateException;

    public void rollback() throws IllegalStateException;

    public String getTxnId();

    public State getState();

    public long getTimeoutMillis();

    public void add(TransactionLogRecord var1);

    public void remove(Object var1);

    public TransactionLogRecord get(Object var1);

    public String getOwnerUuid();

    public boolean isOriginatedFromClient();

    public TransactionOptions.TransactionType getTransactionType();

    public static enum State {
        NO_TXN,
        ACTIVE,
        PREPARING,
        PREPARED,
        COMMITTING,
        COMMITTED,
        COMMIT_FAILED,
        ROLLING_BACK,
        ROLLED_BACK;

    }
}

