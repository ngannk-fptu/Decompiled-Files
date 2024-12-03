/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public abstract class SynchronizedFilterDataSource
implements DataSource {
    protected DataSource inner;

    private void __setInner(DataSource dataSource) {
        this.inner = dataSource;
    }

    public SynchronizedFilterDataSource(DataSource dataSource) {
        this.__setInner(dataSource);
    }

    public SynchronizedFilterDataSource() {
    }

    public synchronized void setInner(DataSource dataSource) {
        this.__setInner(dataSource);
    }

    public synchronized DataSource getInner() {
        return this.inner;
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public synchronized Connection getConnection(String string, String string2) throws SQLException {
        return this.inner.getConnection(string, string2);
    }

    @Override
    public synchronized PrintWriter getLogWriter() throws SQLException {
        return this.inner.getLogWriter();
    }

    @Override
    public synchronized int getLoginTimeout() throws SQLException {
        return this.inner.getLoginTimeout();
    }

    @Override
    public synchronized Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.inner.getParentLogger();
    }

    @Override
    public synchronized void setLogWriter(PrintWriter printWriter) throws SQLException {
        this.inner.setLogWriter(printWriter);
    }

    @Override
    public synchronized void setLoginTimeout(int n) throws SQLException {
        this.inner.setLoginTimeout(n);
    }

    public synchronized boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public synchronized Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

