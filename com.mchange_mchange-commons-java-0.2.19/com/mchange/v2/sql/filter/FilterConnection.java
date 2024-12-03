/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

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

public abstract class FilterConnection
implements Connection {
    protected Connection inner;

    private void __setInner(Connection connection) {
        this.inner = connection;
    }

    public FilterConnection(Connection connection) {
        this.__setInner(connection);
    }

    public FilterConnection() {
    }

    public void setInner(Connection connection) {
        this.__setInner(connection);
    }

    public Connection getInner() {
        return this.inner;
    }

    @Override
    public void commit() throws SQLException {
        this.inner.commit();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
    }

    @Override
    public Array createArrayOf(String string, Object[] objectArray) throws SQLException {
        return this.inner.createArrayOf(string, objectArray);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return this.inner.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        return this.inner.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return this.inner.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.inner.createSQLXML();
    }

    @Override
    public Statement createStatement(int n, int n2, int n3) throws SQLException {
        return this.inner.createStatement(n, n2, n3);
    }

    @Override
    public Statement createStatement(int n, int n2) throws SQLException {
        return this.inner.createStatement(n, n2);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return this.inner.createStatement();
    }

    @Override
    public Struct createStruct(String string, Object[] objectArray) throws SQLException {
        return this.inner.createStruct(string, objectArray);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return this.inner.getAutoCommit();
    }

    @Override
    public String getCatalog() throws SQLException {
        return this.inner.getCatalog();
    }

    @Override
    public String getClientInfo(String string) throws SQLException {
        return this.inner.getClientInfo(string);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return this.inner.getClientInfo();
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.inner.getHoldability();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.inner.getMetaData();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.inner.getNetworkTimeout();
    }

    @Override
    public String getSchema() throws SQLException {
        return this.inner.getSchema();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.inner.getTransactionIsolation();
    }

    public Map getTypeMap() throws SQLException {
        return this.inner.getTypeMap();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.inner.getWarnings();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.inner.isClosed();
    }

    @Override
    public String nativeSQL(String string) throws SQLException {
        return this.inner.nativeSQL(string);
    }

    @Override
    public CallableStatement prepareCall(String string, int n, int n2, int n3) throws SQLException {
        return this.inner.prepareCall(string, n, n2, n3);
    }

    @Override
    public CallableStatement prepareCall(String string, int n, int n2) throws SQLException {
        return this.inner.prepareCall(string, n, n2);
    }

    @Override
    public CallableStatement prepareCall(String string) throws SQLException {
        return this.inner.prepareCall(string);
    }

    @Override
    public PreparedStatement prepareStatement(String string, int n, int n2, int n3) throws SQLException {
        return this.inner.prepareStatement(string, n, n2, n3);
    }

    @Override
    public PreparedStatement prepareStatement(String string, int n) throws SQLException {
        return this.inner.prepareStatement(string, n);
    }

    @Override
    public PreparedStatement prepareStatement(String string, int[] nArray) throws SQLException {
        return this.inner.prepareStatement(string, nArray);
    }

    @Override
    public PreparedStatement prepareStatement(String string, String[] stringArray) throws SQLException {
        return this.inner.prepareStatement(string, stringArray);
    }

    @Override
    public PreparedStatement prepareStatement(String string) throws SQLException {
        return this.inner.prepareStatement(string);
    }

    @Override
    public PreparedStatement prepareStatement(String string, int n, int n2) throws SQLException {
        return this.inner.prepareStatement(string, n, n2);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.inner.releaseSavepoint(savepoint);
    }

    @Override
    public void rollback() throws SQLException {
        this.inner.rollback();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        this.inner.rollback(savepoint);
    }

    @Override
    public void setAutoCommit(boolean bl) throws SQLException {
        this.inner.setAutoCommit(bl);
    }

    @Override
    public void setCatalog(String string) throws SQLException {
        this.inner.setCatalog(string);
    }

    @Override
    public void setClientInfo(String string, String string2) throws SQLClientInfoException {
        this.inner.setClientInfo(string, string2);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.inner.setClientInfo(properties);
    }

    @Override
    public void setHoldability(int n) throws SQLException {
        this.inner.setHoldability(n);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int n) throws SQLException {
        this.inner.setNetworkTimeout(executor, n);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return this.inner.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String string) throws SQLException {
        return this.inner.setSavepoint(string);
    }

    @Override
    public void setSchema(String string) throws SQLException {
        this.inner.setSchema(string);
    }

    @Override
    public void setTransactionIsolation(int n) throws SQLException {
        this.inner.setTransactionIsolation(n);
    }

    public void setTypeMap(Map map) throws SQLException {
        this.inner.setTypeMap(map);
    }

    @Override
    public void setReadOnly(boolean bl) throws SQLException {
        this.inner.setReadOnly(bl);
    }

    @Override
    public void close() throws SQLException {
        this.inner.close();
    }

    @Override
    public boolean isValid(int n) throws SQLException {
        return this.inner.isValid(n);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.inner.isReadOnly();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        this.inner.abort(executor);
    }

    public boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

