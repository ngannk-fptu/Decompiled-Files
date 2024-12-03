/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.lang.Nullable;

public abstract class AbstractDriverBasedDataSource
extends AbstractDataSource {
    @Nullable
    private String url;
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String catalog;
    @Nullable
    private String schema;
    @Nullable
    private Properties connectionProperties;

    public void setUrl(@Nullable String url) {
        this.url = url != null ? url.trim() : null;
    }

    @Nullable
    public String getUrl() {
        return this.url;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    public void setCatalog(@Nullable String catalog) {
        this.catalog = catalog;
    }

    @Nullable
    public String getCatalog() {
        return this.catalog;
    }

    public void setSchema(@Nullable String schema) {
        this.schema = schema;
    }

    @Nullable
    public String getSchema() {
        return this.schema;
    }

    public void setConnectionProperties(@Nullable Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    @Nullable
    public Properties getConnectionProperties() {
        return this.connectionProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnectionFromDriver(this.getUsername(), this.getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnectionFromDriver(username, password);
    }

    protected Connection getConnectionFromDriver(@Nullable String username, @Nullable String password) throws SQLException {
        Properties mergedProps = new Properties();
        Properties connProps = this.getConnectionProperties();
        if (connProps != null) {
            mergedProps.putAll((Map<?, ?>)connProps);
        }
        if (username != null) {
            mergedProps.setProperty("user", username);
        }
        if (password != null) {
            mergedProps.setProperty("password", password);
        }
        Connection con = this.getConnectionFromDriver(mergedProps);
        if (this.catalog != null) {
            con.setCatalog(this.catalog);
        }
        if (this.schema != null) {
            con.setSchema(this.schema);
        }
        return con;
    }

    protected abstract Connection getConnectionFromDriver(Properties var1) throws SQLException;
}

