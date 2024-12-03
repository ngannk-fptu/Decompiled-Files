/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;

public class NoJtaPlatform
implements JtaPlatform {
    public static final NoJtaPlatform INSTANCE = new NoJtaPlatform();

    @Override
    public TransactionManager retrieveTransactionManager() {
        return null;
    }

    @Override
    public UserTransaction retrieveUserTransaction() {
        return null;
    }

    @Override
    public Object getTransactionIdentifier(Transaction transaction) {
        return null;
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) {
    }

    @Override
    public boolean canRegisterSynchronization() {
        return false;
    }

    @Override
    public int getCurrentStatus() throws SystemException {
        return 5;
    }
}

