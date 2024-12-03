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

public class OrionJtaPlatform
extends AbstractJtaPlatform {
    public static final String TM_NAME = "java:comp/UserTransaction";
    public static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected TransactionManager locateTransactionManager() {
        return (TransactionManager)this.jndiService().locate("java:comp/UserTransaction");
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)this.jndiService().locate("java:comp/UserTransaction");
    }
}

