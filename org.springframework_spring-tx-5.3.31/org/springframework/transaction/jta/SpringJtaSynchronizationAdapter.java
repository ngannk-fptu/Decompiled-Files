/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.jta;

import javax.transaction.Synchronization;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.jta.UserTransactionAdapter;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class SpringJtaSynchronizationAdapter
implements Synchronization {
    protected static final Log logger = LogFactory.getLog(SpringJtaSynchronizationAdapter.class);
    private final TransactionSynchronization springSynchronization;
    @Nullable
    private UserTransaction jtaTransaction;
    private boolean beforeCompletionCalled = false;

    public SpringJtaSynchronizationAdapter(TransactionSynchronization springSynchronization) {
        Assert.notNull((Object)springSynchronization, (String)"TransactionSynchronization must not be null");
        this.springSynchronization = springSynchronization;
    }

    public SpringJtaSynchronizationAdapter(TransactionSynchronization springSynchronization, @Nullable UserTransaction jtaUserTransaction) {
        this(springSynchronization);
        if (jtaUserTransaction != null && !jtaUserTransaction.getClass().getName().startsWith("weblogic.")) {
            this.jtaTransaction = jtaUserTransaction;
        }
    }

    public SpringJtaSynchronizationAdapter(TransactionSynchronization springSynchronization, @Nullable TransactionManager jtaTransactionManager) {
        this(springSynchronization);
        if (jtaTransactionManager != null && !jtaTransactionManager.getClass().getName().startsWith("weblogic.")) {
            this.jtaTransaction = new UserTransactionAdapter(jtaTransactionManager);
        }
    }

    public void beforeCompletion() {
        try {
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            this.springSynchronization.beforeCommit(readOnly);
        }
        catch (Error | RuntimeException ex) {
            this.setRollbackOnlyIfPossible();
            throw ex;
        }
        finally {
            this.beforeCompletionCalled = true;
            this.springSynchronization.beforeCompletion();
        }
    }

    private void setRollbackOnlyIfPossible() {
        if (this.jtaTransaction != null) {
            try {
                this.jtaTransaction.setRollbackOnly();
            }
            catch (UnsupportedOperationException ex) {
                logger.debug((Object)"JTA transaction handle does not support setRollbackOnly method - relying on JTA provider to mark the transaction as rollback-only based on the exception thrown from beforeCompletion", (Throwable)ex);
            }
            catch (Throwable ex) {
                logger.error((Object)"Could not set JTA transaction rollback-only", ex);
            }
        } else {
            logger.debug((Object)"No JTA transaction handle available and/or running on WebLogic - relying on JTA provider to mark the transaction as rollback-only based on the exception thrown from beforeCompletion");
        }
    }

    public void afterCompletion(int status) {
        if (!this.beforeCompletionCalled) {
            this.springSynchronization.beforeCompletion();
        }
        switch (status) {
            case 3: {
                this.springSynchronization.afterCompletion(0);
                break;
            }
            case 4: {
                this.springSynchronization.afterCompletion(1);
                break;
            }
            default: {
                this.springSynchronization.afterCompletion(2);
            }
        }
    }
}

