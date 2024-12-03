/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.DelegatingConnection
 *  com.atlassian.dbexporter.ConnectionProvider
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.config.db.DelegatingConnection;
import com.atlassian.dbexporter.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;

public class HibernateConnectionProvider
implements ConnectionProvider {
    private final Connection connection;

    public HibernateConnectionProvider(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() throws SQLException {
        return new DelegatingConnection(this.connection){

            public void close() throws SQLException {
            }
        };
    }
}

