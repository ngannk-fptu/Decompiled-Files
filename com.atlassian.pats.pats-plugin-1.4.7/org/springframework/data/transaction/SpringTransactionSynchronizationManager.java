/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.data.transaction;

import org.springframework.data.transaction.SynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

enum SpringTransactionSynchronizationManager implements SynchronizationManager
{
    INSTANCE;


    @Override
    public void initSynchronization() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @Override
    public boolean isSynchronizationActive() {
        return TransactionSynchronizationManager.isSynchronizationActive();
    }

    @Override
    public void clearSynchronization() {
        TransactionSynchronizationManager.clear();
    }
}

