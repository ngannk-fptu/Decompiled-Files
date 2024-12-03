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

public class JOTMJtaPlatform
extends AbstractJtaPlatform {
    public static final String TM_CLASS_NAME = "org.objectweb.jotm.Current";
    public static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            Class tmClass = this.serviceRegistry().getService(ClassLoaderService.class).classForName(TM_CLASS_NAME);
            Method getTransactionManagerMethod = tmClass.getMethod("getTransactionManager", new Class[0]);
            return (TransactionManager)getTransactionManagerMethod.invoke(null, (Object[])null);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not obtain JOTM transaction manager instance", e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate(UT_NAME);
    }
}

