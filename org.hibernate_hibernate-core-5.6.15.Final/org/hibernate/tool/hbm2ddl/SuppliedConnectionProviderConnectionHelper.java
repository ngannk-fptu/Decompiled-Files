/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;

@Deprecated
class SuppliedConnectionProviderConnectionHelper
implements ConnectionHelper {
    private ConnectionProvider provider;
    private Connection connection;
    private boolean toggleAutoCommit;
    private final SqlExceptionHelper sqlExceptionHelper;

    public SuppliedConnectionProviderConnectionHelper(ConnectionProvider provider, SqlExceptionHelper sqlExceptionHelper) {
        this.provider = provider;
        this.sqlExceptionHelper = sqlExceptionHelper;
    }

    @Override
    public void prepare(boolean needsAutoCommit) throws SQLException {
        this.connection = this.provider.getConnection();
        boolean bl = this.toggleAutoCommit = needsAutoCommit && !this.connection.getAutoCommit();
        if (this.toggleAutoCommit) {
            try {
                this.connection.commit();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.connection.setAutoCommit(true);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public void release() throws SQLException {
        if (this.connection != null) {
            this.sqlExceptionHelper.logAndClearWarnings(this.connection);
            if (this.toggleAutoCommit) {
                this.connection.setAutoCommit(false);
            }
            this.provider.closeConnection(this.connection);
            this.connection = null;
        }
    }
}

