/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 *  javax.transaction.TransactionManager
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import org.hibernate.TransactionException;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionAdapter;
import org.hibernate.resource.transaction.backend.jta.internal.StatusTranslator;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jboss.logging.Logger;

public class JtaTransactionAdapterTransactionManagerImpl
implements JtaTransactionAdapter {
    private static final Logger log = Logger.getLogger(JtaTransactionAdapterTransactionManagerImpl.class);
    private final TransactionManager transactionManager;
    private boolean initiator;

    public JtaTransactionAdapterTransactionManagerImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void begin() {
        try {
            if (this.getStatus() == TransactionStatus.NOT_ACTIVE) {
                log.trace((Object)"Calling TransactionManager#begin");
                this.transactionManager.begin();
                this.initiator = true;
                log.trace((Object)"Called TransactionManager#begin");
            } else {
                log.trace((Object)"Skipping TransactionManager#begin due to already active transaction");
            }
        }
        catch (Exception e) {
            throw new TransactionException("JTA TransactionManager#begin failed", e);
        }
    }

    @Override
    public void commit() {
        try {
            if (this.initiator) {
                this.initiator = false;
                log.trace((Object)"Calling TransactionManager#commit");
                this.transactionManager.commit();
                log.trace((Object)"Called TransactionManager#commit");
            } else {
                log.trace((Object)"Skipping TransactionManager#commit due to not being initiator");
            }
        }
        catch (Exception e) {
            throw new TransactionException("JTA TransactionManager#commit failed", e);
        }
    }

    @Override
    public void rollback() {
        try {
            if (this.initiator) {
                this.initiator = false;
                log.trace((Object)"Calling TransactionManager#rollback");
                this.transactionManager.rollback();
                log.trace((Object)"Called TransactionManager#rollback");
            } else {
                this.markRollbackOnly();
            }
        }
        catch (Exception e) {
            throw new TransactionException("JTA TransactionManager#rollback failed", e);
        }
    }

    @Override
    public TransactionStatus getStatus() {
        try {
            return StatusTranslator.translate(this.transactionManager.getStatus());
        }
        catch (SystemException e) {
            throw new TransactionException("JTA TransactionManager#getStatus failed", e);
        }
    }

    @Override
    public void markRollbackOnly() {
        try {
            this.transactionManager.setRollbackOnly();
        }
        catch (SystemException e) {
            throw new TransactionException("Could not set transaction to rollback only", e);
        }
    }

    @Override
    public void setTimeOut(int seconds) {
    }
}

