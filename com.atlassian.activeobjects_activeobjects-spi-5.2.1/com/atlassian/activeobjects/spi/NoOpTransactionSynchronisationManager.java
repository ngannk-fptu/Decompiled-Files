/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.TransactionSynchronisationManager;

public final class NoOpTransactionSynchronisationManager
implements TransactionSynchronisationManager {
    @Override
    public boolean runOnRollBack(Runnable callback) {
        return false;
    }

    @Override
    public boolean runOnSuccessfulCommit(Runnable callback) {
        return false;
    }

    @Override
    public boolean isActiveSynchronisedTransaction() {
        return false;
    }
}

