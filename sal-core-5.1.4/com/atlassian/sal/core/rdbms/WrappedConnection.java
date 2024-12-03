/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.sal.core.rdbms;

import com.google.common.annotations.VisibleForTesting;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
class WrappedConnection
implements Connection {
    @VisibleForTesting
    Connection connection;

    public WrappedConnection(@Nonnull Connection connection) {
        this.connection = connection;
    }

    void expire() {
        this.connection = null;
    }

    private Connection connection() {
        if (this.connection == null) {
            throw new IllegalStateException("Connection accessed outside the scope of a callback passed to com.atlassian.sal.api.rdbms.TransactionalExecutor");
        }
        return this.connection;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setAutoCommit(boolean) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void commit() throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#commit() called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void close() {
        this.connection();
        this.expire();
    }

    @Override
    public void rollback() throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#rollback() called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setReadOnly(boolean) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#abort(java.util.concurrent.Executor)} called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setCatalog(String) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setSchema(String) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setTransactionIsolation(int) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setNetworkTimeout(java.util.concurrent.Executor, int)} called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setSavepoint() called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setSavepoint(String) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#setSavepoint(java.sql.Savepoint) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.connection();
        throw new UnsupportedOperationException("java.sql.Connection#releaseSavepoint(java.sql.Savepoint) called from a connection provided by com.atlassian.sal.api.rdbms.TransactionalExecutor");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.connection().isWrapperFor(iface);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return this.connection().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection().prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.connection().prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.connection().nativeSQL(sql);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.connection().getAutoCommit();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.connection().isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection().getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.connection().isReadOnly();
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.connection().getCatalog();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.connection().getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.connection().getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.connection().clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection().createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.connection().getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.connection().setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.connection().setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.connection().getHoldability();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.connection().prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.connection().prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.connection().prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.connection().createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.connection().createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.connection().createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.connection().createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.connection().isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.connection().setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.connection().setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.connection().getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.connection().getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.connection().createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.connection().createStruct(typeName, attributes);
    }

    @Override
    public String getSchema() throws SQLException {
        return this.connection().getSchema();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.connection().getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.connection().unwrap(iface);
    }
}

