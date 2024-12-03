/*
 * Decompiled with CFR 0.152.
 */
package javax.transaction;

import javax.transaction.Synchronization;

public interface TransactionSynchronizationRegistry {
    public Object getTransactionKey();

    public void putResource(Object var1, Object var2);

    public Object getResource(Object var1);

    public void registerInterposedSynchronization(Synchronization var1);

    public int getTransactionStatus();

    public void setRollbackOnly();

    public boolean getRollbackOnly();
}

