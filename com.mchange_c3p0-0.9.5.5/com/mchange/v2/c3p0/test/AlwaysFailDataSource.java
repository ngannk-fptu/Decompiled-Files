/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.test;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public final class AlwaysFailDataSource
implements DataSource {
    private static String MESSAGE = "AlwaysFailDataSource always fails.";

    private static SQLException failure() {
        return new SQLException(MESSAGE);
    }

    @Override
    public Connection getConnection() throws SQLException {
        throw AlwaysFailDataSource.failure();
    }

    @Override
    public Connection getConnection(String user, String password) throws SQLException {
        throw AlwaysFailDataSource.failure();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw AlwaysFailDataSource.failure();
    }

    @Override
    public void setLogWriter(PrintWriter pw) throws SQLException {
        throw AlwaysFailDataSource.failure();
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        throw AlwaysFailDataSource.failure();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw AlwaysFailDataSource.failure();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException(MESSAGE);
    }

    @Override
    public <T> T unwrap(Class<T> clz) throws SQLException {
        throw AlwaysFailDataSource.failure();
    }

    @Override
    public boolean isWrapperFor(Class<?> clz) throws SQLException {
        throw AlwaysFailDataSource.failure();
    }
}

