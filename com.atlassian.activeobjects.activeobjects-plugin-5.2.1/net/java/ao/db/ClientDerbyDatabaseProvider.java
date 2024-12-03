/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.db;

import java.sql.Connection;
import java.sql.SQLException;
import net.java.ao.DisposableDataSource;
import net.java.ao.db.DerbyDatabaseProvider;

public class ClientDerbyDatabaseProvider
extends DerbyDatabaseProvider {
    public ClientDerbyDatabaseProvider(DisposableDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void setPostConnectionProperties(Connection conn) throws SQLException {
    }
}

