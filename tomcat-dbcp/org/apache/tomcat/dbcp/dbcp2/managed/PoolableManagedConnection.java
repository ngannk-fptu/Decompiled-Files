/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnection;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;
import org.apache.tomcat.dbcp.pool2.ObjectPool;

public class PoolableManagedConnection
extends PoolableConnection {
    private final TransactionRegistry transactionRegistry;

    public PoolableManagedConnection(TransactionRegistry transactionRegistry, Connection conn, ObjectPool<PoolableConnection> pool) {
        this(transactionRegistry, conn, pool, null, true);
    }

    public PoolableManagedConnection(TransactionRegistry transactionRegistry, Connection conn, ObjectPool<PoolableConnection> pool, Collection<String> disconnectSqlCodes, boolean fastFailValidation) {
        super(conn, pool, null, disconnectSqlCodes, fastFailValidation);
        this.transactionRegistry = transactionRegistry;
    }

    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }

    @Override
    public void reallyClose() throws SQLException {
        try {
            super.reallyClose();
        }
        finally {
            this.transactionRegistry.unregisterConnection(this);
        }
    }
}

