/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.spi;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;

public abstract class AbstractMultiTenantConnectionProvider
implements MultiTenantConnectionProvider {
    protected abstract ConnectionProvider getAnyConnectionProvider();

    protected abstract ConnectionProvider selectConnectionProvider(String var1);

    @Override
    public Connection getAnyConnection() throws SQLException {
        return this.getAnyConnectionProvider().getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        this.getAnyConnectionProvider().closeConnection(connection);
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        return this.selectConnectionProvider(tenantIdentifier).getConnection();
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        this.selectConnectionProvider(tenantIdentifier).closeConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return this.getAnyConnectionProvider().supportsAggressiveRelease();
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.isAssignableFrom(unwrapType) || MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return (T)this;
        }
        if (ConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return (T)this.getAnyConnectionProvider();
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }
}

