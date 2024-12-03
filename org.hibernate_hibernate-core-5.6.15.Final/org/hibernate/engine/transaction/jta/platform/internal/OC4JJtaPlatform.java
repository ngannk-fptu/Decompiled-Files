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
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

public class OC4JJtaPlatform
extends AbstractJtaPlatform {
    public static final String TM_NAME = "java:comp/pm/TransactionManager";
    public static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected TransactionManager locateTransactionManager() {
        return (TransactionManager)this.jndiService().locate(TM_NAME);
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate(UT_NAME);
    }
}

