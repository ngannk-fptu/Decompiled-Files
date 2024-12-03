/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  javax.transaction.TransactionSynchronizationRegistry
 *  javax.transaction.UserTransaction
 *  org.hibernate.TransactionException
 *  org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.jta.UserTransactionAdapter
 *  org.springframework.util.Assert
 */
package org.springframework.orm.hibernate5;

import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import org.hibernate.TransactionException;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.springframework.lang.Nullable;
import org.springframework.transaction.jta.UserTransactionAdapter;
import org.springframework.util.Assert;

class ConfigurableJtaPlatform
implements JtaPlatform {
    private final TransactionManager transactionManager;
    private final UserTransaction userTransaction;
    @Nullable
    private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    public ConfigurableJtaPlatform(TransactionManager tm, @Nullable UserTransaction ut, @Nullable TransactionSynchronizationRegistry tsr) {
        Assert.notNull((Object)tm, (String)"TransactionManager reference must not be null");
        this.transactionManager = tm;
        this.userTransaction = ut != null ? ut : new UserTransactionAdapter(tm);
        this.transactionSynchronizationRegistry = tsr;
    }

    public TransactionManager retrieveTransactionManager() {
        return this.transactionManager;
    }

    public UserTransaction retrieveUserTransaction() {
        return this.userTransaction;
    }

    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }

    public boolean canRegisterSynchronization() {
        try {
            return this.transactionManager.getStatus() == 0;
        }
        catch (SystemException ex) {
            throw new TransactionException("Could not determine JTA transaction status", (Throwable)ex);
        }
    }

    public void registerSynchronization(Synchronization synchronization) {
        if (this.transactionSynchronizationRegistry != null) {
            this.transactionSynchronizationRegistry.registerInterposedSynchronization(synchronization);
        } else {
            try {
                this.transactionManager.getTransaction().registerSynchronization(synchronization);
            }
            catch (Exception ex) {
                throw new TransactionException("Could not access JTA Transaction to register synchronization", (Throwable)ex);
            }
        }
    }

    public int getCurrentStatus() throws SystemException {
        return this.transactionManager.getStatus();
    }
}

