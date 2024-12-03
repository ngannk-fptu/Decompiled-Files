/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ConnectionHandler
 *  com.atlassian.activeobjects.spi.ConnectionHandler$Closeable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dbexporter.jdbc;

import com.atlassian.activeobjects.spi.ConnectionHandler;
import com.atlassian.dbexporter.ConnectionProvider;
import com.atlassian.dbexporter.ImportExportErrorService;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JdbcUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);

    public static <T> T withConnection(ImportExportErrorService errorService, ConnectionProvider provider, JdbcCallable<T> callable) {
        Connection connection = null;
        try {
            connection = provider.getConnection();
            T t = callable.call(ConnectionHandler.newInstance((Connection)connection, (ConnectionHandler.Closeable)new ConnectionHandler.Closeable(){

                public void close() throws SQLException {
                }
            }));
            return t;
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(null, "", e);
        }
        finally {
            JdbcUtils.closeQuietly(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T withNoAutoCommit(ImportExportErrorService errorService, Connection connection, JdbcCallable<T> callable) {
        try {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T t = callable.call(connection);
                return t;
            }
            finally {
                connection.setAutoCommit(autoCommit);
            }
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(null, "", e);
        }
    }

    public static void closeQuietly(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            }
            catch (SQLException se) {
                LOGGER.warn("ResultSet close threw exception", (Throwable)se);
            }
        }
    }

    public static void closeQuietly(Statement ... statements) {
        for (Statement statement : statements) {
            JdbcUtils.closeQuietly(statement);
        }
    }

    private static void closeQuietly(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException se) {
                LOGGER.warn("Statement close threw exception", (Throwable)se);
            }
        }
    }

    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException se) {
                LOGGER.warn("Connection close threw exception", (Throwable)se);
            }
        }
    }

    public static void closeQuietly(ResultSet resultSet, Statement statement) {
        JdbcUtils.closeQuietly(resultSet);
        JdbcUtils.closeQuietly(statement);
    }

    public static String quote(ImportExportErrorService errorService, String table, Connection connection, String identifier) {
        String quoteString = JdbcUtils.identifierQuoteString(errorService, table, connection).trim();
        return new StringBuilder(identifier.length() + 2 * quoteString.length()).append(quoteString).append(identifier).append(quoteString).toString();
    }

    private static String identifierQuoteString(ImportExportErrorService errorService, String table, Connection connection) {
        try {
            return JdbcUtils.metadata(errorService, connection).getIdentifierQuoteString();
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(table, "", e);
        }
    }

    public static DatabaseMetaData metadata(ImportExportErrorService errorService, Connection connection) {
        try {
            return connection.getMetaData();
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(null, "", e);
        }
    }

    public static Statement createStatement(ImportExportErrorService errorService, String table, Connection connection) {
        try {
            return connection.createStatement();
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(table, "Could not create statement from connection", e);
        }
    }

    public static PreparedStatement preparedStatement(ImportExportErrorService errorService, String table, Connection connection, String sql) {
        try {
            return connection.prepareStatement(sql);
        }
        catch (SQLException e) {
            throw errorService.newImportExportSqlException(table, "Could not create prepared statement for SQL query, [" + sql + "]", e);
        }
    }

    public static interface JdbcCallable<T> {
        public T call(Connection var1);
    }
}

