/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DelegatingDataSource
implements DataSource,
InitializingBean {
    @Nullable
    private DataSource targetDataSource;

    public DelegatingDataSource() {
    }

    public DelegatingDataSource(DataSource targetDataSource) {
        this.setTargetDataSource(targetDataSource);
    }

    public void setTargetDataSource(@Nullable DataSource targetDataSource) {
        this.targetDataSource = targetDataSource;
    }

    @Nullable
    public DataSource getTargetDataSource() {
        return this.targetDataSource;
    }

    protected DataSource obtainTargetDataSource() {
        DataSource dataSource = this.getTargetDataSource();
        Assert.state((dataSource != null ? 1 : 0) != 0, (String)"No 'targetDataSource' set");
        return dataSource;
    }

    public void afterPropertiesSet() {
        if (this.getTargetDataSource() == null) {
            throw new IllegalArgumentException("Property 'targetDataSource' is required");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.obtainTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.obtainTargetDataSource().getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.obtainTargetDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.obtainTargetDataSource().setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.obtainTargetDataSource().getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.obtainTargetDataSource().setLoginTimeout(seconds);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T)this;
        }
        return this.obtainTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || this.obtainTargetDataSource().isWrapperFor(iface);
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger("global");
    }
}

