/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityTransaction
 *  javax.transaction.Synchronization
 */
package org.hibernate;

import javax.persistence.EntityTransaction;
import javax.transaction.Synchronization;
import org.hibernate.HibernateException;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public interface Transaction
extends EntityTransaction {
    public TransactionStatus getStatus();

    public void registerSynchronization(Synchronization var1) throws HibernateException;

    public void setTimeout(int var1);

    public int getTimeout();

    default public void markRollbackOnly() {
        this.setRollbackOnly();
    }
}

