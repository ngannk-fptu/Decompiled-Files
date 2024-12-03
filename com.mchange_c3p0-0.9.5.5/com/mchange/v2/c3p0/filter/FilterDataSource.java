/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.filter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public abstract class FilterDataSource
implements DataSource {
    protected DataSource inner;

    public FilterDataSource(DataSource inner) {
        this.inner = inner;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public Connection getConnection(String a, String b) throws SQLException {
        return this.inner.getConnection(a, b);
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
    public void setLogWriter(PrintWriter a) throws SQLException {
        this.inner.setLogWriter(a);
    }

    @Override
    public void setLoginTimeout(int a) throws SQLException {
        this.inner.setLoginTimeout(a);
    }
}

