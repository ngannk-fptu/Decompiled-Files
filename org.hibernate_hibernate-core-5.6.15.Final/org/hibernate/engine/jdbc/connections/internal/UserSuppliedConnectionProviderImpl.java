/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;

public class UserSuppliedConnectionProviderImpl
implements ConnectionProvider {
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return ConnectionProvider.class.equals((Object)unwrapType) || UserSuppliedConnectionProviderImpl.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (ConnectionProvider.class.equals(unwrapType) || UserSuppliedConnectionProviderImpl.class.isAssignableFrom(unwrapType)) {
            return (T)this;
        }
        throw new UnknownUnwrapTypeException(unwrapType);
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException("The application must supply JDBC connections");
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        throw new UnsupportedOperationException("The application must supply JDBC connections");
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }
}

