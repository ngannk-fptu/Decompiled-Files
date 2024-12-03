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
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

public class JOnASJtaPlatform
extends AbstractJtaPlatform {
    public static final String UT_NAME = "java:comp/UserTransaction";
    public static final String TM_CLASS_NAME = "org.objectweb.jonas_tm.Current";

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            Class<?> clazz = Class.forName(TM_CLASS_NAME);
            Method getTransactionManagerMethod = clazz.getMethod("getTransactionManager", new Class[0]);
            return (TransactionManager)getTransactionManagerMethod.invoke(null, new Object[0]);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not obtain JOnAS transaction manager instance", e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate(UT_NAME);
    }
}

