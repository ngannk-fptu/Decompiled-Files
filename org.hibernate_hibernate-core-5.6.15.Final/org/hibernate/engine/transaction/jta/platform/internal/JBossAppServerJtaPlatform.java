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
import org.hibernate.engine.jndi.JndiException;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

public class JBossAppServerJtaPlatform
extends AbstractJtaPlatform {
    public static final String AS7_TM_NAME = "java:jboss/TransactionManager";
    public static final String AS4_TM_NAME = "java:/TransactionManager";
    public static final String JBOSS_UT_NAME = "java:jboss/UserTransaction";
    public static final String UT_NAME = "java:comp/UserTransaction";

    @Override
    protected boolean canCacheUserTransactionByDefault() {
        return true;
    }

    @Override
    protected boolean canCacheTransactionManagerByDefault() {
        return true;
    }

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            return (TransactionManager)this.jndiService().locate(AS7_TM_NAME);
        }
        catch (JndiException jndiException) {
            try {
                return (TransactionManager)this.jndiService().locate(AS4_TM_NAME);
            }
            catch (JndiException jndiExceptionInner) {
                throw new JndiException("unable to find transaction manager", (Throwable)((Object)jndiException));
            }
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        try {
            return (UserTransaction)this.jndiService().locate(JBOSS_UT_NAME);
        }
        catch (JndiException jndiException) {
            try {
                return (UserTransaction)this.jndiService().locate(UT_NAME);
            }
            catch (JndiException jndiExceptionInner) {
                throw new JndiException("unable to find UserTransaction", (Throwable)((Object)jndiException));
            }
        }
    }
}

