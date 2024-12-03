/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.pocketknife.internal.querydsl;

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

class SpecificBehaviourConnection
implements Connection {
    private final Connection salConnection;

    SpecificBehaviourConnection(@Nonnull Connection connection) {
        this.salConnection = connection;
    }

    private Connection underlying(@Nonnull Connection connection) {
        String implementationClassName = connection.getClass().getCanonicalName();
        if ("com.atlassian.sal.core.rdbms.WrappedConnection".equals(implementationClassName)) {
            try {
                return connection.getMetaData().getConnection();
            }
            catch (SQLException e) {
                throw new RuntimeException("Unable to call connection.getMetaData()", e);
            }
        }
        return connection;
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.underlying(this.salConnection).releaseSavepoint(savepoint);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.underlying(this.salConnection).rollback(savepoint);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.underlying(this.salConnection).setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.underlying(this.salConnection).setSavepoint(name);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.salConnection.abort(executor);
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.salConnection.clearWarnings();
    }

    @Override
    public void close() throws SQLException {
        this.salConnection.close();
    }

    @Override
    public void commit() throws SQLException {
        this.salConnection.commit();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.salConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.salConnection.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.salConnection.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.salConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.salConnection.createSQLXML();
    }

    @Override
    public Statement createStatement() throws SQLException {
        return this.salConnection.createStatement();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.salConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.salConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.salConnection.createStruct(typeName, attributes);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.salConnection.getAutoCommit();
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.salConnection.getCatalog();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.salConnection.getClientInfo();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return this.salConnection.getClientInfo(name);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.salConnection.getHoldability();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.salConnection.getMetaData();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.salConnection.getNetworkTimeout();
    }

    @Override
    public String getSchema() throws SQLException {
        return this.salConnection.getSchema();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.salConnection.getTransactionIsolation();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.salConnection.getTypeMap();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.salConnection.getWarnings();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.salConnection.isClosed();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.salConnection.isReadOnly();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return this.salConnection.isValid(timeout);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return this.salConnection.nativeSQL(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.salConnection.prepareCall(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.salConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.salConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.salConnection.prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.salConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.salConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.salConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.salConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.salConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public void rollback() throws SQLException {
        this.salConnection.rollback();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.salConnection.setAutoCommit(autoCommit);
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.salConnection.setCatalog(catalog);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.salConnection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.salConnection.setClientInfo(properties);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        this.salConnection.setHoldability(holdability);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.salConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.salConnection.setReadOnly(readOnly);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.salConnection.setSchema(schema);
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.salConnection.setTransactionIsolation(level);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.salConnection.setTypeMap(map);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.salConnection.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.salConnection.unwrap(iface);
    }
}

