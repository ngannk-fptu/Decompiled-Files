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
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.jboss.logging.Logger;

public class JdbcConnectionAccessProvidedConnectionImpl
implements JdbcConnectionAccess {
    private static final Logger log = Logger.getLogger(JdbcConnectionAccessProvidedConnectionImpl.class);
    private final Connection jdbcConnection;
    private final boolean wasInitiallyAutoCommit;

    public JdbcConnectionAccessProvidedConnectionImpl(Connection jdbcConnection) {
        boolean wasInitiallyAutoCommit;
        block4: {
            this.jdbcConnection = jdbcConnection;
            try {
                wasInitiallyAutoCommit = jdbcConnection.getAutoCommit();
                if (wasInitiallyAutoCommit) break block4;
                try {
                    jdbcConnection.setAutoCommit(true);
                }
                catch (SQLException e) {
                    throw new PersistenceException(String.format("Could not set provided connection [%s] to auto-commit mode (needed for schema generation)", jdbcConnection), (Throwable)e);
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
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }
}

