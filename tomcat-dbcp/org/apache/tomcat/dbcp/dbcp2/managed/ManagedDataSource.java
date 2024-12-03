/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.tomcat.dbcp.dbcp2.PoolingDataSource;
import org.apache.tomcat.dbcp.dbcp2.managed.ManagedConnection;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;
import org.apache.tomcat.dbcp.pool2.ObjectPool;

public class ManagedDataSource<C extends Connection>
extends PoolingDataSource<C> {
    private TransactionRegistry transactionRegistry;

    public ManagedDataSource(ObjectPool<C> pool, TransactionRegistry transactionRegistry) {
        super(pool);
        this.transactionRegistry = transactionRegistry;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.getPool() == null) {
            throw new IllegalStateException("Pool has not been set");
        }
        if (this.transactionRegistry == null) {
            throw new IllegalStateException("TransactionRegistry has not been set");
        }
        return new ManagedConnection(this.getPool(), this.transactionRegistry, this.isAccessToUnderlyingConnectionAllowed());
    }

    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }

    public void setTransactionRegistry(TransactionRegistry transactionRegistry) {
        if (this.transactionRegistry != null) {
            throw new IllegalStateException("TransactionRegistry already set");
        }
        Objects.requireNonNull(transactionRegistry, "transactionRegistry");
        this.transactionRegistry = transactionRegistry;
    }
}

