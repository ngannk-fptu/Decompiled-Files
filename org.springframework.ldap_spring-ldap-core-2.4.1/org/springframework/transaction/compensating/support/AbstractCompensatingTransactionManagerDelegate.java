/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.CannotCreateTransactionException
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.support.DefaultTransactionStatus
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.transaction.compensating.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.compensating.support.CompensatingTransactionHolderSupport;
import org.springframework.transaction.compensating.support.CompensatingTransactionObject;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class AbstractCompensatingTransactionManagerDelegate {
    private static Logger log = LoggerFactory.getLogger(AbstractCompensatingTransactionManagerDelegate.class);

    protected abstract void closeTargetResource(CompensatingTransactionHolderSupport var1);

    protected abstract CompensatingTransactionHolderSupport getNewHolder();

    protected abstract Object getTransactionSynchronizationKey();

    public Object doGetTransaction() throws TransactionException {
        CompensatingTransactionHolderSupport holder = (CompensatingTransactionHolderSupport)((Object)TransactionSynchronizationManager.getResource((Object)this.getTransactionSynchronizationKey()));
        return new CompensatingTransactionObject(holder);
    }

    public void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        try {
            CompensatingTransactionObject txObject = (CompensatingTransactionObject)transaction;
            if (txObject.getHolder() == null) {
                CompensatingTransactionHolderSupport contextHolder = this.getNewHolder();
                txObject.setHolder(contextHolder);
                TransactionSynchronizationManager.bindResource((Object)this.getTransactionSynchronizationKey(), (Object)((Object)contextHolder));
            }
        }
        catch (Exception e) {
            throw new CannotCreateTransactionException("Could not create DirContext instance for transaction", (Throwable)e);
        }
    }

    public void doCommit(DefaultTransactionStatus status) throws TransactionException {
        CompensatingTransactionObject txObject = (CompensatingTransactionObject)status.getTransaction();
        txObject.getHolder().getTransactionOperationManager().commit();
    }

    public void doRollback(DefaultTransactionStatus status) throws TransactionException {
        CompensatingTransactionObject txObject = (CompensatingTransactionObject)status.getTransaction();
        txObject.getHolder().getTransactionOperationManager().rollback();
    }

    public void doCleanupAfterCompletion(Object transaction) {
        log.debug("Cleaning stored transaction synchronization");
        TransactionSynchronizationManager.unbindResource((Object)this.getTransactionSynchronizationKey());
        CompensatingTransactionObject txObject = (CompensatingTransactionObject)transaction;
        CompensatingTransactionHolderSupport transactionHolderSupport = txObject.getHolder();
        this.closeTargetResource(transactionHolderSupport);
        txObject.getHolder().clear();
    }
}

