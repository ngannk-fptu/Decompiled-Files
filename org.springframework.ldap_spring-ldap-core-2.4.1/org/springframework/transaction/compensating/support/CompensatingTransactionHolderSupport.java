/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.support.ResourceHolderSupport
 */
package org.springframework.transaction.compensating.support;

import org.springframework.transaction.compensating.CompensatingTransactionOperationManager;
import org.springframework.transaction.support.ResourceHolderSupport;

public abstract class CompensatingTransactionHolderSupport
extends ResourceHolderSupport {
    private CompensatingTransactionOperationManager transactionOperationManager;

    public CompensatingTransactionHolderSupport(CompensatingTransactionOperationManager manager) {
        this.transactionOperationManager = manager;
    }

    protected abstract Object getTransactedResource();

    public void clear() {
        super.clear();
        this.transactionOperationManager = null;
    }

    public CompensatingTransactionOperationManager getTransactionOperationManager() {
        return this.transactionOperationManager;
    }

    public void setTransactionOperationManager(CompensatingTransactionOperationManager transactionOperationManager) {
        this.transactionOperationManager = transactionOperationManager;
    }
}

