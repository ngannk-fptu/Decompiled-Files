/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 */
package org.hibernate.engine.transaction.internal.jta;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.TransactionException;

public final class JtaStatusHelper {
    private JtaStatusHelper() {
    }

    public static int getStatus(UserTransaction userTransaction) {
        try {
            int status = userTransaction.getStatus();
            if (status == 5) {
                throw new TransactionException("UserTransaction reported transaction status as unknown");
            }
            return status;
        }
        catch (SystemException se) {
            throw new TransactionException("Could not determine transaction status", se);
        }
    }

    public static int getStatus(TransactionManager transactionManager) {
        try {
            int status = transactionManager.getStatus();
            if (status == 5) {
                throw new TransactionException("TransactionManager reported transaction status as unknwon");
            }
            return status;
        }
        catch (SystemException se) {
            throw new TransactionException("Could not determine transaction status", se);
        }
    }

    public static boolean isActive(int status) {
        return status == 0;
    }

    public static boolean isActive(UserTransaction userTransaction) {
        int status = JtaStatusHelper.getStatus(userTransaction);
        return JtaStatusHelper.isActive(status);
    }

    public static boolean isActive(TransactionManager transactionManager) {
        return JtaStatusHelper.isActive(JtaStatusHelper.getStatus(transactionManager));
    }

    public static boolean isRollback(int status) {
        return status == 1 || status == 9 || status == 4;
    }

    public static boolean isRollback(UserTransaction userTransaction) {
        return JtaStatusHelper.isRollback(JtaStatusHelper.getStatus(userTransaction));
    }

    public static boolean isRollback(TransactionManager transactionManager) {
        return JtaStatusHelper.isRollback(JtaStatusHelper.getStatus(transactionManager));
    }

    public static boolean isCommitted(int status) {
        return status == 3;
    }

    public static boolean isCommitted(UserTransaction userTransaction) {
        return JtaStatusHelper.isCommitted(JtaStatusHelper.getStatus(userTransaction));
    }

    public static boolean isCommitted(TransactionManager transactionManager) {
        return JtaStatusHelper.isCommitted(JtaStatusHelper.getStatus(transactionManager));
    }

    public static boolean isMarkedForRollback(int status) {
        return status == 1;
    }
}

