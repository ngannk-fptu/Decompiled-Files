/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema.internal.exec;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.PersistenceException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.jboss.logging.Logger;

public class JdbcConnectionAccessConnectionProviderImpl
implements JdbcConnectionAccess {
    private static final Logger log = Logger.getLogger(JdbcConnectionAccessConnectionProviderImpl.class);
    private final ConnectionProvider connectionProvider;
    private final Connection jdbcConnection;
    private final boolean wasInitiallyAutoCommit;

    public JdbcConnectionAccessConnectionProviderImpl(ConnectionProvider connectionProvider) {
        boolean wasInitiallyAutoCommit;
        block6: {
            this.connectionProvider = connectionProvider;
            try {
                this.jdbcConnection = connectionProvider.getConnection();
            }
            catch (SQLException e) {
                throw new PersistenceException("Unable to obtain JDBC Connection", (Throwable)e);
            }
            try {
                wasInitiallyAutoCommit = this.jdbcConnection.getAutoCommit();
                if (wasInitiallyAutoCommit) break block6;
                try {
                    this.jdbcConnection.setAutoCommit(true);
                }
                catch (SQLException e) {
                    throw new PersistenceException(String.format("Could not set provided connection [%s] to auto-commit mode (needed for schema generation)", this.jdbcConnection), (Throwable)e);
                }
            }
            catch (SQLException ignore) {
                wasInitiallyAutoCommit = false;
            }
        }
        log.debugf("wasInitiallyAutoCommit=%s", (Object)wasInitiallyAutoCommit);
        this.wasInitiallyAutoCommit = wasInitiallyAutoCommit;
    }

    @Override
    public Connection obtainConnection() throws SQLException {
        return this.jdbcConnection;
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        if (connection != this.jdbcConnection) {
            throw new PersistenceException(String.format("Connection [%s] passed back to %s was not the one obtained [%s] from it", connection, JdbcConnectionAccessConnectionProviderImpl.class.getName(), this.jdbcConnection));
        }
        if (!this.wasInitiallyAutoCommit) {
            try {
                if (this.jdbcConnection.getAutoCommit()) {
                    this.jdbcConnection.setAutoCommit(false);
                }
            }
            catch (SQLException e) {
                log.info((Object)"Was unable to reset JDBC connection to no longer be in auto-commit mode");
            }
        }
        this.connectionProvider.closeConnection(this.jdbcConnection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }
}

