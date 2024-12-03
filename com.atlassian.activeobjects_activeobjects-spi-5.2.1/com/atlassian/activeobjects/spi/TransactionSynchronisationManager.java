/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

public interface TransactionSynchronisationManager {
    public boolean runOnRollBack(Runnable var1);

    public boolean runOnSuccessfulCommit(Runnable var1);

    public boolean isActiveSynchronisedTransaction();
}

