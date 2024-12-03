/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

public class AtomikosJtaPlatform
extends AbstractJtaPlatform {
    public static final String TM_CLASS_NAME = "com.atomikos.icatch.jta.UserTransactionManager";

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            Class transactionManagerClass = this.serviceRegistry().getService(ClassLoaderService.class).classForName(TM_CLASS_NAME);
            return (TransactionManager)transactionManagerClass.newInstance();
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not instantiate Atomikos TransactionManager", e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate("java:comp/UserTransaction");
    }
}

