/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.support;

import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public abstract class TransactionCallbackWithoutResult
implements TransactionCallback<Object> {
    @Override
    @Nullable
    public final Object doInTransaction(TransactionStatus status) {
        this.doInTransactionWithoutResult(status);
        return null;
    }

    protected abstract void doInTransactionWithoutResult(TransactionStatus var1);
}

