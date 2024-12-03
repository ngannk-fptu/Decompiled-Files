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

public abstract class SynchronizedFilterConnection
implements Connection {
    protected Connection inner;

    private void __setInner(Connection connection) {
        this.inner = connection;
    }

    public SynchronizedFilterConnection(Connection connection) {
        this.__setInner(connection);
    }

    public SynchronizedFilterConnection() {
    }

    public synchronized void setInner(Connection connection) {
        this.__setInner(connection);
    }

    public synchronized Connection getInner() {
        return this.inner;
    }

    @Override
    public synchronized void commit() throws SQLException {
        this.inner.commit();
    }

    @Override
    public synchronized void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
    }

    @Override
    public synchronized Array createArrayOf(String string, Object[] objectArray) throws SQLException {
        return this.inner.createArrayOf(string, objectArray);
    }

    @Override
    public synchronized Blob createBlob() throws SQLException {
        return this.inner.createBlob();
    }

    @Override
    public synchronized Clob createClob() throws SQLException {
        return this.inner.createClob();
    }

    @Override
    public synchronized NClob createNClob() throws SQLException {
        return this.inner.createNClob();
    }

    @Override
    public synchronized SQLXML createSQLXML() throws SQLException {
        return this.inner.createSQLXML();
    }

    @Override
    public synchronized Statement createStatement(int n, int n2, int n3) throws SQLException {
        return this.inner.createStatement(n, n2, n3);
    }

    @Override
    public synchronized Statement createStatement(int n, int n2) throws SQLException {
        return this.inner.createStatement(n, n2);
    }

    @Override
    public synchronized Statement createStatement() throws SQLException {
        return this.inner.createStatement();
    }

    @Override
    public synchronized Struct createStruct(String string, Object[] objectArray) throws SQLException {
        return this.inner.createStruct(string, objectArray);
    }

    @Override
    public synchronized boolean getAutoCommit() throws SQLException {
        return this.inner.getAutoCommit();
    }

    @Override
    public synchronized String getCatalog() throws SQLException {
        return this.inner.getCatalog();
    }

    @Override
    public synchronized String getClientInfo(String string) throws SQLException {
        return this.inner.getClientInfo(string);
    }

    @Override
    public synchronized Properties getClientInfo() throws SQLException {
        return this.inner.getClientInfo();
    }

    @Override
    public synchronized int getHoldability() throws SQLException {
        return this.inner.getHoldability();
    }

    @Override
    public synchronized DatabaseMetaData getMetaData() throws SQLException {
        return this.inner.getMetaData();
    }

    @Override
    public synchronized int getNetworkTimeout() throws SQLException {
        return this.inner.getNetworkTimeout();
    }

    @Override
    public synchronized String getSchema() throws SQLException {
        return this.inner.getSchema();
    }

    @Override
    public synchronized int getTransactionIsolation() throws SQLException {
        return this.inner.getTransactionIsolation();
    }

    public synchronized Map getTypeMap() throws SQLException {
        return this.inner.getTypeMap();
    }

    @Override
    public synchronized SQLWarning getWarnings() throws SQLException {
        return this.inner.getWarnings();
    }

    @Override
    public synchronized boolean isClosed() throws SQLException {
        return this.inner.isClosed();
    }

    @Override
    public synchronized String nativeSQL(String string) throws SQLException {
        return this.inner.nativeSQL(string);
    }

    @Override
    public synchronized CallableStatement prepareCall(String string, int n, int n2, int n3) throws SQLException {
        return this.inner.prepareCall(string, n, n2, n3);
    }

    @Override
    public synchronized CallableStatement prepareCall(String string, int n, int n2) throws SQLException {
        return this.inner.prepareCall(string, n, n2);
    }

    @Override
    public synchronized CallableStatement prepareCall(String string) throws SQLException {
        return this.inner.prepareCall(string);
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String string, int n, int n2, int n3) throws SQLException {
        return this.inner.prepareStatement(string, n, n2, n3);
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String string, int n) throws SQLException {
        return this.inner.prepareStatement(string, n);
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String string, int[] nArray) throws SQLException {
        return this.inner.prepareStatement(string, nArray);
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String string, String[] stringArray) throws SQLException {
        return this.inner.prepareStatement(string, stringArray);
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String string) throws SQLException {
        return this.inner.prepareStatement(string);
    }

    @Override
    public synchronized PreparedStatement prepareStatement(String string, int n, int n2) throws SQLException {
        return this.inner.prepareStatement(string, n, n2);
    }

    @Override
    public synchronized void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.inner.releaseSavepoint(savepoint);
    }

    @Override
    public synchronized void rollback() throws SQLException {
        this.inner.rollback();
    }

    @Override
    public synchronized void rollback(Savepoint savepoint) throws SQLException {
        this.inner.rollback(savepoint);
    }

    @Override
    public synchronized void setAutoCommit(boolean bl) throws SQLException {
        this.inner.setAutoCommit(bl);
    }

    @Override
    public synchronized void setCatalog(String string) throws SQLException {
        this.inner.setCatalog(string);
    }

    @Override
    public synchronized void setClientInfo(String string, String string2) throws SQLClientInfoException {
        this.inner.setClientInfo(string, string2);
    }

    @Override
    public synchronized void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.inner.setClientInfo(properties);
    }

    @Override
    public synchronized void setHoldability(int n) throws SQLException {
        this.inner.setHoldability(n);
    }

    @Override
    public synchronized void setNetworkTimeout(Executor executor, int n) throws SQLException {
        this.inner.setNetworkTimeout(executor, n);
    }

    @Override
    public synchronized Savepoint setSavepoint() throws SQLException {
        return this.inner.setSavepoint();
    }

    @Override
    public synchronized Savepoint setSavepoint(String string) throws SQLException {
        return this.inner.setSavepoint(string);
    }

    @Override
    public synchronized void setSchema(String string) throws SQLException {
        this.inner.setSchema(string);
    }

    @Override
    public synchronized void setTransactionIsolation(int n) throws SQLException {
        this.inner.setTransactionIsolation(n);
    }

    public synchronized void setTypeMap(Map map) throws SQLException {
        this.inner.setTypeMap(map);
    }

    @Override
    public synchronized void setReadOnly(boolean bl) throws SQLException {
        this.inner.setReadOnly(bl);
    }

    @Override
    public synchronized void close() throws SQLException {
        this.inner.close();
    }

    @Override
    public synchronized boolean isValid(int n) throws SQLException {
        return this.inner.isValid(n);
    }

    @Override
    public synchronized boolean isReadOnly() throws SQLException {
        return this.inner.isReadOnly();
    }

    @Override
    public synchronized void abort(Executor executor) throws SQLException {
        this.inner.abort(executor);
    }

    public synchronized boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public synchronized Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

