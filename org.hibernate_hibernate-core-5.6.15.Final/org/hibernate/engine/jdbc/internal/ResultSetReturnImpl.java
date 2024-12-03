/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.ResultSetReturn;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;

public class ResultSetReturnImpl
implements ResultSetReturn {
    private final JdbcCoordinator jdbcCoordinator;
    private final Dialect dialect;
    private final SqlStatementLogger sqlStatementLogger;
    private final SqlExceptionHelper sqlExceptionHelper;

    public ResultSetReturnImpl(JdbcCoordinator jdbcCoordinator, JdbcServices jdbcServices) {
        this.jdbcCoordinator = jdbcCoordinator;
        this.dialect = jdbcServices.getDialect();
        this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
        this.sqlExceptionHelper = jdbcServices.getSqlExceptionHelper();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet extract(PreparedStatement statement) {
        long executeStartNanos = 0L;
        if (this.sqlStatementLogger.getLogSlowQuery() > 0L) {
            executeStartNanos = System.nanoTime();
        }
        try {
            ResultSet rs;
            try {
                this.jdbcExecuteStatementStart();
                rs = statement.executeQuery();
            }
            finally {
                this.jdbcExecuteStatementEnd();
                this.sqlStatementLogger.logSlowQuery(statement, executeStartNanos);
            }
            this.postExtract(rs, statement);
            return rs;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "could not extract ResultSet");
        }
    }

    private void jdbcExecuteStatementEnd() {
        this.jdbcCoordinator.getJdbcSessionOwner().getJdbcSessionContext().getObserver().jdbcExecuteStatementEnd();
    }

    private void jdbcExecuteStatementStart() {
        this.jdbcCoordinator.getJdbcSessionOwner().getJdbcSessionContext().getObserver().jdbcExecuteStatementStart();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet extract(CallableStatement callableStatement) {
        long executeStartNanos = 0L;
        if (this.sqlStatementLogger.getLogSlowQuery() > 0L) {
            executeStartNanos = System.nanoTime();
        }
        try {
            ResultSet rs;
            try {
                this.jdbcExecuteStatementStart();
                rs = this.dialect.getResultSet(callableStatement);
            }
            finally {
                this.jdbcExecuteStatementEnd();
                this.sqlStatementLogger.logSlowQuery(callableStatement, executeStartNanos);
            }
            this.postExtract(rs, callableStatement);
            return rs;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "could not extract ResultSet");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet extract(Statement statement, String sql) {
        this.sqlStatementLogger.logStatement(sql);
        long executeStartNanos = 0L;
        if (this.sqlStatementLogger.getLogSlowQuery() > 0L) {
            executeStartNanos = System.nanoTime();
        }
        try {
            ResultSet rs;
            try {
                this.jdbcExecuteStatementStart();
                rs = statement.executeQuery(sql);
            }
            finally {
                this.jdbcExecuteStatementEnd();
                this.sqlStatementLogger.logSlowQuery(sql, executeStartNanos);
            }
            this.postExtract(rs, statement);
            return rs;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "could not extract ResultSet");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet execute(PreparedStatement statement) {
        long executeStartNanos = 0L;
        if (this.sqlStatementLogger.getLogSlowQuery() > 0L) {
            executeStartNanos = System.nanoTime();
        }
        try {
            ResultSet rs;
            try {
                this.jdbcExecuteStatementStart();
                if (!statement.execute()) {
                    while (!statement.getMoreResults() && statement.getUpdateCount() != -1) {
                    }
                }
                rs = statement.getResultSet();
            }
            finally {
                this.jdbcExecuteStatementEnd();
                this.sqlStatementLogger.logSlowQuery(statement, executeStartNanos);
            }
            this.postExtract(rs, statement);
            return rs;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "could not execute statement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet execute(Statement statement, String sql) {
        this.sqlStatementLogger.logStatement(sql);
        long executeStartNanos = 0L;
        if (this.sqlStatementLogger.getLogSlowQuery() > 0L) {
            executeStartNanos = System.nanoTime();
        }
        try {
            ResultSet rs;
            try {
                this.jdbcExecuteStatementStart();
                if (!statement.execute(sql)) {
                    while (!statement.getMoreResults() && statement.getUpdateCount() != -1) {
                    }
                }
                rs = statement.getResultSet();
            }
            finally {
                this.jdbcExecuteStatementEnd();
                this.sqlStatementLogger.logSlowQuery(statement, executeStartNanos);
            }
            this.postExtract(rs, statement);
            return rs;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "could not execute statement");
        }
    }

    @Override
    public int executeUpdate(PreparedStatement statement) {
        long executeStartNanos = 0L;
        if (this.sqlStatementLogger.getLogSlowQuery() > 0L) {
            executeStartNanos = System.nanoTime();
        }
        try {
            this.jdbcExecuteStatementStart();
            int n = statement.executeUpdate();
            return n;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "could not execute statement");
        }
        finally {
            this.jdbcExecuteStatementEnd();
            this.sqlStatementLogger.logSlowQuery(statement, executeStartNanos);
        }
    }

    @Override
    public int executeUpdate(Statement statement, String sql) {
        this.sqlStatementLogger.logStatement(sql);
        long executeStartNanos = 0L;
        if (this.sqlStatementLogger.getLogSlowQuery() > 0L) {
            executeStartNanos = System.nanoTime();
        }
        try {
            this.jdbcExecuteStatementStart();
            int n = statement.executeUpdate(sql);
            return n;
        }
        catch (SQLException e) {
            throw this.sqlExceptionHelper.convert(e, "could not execute statement");
        }
        finally {
            this.jdbcExecuteStatementEnd();
            this.sqlStatementLogger.logSlowQuery(statement, executeStartNanos);
        }
    }

    private void postExtract(ResultSet rs, Statement st) {
        if (rs != null) {
            this.jdbcCoordinator.getResourceRegistry().register(rs, st);
        }
    }
}

