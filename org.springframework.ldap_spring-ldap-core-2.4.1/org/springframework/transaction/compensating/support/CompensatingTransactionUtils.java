/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.transaction.compensating.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.transaction.compensating.CompensatingTransactionOperationManager;
import org.springframework.transaction.compensating.support.CompensatingTransactionHolderSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class CompensatingTransactionUtils {
    private CompensatingTransactionUtils() {
    }

    public static void performOperation(Object synchronizationKey, Object target, Method method, Object[] args) throws Throwable {
        CompensatingTransactionHolderSupport transactionResourceHolder = (CompensatingTransactionHolderSupport)((Object)TransactionSynchronizationManager.getResource((Object)synchronizationKey));
        if (transactionResourceHolder != null) {
            CompensatingTransactionOperationManager transactionOperationManager = transactionResourceHolder.getTransactionOperationManager();
            transactionOperationManager.performOperation(transactionResourceHolder.getTransactedResource(), method.getName(), args);
        } else {
            try {
                method.invoke(target, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}

