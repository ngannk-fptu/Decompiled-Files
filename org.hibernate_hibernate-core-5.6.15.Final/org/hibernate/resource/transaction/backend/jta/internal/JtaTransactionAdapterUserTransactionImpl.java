/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 *  javax.transaction.UserTransaction
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.hibernate.TransactionException;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionAdapter;
import org.hibernate.resource.transaction.backend.jta.internal.StatusTranslator;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jboss.logging.Logger;

public class JtaTransactionAdapterUserTransactionImpl
implements JtaTransactionAdapter {
    private static final Logger log = Logger.getLogger(JtaTransactionAdapterUserTransactionImpl.class);
    private final UserTransaction userTransaction;
    private boolean initiator;

    public JtaTransactionAdapterUserTransactionImpl(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    @Override
    public void begin() {
        try {
            if (this.getStatus() == TransactionStatus.NOT_ACTIVE) {
                log.trace((Object)"Calling UserTransaction#begin");
                this.userTransaction.begin();
                this.initiator = true;
                log.trace((Object)"Called UserTransaction#begin");
            } else {
                log.trace((Object)"Skipping TransactionManager#begin due to already active transaction");
            }
        }
        catch (Exception e) {
            throw new TransactionException("JTA UserTransaction#begin failed", e);
        }
    }

    @Override
    public void commit() {
        try {
            if (this.initiator) {
                this.initiator = false;
                log.trace((Object)"Calling UserTransaction#commit");
                this.userTransaction.commit();
                log.trace((Object)"Called UserTransaction#commit");
            } else {
                log.trace((Object)"Skipping TransactionManager#commit due to not being initiator");
            }
        }
        catch (Exception e) {
            throw new TransactionException("JTA UserTransaction#commit failed", e);
        }
    }

    @Override
    public void rollback() {
        try {
            if (this.initiator) {
                this.initiator = false;
                log.trace((Object)"Calling UserTransaction#rollback");
                this.userTransaction.rollback();
                log.trace((Object)"Called UserTransaction#rollback");
            } else {
                this.markRollbackOnly();
            }
        }
        catch (Exception e) {
            throw new TransactionException("JTA UserTransaction#rollback failed", e);
        }
    }

    @Override
    public TransactionStatus getStatus() {
        try {
            return StatusTranslator.translate(this.userTransaction.getStatus());
        }
        catch (SystemException e) {
            throw new TransactionException("JTA TransactionManager#getStatus failed", e);
        }
    }

    @Override
    public void markRollbackOnly() {
        try {
            this.userTransaction.setRollbackOnly();
        }
        catch (SystemException e) {
            throw new TransactionException("Unable to mark transaction for rollback only", e);
        }
    }

    @Override
    public void setTimeOut(int seconds) {
        if (seconds > 0) {
            try {
                this.userTransaction.setTransactionTimeout(seconds);
            }
            catch (SystemException e) {
                throw new TransactionException("Unable to apply requested transaction timeout", e);
            }
        }
    }
}

