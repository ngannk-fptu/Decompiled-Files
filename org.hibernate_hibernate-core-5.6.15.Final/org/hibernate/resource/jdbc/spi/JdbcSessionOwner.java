/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc.spi;

import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;

public interface JdbcSessionOwner {
    public JdbcSessionContext getJdbcSessionContext();

    public JdbcConnectionAccess getJdbcConnectionAccess();

    public TransactionCoordinator getTransactionCoordinator();

    public void startTransactionBoundary();

    public void afterTransactionBegin();

    public void beforeTransactionCompletion();

    public void afterTransactionCompletion(boolean var1, boolean var2);

    public void flushBeforeTransactionCompletion();

    public Integer getJdbcBatchSize();
}

