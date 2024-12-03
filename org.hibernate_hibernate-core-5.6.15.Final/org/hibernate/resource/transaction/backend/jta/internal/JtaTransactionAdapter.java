/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import org.hibernate.resource.transaction.spi.TransactionStatus;

public interface JtaTransactionAdapter {
    public void begin();

    public void commit();

    public void rollback();

    public TransactionStatus getStatus();

    public void markRollbackOnly();

    public void setTimeOut(int var1);
}

