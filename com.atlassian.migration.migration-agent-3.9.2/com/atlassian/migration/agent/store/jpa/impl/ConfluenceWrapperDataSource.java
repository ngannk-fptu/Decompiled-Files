/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.springframework.jdbc.datasource.AbstractDataSource
 */
package com.atlassian.migration.agent.store.jpa.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.jdbc.datasource.AbstractDataSource;

public class ConfluenceWrapperDataSource
extends AbstractDataSource {
    private final ConnectionProvider connectionProvider;

    public ConfluenceWrapperDataSource(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Connection getConnection() throws SQLException {
        return this.connectionProvider.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("getConnection(String username, String password)");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance((Object)this)) {
            return (T)((Object)this);
        }
        throw new SQLException("DataSource of type [" + ((Object)((Object)this)).getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance((Object)this);
    }

    public Logger getParentLogger() {
        return Logger.getLogger("global");
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connectionProvider;
    }
}

