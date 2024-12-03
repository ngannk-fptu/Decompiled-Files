/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.HeuristicMixedException
 *  javax.transaction.HeuristicRollbackException
 *  javax.transaction.NotSupportedException
 *  javax.transaction.RollbackException
 *  javax.transaction.SystemException
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.jta;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.springframework.util.Assert;

public class UserTransactionAdapter
implements UserTransaction {
    private final TransactionManager transactionManager;

    public UserTransactionAdapter(TransactionManager transactionManager) {
        Assert.notNull((Object)transactionManager, (String)"TransactionManager must not be null");
        this.transactionManager = transactionManager;
    }

    public final TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public void setTransactionTimeout(int timeout) throws SystemException {
        this.transactionManager.setTransactionTimeout(timeout);
    }

    public void begin() throws NotSupportedException, SystemException {
        this.transactionManager.begin();
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
        this.transactionManager.commit();
    }

    public void rollback() throws SecurityException, SystemException {
        this.transactionManager.rollback();
    }

    public void setRollbackOnly() throws SystemException {
        this.transactionManager.setRollbackOnly();
    }

    public int getStatus() throws SystemException {
        return this.transactionManager.getStatus();
    }
}

