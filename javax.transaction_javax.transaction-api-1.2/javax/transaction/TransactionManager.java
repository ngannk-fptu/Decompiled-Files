/*
 * Decompiled with CFR 0.152.
 */
package javax.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

public interface TransactionManager {
    public void begin() throws NotSupportedException, SystemException;

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException;

    public int getStatus() throws SystemException;

    public Transaction getTransaction() throws SystemException;

    public void resume(Transaction var1) throws InvalidTransactionException, IllegalStateException, SystemException;

    public void rollback() throws IllegalStateException, SecurityException, SystemException;

    public void setRollbackOnly() throws IllegalStateException, SystemException;

    public void setTransactionTimeout(int var1) throws SystemException;

    public Transaction suspend() throws SystemException;
}

