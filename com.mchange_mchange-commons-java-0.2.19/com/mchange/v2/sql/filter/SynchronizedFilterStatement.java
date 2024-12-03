/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public abstract class SynchronizedFilterStatement
implements Statement {
    protected Statement inner;

    private void __setInner(Statement statement) {
        this.inner = statement;
    }

    public SynchronizedFilterStatement(Statement statement) {
        this.__setInner(statement);
    }

    public SynchronizedFilterStatement() {
    }

    public synchronized void setInner(Statement statement) {
        this.__setInner(statement);
    }

    public synchronized Statement getInner() {
        return this.inner;
    }

    @Override
    public synchronized boolean execute(String string, int n) throws SQLException {
        return this.inner.execute(string, n);
    }

    @Override
    public synchronized boolean execute(String string, String[] stringArray) throws SQLException {
        return this.inner.execute(string, stringArray);
    }

    @Override
    public synchronized boolean execute(String string) throws SQLException {
        return this.inner.execute(string);
    }

    @Override
    public synchronized boolean execute(String string, int[] nArray) throws SQLException {
        return this.inner.execute(string, nArray);
    }

    @Override
    public synchronized void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
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
    public synchronized int getFetchDirection() throws SQLException {
        return this.inner.getFetchDirection();
    }

    @Override
    public synchronized int getFetchSize() throws SQLException {
        return this.inner.getFetchSize();
    }

    @Override
    public synchronized void setFetchDirection(int n) throws SQLException {
        this.inner.setFetchDirection(n);
    }

    @Override
    public synchronized void setFetchSize(int n) throws SQLException {
        this.inner.setFetchSize(n);
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public synchronized int getResultSetHoldability() throws SQLException {
        return this.inner.getResultSetHoldability();
    }

    @Override
    public synchronized void addBatch(String string) throws SQLException {
        this.inner.addBatch(string);
    }

    @Override
    public synchronized void cancel() throws SQLException {
        this.inner.cancel();
    }

    @Override
    public synchronized void clearBatch() throws SQLException {
        this.inner.clearBatch();
    }

    @Override
    public synchronized void closeOnCompletion() throws SQLException {
        this.inner.closeOnCompletion();
    }

    @Override
    public synchronized int[] executeBatch() throws SQLException {
        return this.inner.executeBatch();
    }

    @Override
    public synchronized ResultSet executeQuery(String string) throws SQLException {
        return this.inner.executeQuery(string);
    }

    @Override
    public synchronized int executeUpdate(String string, int[] nArray) throws SQLException {
        return this.inner.executeUpdate(string, nArray);
    }

    @Override
    public synchronized int executeUpdate(String string, String[] stringArray) throws SQLException {
        return this.inner.executeUpdate(string, stringArray);
    }

    @Override
    public synchronized int executeUpdate(String string) throws SQLException {
        return this.inner.executeUpdate(string);
    }

    @Override
    public synchronized int executeUpdate(String string, int n) throws SQLException {
        return this.inner.executeUpdate(string, n);
    }

    @Override
    public synchronized ResultSet getGeneratedKeys() throws SQLException {
        return this.inner.getGeneratedKeys();
    }

    @Override
    public synchronized int getMaxFieldSize() throws SQLException {
        return this.inner.getMaxFieldSize();
    }

    @Override
    public synchronized int getMaxRows() throws SQLException {
        return this.inner.getMaxRows();
    }

    @Override
    public synchronized boolean getMoreResults() throws SQLException {
        return this.inner.getMoreResults();
    }

    @Override
    public synchronized boolean getMoreResults(int n) throws SQLException {
        return this.inner.getMoreResults(n);
    }

    @Override
    public synchronized int getQueryTimeout() throws SQLException {
        return this.inner.getQueryTimeout();
    }

    @Override
    public synchronized ResultSet getResultSet() throws SQLException {
        return this.inner.getResultSet();
    }

    @Override
    public synchronized int getResultSetConcurrency() throws SQLException {
        return this.inner.getResultSetConcurrency();
    }

    @Override
    public synchronized int getResultSetType() throws SQLException {
        return this.inner.getResultSetType();
    }

    @Override
    public synchronized int getUpdateCount() throws SQLException {
        return this.inner.getUpdateCount();
    }

    @Override
    public synchronized boolean isCloseOnCompletion() throws SQLException {
        return this.inner.isCloseOnCompletion();
    }

    @Override
    public synchronized boolean isPoolable() throws SQLException {
        return this.inner.isPoolable();
    }

    @Override
    public synchronized void setCursorName(String string) throws SQLException {
        this.inner.setCursorName(string);
    }

    @Override
    public synchronized void setEscapeProcessing(boolean bl) throws SQLException {
        this.inner.setEscapeProcessing(bl);
    }

    @Override
    public synchronized void setMaxFieldSize(int n) throws SQLException {
        this.inner.setMaxFieldSize(n);
    }

    @Override
    public synchronized void setMaxRows(int n) throws SQLException {
        this.inner.setMaxRows(n);
    }

    @Override
    public synchronized void setPoolable(boolean bl) throws SQLException {
        this.inner.setPoolable(bl);
    }

    @Override
    public synchronized void setQueryTimeout(int n) throws SQLException {
        this.inner.setQueryTimeout(n);
    }

    @Override
    public synchronized void close() throws SQLException {
        this.inner.close();
    }

    public synchronized boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public synchronized Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

