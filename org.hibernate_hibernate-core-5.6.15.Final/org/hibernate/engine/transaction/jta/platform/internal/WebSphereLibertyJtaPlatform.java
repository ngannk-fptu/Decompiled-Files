/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.RollbackException
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

public class WebSphereLibertyJtaPlatform
extends AbstractJtaPlatform {
    public static final String TMF_CLASS_NAME = "com.ibm.tx.jta.TransactionManagerFactory";
    public static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            Class TransactionManagerFactory = this.serviceRegistry().getService(ClassLoaderService.class).classForName(TMF_CLASS_NAME);
            return (TransactionManager)TransactionManagerFactory.getMethod("getTransactionManager", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not obtain WebSphere Liberty transaction manager instance", e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate(UT_NAME);
    }

    @Override
    public boolean canRegisterSynchronization() {
        try {
            return this.getCurrentStatus() == 0;
        }
        catch (SystemException x) {
            throw new RuntimeException(x);
        }
    }

    @Override
    public int getCurrentStatus() throws SystemException {
        return this.retrieveTransactionManager().getStatus();
    }

    @Override
    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) {
        try {
            this.retrieveTransactionManager().getTransaction().registerSynchronization(synchronization);
        }
        catch (RollbackException | SystemException x) {
            throw new RuntimeException(x);
        }
    }
}

