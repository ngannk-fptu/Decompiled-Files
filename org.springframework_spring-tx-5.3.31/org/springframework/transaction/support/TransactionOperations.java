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
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.WithoutTransactionOperations;

public interface TransactionOperations {
    @Nullable
    public <T> T execute(TransactionCallback<T> var1) throws TransactionException;

    default public void executeWithoutResult(Consumer<TransactionStatus> action) throws TransactionException {
        this.execute(status -> {
            action.accept(status);
            return null;
        });
    }

    public static TransactionOperations withoutTransaction() {
        return WithoutTransactionOperations.INSTANCE;
    }
}

