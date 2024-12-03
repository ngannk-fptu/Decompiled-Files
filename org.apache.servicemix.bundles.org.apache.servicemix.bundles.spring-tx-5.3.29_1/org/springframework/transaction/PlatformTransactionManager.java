/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction;

import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;

public interface PlatformTransactionManager
extends TransactionManager {
    public TransactionStatus getTransaction(@Nullable TransactionDefinition var1) throws TransactionException;

    public void commit(TransactionStatus var1) throws TransactionException;

    public void rollback(TransactionStatus var1) throws TransactionException;
}

