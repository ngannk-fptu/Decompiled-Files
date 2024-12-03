/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.NotSupportedException
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.jta;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import org.springframework.lang.Nullable;

public interface TransactionFactory {
    public Transaction createTransaction(@Nullable String var1, int var2) throws NotSupportedException, SystemException;

    public boolean supportsResourceAdapterManagedTransactions();
}

