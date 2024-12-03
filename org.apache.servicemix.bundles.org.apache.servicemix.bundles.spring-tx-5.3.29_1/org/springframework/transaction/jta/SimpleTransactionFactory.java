/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.NotSupportedException
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.jta;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.jta.ManagedTransactionAdapter;
import org.springframework.transaction.jta.TransactionFactory;
import org.springframework.util.Assert;

public class SimpleTransactionFactory
implements TransactionFactory {
    private final TransactionManager transactionManager;

    public SimpleTransactionFactory(TransactionManager transactionManager) {
        Assert.notNull((Object)transactionManager, (String)"TransactionManager must not be null");
        this.transactionManager = transactionManager;
    }

    @Override
    public Transaction createTransaction(@Nullable String name, int timeout) throws NotSupportedException, SystemException {
        if (timeout >= 0) {
            this.transactionManager.setTransactionTimeout(timeout);
        }
        this.transactionManager.begin();
        return new ManagedTransactionAdapter(this.transactionManager);
    }

    @Override
    public boolean supportsResourceAdapterManagedTransactions() {
        return false;
    }
}

