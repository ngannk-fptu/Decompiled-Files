/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.util.Objects;
import net.java.ao.builder.ConnectionPool;
import net.java.ao.builder.DatabaseProperties;

final class BuilderDatabaseProperties
implements DatabaseProperties {
    private final String url;
    private final String username;
    private final String password;
    private final ConnectionPool pool;
    private String schema = null;

    public BuilderDatabaseProperties(String url, String username, String password, ConnectionPool pool) {
        this.url = Objects.requireNonNull(url, "url can't be null");
        this.username = Objects.requireNonNull(username, "username can't be null");
        this.password = Objects.requireNonNull(password, "password can't be null");
        this.pool = Objects.requireNonNull(pool, "pool can't be null");
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public ConnectionPool getConnectionPool() {
        return this.pool;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}

