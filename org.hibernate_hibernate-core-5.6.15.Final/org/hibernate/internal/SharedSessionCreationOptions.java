/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.transaction.spi.TransactionImplementor;
import org.hibernate.internal.SessionCreationOptions;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;

public interface SharedSessionCreationOptions
extends SessionCreationOptions {
    public boolean isTransactionCoordinatorShared();

    public TransactionCoordinator getTransactionCoordinator();

    public JdbcCoordinator getJdbcCoordinator();

    public TransactionImplementor getTransaction();

    public ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses();
}

