/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.dbexporter.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import net.java.ao.DatabaseProvider;

final class DatabaseProviderConnectionProvider
implements ConnectionProvider {
    private final DatabaseProvider provider;

    public DatabaseProviderConnectionProvider(DatabaseProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.provider.getConnection();
    }
}

