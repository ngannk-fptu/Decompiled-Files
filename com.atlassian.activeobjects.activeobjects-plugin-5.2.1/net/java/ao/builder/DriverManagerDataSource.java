/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;
import javax.sql.DataSource;

public final class DriverManagerDataSource
implements DataSource {
    private final String url;
    private final String username;
    private final String password;
    private PrintWriter out;
    private int loginTimeOut;

    public DriverManagerDataSource(String url, String username, String password) {
        this.url = Objects.requireNonNull(url, "url can't be null");
        this.username = Objects.requireNonNull(username, "username can't be null");
        this.password = Objects.requireNonNull(password, "password can't be null");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection(this.username, this.password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(this.url, username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return iface.cast(this);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.getClass().equals(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.out;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.out = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeOut = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.loginTimeOut;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}

