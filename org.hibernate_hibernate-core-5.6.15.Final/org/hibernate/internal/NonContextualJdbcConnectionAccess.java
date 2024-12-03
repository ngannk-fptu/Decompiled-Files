/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;

public class NonContextualJdbcConnectionAccess
implements JdbcConnectionAccess,
Serializable {
    private final SessionEventListener listener;
    private final ConnectionProvider connectionProvider;

    public NonContextualJdbcConnectionAccess(SessionEventListener listener, ConnectionProvider connectionProvider) {
        Objects.requireNonNull(listener);
        Objects.requireNonNull(connectionProvider);
        this.listener = listener;
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Connection obtainConnection() throws SQLException {
        try {
            this.listener.jdbcConnectionAcquisitionStart();
            Connection connection = this.connectionProvider.getConnection();
            return connection;
        }
        finally {
            this.listener.jdbcConnectionAcquisitionEnd();
        }
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        try {
            this.listener.jdbcConnectionReleaseStart();
            this.connectionProvider.closeConnection(connection);
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

