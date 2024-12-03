/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.spi;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;

public abstract class AbstractDataSourceBasedMultiTenantConnectionProviderImpl
implements MultiTenantConnectionProvider {
    protected abstract DataSource selectAnyDataSource();

    protected abstract DataSource selectDataSource(String var1);

    @Override
    public Connection getAnyConnection() throws SQLException {
        return this.selectAnyDataSource().getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        return this.selectDataSource(tenantIdentifier).getConnection();
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return DataSource.class.isAssignableFrom(unwrapType) || MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return (T)this;
        }
        if (DataSource.class.isAssignableFrom(unwrapType)) {
            return (T)this.selectAnyDataSource();
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }
}

