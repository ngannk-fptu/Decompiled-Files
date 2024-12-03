/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.support;

import java.util.function.Consumer;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

final class WithoutTransactionOperations
implements TransactionOperations {
    static final WithoutTransactionOperations INSTANCE = new WithoutTransactionOperations();

    private WithoutTransactionOperations() {
    }

    @Override
    @Nullable
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        return action.doInTransaction(new SimpleTransactionStatus(false));
    }

    @Override
    public void executeWithoutResult(Consumer<TransactionStatus> action) throws TransactionException {
        action.accept(new SimpleTransactionStatus(false));
    }
}

