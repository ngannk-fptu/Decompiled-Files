/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.TransactionSynchronisationManager
 *  com.atlassian.confluence.core.SynchronizationManager
 *  org.springframework.transaction.support.TransactionSynchronization
 */
package com.atlassian.confluence.activeobjects.transaction;

import com.atlassian.activeobjects.spi.TransactionSynchronisationManager;
import com.atlassian.confluence.core.SynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

public class ConfluenceAOSynchronisationManager
implements TransactionSynchronisationManager {
    private SynchronizationManager synchronisationManager;

    public ConfluenceAOSynchronisationManager(SynchronizationManager synchManager) {
        this.synchronisationManager = synchManager;
    }

    public boolean runOnRollBack(final Runnable callback) {
        if (this.synchronisationManager.isTransactionActive()) {
            this.synchronisationManager.registerSynchronization(new TransactionSynchronization(){

                public void afterCompletion(int status) {
                    if (status == 1) {
                        callback.run();
                    }
                }
            });
            return true;
        }
        return false;
    }

    public boolean runOnSuccessfulCommit(Runnable callback) {
        if (this.synchronisationManager.isTransactionActive()) {
            this.synchronisationManager.runOnSuccessfulCommit(callback);
            return true;
        }
        return false;
    }

    public boolean isActiveSynchronisedTransaction() {
        return this.synchronisationManager.isTransactionActive();
    }
}

