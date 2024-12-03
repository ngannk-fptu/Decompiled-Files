/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.core.NamedThreadLocal;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class UserCredentialsDataSourceAdapter
extends DelegatingDataSource {
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String catalog;
    @Nullable
    private String schema;
    private final ThreadLocal<JdbcUserCredentials> threadBoundCredentials = new NamedThreadLocal("Current JDBC user credentials");

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setCredentialsForCurrentThread(String username, String password) {
        this.threadBoundCredentials.set(new JdbcUserCredentials(username, password));
    }

    public void removeCredentialsFromCurrentThread() {
        this.threadBoundCredentials.remove();
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection con;
        JdbcUserCredentials threadCredentials = this.threadBoundCredentials.get();
        Connection connection = con = threadCredentials != null ? this.doGetConnection(threadCredentials.username, threadCredentials.password) : this.doGetConnection(this.username, this.password);
        if (this.catalog != null) {
            con.setCatalog(this.catalog);
        }
        if (this.schema != null) {
            con.setSchema(this.schema);
        }
        return con;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.doGetConnection(username, password);
    }

    protected Connection doGetConnection(@Nullable String username, @Nullable String password) throws SQLException {
        Assert.state((this.getTargetDataSource() != null ? 1 : 0) != 0, (String)"'targetDataSource' is required");
        if (StringUtils.hasLength((String)username)) {
            return this.getTargetDataSource().getConnection(username, password);
        }
        return this.getTargetDataSource().getConnection();
    }

    private static final class JdbcUserCredentials {
        public final String username;
        public final String password;

        public JdbcUserCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String toString() {
            return "JdbcUserCredentials[username='" + this.username + "',password='" + this.password + "']";
        }
    }
}

