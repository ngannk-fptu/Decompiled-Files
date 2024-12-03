/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.transaction.TransactionalObject;
import com.hazelcast.transaction.impl.Transaction;

public interface TransactionalService {
    public <T extends TransactionalObject> T createTransactionalObject(String var1, Transaction var2);

    public void rollbackTransaction(String var1);
}

