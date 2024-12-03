/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.support;

import org.springframework.lang.Nullable;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.support.AbstractTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.util.Assert;

public class DefaultTransactionStatus
extends AbstractTransactionStatus {
    @Nullable
    private final Object transaction;
    private final boolean newTransaction;
    private final boolean newSynchronization;
    private final boolean readOnly;
    private final boolean debug;
    @Nullable
    private final Object suspendedResources;

    public DefaultTransactionStatus(@Nullable Object transaction, boolean newTransaction, boolean newSynchronization, boolean readOnly, boolean debug, @Nullable Object suspendedResources) {
        this.transaction = transaction;
        this.newTransaction = newTransaction;
        this.newSynchronization = newSynchronization;
        this.readOnly = readOnly;
        this.debug = debug;
        this.suspendedResources = suspendedResources;
    }

    public Object getTransaction() {
        Assert.state((this.transaction != null ? 1 : 0) != 0, (String)"No transaction active");
        return this.transaction;
    }

    public boolean hasTransaction() {
        return this.transaction != null;
    }

    @Override
    public boolean isNewTransaction() {
        return this.hasTransaction() && this.newTransaction;
    }

    public boolean isNewSynchronization() {
        return this.newSynchronization;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public boolean isDebug() {
        return this.debug;
    }

    @Nullable
    public Object getSuspendedResources() {
        return this.suspendedResources;
    }

    @Override
    public boolean isGlobalRollbackOnly() {
        return this.transaction instanceof SmartTransactionObject && ((SmartTransactionObject)this.transaction).isRollbackOnly();
    }

    @Override
    protected SavepointManager getSavepointManager() {
        Object transaction = this.transaction;
        if (!(transaction instanceof SavepointManager)) {
            throw new NestedTransactionNotSupportedException("Transaction object [" + this.transaction + "] does not support savepoints");
        }
        return (SavepointManager)transaction;
    }

    public boolean isTransactionSavepointManager() {
        return this.transaction instanceof SavepointManager;
    }

    @Override
    public void flush() {
        if (this.transaction instanceof SmartTransactionObject) {
            ((SmartTransactionObject)this.transaction).flush();
        }
    }
}

