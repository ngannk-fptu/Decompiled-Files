/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jdbc.internal;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.internal.log.ConnectionAccessLogger;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.tool.schema.internal.exec.JdbcContext;

public class DdlTransactionIsolatorNonJtaImpl
implements DdlTransactionIsolator {
    private final JdbcContext jdbcContext;
    private Connection jdbcConnection;
    private boolean unsetAutoCommit;

    public DdlTransactionIsolatorNonJtaImpl(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    @Override
    public void prepare() {
    }

    @Override
    public JdbcContext getJdbcContext() {
        return this.jdbcContext;
    }

    @Override
    public Connection getIsolatedConnection() {
        block7: {
            if (this.jdbcConnection == null) {
                try {
                    this.jdbcConnection = this.jdbcContext.getJdbcConnectionAccess().obtainConnection();
                    try {
                        if (this.jdbcConnection.getAutoCommit()) break block7;
                        ConnectionAccessLogger.INSTANCE.informConnectionLocalTransactionForNonJtaDdl(this.jdbcContext.getJdbcConnectionAccess());
                        try {
                            this.jdbcConnection.commit();
                            this.jdbcConnection.setAutoCommit(true);
                            this.unsetAutoCommit = true;
                        }
                        catch (SQLException e) {
                            throw this.jdbcContext.getSqlExceptionHelper().convert(e, "Unable to set JDBC Connection into auto-commit mode in preparation for DDL execution");
                        }
                    }
                    catch (SQLException e) {
                        throw this.jdbcContext.getSqlExceptionHelper().convert(e, "Unable to check JDBC Connection auto-commit in preparation for DDL execution");
                    }
                }
                catch (SQLException e) {
                    throw this.jdbcContext.getSqlExceptionHelper().convert(e, "Unable to open JDBC Connection for DDL execution");
                }
            }
        }
        return this.jdbcConnection;
    }

    @Override
    public void release() {
        block10: {
            if (this.jdbcConnection != null) {
                try {
                    if (!this.unsetAutoCommit) break block10;
                    try {
                        this.jdbcConnection.setAutoCommit(false);
                    }
                    catch (SQLException e) {
                        throw this.jdbcContext.getSqlExceptionHelper().convert(e, "Unable to set auto commit to false for JDBC Connection used for DDL execution");
                    }
                }
                finally {
                    try {
                        this.jdbcContext.getJdbcConnectionAccess().releaseConnection(this.jdbcConnection);
                    }
                    catch (SQLException e) {
                        throw this.jdbcContext.getSqlExceptionHelper().convert(e, "Unable to release JDBC Connection used for DDL execution");
                    }
                }
            }
        }
    }
}

