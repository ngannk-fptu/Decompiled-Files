/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.spi;

import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;

public interface TransactionCoordinatorOwner {
    public boolean isActive();

    default public void startTransactionBoundary() {
        this.getJdbcSessionOwner().startTransactionBoundary();
    }

    public void afterTransactionBegin();

    public void beforeTransactionCompletion();

    public void afterTransactionCompletion(boolean var1, boolean var2);

    public JdbcSessionOwner getJdbcSessionOwner();

    public void setTransactionTimeOut(int var1);

    public void flushBeforeTransactionCompletion();
}

