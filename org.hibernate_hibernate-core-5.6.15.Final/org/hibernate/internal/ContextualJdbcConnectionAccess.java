/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

public class ContextualJdbcConnectionAccess
implements JdbcConnectionAccess,
Serializable {
    private final String tenantIdentifier;
    private final SessionEventListener listener;
    private final MultiTenantConnectionProvider connectionProvider;

    public ContextualJdbcConnectionAccess(String tenantIdentifier, SessionEventListener listener, MultiTenantConnectionProvider connectionProvider) {
        this.tenantIdentifier = tenantIdentifier;
        this.listener = listener;
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Connection obtainConnection() throws SQLException {
        if (this.tenantIdentifier == null) {
            throw new HibernateException("Tenant identifier required!");
        }
        try {
            this.listener.jdbcConnectionAcquisitionStart();
            Connection connection = this.connectionProvider.getConnection(this.tenantIdentifier);
            return connection;
        }
        finally {
            this.listener.jdbcConnectionAcquisitionEnd();
        }
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        if (this.tenantIdentifier == null) {
            throw new HibernateException("Tenant identifier required!");
        }
        try {
            this.listener.jdbcConnectionReleaseStart();
            this.connectionProvider.releaseConnection(this.tenantIdentifier, connection);
        }
        finally {
            this.listener.jdbcConnectionReleaseEnd();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return this.connectionProvider.supportsAggressiveRelease();
    }
}

