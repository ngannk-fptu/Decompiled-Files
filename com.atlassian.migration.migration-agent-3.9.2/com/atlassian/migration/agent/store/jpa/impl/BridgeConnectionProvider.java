/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.interfaces.ConnectionHelper;
import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

public class BridgeConnectionProvider
implements ConnectionProvider {
    private final transient ConnectionHelper connectionHelper;

    public BridgeConnectionProvider(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    public Connection getConnection() throws SQLException {
        return this.connectionHelper.getConnection();
    }

    public void closeConnection(Connection conn) throws SQLException {
        this.connectionHelper.closeConnection(conn);
    }

    public boolean supportsAggressiveRelease() {
        return false;
    }

    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}

