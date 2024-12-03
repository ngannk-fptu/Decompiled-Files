/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.persistence.DatabaseConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;

final class HibernateDatabaseConnectionProvider
implements DatabaseConnectionProvider {
    private final SessionFactoryImplementor sessionFactory;

    HibernateDatabaseConnectionProvider(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Connection getConnection() {
        try {
            return this.getConnectionProvider().getConnection();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionProvider getConnectionProvider() {
        return (ConnectionProvider)this.sessionFactory.getServiceRegistry().getService(ConnectionProvider.class);
    }
}

