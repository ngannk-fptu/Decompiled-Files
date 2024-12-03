/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.support.TransactionSynchronization
 */
package com.atlassian.confluence.core;

import java.util.List;
import org.springframework.transaction.support.TransactionSynchronization;

public interface SynchronizationManager {
    public void runOnSuccessfulCommit(Runnable var1);

    public void registerSynchronization(TransactionSynchronization var1);

    public List getSynchronizations();

    public boolean isTransactionActive();
}

