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
package org.hibernate.engine.transaction.jta.platform.spi;

import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.service.Service;

public interface JtaPlatform
extends Service {
    public TransactionManager retrieveTransactionManager();

    public UserTransaction retrieveUserTransaction();

    public Object getTransactionIdentifier(Transaction var1);

    public boolean canRegisterSynchronization();

    public void registerSynchronization(Synchronization var1);

    public int getCurrentStatus() throws SystemException;
}

