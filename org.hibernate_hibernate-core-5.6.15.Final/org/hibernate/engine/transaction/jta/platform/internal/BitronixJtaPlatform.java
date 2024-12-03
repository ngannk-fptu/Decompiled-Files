/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.lang.reflect.Method;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

public class BitronixJtaPlatform
extends AbstractJtaPlatform {
    public static final String TM_CLASS_NAME = "bitronix.tm.TransactionManagerServices";

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            Class transactionManagerServicesClass = this.serviceRegistry().getService(ClassLoaderService.class).classForName(TM_CLASS_NAME);
            Method getTransactionManagerMethod = transactionManagerServicesClass.getMethod("getTransactionManager", new Class[0]);
            return (TransactionManager)getTransactionManagerMethod.invoke(null, new Object[0]);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not locate Bitronix TransactionManager", e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate("java:comp/UserTransaction");
    }
}

