/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 *  javax.transaction.UserTransaction
 */
package org.springframework.transaction.jta;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

public class JtaTransactionObject
implements SmartTransactionObject {
    private final UserTransaction userTransaction;
    boolean resetTransactionTimeout = false;

    public JtaTransactionObject(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    public final UserTransaction getUserTransaction() {
        return this.userTransaction;
    }

    @Override
    public boolean isRollbackOnly() {
        try {
            int jtaStatus = this.userTransaction.getStatus();
            return jtaStatus == 1 || jtaStatus == 4;
        }
        catch (SystemException ex) {
            throw new TransactionSystemException("JTA failure on getStatus", ex);
        }
    }

    @Override
    public void flush() {
        TransactionSynchronizationUtils.triggerFlush();
    }
}

