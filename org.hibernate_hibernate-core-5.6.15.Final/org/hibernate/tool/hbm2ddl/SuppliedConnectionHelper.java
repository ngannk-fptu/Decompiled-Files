/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;

@Deprecated
class SuppliedConnectionHelper
implements ConnectionHelper {
    private Connection connection;
    private boolean toggleAutoCommit;
    private final SqlExceptionHelper sqlExceptionHelper;

    public SuppliedConnectionHelper(Connection connection, SqlExceptionHelper sqlExceptionHelper) {
        this.connection = connection;
        this.sqlExceptionHelper = sqlExceptionHelper;
    }

    @Override
    public void prepare(boolean needsAutoCommit) throws SQLException {
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
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void release() throws SQLException {
        this.sqlExceptionHelper.logAndClearWarnings(this.connection);
        if (this.toggleAutoCommit) {
            this.connection.setAutoCommit(false);
        }
        this.connection = null;
    }
}

