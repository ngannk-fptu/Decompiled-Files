/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jdbc.spi;

import org.hibernate.resource.transaction.spi.TransactionStatus;

public interface JdbcResourceTransaction {
    public void begin();

    public void commit();

    public void rollback();

    public TransactionStatus getStatus();
}

