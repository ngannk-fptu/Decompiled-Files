/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jdbc.spi;

import org.hibernate.resource.transaction.backend.jdbc.spi.JdbcResourceTransaction;

public interface JdbcResourceTransactionAccess {
    public JdbcResourceTransaction getResourceLocalTransaction();
}

