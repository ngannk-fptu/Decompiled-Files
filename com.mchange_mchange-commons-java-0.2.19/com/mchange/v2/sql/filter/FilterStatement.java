/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public abstract class FilterStatement
implements Statement {
    protected Statement inner;

    private void __setInner(Statement statement) {
        this.inner = statement;
    }

    public FilterStatement(Statement statement) {
        this.__setInner(statement);
    }

    public FilterStatement() {
    }

    public void setInner(Statement statement) {
        this.__setInner(statement);
    }

    public Statement getInner() {
        return this.inner;
    }

    @Override
    public boolean execute(String string, int n) throws SQLException {
        return this.inner.execute(string, n);
    }

    @Override
    public boolean execute(String string, String[] stringArray) throws SQLException {
        return this.inner.execute(string, stringArray);
    }

    @Override
    public boolean execute(String string) throws SQLException {
        return this.inner.execute(string);
    }

    @Override
    public boolean execute(String string, int[] nArray) throws SQLException {
        return this.inner.execute(string, nArray);
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
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
    public int getFetchDirection() throws SQLException {
        return this.inner.getFetchDirection();
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.inner.getFetchSize();
    }

    @Override
    public void setFetchDirection(int n) throws SQLException {
        this.inner.setFetchDirection(n);
    }

    @Override
    public void setFetchSize(int n) throws SQLException {
        this.inner.setFetchSize(n);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.inner.getResultSetHoldability();
    }

    @Override
    public void addBatch(String string) throws SQLException {
        this.inner.addBatch(string);
    }

    @Override
    public void cancel() throws SQLException {
        this.inner.cancel();
    }

    @Override
    public void clearBatch() throws SQLException {
        this.inner.clearBatch();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        this.inner.closeOnCompletion();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return this.inner.executeBatch();
    }

    @Override
    public ResultSet executeQuery(String string) throws SQLException {
        return this.inner.executeQuery(string);
    }

    @Override
    public int executeUpdate(String string, int[] nArray) throws SQLException {
        return this.inner.executeUpdate(string, nArray);
    }

    @Override
    public int executeUpdate(String string, String[] stringArray) throws SQLException {
        return this.inner.executeUpdate(string, stringArray);
    }

    @Override
    public int executeUpdate(String string) throws SQLException {
        return this.inner.executeUpdate(string);
    }

    @Override
    public int executeUpdate(String string, int n) throws SQLException {
        return this.inner.executeUpdate(string, n);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.inner.getGeneratedKeys();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.inner.getMaxFieldSize();
    }

    @Override
    public int getMaxRows() throws SQLException {
        return this.inner.getMaxRows();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return this.inner.getMoreResults();
    }

    @Override
    public boolean getMoreResults(int n) throws SQLException {
        return this.inner.getMoreResults(n);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return this.inner.getQueryTimeout();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.inner.getResultSet();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.inner.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return this.inner.getResultSetType();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return this.inner.getUpdateCount();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this.inner.isCloseOnCompletion();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return this.inner.isPoolable();
    }

    @Override
    public void setCursorName(String string) throws SQLException {
        this.inner.setCursorName(string);
    }

    @Override
    public void setEscapeProcessing(boolean bl) throws SQLException {
        this.inner.setEscapeProcessing(bl);
    }

    @Override
    public void setMaxFieldSize(int n) throws SQLException {
        this.inner.setMaxFieldSize(n);
    }

    @Override
    public void setMaxRows(int n) throws SQLException {
        this.inner.setMaxRows(n);
    }

    @Override
    public void setPoolable(boolean bl) throws SQLException {
        this.inner.setPoolable(bl);
    }

    @Override
    public void setQueryTimeout(int n) throws SQLException {
        this.inner.setQueryTimeout(n);
    }

    @Override
    public void close() throws SQLException {
        this.inner.close();
    }

    public boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

