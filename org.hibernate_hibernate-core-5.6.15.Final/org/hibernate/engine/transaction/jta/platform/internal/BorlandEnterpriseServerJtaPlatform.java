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

public class BorlandEnterpriseServerJtaPlatform
extends AbstractJtaPlatform {
    protected static final String TM_NAME = "java:pm/TransactionManager";
    protected static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected TransactionManager locateTransactionManager() {
        return (TransactionManager)this.jndiService().locate(TM_NAME);
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate(UT_NAME);
    }
}

