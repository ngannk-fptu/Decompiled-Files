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

public abstract class FilterDataSource
implements DataSource {
    protected DataSource inner;

    private void __setInner(DataSource dataSource) {
        this.inner = dataSource;
    }

    public FilterDataSource(DataSource dataSource) {
        this.__setInner(dataSource);
    }

    public FilterDataSource() {
    }

    public void setInner(DataSource dataSource) {
        this.__setInner(dataSource);
    }

    public DataSource getInner() {
        return this.inner;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public Connection getConnection(String string, String string2) throws SQLException {
        return this.inner.getConnection(string, string2);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.inner.getLogWriter();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.inner.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.inner.getParentLogger();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        this.inner.setLogWriter(printWriter);
    }

    @Override
    public void setLoginTimeout(int n) throws SQLException {
        this.inner.setLoginTimeout(n);
    }

    public boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

